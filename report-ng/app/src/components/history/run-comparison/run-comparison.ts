/*
 * Testerra
 *
 * (C) 2025, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
import {ResultStatusType} from "../../../services/report-model/framework_pb";

@autoinject()
export class RunComparison extends AbstractViewModel {
    private _historyStatistics: HistoryStatistics;
    private _methodsToCompare;
    private _historyAvailable = false;
    private _availableRuns: any[] = [];
    currentRunStatistics;
    selectedRunStatistics;
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

            this._availableRuns = this._historyStatistics.getHistoryAggregateStatistics().slice(0, -1).map(aggregate => {
                return {
                    historyIndex: aggregate.historyIndex,
                    startTime: aggregate.historyAggregate.executionContext.contextValues.startTime
                }
            });

            // this._availableRuns = this._historyStatistics.availableRuns.slice(0, -1); // Get all runs except the current run
            this._availableRuns.reverse();
            this.currentRunStatistics = this._historyStatistics.getLastEntry();
            this._selectedHistoryIndex = this._availableRuns[0].historyIndex;
            this._historyIndexChanged();
        });
    }

    private _historyIndexChanged() {
        this.selectedRunStatistics = this._historyStatistics.getHistoryAggregateStatistics().find(historyAggregate => historyAggregate.historyIndex === this._selectedHistoryIndex);
        this._methodsToCompare = this._historyStatistics.getClassHistory().flatMap(classItem =>
            classItem.methods.map(method => {
                const currentRun = method.runs.find(run => run.historyIndex === this.currentRunStatistics.historyIndex);
                const pastRun = method.runs.find(run => run.historyIndex === this._selectedHistoryIndex);
                if (!pastRun || !currentRun) return null;

                return {
                    classIdentifier: classItem.identifier,
                    methodIdentifier: method.identifier,
                    methodRunId: currentRun.context.contextValues.id,
                    currentStatus: currentRun.context.resultStatus,
                    pastStatus: pastRun.context.resultStatus
                }
            })
        ).filter(methodObj => methodObj !== null && !(methodObj.currentStatus === ResultStatusType.PASSED && methodObj.pastStatus === ResultStatusType.PASSED)
        ).sort((a, b) => {
            const aChanged = a.currentStatus !== a.pastStatus ? 0 : 1;
            const bChanged = b.currentStatus !== b.pastStatus ? 0 : 1;

            if (aChanged !== bChanged) {
                return aChanged - bChanged;
            }

            return a.methodIdentifier.localeCompare(b.methodIdentifier);
        });
    }

    async activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;
    }
}
