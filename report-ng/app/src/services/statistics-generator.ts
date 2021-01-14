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

export class MethodDetails {
    executionStatistics: ExecutionStatistics;
    methodContext: IMethodContext;
    classStatistics: ClassStatistics;
    testContext: ITestContext;
    suiteContext: ISuiteContext;
    failureAspectStatistics:FailureAspectStatistics;
    sessionContexts:ISessionContext[];
    private _customContexts:any[];

    get identifier() {
        let identifier = this.methodContext.contextValues.name;
        const params = [];
        for (const name in this.methodContext.parameters) {
            params.push(name + ": " + this.methodContext.parameters[name]);
        }
        if (params.length > 0) {
            identifier += "(" + params.join(", ") + ")";
        }
        return identifier;
    }

    get numDetails() {
        return (this.methodContext.errorContext ? 1 : 0) + (this.methodContext.customContextJson ? 1 : 0);
    }

    get failedStep() {
        return (this.methodContext.failedStepIndex >= 0 ? this.methodContext.testSteps[this.methodContext.failedStepIndex] : null);
    }

    get customContexts() {
        if (!this._customContexts) {
            this._customContexts = JSON.parse(this.methodContext.customContextJson);
        }
        return this._customContexts;
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
                    let classStatistics:ClassStatistics;

                    // Group by test context name
                    if (classContext.testContextName) {
                        classStatistics = executionStatistics.classStatistics.find(classStatistics => classStatistics.classContext.testContextName == classContext.testContextName);

                        // Or by class name
                    } else {
                        classStatistics = executionStatistics.classStatistics.find(classStatistics => classStatistics.classContext.fullClassName == classContext.fullClassName);
                    }
                    if (!classStatistics) {
                        classStatistics = new ClassStatistics().setClassContext(classContext);
                        executionStatistics.addClassStatistics(classStatistics);
                    }

                    methodContext.resultStatus = this._statusConverter.correctStatus(methodContext.resultStatus);
                    classStatistics.addMethodContext(methodContext);
                }
                executionStatistics.updateStatistics();
                // console.log(executionStatistics);
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

                        const methodDetails = new MethodDetails();
                        methodDetails.executionStatistics = executionStatistics;
                        methodDetails.methodContext = methodContext;
                        methodDetails.classStatistics = classStatistic;
                        methodDetails.testContext = testContext;
                        methodDetails.suiteContext = suiteContext;
                        methodDetails.sessionContexts = sessionContexts;
                        return methodDetails;
                    }
                }
            });
        });
    }

    getScreenshotsFromMethodContext(methodContext:IMethodContext) {
        return this.getFilesForIds(methodContext.testSteps
            .flatMap(value => value.actions)
            .flatMap(value => value.entries)
            .filter(value => value.screenshotId)
            .map(value => value.screenshotId));
    }

    getFilesForIds(fileIds:string[]) {
        const files:IFile[] = [];
        const allFilePromises = [];
        fileIds.forEach(value => {
            allFilePromises.push(this._dataLoader.getFile(value).then(file => {
                file.relativePath = this._config.correctRelativePath(file.relativePath);
                files.push(file);
            }));
        })
        return Promise.all(allFilePromises).then(()=>files);
    }

}

