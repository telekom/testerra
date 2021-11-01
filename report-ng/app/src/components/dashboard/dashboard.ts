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
import {IFilter, StatusConverter} from "services/status-converter";
import {StatisticsGenerator} from "services/statistics-generator";
import {ExecutionStatistics, FailureAspectStatistics} from "services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import {data} from "../../services/report-model";
import MethodType = data.MethodType;
import FailureCorridorValue = data.FailureCorridorValue;
import ResultStatusType = data.ResultStatusType;
import "./dashboard.scss"
import {ClassBarClick} from "../test-classes-card/test-classes-card";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {PieceClickedEvent} from "../test-results-card/test-results-card";

class FailureCorridor {
    count:number = 0;
    limit:number = 0;
    get matched() {
        return this.count <= this.limit;
    }
}

interface IItem {
    status: ResultStatusType,
    counts: (string|number)[],
    labels: string[],
}

@autoinject()
export class Dashboard extends AbstractViewModel {
    private _executionStatistics: ExecutionStatistics;
    private _passedRetried = 0;
    private _majorFailures = 0;
    private _minorFailures = 0;
    private _highCorridor = new FailureCorridor();
    private _midCorridor = new FailureCorridor();
    private _lowCorridor = new FailureCorridor();
    private _filterItems:IItem[];
    private _filter:IFilter;
    private _topFailureAspects:FailureAspectStatistics[];
    private _loading = true;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        if (this.queryParams.status) {
            this._filter = {
                status: this._statusConverter.getStatusForClass(this.queryParams.status)
            }
        }
    }

    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            this._topFailureAspects = this._executionStatistics.uniqueFailureAspects.slice(0,3);
            this._passedRetried = this._executionStatistics.getStatusesCount([ResultStatusType.PASSED_RETRY,ResultStatusType.MINOR_RETRY]);

            this._filterItems = [];
            let count = this._executionStatistics.getStatusCount(data.ResultStatusType.FAILED);
            if (count > 0) {
                const failedRetriedCount = this._executionStatistics.getStatusCount(data.ResultStatusType.FAILED_RETRIED);
                this._filterItems.push({
                    status: ResultStatusType.FAILED,
                    counts: [
                        count,
                        failedRetriedCount>0?" + " + failedRetriedCount:null
                    ],
                    labels: [
                        this._statusConverter.getLabelForStatus(ResultStatusType.FAILED),
                        failedRetriedCount>0?this._statusConverter.getLabelForStatus(data.ResultStatusType.FAILED_RETRIED):null
                    ],
                });
            }

            count = this._executionStatistics.getStatusCount(ResultStatusType.FAILED_EXPECTED);
            if (count > 0) {
                this._filterItems.push({
                    status: ResultStatusType.FAILED_EXPECTED,
                    counts: [count],
                    labels: [this._statusConverter.getLabelForStatus(ResultStatusType.FAILED_EXPECTED)],
                });
            }

            count = this._executionStatistics.getStatusCount(ResultStatusType.SKIPPED);
            if (count > 0) {
                this._filterItems.push({
                    status: ResultStatusType.SKIPPED,
                    counts: [count],
                    labels: [this._statusConverter.getLabelForStatus(ResultStatusType.SKIPPED)],
                });
            }

            count = this._executionStatistics.overallPassed;
            if (count > 0) {
                this._filterItems.push({
                    status: ResultStatusType.PASSED,
                    counts: [
                        count,
                        (this._executionStatistics.repairedTests>0?`&sup; ${this._executionStatistics.repairedTests}`:null),
                        (this._passedRetried>0?`&sup; ${this._passedRetried}`:null),
                    ],
                    labels: [
                        this._statusConverter.getLabelForStatus(ResultStatusType.PASSED),
                        (this._executionStatistics.repairedTests>0?"Repaired":null),
                        (this._passedRetried>0?this._statusConverter.getLabelForStatus(ResultStatusType.PASSED_RETRY):null),
                    ],
                });
            }

            this._executionStatistics.uniqueFailureAspects.forEach(failureAspectStatistics => {
                if (failureAspectStatistics.isMinor) {
                    ++this._minorFailures;
                } else {
                    ++this._majorFailures
                }
            })

            /**
             * @todo Move this to {@link ExecutionStatistics}?
             */
            const executionAggregate = this._executionStatistics.executionAggregate;
            this._highCorridor.limit = executionAggregate.executionContext.failureCorridorLimits[FailureCorridorValue.FCV_MID];
            this._midCorridor.limit = executionAggregate.executionContext.failureCorridorLimits[FailureCorridorValue.FCV_MID];
            this._lowCorridor.limit = executionAggregate.executionContext.failureCorridorLimits[FailureCorridorValue.FCV_LOW];
            Object.values(executionAggregate.methodContexts)
                .filter(value => value.methodType==MethodType.TEST_METHOD)
                .forEach(value => {
                    if (this._statusConverter.failedStatuses.indexOf(value.resultStatus) >= 0) {
                        switch (value.failureCorridorValue) {
                            case data.FailureCorridorValue.FCV_HIGH: this._highCorridor.count++; break;
                            case data.FailureCorridorValue.FCV_MID: this._midCorridor.count++; break;
                            case data.FailureCorridorValue.FCV_LOW: this._lowCorridor.count++; break;
                        }
                    }
                });

            this._loading = false;
        });
    };

    private _setFilter(filter:IFilter, updateUrl:boolean = true) {
        this._filter = filter;
        if (filter) {
            this.queryParams.status = this._statusConverter.getClassForStatus(this._filter.status);
            this.queryParams.class = this._filter.class;
        } else {
            delete this.queryParams.status;
        }
        if (updateUrl) {
            this.updateUrl(this.queryParams);
        }
    }

    private _resultItemClicked(item:IItem) {
        /**
         * It still happens that items keep selected when they shouldn't
         * https://gist.dumber.app/?gist=f09831456ae377d1121e8a41eece1c42
         */
        if (item.status === this._filter?.status) {
            this._setFilter(null);
        } else {
            this._setFilter({
                status: item.status
            });
        }
    }

    private _piePieceClicked(ev:PieceClickedEvent) {
        if (ev.detail.filter?.status === this._filter?.status) {
            this._setFilter(null);
        } else {
            this._setFilter(ev.detail.filter);
        }
    }

    private _classBarClicked(ev:ClassBarClick) {
        this._setFilter(ev.detail.filter, false);
        if (ev.detail.mouseEvent.button == 0) {
            this.navInstruction.router.navigateToRoute("tests", this.queryParams);
        } else {
            // Open window in new tab
            const classView = window.open(this.navInstruction.router.generate("tests", this.queryParams));
            classView.blur();
            window.focus()
        }
    }

    private _gotoFailureAspect(failureAspect:FailureAspectStatistics) {
        this.navInstruction.router.navigateToRoute("tests", {
            failureAspect: failureAspect.index+1,
            status: this._statusConverter.getClassForStatus(failureAspect.getUpmostStatus())
        });
    }
}
