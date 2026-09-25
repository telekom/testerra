/*
 * Testerra
 *
 * (C) 2026, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import type {HistoricalMethod} from "./HistoricalMethod.ts";
import {HistoricalMethodRun} from "./HistoricalMethodRun.ts";
import {MethodType, ResultStatusType} from "./report-model/framework_pb.ts";
import {Statistics} from "./Statistics.ts";

export class MethodHistoryStatistics extends Statistics {
    private readonly _identifier: string;
    private readonly _relatedMethods: string[] = [];
    private _runs: HistoricalMethodRun[] = [];
    private _flakinessFullWeightRunCount = 10;
    private _flakinessDecayFactor = 0.9;

    constructor(method: HistoricalMethod) {
        super();
        this._identifier = method.identifier;
        this._relatedMethods = method.relatedMethods;
    }

    private _getFlakiness(runs: HistoricalMethodRun[]): number {
        const runCount = runs.length;
        if (runCount < 2) {
            return 0;
        }

        let weightedSwitchSum = 0;
        let totalWeight = 0;

        for (let i = 1; i < runCount; i++) {
            const currentResultStatus: ResultStatusType = runs[i].getParsedResultStatus();
            const previousResultStatus: ResultStatusType = runs[i - 1].getParsedResultStatus();

            const isSwitch = currentResultStatus !== previousResultStatus ? 1 : 0;
            let weight: number;
            if (i > runCount - this._flakinessFullWeightRunCount) {
                weight = 1;
            } else {
                weight = Math.pow(this._flakinessDecayFactor, (runCount - this._flakinessFullWeightRunCount) - i);
            }

            weightedSwitchSum += isSwitch * weight;
            totalWeight += weight;
        }

        return (weightedSwitchSum / totalWeight * 100);
    }

    private _getMethodRunsInRange(startIndex: number, endIndex: number) {
        const runsInRange: HistoricalMethodRun[] = [];
        for (let currentIndex = startIndex; currentIndex <= endIndex; currentIndex++) {
            const currentRun = this._runs.find(run => run.historyIndex === currentIndex);
            if (currentRun) {
                runsInRange.push(currentRun);
            }
        }
        return runsInRange;
    }

    private _getContextOfLatestRun() {
        return this.runs[this.runs.length - 1].context;
    }

    private _getPassingStreak(runs: HistoricalMethodRun[]): number {
        if ((runs.length < 2) || (runs[runs.length - 1].getParsedResultStatus() != ResultStatusType.PASSED)) {
            return 0;
        }

        return this._getStatusStreak(runs);
    }

    private _getFailingStreak(runs: HistoricalMethodRun[]): number {
        if ((runs.length < 2) || (runs[runs.length - 1].context.resultStatus === ResultStatusType.FAILED_RETRIED) || (runs[runs.length - 1].getParsedResultStatus() === ResultStatusType.PASSED)) {
            return 0;
        }

        return this._getStatusStreak(runs);
    }

    private _getStatusStreak(runs: HistoricalMethodRun[]): number {
        let statusStreak = 1;
        for (let i = runs.length - 2; i >= 0; i--) {
            if ((runs[i].getParsedResultStatus() != runs[i + 1].getParsedResultStatus()) || (runs[i].historyIndex != runs[i + 1].historyIndex - 1)) {
                break;
            }
            statusStreak++;
        }
        return statusStreak;
    }

    private _getAverageDuration(runs: HistoricalMethodRun[]): number {
        const durations = runs
            .map(run => {
                const startTime = run.context?.contextValues?.startTime;
                const endTime = run.context?.contextValues?.endTime;
                if ((typeof startTime !== "number") || (typeof endTime !== "number")) {
                    return undefined;
                }
                return endTime - startTime;
            })
            .filter((duration): duration is number => duration !== undefined);

        if (durations.length === 0) {
            return 0;
        }

        const sum = durations.reduce((accumulator, currentValue) => accumulator + currentValue, 0);
        return Math.round(sum / durations.length);
    }

    isTestMethod(): boolean {
        return this._getContextOfLatestRun().methodType === MethodType.TEST_METHOD;
    }

    addRun(historicalMethod: HistoricalMethod, historyIndex: number) {
        this._runs.push(new HistoricalMethodRun(historicalMethod, historyIndex));
        this.addResultStatus(historicalMethod.context.resultStatus ?? ResultStatusType.NO_RUN);
    }

    getRunCount() {
        return this._runs.length;
    }

    getPassingStreakInRange(startIndex: number, endIndex: number): number {
        if (this._runs.map(run => run.historyIndex).includes(endIndex)) {
            return this._getPassingStreak(this._getMethodRunsInRange(startIndex, endIndex));
        }
        return 0;
    }

    getFailingStreakInRange(startIndex: number, endIndex: number): number {
        if (this._runs.map(run => run.historyIndex).includes(endIndex)) {
            return this._getFailingStreak(this._getMethodRunsInRange(startIndex, endIndex));
        }
        return 0;
    }

    getFlakinessInRange(startIndex: number, endIndex: number): number {
        if (this._runs.map(run => run.historyIndex).includes(endIndex)) {
            return this._getFlakiness(this._getMethodRunsInRange(startIndex, endIndex));
        }
        return 0;
    }

    getAverageDurationInRange(startIndex: number, endIndex: number): number {
        return this._getAverageDuration(this._getMethodRunsInRange(startIndex, endIndex));
    }

    getErrorCount() {
        const errorCount = new Map<string, number>();
        this._runs.forEach(run => {
            const failureAspects = run.failureAspects;
            if (failureAspects.length > 0) {
                failureAspects.forEach(error => {
                    if (error) {
                        const currentErrorCount = errorCount.get(error) || 0;
                        errorCount.set(error, currentErrorCount + 1);
                    }
                });
            }
        });
        return errorCount;
    }

    getRunWithHistoryIndex(historyIndex: number) {
        return this._runs.find(run => run.historyIndex === historyIndex);
    }

    getIdOfRun(historyIndex: number) {
        const foundRun = this.getRunWithHistoryIndex(historyIndex);
        if (foundRun && foundRun.context.contextValues) {
            return foundRun.context.contextValues.id;
        }
        return null;
    }

    getStatusOfLatestRun() {
        return this._getContextOfLatestRun().resultStatus;
    }

    get averageDuration(): number {
        return this._getAverageDuration(this._runs);
    }

    get flakiness(): number {
        return this._getFlakiness(this._runs);
    }

    get identifier() {
        return this._identifier;
    }

    get runs() {
        return this._runs;
    }

    get relatedMethods() {
        return this._relatedMethods;
    }
}
