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

import {HistoryAggregateStatistics} from "./HistoryAggregateStatistics.ts";
import {ClassHistory} from "./ClassHistory.ts";
import { History } from "./report-model/report_pb.ts";
import {MethodHistoryStatistics} from "./MethodHistoryStatistics.ts";

export class HistoryStatistics {
    private _historyAggregateStatistics: HistoryAggregateStatistics[] = [];
    private _classHistory: ClassHistory[] = [];
    private _availableRuns: number[] = [];
    readonly history: History;

    constructor(history: History) {
        this.history = history;
        const entries = history.entries ?? [];
        if (entries.length < 2) {
            return;
        }
        entries.forEach(entry => {
            this._availableRuns.push(entry.historyIndex ?? 0);
            this._historyAggregateStatistics.push(new HistoryAggregateStatistics(entry));
        });

        const classHistoryMap = new Map<string, ClassHistory>();

        this._historyAggregateStatistics.forEach(aggregate => {
            const currentHistoryIndex = aggregate.historyIndex;

            aggregate.classes.forEach(clsStat => {
                const classIdentifier = clsStat.identifier;
                const existingClassHistory = classHistoryMap.get(classIdentifier);
                const classHistory = existingClassHistory ?? new ClassHistory(classIdentifier);

                if (!existingClassHistory) {
                    classHistoryMap.set(classIdentifier, classHistory);
                }

                clsStat.methods.forEach((method) => {
                    const relatedMethodsKey = this._buildRelatedMethodsKey(method.relatedMethods);
                    let methodHistory = classHistory.methods.find(
                        methodHistoryStatistics => {
                            return methodHistoryStatistics.identifier === method.identifier
                                && this._buildRelatedMethodsKey(methodHistoryStatistics.relatedMethods) === relatedMethodsKey;
                        }
                    );
                    if (!methodHistory) {
                        methodHistory = new MethodHistoryStatistics(method);
                        classHistory.addMethod(methodHistory);
                    }
                    methodHistory.addRun(method, currentHistoryIndex);
                });
            });
        });

        this._classHistory = Array.from(classHistoryMap.values());
    }

    private _buildRelatedMethodsKey(relatedMethods: string[]): string {
        // Retried methods can contain duplicated related methods; dedupe and sort for stable matching.
        return Array.from(new Set(relatedMethods)).sort().join("|");
    }

    getRunWithHistoryIndex(historyIndex: number): HistoryAggregateStatistics | undefined {
        return this._historyAggregateStatistics.find(aggregate => aggregate.historyIndex === historyIndex);
    }

    getLastEntry(): HistoryAggregateStatistics | undefined {
        return this._historyAggregateStatistics[this._historyAggregateStatistics.length - 1];
    }

    lastEntryDifferentFrom(runToCompare: HistoryAggregateStatistics): boolean {
        return this.runsAreDifferent(this.getLastEntry(), runToCompare);
    }

    runsAreDifferent(runA: HistoryAggregateStatistics | undefined, runB: HistoryAggregateStatistics | undefined): boolean {
        if (!runA || !runB) {
            return true;
        }
        if (runA.overallTestCases != runB.overallTestCases) {
            return true;
        }

        for (const cls of this._classHistory) {
            for (const method of cls.methods) {
                const methodRunA = method.getRunWithHistoryIndex(runA.historyIndex);
                const methodRunB = method.getRunWithHistoryIndex(runB.historyIndex);

                if (methodRunA && methodRunB && methodRunA.context.resultStatus !== methodRunB.context.resultStatus) {
                    return true;
                }
            }
        }
        return false;
    }

    getHistoryAggregateStatistics(): HistoryAggregateStatistics[] {
        return this._historyAggregateStatistics;
    }

    getClassHistory(): ClassHistory[] {
        return this._classHistory;
    }

    getTotalRunCount(): number {
        return this._historyAggregateStatistics.length;
    }

    getAverageDuration(): number {
        const durations: number[] = [];
        this.getHistoryAggregateStatistics().forEach(aggregate => {
            const contextValues = aggregate.historyAggregate.executionContext?.contextValues;
            if (contextValues?.startTime !== undefined && contextValues.endTime !== undefined) {
                durations.push(contextValues.endTime - contextValues.startTime);
            }
        });
        if (durations.length > 0) {
            const sum = durations.reduce((accumulator, currentValue) => accumulator + currentValue, 0);
            return Math.round(sum / durations.length);
        }
        return 0;
    }

    get availableRuns(): number[] {
        return this._availableRuns;
    }
}
