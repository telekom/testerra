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
import {ExecutionStatistics} from "services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import {data} from "../../services/report-model";
import MethodType = data.MethodType;
import FailureCorridorValue = data.FailureCorridorValue;
import ResultStatusType = data.ResultStatusType;

class FailureCorridor {
    count:number = 0;
    limit:number = 0;
    get matched() {
        return this.count <= this.limit;
    }
}

interface Item {
    status: ResultStatusType,
    count: number,
    active: boolean,
}

@autoinject()
export class Dashboard extends AbstractViewModel {
    private _executionStatistics: ExecutionStatistics;
    private _failedRetried = 0;
    private _passedRetried = 0;
    private _majorFailures = 0;
    private _minorFailures = 0;
    private _highCorridor = new FailureCorridor();
    private _midCorridor = new FailureCorridor();
    private _lowCorridor = new FailureCorridor();
    private _filterItems:Item[];
    private _breakdownFilter:IFilter;
    private _classFilter:IFilter;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }


    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            this._failedRetried = this._executionStatistics.getStatusCount(ResultStatusType.FAILED_RETRIED);
            this._passedRetried = this._executionStatistics.getStatusesCount([ResultStatusType.PASSED_RETRY,ResultStatusType.MINOR_RETRY]);

            this._filterItems = [];
            this._filterItems.push({
                status: ResultStatusType.FAILED,
                count: this._executionStatistics.overallFailed,
                active:false,
            });
            this._filterItems.push({
                status: ResultStatusType.FAILED_EXPECTED,
                count: this._executionStatistics.getStatusCount(ResultStatusType.FAILED_EXPECTED),
                active:false
            });
            this._filterItems.push({
                status: ResultStatusType.SKIPPED,
                count: this._executionStatistics.getStatusCount(ResultStatusType.SKIPPED),
                active:false
            });
            this._filterItems.push({
                status: ResultStatusType.PASSED,
                count: this._executionStatistics.overallPassed,
                active:false
            });

            this._executionStatistics.failureAspectStatistics.forEach(failureAspectStatistics => {
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
        });
    };

    private _resultClicked(item:Item) {
        /**
         * It still happens that items keep selected when they shouldn't
         * https://gist.dumber.app/?gist=f09831456ae377d1121e8a41eece1c42
         */
        this._filterItems.filter(value => value !== item).forEach(value => value.active = false);
        item.active = !item.active;
        if (item.active) {
            this._classFilter = this._breakdownFilter = {
                status: item.status
            };
        } else {
            this._classFilter = this._breakdownFilter = null;
        }
    }

    private _pieFilterChanged(ev:CustomEvent) {
        this._classFilter = ev.detail;
        this._filterItems.forEach(value => {
            value.active = ev.detail?.status === value.status;
        })
    }

    private _barFilterChanged(ev:CustomEvent) {
        const filter:IFilter = ev.detail;
        const queryParams:any = filter;
        queryParams.status = this._statusConverter.getClassForStatus(filter.status);
        this.navInstruction.router.navigateToRoute("tests", queryParams);
    }
}
