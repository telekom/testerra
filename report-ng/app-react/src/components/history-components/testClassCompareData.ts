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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {MethodType, ResultStatusType} from "../../model/report-model/framework_pb.ts";
import {StatusService, type ResultStatus} from "../../model/status-service.tsx";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";

export interface HistoryRunPointBase {
    startTime?: number;
    endTime?: number;
    duration?: number;
}

export interface TestClassRunPoint extends HistoryRunPointBase {
    kind: "class";
    value: [string, string, number, number, number, number];
    total: number;
}

export type HistoryRunPoint = TestClassRunPoint | TestMethodRunPoint;

export interface TestClassCompareData {
    classes: {identifier: string; name: string}[];
    runs: number[];
    points: TestClassRunPoint[];
    statuses: ResultStatus[];
    classStatuses: Map<string, Set<ResultStatus>>;
}

export const buildTestClassCompareData = (historyStatistics: HistoryStatistics): TestClassCompareData => {
    const availableStatuses = StatusService.getRelevantStatuses();
    const classes = new Map<string, {identifier: string; name: string}>();
    const statuses = new Set<ResultStatus>();
    const classStatuses = new Map<string, Set<ResultStatus>>();
    const points: TestClassRunPoint[] = [];
    const aggregates = historyStatistics.getHistoryAggregateStatistics();

    aggregates.forEach(aggregate => {
        const historyIndex = aggregate.historyIndex;
        const contextValues = aggregate.historyAggregate.executionContext?.contextValues;
        const startTime = contextValues?.startTime;
        const endTime = contextValues?.endTime;
        const duration = startTime !== undefined && endTime !== undefined ? endTime - startTime : undefined;

        aggregate.classes.forEach(testClass => {
            const counts = availableStatuses.map(status => status === ResultStatusType.PASSED
                ? testClass.overallPassed
                : testClass.getStatusCount(status));
            const total = counts.reduce((sum, count) => sum + count, 0);
            if (total === 0) {
                return;
            }

            const identifier = testClass.identifier;
            const name = classNameConverter(identifier, ClassName.simpleName);
            classes.set(identifier, {identifier, name});

            const classStatusSet = classStatuses.get(identifier) ?? new Set<ResultStatus>();
            counts.forEach((count, index) => {
                if (count > 0) {
                    statuses.add(availableStatuses[index]);
                    classStatusSet.add(availableStatuses[index]);
                }
            });
            classStatuses.set(identifier, classStatusSet);

            points.push({
                kind: "class",
                value: [
                    String(historyIndex),
                    identifier,
                    counts[0],
                    counts[1],
                    counts[2],
                    counts[3],
                ],
                total,
                startTime,
                endTime,
                duration,
            });
        });
    });

    return {
        classes: Array.from(classes.values()).sort((a, b) => a.name.localeCompare(b.name) || a.identifier.localeCompare(b.identifier)),
        runs: aggregates.map(aggregate => aggregate.historyIndex),
        points,
        statuses: availableStatuses.filter(status => statuses.has(status)),
        classStatuses,
    };
};

export interface TestMethodRunPoint extends HistoryRunPointBase {
    kind: "method";
    value: [string, string, ResultStatus];
    status: ResultStatus;
    errorMessage: string;
}

export const buildTestMethodCompareData = (
    historyStatistics: HistoryStatistics,
    classIdentifier: string,
    selectedStatus?: ResultStatus
) => {
    const testClass = historyStatistics.getClassHistory().find(entry => entry.identifier === classIdentifier);
    const latestRun = historyStatistics.getLastEntry();
    const methodLinks = new Map<string, string>();
    const statuses = new Set<ResultStatus>();
    const points: TestMethodRunPoint[] = [];

    testClass?.methods.forEach(method => {
        const methodId = latestRun && method.isTestMethod()
            ? method.getIdOfRun(latestRun.historyIndex)
            : undefined;
        if (methodId && !methodLinks.has(method.identifier)) {
            methodLinks.set(method.identifier, `/method/${encodeURIComponent(methodId)}/method-history`);
        }
        const runs = method.runs.filter(run =>
            run.context.methodType === MethodType.TEST_METHOD
            && run.context.resultStatus !== ResultStatusType.FAILED_RETRIED
        );
        runs.forEach(run => statuses.add(run.context.resultStatus ?? ResultStatusType.NO_RUN));
        if (selectedStatus !== undefined && !runs.some(run => run.context.resultStatus === selectedStatus)) {
            return;
        }

        runs.forEach(run => {
            const startTime = run.context.contextValues?.startTime;
            const endTime = run.context.contextValues?.endTime;
            const status = run.context.resultStatus ?? ResultStatusType.NO_RUN;
            points.push({
                kind: "method",
                value: [String(run.historyIndex), method.identifier, status],
                status,
                errorMessage: run.combinedErrorMessage,
                startTime,
                endTime,
                duration: startTime !== undefined && endTime !== undefined ? endTime - startTime : undefined,
            });
        });
    });

    const historyIndices = points.map(point => Number(point.value[0]));
    const firstRun = historyIndices.length > 0 ? Math.min(...historyIndices) : 0;
    const lastRun = historyIndices.length > 0 ? Math.max(...historyIndices) : -1;

    return {
        methods: Array.from(new Set(points.map(point => point.value[1]))).sort((a, b) => a.localeCompare(b)),
        methodLinks,
        runs: Array.from({length: lastRun - firstRun + 1}, (_, index) => firstRun + index),
        points,
        statuses: Array.from(statuses).sort((a, b) => a - b),
    };
};
