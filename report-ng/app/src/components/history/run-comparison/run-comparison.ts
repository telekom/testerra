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
import {Router} from "aurelia-router";
import {AbstractViewModel} from "../../abstract-view-model";
import {StatisticsGenerator} from "../../../services/statistics-generator";
import {HistoryAggregateStatistics, HistoryStatistics} from "../../../services/statistic-models";
import {ResultStatusType} from "../../../services/report-model/framework_pb";

export interface IComparableMethod {
    classIdentifier: string,
    methodIdentifier: string,
    methodRunId: number,
    currentStatus: ResultStatusType,
    pastStatus: ResultStatusType,
    changedFailureAspect: boolean,
    methodHistoryAvailable: boolean
}

@autoinject()
export class RunComparison extends AbstractViewModel {
    private _historyStatistics: HistoryStatistics;
    private _methodsToCompare: IComparableMethod[];
    private _historyAvailable: boolean = false;
    private _availableRuns: IRunToCompare[] = [];
    currentRunStatistics: HistoryAggregateStatistics;
    selectedRunStatistics: HistoryAggregateStatistics;
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

            this._availableRuns.reverse();
            this.currentRunStatistics = this._historyStatistics.getLastEntry();
            if (this.queryParams.run) {
                this._selectedHistoryIndex = Number(this.queryParams.run);
            } else {
                this._selectedHistoryIndex = this._availableRuns[0].historyIndex;
            }
            this._historyIndexChanged();
        });
    }

    private _historyIndexChanged() {
        this.selectedRunStatistics = this._historyStatistics.getRunWithHistoryIndex(this._selectedHistoryIndex);
        this.queryParams.run = this._selectedHistoryIndex;
        this.updateUrl(this.queryParams);
        this._methodsToCompare = this._historyStatistics.getClassHistory().flatMap(cls =>
            cls.methods.map(method => {
                const currentRun = method.getRunWithHistoryIndex(this.currentRunStatistics.historyIndex);
                const pastRun = method.getRunWithHistoryIndex(this._selectedHistoryIndex);
                let methodHistoryAvailable = false;

                let currentStatus = null;
                let pastStatus = null;
                let methodRunId = null;
                if (currentRun) {
                    methodRunId = currentRun.context.contextValues.id
                    currentStatus = currentRun.context.resultStatus;
                }
                if (pastRun) {
                    pastStatus = pastRun.context.resultStatus;
                }

                if (currentStatus === ResultStatusType.PASSED && pastStatus === ResultStatusType.PASSED) {
                    return null;
                }

                if (method.runs.length > 1) {
                    methodHistoryAvailable = true;
                }

                let differentFailureAspect = false;
                if (pastRun && currentRun) {
                    if (currentRun.combinedErrorMessage != pastRun.combinedErrorMessage) {
                        differentFailureAspect = true;
                    }
                }

                if (currentStatus === pastStatus && !differentFailureAspect) {
                    return null;
                }

                return {
                    classIdentifier: cls.identifier,
                    methodIdentifier: method.identifier,
                    methodRunId: methodRunId,
                    currentStatus: currentStatus,
                    pastStatus: pastStatus,
                    changedFailureAspect: differentFailureAspect,
                    methodHistoryAvailable: methodHistoryAvailable
                }
            })
        ).filter(methodObj => methodObj !== null
        ).sort((a, b) => {
            const aChanged = a.currentStatus !== a.pastStatus ? 0 : 1;
            const bChanged = b.currentStatus !== b.pastStatus ? 0 : 1;

            if (aChanged !== bChanged) {
                return aChanged - bChanged;
            }

            return a.methodIdentifier.localeCompare(b.methodIdentifier);
        });
    }
}

interface IRunToCompare {
    historyIndex: number,
    startTime: number
}
