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

import {DataLoader} from "../../services/data-loader";
import {StatusConverter} from "../../services/status-converter";
import {autoinject} from "aurelia-framework";
import {MethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics, FailureAspectStatistics} from "../../services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import {data} from "../../services/report-model";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import MethodType = data.MethodType;
import "./classes.scss"
import {ClassName, ClassNameValueConverter} from "../../value-converters/class-name-value-converter";

enum SortBy {
    Class = "CLASS",
    Method = "METHOD",
    RunIndex = "RUNINDEX",
}

@autoinject()
export class Classes extends AbstractViewModel {
    private _executionStatistics: ExecutionStatistics;
    private _selectedStatus: data.ResultStatusType;
    private _availableStatuses: data.ResultStatusType[] | number[];
    private _filteredMethodDetails: MethodDetails[];
    private _showConfigurationMethods: boolean = null;
    private _searchRegexp: RegExp;
    private _uniqueStatuses = 0;
    private _uniqueClasses = 0;
    private _loading = false;
    private _sortBy = SortBy.Class;

    constructor(
        private _dataLoader: DataLoader,
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _classNameValueConverter: ClassNameValueConverter,
    ) {
        super();
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        super.activate(params, routeConfig, navInstruction);

        if (params.status) {
            this._selectedStatus = this._statusConverter.getStatusForClass(params.status);
        } else {
            this._selectedStatus = null;
        }

        if (params.config) {
            if (params.config.toLowerCase() == "true") {
                this._showConfigurationMethods = true;
            } else {
                this._showConfigurationMethods = false;
            }
        }
        this._filter();
    }

    private async _filter() {
        this._loading = true;

        if (this.queryParams.q?.length > 0) {
            this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this.queryParams.q);
        } else {
            this._searchRegexp = null;
            delete this.queryParams.q;
        }

        if (this._selectedStatus > 0) {
            this.queryParams.status = this._statusConverter.getClassForStatus(this._selectedStatus);
        } else {
            delete this.queryParams.status;
        }

        const uniqueClasses = {};
        const uniqueStatuses = {};
        this._filteredMethodDetails = [];
        this._availableStatuses = [];

        const executionStatistics = await this._statisticsGenerator.getExecutionStatistics()
        this._availableStatuses = executionStatistics.availableStatuses;
        // this._availableStatuses = Object.keys();
        // this._statusConverter.relevantStatuses
        //     .concat(...[data.ResultStatusType.FAILED_RETRIED, data.ResultStatusType.PASSED_RETRY])
        //     .forEach(status => {
        //         if (executionStatistics.getStatusesCount(this._statusConverter.groupStatus(status)) > 0) {
        //             this._availableStatuses.push(status)
        //         }
        //     });

        let relevantFailureAspect: FailureAspectStatistics;
        let filterByFailureAspect = false;
        if (this.queryParams.failureAspect > 0) {
            relevantFailureAspect = executionStatistics.uniqueFailureAspects[this.queryParams.failureAspect - 1];
            filterByFailureAspect = true;
        }
        this._executionStatistics = executionStatistics;

        const filteredClassStatistics = executionStatistics.classStatistics
            .map(classStatistics => {
                // Determine if we need to enable showing config methods by default if there has any error occured
                if (this._showConfigurationMethods === null) {
                    this._showConfigurationMethods = classStatistics.configStatistics.overallFailed > 0;
                }
                return classStatistics;
            })
            .filter(classStatistic => {
                return !this.queryParams.class
                    || (
                        this.queryParams.class == this._classNameValueConverter.toView(classStatistic.classIdentifier, ClassName.simpleName)
                        || this.queryParams.class == classStatistic.classIdentifier
                    );
            })

        for (const classStatistic of filteredClassStatistics) {
            let methodContexts = classStatistic.methodContexts;

            if (this._selectedStatus > 0) {
                const selectedStatusGroup = this._statusConverter.groupStatus(this._statusConverter.normalizeStatus(this._selectedStatus));
                methodContexts = methodContexts.filter(methodContext => {
                    return selectedStatusGroup.indexOf(methodContext.resultStatus) >= 0
                });
            }

            if (filterByFailureAspect) {
                methodContexts = methodContexts.filter(methodContext => relevantFailureAspect.methodContexts.indexOf(methodContext) >= 0);
            }

            if (this._showConfigurationMethods === false) {
                methodContexts = methodContexts.filter(methodContext => methodContext.methodType == MethodType.TEST_METHOD);
            }

            let methodDetails = await Promise.all(methodContexts.map(methodContext => {
                return this._statisticsGenerator.getMethodDetails(methodContext.contextValues.id)
            }))


            if (this._searchRegexp) {
                methodDetails = methodDetails.filter(methodDetails => {
                    return (
                        methodDetails.identifier.match(this._searchRegexp)
                        || methodDetails.failureAspects.find(failureAspect => failureAspect.identifier.match(this._searchRegexp))
                        || methodDetails.failsAnnotation?.description?.match(this._searchRegexp)
                        || methodDetails.failsAnnotation?.ticketString?.match(this._searchRegexp)
                        || methodDetails.promptLogs.find(logMessage => logMessage.message.match(this._searchRegexp))
                    )
                });
            }

            methodDetails.forEach(methodDetails => {
                uniqueClasses[classStatistic.classContext.fullClassName] = true;
                uniqueStatuses[methodDetails.methodContext.resultStatus] = true;
                this._filteredMethodDetails.push(methodDetails);
            });
        }

        switch (this._sortBy) {
            case SortBy.Method :
                this._filteredMethodDetails = this._filteredMethodDetails.sort((a, b) => a.identifier.localeCompare(b.identifier));
                break;
            case SortBy.RunIndex :  // Sort by run index
                this._filteredMethodDetails = this._filteredMethodDetails.sort((a, b) => a.methodContext.methodRunIndex - b.methodContext.methodRunIndex);
                break;
            case SortBy.Class :
            default:
                // Sort by class and method name
                this._filteredMethodDetails = this._filteredMethodDetails.sort(
                    (a, b) => a.classStatistics.classIdentifier === b.classStatistics.classIdentifier ?
                        a.identifier.localeCompare(b.identifier) : a.classStatistics.classIdentifier.localeCompare(b.classStatistics.classIdentifier));
        }

        this._uniqueClasses = Object.keys(uniqueClasses).length;
        this._uniqueStatuses = Object.keys(uniqueStatuses).length;
        if (this._showConfigurationMethods) {
            this.queryParams.config = this._showConfigurationMethods;
        } else {
            delete this.queryParams.config;
        }

        this.updateUrl(this.queryParams);
        this._loading = false;
    }

    private _filterOnce() {
        if (!this._loading) {
            this._filter();
        }
    }

    private _sortByRunIndex() {
        this._sortBy = SortBy.RunIndex;
        this._filterOnce();
    }

    private _sortByClass() {
        this._sortBy = SortBy.Class;
        this._filterOnce();
    }

    private _sortByMethod() {
        this._sortBy = SortBy.Method;
        this._filterOnce();
    }

    private _statusChanged() {
        this._filterOnce();
    }

    private _classChanged() {
        this._filterOnce();
    }


    private _showConfigurationChanged() {
        this._filterOnce();
    }

    private _searchQueryChanged() {
        this._filterOnce();
    }
}

