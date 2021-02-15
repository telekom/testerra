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
import IFile = data.IFile;
import IMethodContext = data.IMethodContext;
import {StatusConverter} from "./status-converter";
import ITestContext = data.ITestContext;
import ISuiteContext = data.ISuiteContext;
import ISessionContext = data.ISessionContext;

export class FailsAnnotation {
    constructor(
        readonly annotation:any
    ) {
    }
    get ticketIsUrl() {
        return this.annotation.ticketString?.match(StatusConverter.urlRegexp);
    }
}

export class MethodDetails {
    executionStatistics: ExecutionStatistics;
    testContext: ITestContext;
    suiteContext: ISuiteContext;
    failureAspectStatistics:FailureAspectStatistics;
    sessionContexts:ISessionContext[];
    private _identifier:string = null;
    static readonly FAIL_ANNOTATION_NAME="eu.tsystems.mms.tic.testframework.annotations.Fails";
    private _decodedAnnotations = {};
    private _decodedCustomContexts = {};

    constructor(
        readonly methodContext:IMethodContext,
        readonly classStatistics:ClassStatistics,
    ) {
    }

    get identifier() {
        if (!this._identifier) {
            this._identifier = this.methodContext.contextValues.name;
            const otherMethodWithSameName = this.classStatistics.methodContexts
                .find(otherMethodContext => {
                    return (otherMethodContext.contextValues.id !== this.methodContext.contextValues.id && this.methodContext.contextValues.name === otherMethodContext.contextValues.name)
                });
            if (otherMethodWithSameName) {
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
        return (this.methodContext.errorContext ? 1 : 0) + Object.keys(this.methodContext.customContexts).length;
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

                const executionStatistics = new ExecutionStatistics();
                executionStatistics.setExecutionAggregate(executionAggregate);

                for (const id in executionAggregate.methodContexts) {
                    const methodContext = executionAggregate.methodContexts[id];
                    const classContext = executionAggregate.classContexts[methodContext.classContextId];

                    let currentClassStatistics:ClassStatistics = new ClassStatistics().setClassContext(classContext);
                    const existingClassStatistics = executionStatistics.classStatistics.find(classStatistics => classStatistics.classIdentifier === currentClassStatistics.classIdentifier);
                    if (!existingClassStatistics) {
                        executionStatistics.addClassStatistics(currentClassStatistics);
                    } else {
                        currentClassStatistics = existingClassStatistics;
                    }

                    methodContext.resultStatus = this._statusConverter.correctStatus(methodContext.resultStatus);
                    currentClassStatistics.addMethodContext(methodContext);
                }
                executionStatistics.updateStatistics();
                return executionStatistics;
            });
        })
    }

    getMethodDetails(methodId:string):Promise<MethodDetails> {
        return this._cacheService.getForKeyWithLoadingFunction("method:"+methodId, () => {
            return this.getExecutionStatistics().then(executionStatistics => {
                for (const classStatistic of executionStatistics.classStatistics) {
                    const methodContext = classStatistic.methodContexts.find(methodContext => methodContext.contextValues.id == methodId);
                    if (methodContext) {
                        const classContext = classStatistic.classContext;
                        const testContext = executionStatistics.executionAggregate.testContexts[classContext.testContextId];
                        const suiteContext = executionStatistics.executionAggregate.suiteContexts[testContext.suiteContextId];
                        const sessionContexts = [];
                        methodContext.sessionContextIds.forEach(value => {
                            sessionContexts.push(executionStatistics.executionAggregate.sessionContexts[value]);
                        })

                        const methodDetails = new MethodDetails(methodContext, classStatistic);
                        methodDetails.executionStatistics = executionStatistics;
                        methodDetails.testContext = testContext;
                        methodDetails.suiteContext = suiteContext;
                        methodDetails.sessionContexts = sessionContexts;
                        return methodDetails;
                    }
                }
            });
        });
    }

    getScreenshotIdsFromMethodContext(methodContext:IMethodContext):string[] {
        return methodContext.testSteps
            .flatMap(value => value.actions)
            .flatMap(value => value.entries)
            .filter(value => value.screenshotId)
            .map(value => value.screenshotId);
    }

    getFilesForIds(fileIds:string[]) {
        const files:IFile[] = [];
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

