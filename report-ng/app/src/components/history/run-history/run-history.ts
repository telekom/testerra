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
import {StatusConverter} from "../../../services/status-converter";
import {StatisticsGenerator} from "../../../services/statistics-generator";
import {ResultStatusType} from "../../../services/report-model/framework_pb";

@autoinject()
export class RunHistory extends AbstractViewModel {

    totalRunCount: number = 0;
    avgRunDuration: number = 0;
    overallSuccessRate: number = 0;
    private _historyStatistics: HistoryStatistics;
    statusData: any[] = [];

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _router: Router
    ) {
        super();
    }

    async attached() {
        this._historyStatistics = await this._statisticsGenerator.getHistoryStatistics();

        let overallFailed = 0;
        let overallExpectedFailed = 0;
        let overallSkipped = 0;
        let overallPassed = 0;

        this._historyStatistics.getHistoryAggregateStatistics().forEach(aggregate => {
            overallFailed += aggregate.getStatusCount(ResultStatusType.FAILED);
            overallExpectedFailed += aggregate.getStatusCount(ResultStatusType.FAILED_EXPECTED);
            overallSkipped += aggregate.getStatusCount(ResultStatusType.SKIPPED);
            overallPassed += aggregate.overallPassed;
        });

        // TODO: Optimize code
        if (overallFailed) {
            this.statusData.push({
                status: ResultStatusType.FAILED,
                statusName: this._statusConverter.getLabelForStatus(ResultStatusType.FAILED),
                value: overallFailed,
                itemStyle: {color: this._statusConverter.getColorForStatus(ResultStatusType.FAILED)}
            })
        }
        if (overallExpectedFailed) {
            this.statusData.push({
                status: ResultStatusType.FAILED_EXPECTED,
                statusName: this._statusConverter.getLabelForStatus(ResultStatusType.FAILED_EXPECTED),
                value: overallExpectedFailed,
                itemStyle: {color: this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED)}
            })
        }
        if (overallSkipped) {
            this.statusData.push({
                status: ResultStatusType.SKIPPED,
                statusName: this._statusConverter.getLabelForStatus(ResultStatusType.SKIPPED),
                value: overallSkipped,
                itemStyle: {color: this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED)}
            })
        }
        if (overallPassed) {
            this.statusData.push({
                status: ResultStatusType.PASSED,
                statusName: this._statusConverter.getLabelForStatus(ResultStatusType.PASSED),
                value: overallPassed,
                itemStyle: {color: this._statusConverter.getColorForStatus(ResultStatusType.PASSED)}
            })
        }

        this.totalRunCount = this._historyStatistics.getTotalRuns();
        this.avgRunDuration = this._historyStatistics.getAverageDuration();
        this.overallSuccessRate = (overallPassed / (overallFailed + overallExpectedFailed + overallSkipped + overallPassed)) * 100;
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;
    }
}
