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
import "./run-comparison.scss";
import {StatisticsGenerator} from "../../../services/statistics-generator";
import {HistoryStatistics} from "../../../services/statistic-models";

@autoinject()
export class RunComparison extends AbstractViewModel {
    private _historyStatistics: HistoryStatistics;
    private _historyAvailable = false;
    private _availableRuns: number[] = [];
    private _currentRunStatistics;
    private _selectedRunStatistics;
    private _selectedHistoryIndex: number;

    constructor(
        private _statisticsGenerator: StatisticsGenerator,
        private _router: Router
    ) {
        super();
    }

    attached() {
        this._statisticsGenerator.getHistoryStatistics().then(historyStatistics => {
            this._historyStatistics = historyStatistics;
            if (this._historyStatistics.getTotalRunCount() <= 1) {
                return;
            }
            this._historyAvailable = true;

            this._availableRuns = this._historyStatistics.availableRuns.slice(0, -1); // Get all runs except the current run
            this._availableRuns.reverse();
            this._currentRunStatistics = this._historyStatistics.getLastEntry();
            this._selectedHistoryIndex = this._availableRuns[0];
            this._historyIndexChanged();
        });
    }

    private _historyIndexChanged() {
        this._selectedRunStatistics = this._historyStatistics.getHistoryAggregateStatistics().find(historyAggregate => historyAggregate.historyIndex === this._selectedHistoryIndex);
        // TODO: Visualize the data
    }

    async activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;
    }
}
