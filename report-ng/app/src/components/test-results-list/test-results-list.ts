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

import {data} from "../../services/report-model";
import {autoinject, bindable} from "aurelia-framework";
import {ResultStatusType} from "../../services/report-model/framework_pb";
import {IFilter, StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics, HistoryAggregateStatistics} from "../../services/statistic-models";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {bindingMode} from "aurelia-binding";

interface IItem {
    status: ResultStatusType,
    counts: (string | number)[],
    labels: string[],
    trend: number,
}

@autoinject
export class TestResultsList {
    @bindable({defaultBindingMode: bindingMode.toView})
    filter: IFilter;

    @bindable({defaultBindingMode: bindingMode.toView})
    executionStatistics: ExecutionStatistics;
    private _previousRun: HistoryAggregateStatistics;
    private _historyAvailable = false;
    @bindable({defaultBindingMode: bindingMode.twoWay})
    setFilter: (p: { filter: any; updateUrl: boolean }) => void;
    protected queryParams: any = {};
    private _filterItems: IItem[];
    private _routeConfig: RouteConfig;
    private _navInstruction: NavigationInstruction;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        this._routeConfig = routeConfig;
        this._navInstruction = navInstruction;
        this.queryParams = params;

        if (this.queryParams.status) {
            this.filter = {
                status: this._statusConverter.getStatusForClass(this.queryParams.status)
            }
        }
    }

    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this.executionStatistics = executionStatistics;

            this._statisticsGenerator.getHistoryStatistics().then(historyStatistics => {

                if (historyStatistics.getTotalRunCount() >= 2) {
                    this._previousRun = historyStatistics.getHistoryAggregateStatistics()[historyStatistics.getHistoryAggregateStatistics().length - 2];
                    this._historyAvailable = true;
                }
                this._filterItems = [];

                // Failed
                const failed = this.executionStatistics.overallFailed;
                let failedTrend: number;
                const failedRetried = this.executionStatistics.getStatusCount(data.ResultStatusType.FAILED_RETRIED);
                const counts = [];
                const labels = [];
                counts.push(failed);
                labels.push(this._statusConverter.getLabelForStatus(ResultStatusType.FAILED));
                if (failedRetried > 0) {
                    counts.push("( + " + failedRetried + ")");
                    labels.push(this._statusConverter.getLabelForStatus(data.ResultStatusType.FAILED_RETRIED));
                }

                // Expected failed
                const failedExpected = this.executionStatistics.getStatusCount(ResultStatusType.FAILED_EXPECTED);
                let failedExpectedTrend: number;

                // Skipped
                const skipped = this.executionStatistics.overallSkipped;
                let skippedTrend: number;

                // Passed
                const passed = this.executionStatistics.overallPassed;
                let passedTrend: number;
                const recovered = this.executionStatistics.getStatusCount(data.ResultStatusType.PASSED_RETRY);
                const repaired = this.executionStatistics.getStatusCount(data.ResultStatusType.REPAIRED);

                if (this._historyAvailable) {
                    failedTrend = failed - (this._previousRun.overallFailed ?? 0);
                    failedExpectedTrend = failedExpected - (this._previousRun.getStatusCount(ResultStatusType.FAILED_EXPECTED) ?? 0);
                    skippedTrend = skipped - (this._previousRun.overallSkipped ?? 0);
                    passedTrend = passed - (this._previousRun.overallPassed ?? 0)
                }

                this._filterItems.push({
                    status: ResultStatusType.FAILED,
                    counts: counts,
                    labels: labels,
                    trend: failedTrend,
                });
                this._filterItems.push({
                    status: ResultStatusType.FAILED_EXPECTED,
                    counts: [failedExpected],
                    labels: [this._statusConverter.getLabelForStatus(ResultStatusType.FAILED_EXPECTED)],
                    trend: failedExpectedTrend,
                });
                this._filterItems.push({
                    status: ResultStatusType.SKIPPED,
                    counts: [skipped],
                    labels: [this._statusConverter.getLabelForStatus(ResultStatusType.SKIPPED)],
                    trend: skippedTrend,
                });
                this._filterItems.push({
                    status: ResultStatusType.PASSED,
                    counts: [
                        passed,
                        (repaired > 0 ? `&sup; ${repaired}` : null),
                        (recovered > 0 ? `&sup; ${recovered}` : null),
                    ],
                    labels: [
                        this._statusConverter.getLabelForStatus(ResultStatusType.PASSED),
                        (repaired > 0 ? this._statusConverter.getLabelForStatus(ResultStatusType.REPAIRED) : null),
                        (recovered > 0 ? this._statusConverter.getLabelForStatus(ResultStatusType.PASSED_RETRY) : null),
                    ],
                    trend: passedTrend,
                });
            });
        });
    }

    private _getTrendIcon(trend: number): string {
        if (trend > 0) {
            return 'arrow_upward';
        } else if (trend < 0) {
            return 'arrow_downward';
        } else {
            return 'arrow_forward';
        }
    }

    private _resultItemClicked(item: IItem) {
        /**
         * It still happens that items keep selected when they shouldn't
         * https://gist.dumber.app/?gist=f09831456ae377d1121e8a41eece1c42
         */
        if (item.status === this.filter?.status) {
            this.setFilter({filter: null, updateUrl: true});
        } else {
            this.setFilter({filter: {status: item.status}, updateUrl: true});
        }
    }
}
