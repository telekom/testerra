/*
 * Testerra
 *
 * (C) 2024, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import {AbstractViewModel} from "../../abstract-view-model";
import "./run-history.scss";
import {HistoryStatistics} from "../../../services/statistic-models";
import {IFilter, StatusConverter} from "../../../services/status-converter";
import {StatisticsGenerator} from "../../../services/statistics-generator";
import {ResultStatusType} from "../../../services/report-model/framework_pb";

@autoinject()
export class RunHistory extends AbstractViewModel {

    totalRunCount: number = 0;
    avgRunDuration: number = 0;
    overallSuccessRate: number = 0;
    private _historyStatistics: HistoryStatistics;
    statusData: any[] = [];
    private _filter: IFilter;
    private _availableStatuses: ResultStatusType[] = [];
    private _selectedStatus: ResultStatusType = null;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _router: Router
    ) {
        super();
    }

    async attached() {
        this._historyStatistics = await this._statisticsGenerator.getHistoryStatistics();

        let statusCount = new Map<ResultStatusType, number>();
        this._historyStatistics.getHistoryAggregateStatistics().forEach(aggregate => {
            const currentFailed = statusCount.get(ResultStatusType.FAILED) || 0;
            const currentExpectedFailed = statusCount.get(ResultStatusType.FAILED_EXPECTED) || 0;
            const currentSkipped = statusCount.get(ResultStatusType.SKIPPED) || 0;
            const currentPassed = statusCount.get(ResultStatusType.PASSED) || 0;

            statusCount.set(ResultStatusType.FAILED, currentFailed + aggregate.getStatusCount(ResultStatusType.FAILED));
            statusCount.set(ResultStatusType.FAILED_EXPECTED, currentExpectedFailed + aggregate.getStatusCount(ResultStatusType.FAILED_EXPECTED));
            statusCount.set(ResultStatusType.SKIPPED, currentSkipped + aggregate.getStatusCount(ResultStatusType.SKIPPED));
            statusCount.set(ResultStatusType.PASSED, currentPassed + aggregate.overallPassed);
        });

        let overallTestCount = 0;
        statusCount.forEach((count, status) => {
            overallTestCount += count;
            if (count) {
                this._availableStatuses.push(status);
                this.statusData.push({
                    status: status,
                    statusName: this._statusConverter.getLabelForStatus(status),
                    value: count,
                    itemStyle: {color: this._statusConverter.getColorForStatus(status)}
                })
            }
        });

        this.totalRunCount = this._historyStatistics.getTotalRuns();
        this.avgRunDuration = this._historyStatistics.getAverageDuration();
        this.overallSuccessRate = (statusCount.get(ResultStatusType.PASSED) / overallTestCount) * 100;
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;
    }

    private _statusChanged() {
        console.log(this._selectedStatus);
        this._filter = {
            status: this._selectedStatus
        }
    }
}
