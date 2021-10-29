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
import {ClassStatistics, ExecutionStatistics, FailureAspectStatistics} from "./statistic-models";
import {CacheService} from "t-systems-aurelia-components/src/services/cache-service";
import {Config} from "./config-dev";
import {data} from "./report-model";
import {StatusConverter} from "./status-converter";

export interface ILogEntry extends data.ILogMessage {
    methodContext?: data.IMethodContext;
    index?:number,
    cause?:string;
}

export class MethodDetails {
    executionStatistics: ExecutionStatistics;
    testContext: data.ITestContext;
    suiteContext: data.ISuiteContext;
    sessionContexts:data.ISessionContext[];
    private _identifier:string = null;
    static readonly FAILS_ANNOTATION_NAME="eu.tsystems.mms.tic.testframework.annotations.Fails";
    static readonly TEST_ANNOTATION_NAME="org.testng.annotations.Test";
    static readonly XRAY_ANNOTATION_NAME="eu.tsystems.mms.tic.testerra.plugins.xray.annotation.XrayTest";

    private _decodedAnnotations = {};
    private _decodedCustomContexts = {};
    private _failureAspects:FailureAspectStatistics[] = null;

    constructor(
        readonly methodContext:data.IMethodContext,
        readonly classStatistics:ClassStatistics,
    ) {
    }

    get detailedStatus() {
        if (this.methodContext.resultStatus === data.ResultStatusType.PASSED) {
            if (this.methodContext.annotations[MethodDetails.FAILS_ANNOTATION_NAME]) {
                return data.ResultStatusType.REPAIRED;
            } else if (this.methodContext.retryNumber > 0) {
                return data.ResultStatusType.PASSED_RETRY;
            }
        } else if (this.methodContext.resultStatus === data.ResultStatusType.FAILED && this.methodContext.retryNumber > 0) {
            return data.ResultStatusType.FAILED_RETRIED;
        }
        return this.methodContext.resultStatus;
    }

    matchesStatus(status: data.ResultStatusType) {
        if (this.methodContext.resultStatus === status) {
            return true;
        } else {
            return this.detailedStatus === status;
        }
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
        return this.failureAspects.length + Object.keys(this.methodContext.customContexts).length;
    }

    get failedStep() {
        return (this.methodContext.failedStepIndex >= 0 ? this.methodContext.testSteps[this.methodContext.failedStepIndex] : null);
    }

    private _decode(from:{ [k: string]: string }|null, name:string, to: { [k: string]: any }):any {
        if (to[name] === undefined) {
            if (from[name]) {
                to[name] = JSON.parse(from[name]);
            } else {
                to[name] = null;
            }
        }
        return to[name];
    }

    decodeCustomContext(name:string):any {
        return this._decode(this.methodContext.customContexts, name, this._decodedCustomContexts);
    }

    decodeAnnotation(name:string):any {
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
        private _cacheService:CacheService,
        private _config:Config,
        private _statusConverter:StatusConverter,
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

                    let currentClassStatistics:ClassStatistics = new ClassStatistics(classContext);
                    if (!classStatistics[currentClassStatistics.classIdentifier]) {
                        classStatistics[currentClassStatistics.classIdentifier] = currentClassStatistics;
                    } else {
                        currentClassStatistics = classStatistics[currentClassStatistics.classIdentifier];
                    }

                    methodContext.resultStatus = this._statusConverter.correctStatus(methodContext.resultStatus);
                    currentClassStatistics.addMethodContext(methodContext);
                }
                executionStatistics.setClassStatistics(Object.values(classStatistics));
                return executionStatistics;
            });
        })
    }

    getMethodDetails(methodId:string):Promise<MethodDetails> {
        return this._cacheService.getForKeyWithLoadingFunction("method:"+methodId, () => {
            return this.getExecutionStatistics().then(executionStatistics => {
                const methodContext = executionStatistics.executionAggregate.methodContexts[methodId];
                if (methodContext) {
                    const classContext = executionStatistics.executionAggregate.classContexts[methodContext.classContextId];
                    const testContext = executionStatistics.executionAggregate.testContexts[classContext.testContextId];
                    const suiteContext = executionStatistics.executionAggregate.suiteContexts[testContext.suiteContextId];
                    const sessionContexts = [];
                    methodContext.sessionContextIds.forEach(value => {
                        sessionContexts.push(executionStatistics.executionAggregate.sessionContexts[value]);
                    })

                    const methodDetails = new MethodDetails(methodContext, new ClassStatistics(classContext));
                    methodDetails.executionStatistics = executionStatistics;
                    methodDetails.testContext = testContext;
                    methodDetails.suiteContext = suiteContext;
                    methodDetails.sessionContexts = sessionContexts;
                    return methodDetails;
                }
            });
        });
    }

    getScreenshotIdsFromMethodContext(methodContext:data.IMethodContext):string[] {
        return methodContext.testSteps
            .flatMap(value => value.actions)
            .flatMap(value => value.entries)
            .filter(value => value.screenshotId)
            .map(value => value.screenshotId);
    }

    getFilesForIds(fileIds:string[]) {
        const files:data.IFile[] = [];
        const allFilePromises = [];
        fileIds.forEach(fileId => {
            const loadingPromise = this._getFileForId(fileId).then(file => {
               files.push(file);
            });
            allFilePromises.push(loadingPromise);
        })
        return Promise.all(allFilePromises).then(()=>files);
    }

    private _getFileForId(fileId:string) {
        return this._cacheService.getForKeyWithLoadingFunction("file:"+fileId, () => {
            return this._dataLoader.getFile(fileId).then(file => {
                file.relativePath = this._config.correctRelativePath(file.relativePath);
                return file;
            })
        });
    }
}

