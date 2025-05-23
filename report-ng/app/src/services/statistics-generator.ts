/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {autoinject} from "aurelia-framework";
import {DataLoader} from "./data-loader";
import {ClassStatistics, ExecutionStatistics, FailureAspectStatistics, HistoryStatistics} from "./statistic-models";
import {CacheService} from "t-systems-aurelia-components/src/services/cache-service";
import {Config} from "./config-dev";
import {data} from "./report-model";
import {StatusConverter} from "./status-converter";

export interface ILogEntry extends data.LogMessage {
    methodContext?: data.MethodContext;
    index?: number,
    cause?: string;
}

export class MethodDetails {
    executionStatistics: ExecutionStatistics;
    testContext: data.TestContext;
    suiteContext: data.SuiteContext;
    sessionContexts: data.SessionContext[];
    promptLogs: data.LogMessage[]
    private _identifier: string = null;
    static readonly FAILS_ANNOTATION_NAME = "eu.tsystems.mms.tic.testframework.annotations.Fails";
    static readonly TEST_ANNOTATION_NAME = "org.testng.annotations.Test";
    static readonly XRAY_ANNOTATION_NAME = "eu.tsystems.mms.tic.testerra.plugins.xray.annotation.XrayTest";

    private _decodedAnnotations = {};
    private _decodedCustomContexts = {};
    private _failureAspects: FailureAspectStatistics[] = null;
    private _numDetails = -1;

    constructor(
        readonly methodContext: data.MethodContext,
        readonly classStatistics: ClassStatistics,
    ) {
    }

    get failsAnnotation() {
        return this.decodeAnnotation(MethodDetails.FAILS_ANNOTATION_NAME);
    }

    get testAnnotation() {
        return this.decodeAnnotation(MethodDetails.TEST_ANNOTATION_NAME);
    }

    get xrayAnnotation() {
        return this.decodeAnnotation(MethodDetails.XRAY_ANNOTATION_NAME);
    }

    get identifier() {
        if (!this._identifier) {
            if (this.methodContext.testName) {
                this._identifier = this.methodContext.testName;
            } else {
                this._identifier = this.methodContext.contextValues.name;
                const params = [];
                for (const name in this.methodContext.parameters) {
                    params.push(name + ": " + this.methodContext.parameters[name]);
                }
                if (params.length > 0) {
                    this._identifier += "(" + params.join(", ") + ")";
                }
            }
        }
        return this._identifier;
    }

    get numDetails() {
        if (this._numDetails === -1) {
            this._numDetails = this.failureAspects.length
                + Object.keys(this.methodContext.customContexts).length
            ;
        }
        return this._numDetails;
    }

    get failedStep() {
        return (this.methodContext.failedStepIndex >= 0 ? this.methodContext.testSteps[this.methodContext.failedStepIndex] : null);
    }

    private _decode(from: { [k: string]: string } | null, name: string, to: { [k: string]: any }): any {
        if (to[name] === undefined) {
            if (from[name]) {
                to[name] = JSON.parse(from[name]);
            } else {
                to[name] = null;
            }
        }
        return to[name];
    }

    decodeCustomContext(name: string): any {
        return this._decode(this.methodContext.customContexts, name, this._decodedCustomContexts);
    }

    decodeAnnotation(name: string): any {
        return this._decode(this.methodContext.annotations, name, this._decodedAnnotations);
    }

    get errorContexts() {
        return this.methodContext.testSteps
            .flatMap(value => value.actions)
            .flatMap(value => value.entries)
            .filter(value => value.errorContext)
            .map(value => value.errorContext);
    }

    get failureAspects() {
        if (this._failureAspects == null) {
            this._failureAspects = this.errorContexts.map(errorContext => {
                const failureAspect = new FailureAspectStatistics(errorContext);
                failureAspect.addMethodContext(this.methodContext);
                return failureAspect;
            });
        }
        return this._failureAspects;
    }
}

@autoinject()
export class StatisticsGenerator {

    constructor(
        private _dataLoader: DataLoader,
        private _cacheService: CacheService,
        private _config: Config,
        private _statusConverter: StatusConverter,
    ) {
        this._cacheService.setDefaultCacheTtl(120);
    }

    getExecutionStatistics(): Promise<ExecutionStatistics> {
        return this._cacheService.getForKeyWithLoadingFunction("executionStatistics", () => {
            return this._dataLoader.getExecutionAggregate().then(executionAggregate => {

                const executionStatistics = new ExecutionStatistics(executionAggregate);
                const classStatistics = {}

                for (const id in executionAggregate.methodContexts) {
                    const methodContext = executionAggregate.methodContexts[id];
                    const classContext = executionAggregate.classContexts[methodContext.classContextId];

                    let currentClassStatistics: ClassStatistics = new ClassStatistics(classContext);
                    if (!classStatistics[currentClassStatistics.classIdentifier]) {
                        classStatistics[currentClassStatistics.classIdentifier] = currentClassStatistics;
                    } else {
                        currentClassStatistics = classStatistics[currentClassStatistics.classIdentifier];
                    }

                    currentClassStatistics.addMethodContext(methodContext);
                }
                executionStatistics.setClassStatistics(Object.values(classStatistics));
                return executionStatistics;
            });
        })
    }

    getHistoryStatistics(): Promise<HistoryStatistics> {
        return this._cacheService.getForKeyWithLoadingFunction("historyStatistics", () => {
            return this._dataLoader.getHistory().then(history => {
                return new HistoryStatistics(history);
            })
        })
    }

    getLogs() {
        return this._cacheService.getForKeyWithLoadingFunction("logMessages", () => {
            return this._dataLoader.getLogMessages().then(logMessageAggregate => logMessageAggregate.logMessages)
        })
    }

    getMethodPromptLogs(methodContext: data.MethodContext) {
        return this._cacheService.getForKeyWithLoadingFunction("promptLogs:" + methodContext.contextValues.id, async () => {
            const logMessageIds = methodContext.testSteps
                .flatMap(value => value.actions)
                .flatMap(value => value.entries)
                .filter(value => value.logMessageId)
                .map(value => value.logMessageId)
            const logMessages = await this.getLogs()
            return Object.values(logMessages).filter(logMessage => logMessage.prompt && logMessageIds.includes(logMessage.id))
        })
    }

    getMethodDetails(methodId: string) {
        return this._cacheService.getForKeyWithLoadingFunction("method:" + methodId, async () => {
            const executionStatistics = await this.getExecutionStatistics()
            const methodContext = executionStatistics.executionAggregate.methodContexts[methodId];
            if (methodContext) {
                const classContext = executionStatistics.executionAggregate.classContexts[methodContext.classContextId];
                const testContext = executionStatistics.executionAggregate.testContexts[classContext.testContextId];
                const suiteContext = executionStatistics.executionAggregate.suiteContexts[testContext.suiteContextId];
                const sessionContexts = methodContext.sessionContextIds.map(value => executionStatistics.executionAggregate.sessionContexts[value])

                const methodDetails = new MethodDetails(methodContext, new ClassStatistics(classContext));
                methodDetails.executionStatistics = executionStatistics;
                methodDetails.testContext = testContext;
                methodDetails.suiteContext = suiteContext;
                methodDetails.sessionContexts = sessionContexts;
                methodDetails.promptLogs = await this.getMethodPromptLogs(methodContext)
                return methodDetails;
            }
        });
    }

    getSessionMetrics(): Promise<data.SessionMetric[]> {
        return this._cacheService.getForKeyWithLoadingFunction("sessionMetrics", () => {
            return this._dataLoader.getExecutionAggregate().then(executionAggregate => {
                return executionAggregate.testMetrics.sessionMetrics;
            });
        });
    }

    getScreenshotIdsFromMethodContext(methodContext: data.MethodContext): string[] {
        return methodContext.testSteps
            .flatMap(value => value.actions)
            .flatMap(value => value.entries)
            .filter(value => value.screenshotId)
            .map(value => value.screenshotId);
    }

    getFilesForIds(fileIds: string[]) {
        const files: data.File[] = [];
        const allFilePromises = [];
        fileIds.forEach(fileId => {
            const loadingPromise = this._getFileForId(fileId).then(file => {
                files.push(file);
            });
            allFilePromises.push(loadingPromise);
        })
        return Promise.all(allFilePromises).then(() => files);
    }

    private _getFileForId(fileId: string) {
        return this._cacheService.getForKeyWithLoadingFunction("file:" + fileId, () => {
            return this._dataLoader.getFile(fileId).then(file => {
                file.relativePath = this._config.correctRelativePath(file.relativePath);
                return file;
            })
        });
    }
}

