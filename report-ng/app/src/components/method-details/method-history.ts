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
import {StatisticsGenerator} from "services/statistics-generator";
import {AbstractViewModel} from "../abstract-view-model";
import {HistoryStatistics, MethodHistoryStatistics} from "../../services/statistic-models";
import "./method-history.scss";
import {StatusConverter} from "../../services/status-converter";
import {ResultStatusType} from "../../services/report-model/framework_pb";
import {IStatusShare} from "../history/run-history/run-history";

@autoinject()
export class MethodHistory extends AbstractViewModel {
    private _historyStatistics: HistoryStatistics;
    failureAspectsData: any[] = [];
    statusData: IStatusShare[] = [];
    methodHistoryStatistics: MethodHistoryStatistics;
    totalRunCount: number = 0;
    avgRunDuration: number = 0;
    flakiness: string = "0";
    sharedData: string = null;

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

            const foundClass = this._historyStatistics.getClassHistory().find(cls => cls.identifier === methodDetails.classStatistics.classIdentifier);
            if (foundClass) {
                const methodInHistory = foundClass.methods.find(method =>
                    method.getIdOfRun(this._historyStatistics.getLastEntry().historyIndex) === methodDetails.methodContext.contextValues.id
                );
                if (methodInHistory) {
                    this.methodHistoryStatistics = methodInHistory;
                }
            }
        });

        const statusCounts = {
            [ResultStatusType.PASSED]: this.methodHistoryStatistics.overallPassed,
            [ResultStatusType.FAILED]: this.methodHistoryStatistics.getStatusCount(ResultStatusType.FAILED) +
            this.methodHistoryStatistics.getStatusCount(ResultStatusType.FAILED_RETRIED),
            [ResultStatusType.SKIPPED]: this.methodHistoryStatistics.getStatusCount(ResultStatusType.SKIPPED),
            [ResultStatusType.FAILED_EXPECTED]: this.methodHistoryStatistics.getStatusCount(ResultStatusType.FAILED_EXPECTED),
        };

        Object.entries(statusCounts).forEach(([statusKey, count]) => {
            const status = Number(statusKey);
            if (count > 0) {
                this.statusData.push({
                    status,
                    statusName: this._statusConverter.getLabelForStatus(status),
                    value: count,
                    itemStyle: {color: this._statusConverter.getColorForStatus(status)}
                });
            }
        });

        this.totalRunCount = this.methodHistoryStatistics.getRunCount();
        this.avgRunDuration = this.methodHistoryStatistics.averageDuration;
        this.flakiness = this.methodHistoryStatistics.flakiness.toFixed(1);

        this.failureAspectsData = Array.from(this.methodHistoryStatistics.getErrorCount()).sort((a, b) => b[1] - a[1]);
    }

    handleFailureAspectsChartClicked(data: string) {
        this.sharedData = data;
    }
}
