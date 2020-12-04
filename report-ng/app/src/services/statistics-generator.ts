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
import {Config} from "./config";
import {data} from "./report-model";
import IFile = data.IFile;
import IMethodContext = data.IMethodContext;
import {StatusConverter} from "./status-converter";
import ITestContext = data.ITestContext;
import ISuiteContext = data.ISuiteContext;

export interface IMethodDetails {
    executionStatistics?: ExecutionStatistics,
    methodContext: IMethodContext,
    classStatistics?: ClassStatistics,
    testContext?: ITestContext,
    suiteContext?: ISuiteContext,
    hasDetails?: boolean,
    failureAspectStatistics?:FailureAspectStatistics,
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

                executionAggregate.methodContexts.forEach(methodContext => {
                    const classContext = executionAggregate.classContexts.find(classContext => classContext.contextValues.id == methodContext.classContextId);
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

                    methodContext.contextValues.resultStatus = this._statusConverter.correctStatus(methodContext.contextValues.resultStatus);
                    classStatistics.addMethodContext(methodContext);
                })
                executionStatistics.updateStatistics();
                console.log(executionStatistics);
                return executionStatistics;
            });
        })
    }

    getMethodDetails(methodId:string):Promise<IMethodDetails> {
        return this._cacheService.getForKeyWithLoadingFunction("method:"+methodId, () => {
            return this.getExecutionStatistics().then(executionStatistics => {
                for (const classStatistic of executionStatistics.classStatistics) {
                    const methodContext = classStatistic.methodContexts
                        .find(methodContext => methodContext.contextValues.id == methodId);

                    if (methodContext) {
                        const classContext = classStatistic.classContext;
                        const testContext = executionStatistics.executionAggregate.testContexts.find(testContext => testContext.contextValues.id == classContext.testContextId);
                        const suiteContext = executionStatistics.executionAggregate.suiteContexts.find(suiteContext => suiteContext.contextValues.id == testContext.suiteContextId);
                        return {
                            executionStatistics: executionStatistics,
                            methodContext: methodContext,
                            classStatistics: classStatistic,
                            testContext: testContext,
                            suiteContext: suiteContext,
                            hasDetails: (methodContext.errorContext != null && methodContext.customContextJson != null)
                        }
                    }
                }
            });
        })
    }

    getScreenshotsFromMethodContext(methodContext:IMethodContext) {
        const screenshots:IFile[] = [];
        const allFilePromises = [];
        methodContext.testSteps
            .flatMap(testStep => testStep.testStepActions)
            .flatMap(testStepAction => testStepAction.screenshotIds)
            .forEach(id => {
                allFilePromises.push(this._dataLoader.getFile(id).then(file => {
                    file.relativePath = this._config.correctRelativePath(file.relativePath);
                    //screenshots[file.id] = file;
                    screenshots.push(file);
                }));
            })
        return Promise.all(allFilePromises).then(()=>screenshots);
    }
}

