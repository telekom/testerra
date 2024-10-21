/*
 * Testerra
 *
 * (C) 2024, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {autoinject} from 'aurelia-framework';
import './printable.scss';
import {ExecutionStatistics, FailureAspectStatistics} from "../../services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import {MethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {MethodType, ResultStatusType} from "../../services/report-model/framework_pb";
import {ClassName, ClassNameValueConverter} from "../../value-converters/class-name-value-converter";
import {StatusConverter} from "../../services/status-converter";

@autoinject()
export class Printable extends AbstractViewModel {
    private _executionStatistics: ExecutionStatistics;

    private _filteredFailureAspects: FailureAspectStatistics[];
    private _filteredFailureAspectsMajor: FailureAspectStatistics[];
    private _filteredFailureAspectsMinor: FailureAspectStatistics[];

    private _filteredMethodDetails: MethodDetails[];
    private _filteredMethodDetailsFailed: MethodDetails[];
    private _classInformation: IClassInformation[];
    private _relevantStatuses: ResultStatusType[];

    private _title: string;
    private _duration: number;
    private _started: number;
    private _ended: number;
    private _browserString: string;
    private _sessions: number;
    private _runConfig: string;

    constructor(
        private _statisticsGenerator: StatisticsGenerator,
        private _classNameValueConverter: ClassNameValueConverter,
        private _statusConverter: StatusConverter
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
    }

    attached() {
        const uniqueClasses = {};
        const uniqueStatuses = {};
        this._filteredMethodDetails = [];
        this._filteredMethodDetailsFailed = [];
        this._classInformation = [];

        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;

            this._title = this._executionStatistics.executionAggregate.executionContext.runConfig.reportName;
            this._started = this._executionStatistics.executionAggregate.executionContext.contextValues.startTime;
            this._ended = this._executionStatistics.executionAggregate.executionContext.contextValues.endTime;
            this._duration = this._ended - this._started;
            this._runConfig = this._executionStatistics.executionAggregate.executionContext.runConfig.runcfg;

            let browsers: string[] = [];

            this._statisticsGenerator.getSessionMetrics().then(sessionMetrics => {
                this._sessions = sessionMetrics.length;
                sessionMetrics.forEach((metric) => {
                    const browserInfo: string = `${this._executionStatistics.executionAggregate.sessionContexts[metric.sessionContextId].browserName} ${this._executionStatistics.executionAggregate.sessionContexts[metric.sessionContextId].browserVersion}`;
                    if (!browsers.includes(browserInfo)) {
                        browsers.push(browserInfo);
                    }
                })
                this._browserString = browsers.join(", ");
            })

            // since iFrame and actual component are not updating each other once the dialog is open, we cannot directly
            // modify _filteredFailureAspects but instead use a workaround to show and hide filtered versions of the failure aspects table
            this._filteredFailureAspects = executionStatistics.uniqueFailureAspects;
            this._filteredFailureAspectsMajor = this._filteredFailureAspects.filter(failureAspectStats => {
                return !failureAspectStats.isMinor;
            })
            this._filteredFailureAspectsMinor = this._filteredFailureAspects.filter(failureAspectStats => {
                return failureAspectStats.isMinor;
            })

            // get statuses for head of test class table
            this._relevantStatuses = this._statusConverter.relevantStatuses.filter(status => {      // make sure we only use the relevant statuses that are available
                return this._executionStatistics.availableStatuses.some(availableStatus => status == availableStatus)
            })

            executionStatistics.classStatistics
                .sort((a, b) => this._classNameValueConverter.toView(a.classIdentifier, ClassName.simpleName).localeCompare(this._classNameValueConverter.toView(b.classIdentifier, ClassName.simpleName)))
                .forEach(classStatistic => {
                    let methodContexts = classStatistic.methodContexts;
                    let methodDetails = methodContexts.map(methodContext => {
                        return new MethodDetails(methodContext, classStatistic);
                    })

                    methodDetails
                        .filter(methodDetail => methodDetail.methodContext.methodType == MethodType.TEST_METHOD)
                        .forEach(methodDetails => {
                            uniqueClasses[classStatistic.classContext.fullClassName] = true;
                            uniqueStatuses[methodDetails.methodContext.resultStatus] = true;
                            if (methodDetails.methodContext.resultStatus != ResultStatusType.PASSED_RETRY && methodDetails.methodContext.resultStatus != ResultStatusType.FAILED_RETRIED && methodDetails.methodContext.resultStatus != ResultStatusType.REPAIRED) {
                                this._filteredMethodDetails.push(methodDetails);

                                if (methodDetails.methodContext.resultStatus != ResultStatusType.PASSED) {
                                    this._filteredMethodDetailsFailed.push(methodDetails)
                                }
                            }
                        });


                    // get class information for test class table
                    const data = this._relevantStatuses.map(status => {
                        const amount = classStatistic.getSummarizedStatusCount(this._statusConverter.groupStatus(status));      // [failed, expected failed, skipped, passed (incl. recovered and repaired)]
                        return {
                            status: status,
                            amount: amount === 0 ? "-" : amount
                        };
                    })

                    const classInfo: IClassInformation = {
                        name: this._classNameValueConverter.toView(classStatistic.classIdentifier, ClassName.simpleName),
                        data: data
                    };

                    this._classInformation.push(classInfo);
                })
        })

        // check if we use this view in iFrame or not
        if (window.self !== window.top) {
            document.getElementById("mdc-drawer-app-content").setAttribute("style", "padding: 0.75rem 1rem; background-color: white;");     // needs to be set separately otherwise it will change the styling for the whole webpage
        }
    }
}

interface IClassInformation {
    name: string;
    data: { status: ResultStatusType, amount: number | string }[];
}
