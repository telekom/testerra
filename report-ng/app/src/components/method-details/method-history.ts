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
import {MethodDetails, StatisticsGenerator} from "services/statistics-generator";
import {AbstractViewModel} from "../abstract-view-model";
import {HistoryStatistics, MethodHistoryStatistics} from "../../services/statistic-models";
import "./method-history.scss";
import {StatusConverter} from "../../services/status-converter";
import {ResultStatusType} from "../../services/report-model/framework_pb";

@autoinject()
export class MethodHistory extends AbstractViewModel {
    private _historyStatistics: HistoryStatistics;
    private _methodDetails: MethodDetails;
    failureAspectsData: any[] = [];
    statusData: any[] = [];
    methodHistoryStatistics: MethodHistoryStatistics;
    totalRunCount: number = 0;
    avgRunDuration: number = 0;
    overallSuccessRate: number = 0;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _router: Router
    ) {
        super();
    }

    async activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;

        this._historyStatistics = await this._statisticsGenerator.getHistoryStatistics();

        await this._statisticsGenerator.getMethodDetails(params.methodId).then(methodDetails => {
            this._methodDetails = methodDetails;

            this._historyStatistics.getMethodHistoryStatistics().find(method => {
                if (method.isMatchingMethod(methodDetails.methodContext)) {
                    this.methodHistoryStatistics = method;
                }
            });
        });

        const style = new Map<number, string>();
        style.set(ResultStatusType.FAILED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED));
        style.set(ResultStatusType.FAILED_MINOR, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_MINOR));
        style.set(ResultStatusType.FAILED_RETRIED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_RETRIED));
        style.set(ResultStatusType.FAILED_EXPECTED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED));
        style.set(ResultStatusType.SKIPPED, this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED));
        style.set(ResultStatusType.PASSED, this._statusConverter.getColorForStatus(ResultStatusType.PASSED));
        style.set(ResultStatusType.REPAIRED, this._statusConverter.getColorForStatus(ResultStatusType.REPAIRED));
        style.set(ResultStatusType.PASSED_RETRY, this._statusConverter.getColorForStatus(ResultStatusType.PASSED_RETRY));
        let statusKeys = Array.from(style.keys());

        for (const status of statusKeys) {
            const statusCount = this.methodHistoryStatistics.getStatusCount(status);
            if (statusCount) {
                this.statusData.push({
                    status: status,
                    statusName: this._statusConverter.getLabelForStatus(status),
                    value: statusCount,
                    itemStyle: {color: style.get(status)}
                });
            }
        }

        this.totalRunCount = this.methodHistoryStatistics.getMethodRunCount();
        this.avgRunDuration = this.methodHistoryStatistics.getAverageDuration();
        this.overallSuccessRate = this.methodHistoryStatistics.getSuccessRate();

        this.failureAspectsData = Array.from(this.methodHistoryStatistics.getErrorCount()).sort((a, b) => b[1] - a[1]);
    }
}
