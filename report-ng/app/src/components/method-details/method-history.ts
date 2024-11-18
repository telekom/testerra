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

@autoinject()
export class MethodHistory extends AbstractViewModel {
    private _historyStatistics: HistoryStatistics;
    private _methodDetails: MethodDetails;
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

    async attached() {
    };

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

        this.totalRunCount = this.methodHistoryStatistics.getMethodRunCount();
        this.avgRunDuration = this.methodHistoryStatistics.getAverageDuration();
        this.overallSuccessRate = this.methodHistoryStatistics.getSuccessRate();
    }
}
