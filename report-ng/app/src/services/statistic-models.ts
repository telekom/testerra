/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import {data} from "./report-model";
import {Container} from "aurelia-framework";
import {StatusConverter} from "./status-converter";
import {MethodDetails} from "./statistics-generator";
import {MethodType, ResultStatusType} from "./report-model/framework_pb";
import History = data.History;
import HistoryAggregate = data.HistoryAggregate;
import ExecutionAggregate = data.ExecutionAggregate;
import IMethodContext = data.MethodContext;
import IClassContext = data.ClassContext;
import IErrorContext = data.ErrorContext;
import IStackTraceCause = data.StackTraceCause;

class Statistics {
    private _statusConverter: StatusConverter;
    private _resultStatuses: { [key: number]: number } = {};

    constructor() {
        this._statusConverter = Container.instance.get(StatusConverter);
    }

    addResultStatus(status: ResultStatusType) {
        if (!this._resultStatuses[status]) {
            this._resultStatuses[status] = 0;
        }
        this._resultStatuses[status]++;
    }

    get availableStatuses(): ResultStatusType[] {
        const statuses = [];
        for (const status in this._resultStatuses) {
            statuses.push(status);
        }
        return statuses;
    }

    getStatusCount(status: ResultStatusType) {
        return this._resultStatuses[status] | 0;
    }

    getUpmostStatus(): number {
        let count = 0;
        let upmostStatus: any;
        for (let status in this._resultStatuses) {
            if (this._resultStatuses[status] > count) {
                upmostStatus = status;
            }
        }
        return (upmostStatus == data.ResultStatusType.FAILED_EXPECTED && this._resultStatuses.hasOwnProperty(data.ResultStatusType.FAILED))
            ? data.ResultStatusType.FAILED : upmostStatus; // if there is the same failure aspect for one failed and one expected failed test, the failed class color should be displayed
    }

    protected addStatistics(statistics: Statistics) {
        for (const status in statistics._resultStatuses) {
            if (!this._resultStatuses[status]) {
                this._resultStatuses[status] = 0;
            }
            this._resultStatuses[status] += statistics._resultStatuses[status];
        }
    }

    get statusConverter() {
        return this._statusConverter;
    }

    get overallPassed() {
        return this.getSummarizedStatusCount(this._statusConverter.passedStatuses);
    }

    get overallSkipped() {
        return this.getStatusCount(data.ResultStatusType.SKIPPED);
    }

    get overallFailed() {
        return this.getStatusCount(data.ResultStatusType.FAILED);
    }

    /**
     * Returns the number of test cases including passed retried
     */
    get overallTestCases() {
        return this.getSummarizedStatusCount([
            data.ResultStatusType.PASSED,
            data.ResultStatusType.REPAIRED,
            data.ResultStatusType.PASSED_RETRY, // Recovered
            data.ResultStatusType.SKIPPED,
            data.ResultStatusType.FAILED,
            data.ResultStatusType.FAILED_EXPECTED,
            data.ResultStatusType.FAILED_RETRIED,
        ]);
    }

    getSummarizedStatusCount(statuses: number[]) {
        let count = 0;
        statuses.forEach(value => {
            count += this.getStatusCount(value);
        })
        return count;
    }
}

export class ExecutionStatistics extends Statistics {
    private _classStatistics: ClassStatistics[] = [];
    private _uniqueFailureAspects: FailureAspectStatistics[] = [];

    constructor(
        readonly executionAggregate: ExecutionAggregate
    ) {
        super()
    }

    setClassStatistics(classStatistics: ClassStatistics[]) {
        this._classStatistics = classStatistics;
        this._classStatistics.forEach(classStatistics => this.addStatistics(classStatistics));
    }

    private _addUniqueFailureAspect(failureAspect: FailureAspectStatistics, methodContext: data.MethodContext) {
        const foundFailureAspect = this._uniqueFailureAspects.find(existingFailureAspectStatistics => {
            return existingFailureAspectStatistics.identifier == failureAspect.identifier;
        });

        if (foundFailureAspect) {
            failureAspect = foundFailureAspect;
        } else {
            this._uniqueFailureAspects.push(failureAspect);
        }
        failureAspect.addMethodContext(methodContext);
    }

    protected addStatistics(classStatistics: ClassStatistics) {
        super.addStatistics(classStatistics)
        classStatistics.methodContexts
            .forEach(methodContext => {
                const methodDetails = new MethodDetails(methodContext, classStatistics);
                methodDetails.failureAspects.forEach(failureAspect => {
                    this._addUniqueFailureAspect(failureAspect, methodContext);
                })
            })

        // Sort failure aspects by fail count
        this._uniqueFailureAspects = this._uniqueFailureAspects.sort((a, b) => b.overallFailed - a.overallFailed);

        for (let i = 0; i < this._uniqueFailureAspects.length; ++i) {
            this._uniqueFailureAspects[i].index = i;
        }
    }

    get executionContextLogMessageIds() {
        return this.executionAggregate.executionContext.logMessageIds
    }

    get methodContextLogMessageIds() {
        return Object.values(this.executionAggregate.methodContexts)
            .flatMap(methodContext => {
                return methodContext.testSteps
                    .flatMap(testStep => testStep.actions)
                    .flatMap(action => action.entries)
                    .filter(entry => entry.logMessageId)
                    .map(entry => {
                        return {logMessageId: entry.logMessageId, methodContext}
                    })
            })
    }

    get classStatistics() {
        return this._classStatistics;
    }

    //
    // get exitPointStatistics() {
    //     return this._exitPointStatistics;
    // }

    get uniqueFailureAspects() {
        return this._uniqueFailureAspects;
    }
}

export class HistoryStatistics {
    private _historyAggregateStatistics: HistoryAggregateStatistics[] = [];
    private _classHistory: ClassHistory[] = [];
    private _availableRuns: number[] = [];

    constructor(
        readonly history: History
    ) {
        if (history.entries.length < 2) {
            return;
        }
        this.history.entries.forEach(entry => {
            this._availableRuns.push(entry.historyIndex);
            this._historyAggregateStatistics.push(new HistoryAggregateStatistics(entry));
        });

        const classHistoryMap = new Map<string, ClassHistory>();

        this._historyAggregateStatistics.forEach(aggregate => {
            const currentHistoryIndex = aggregate.historyIndex;

            aggregate.classes.forEach(clsStat => {
                const classIdentifier = clsStat.identifier;
                let classHistory = classHistoryMap.get(classIdentifier);

                if (!classHistory) {
                    classHistory = new ClassHistory(classIdentifier);
                    classHistoryMap.set(classIdentifier, classHistory);
                }

                clsStat.methods.forEach((method) => {
                    let methodHistory = classHistory.methods.find(
                        methodHistoryStatistics => methodHistoryStatistics.identifier === method.identifier && this._compareRelatedMethods(methodHistoryStatistics.relatedMethods, method.relatedMethods)
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

    private _compareRelatedMethods(arr1: string[], arr2: string[]): boolean {
        if (arr1.length !== arr2.length) {
            return false;
        }
        const sortedArr1 = [...arr1].sort();
        const sortedArr2 = [...arr2].sort();

        return sortedArr1.every((value, index) => value === sortedArr2[index]);
    }

    getRunWithHistoryIndex(historyIndex): HistoryAggregateStatistics {
        return this._historyAggregateStatistics.find(aggregate => aggregate.historyIndex === historyIndex);
    }

    getLastEntry(): HistoryAggregateStatistics {
        return this._historyAggregateStatistics[this._historyAggregateStatistics.length - 1];
    }

    lastEntryDifferentFrom(runToCompare: HistoryAggregateStatistics): boolean {
        return this.runsAreDifferent(this.getLastEntry(), runToCompare);
    }

    runsAreDifferent(runA: HistoryAggregateStatistics, runB: HistoryAggregateStatistics): boolean {
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
        return this.history.entries.length;
    }

    getAverageDuration(): number {
        let durations: number[] = [];
        this.getHistoryAggregateStatistics().forEach(aggregate => {
            const contextValues = aggregate.historyAggregate.executionContext.contextValues;
            durations.push(contextValues.endTime - contextValues.startTime);
        });
        if (durations.length > 0) {
            let sum = durations.reduce((accumulator, currentValue) => accumulator + currentValue, 0);
            return Math.round(sum / durations.length);
        }
        return 0;
    }

    get availableRuns(): number[] {
        return this._availableRuns;
    }
}

export class ClassHistoryStatistics extends Statistics {
    private readonly _identifier: string;
    private _methods: HistoricalMethod[] = [];

    constructor(
        identifier: string
    ) {
        super();
        this._identifier = identifier;
    }

    addMethod(method: HistoricalMethod) {
        this._methods.push(method);
        if (method.context.methodType === MethodType.TEST_METHOD) {
            this.addResultStatus(method.context.resultStatus);
        }
    }

    get methods() {
        return this._methods;
    }

    get identifier() {
        return this._identifier;
    }
}

export class HistoricalMethod {
    private readonly _methodContext: IMethodContext;
    private readonly _methodIdentifier: string;
    private _relatedMethodIdentifiers: string[] = [];

    constructor(methodContext: IMethodContext) {
        this._methodContext = methodContext;
        this._methodIdentifier = this._getParsedMethodIdentifier();
    }

    private _getParsedMethodIdentifier() {
        let methodName: string;
        if (this._methodContext.testName) {
            methodName = this._methodContext.testName;
        } else {
            methodName = this._methodContext.contextValues.name;
            const params = [];
            for (const name in this._methodContext.parameters) {
                params.push(name + ": " + this._methodContext.parameters[name]);
            }
            if (params.length > 0) {
                methodName += "(" + params.join(", ") + ")";
            }
        }
        return methodName;
    }

    getFailureAspects(): string[] {
        let failureAspects: string[] = [];
        if (this._methodContext.resultStatus != ResultStatusType.PASSED) {
            this._methodContext.testSteps.flatMap(value => value.actions)
                .forEach(actionDetails => {
                    actionDetails.entries.forEach(entry => {
                        const errorContext = entry.errorContext;
                        const errorClassName = errorContext.stackTrace[0].className.substring(errorContext.stackTrace[0].className.lastIndexOf(".") + 1);
                        failureAspects.push((errorClassName + ": " + errorContext.stackTrace[0].message).trim().replaceAll('\n', ' '));
                    });
                });
        }
        return failureAspects;
    }

    getCombinedErrorMessage(): string {
        let combinedErrorMessage = "";
        const failureAspects = this.getFailureAspects();
        if (failureAspects.length > 0) {
            failureAspects.forEach(error => {
                combinedErrorMessage += "\n" + error;
            });
            combinedErrorMessage = combinedErrorMessage.trim();
        }
        return combinedErrorMessage;
    }

    addRelatedMethods(relatedMethod: string) {
        this._relatedMethodIdentifiers.push(relatedMethod);
    }

    get context() {
        return this._methodContext;
    }

    get identifier() {
        return this._methodIdentifier;
    }

    get relatedMethods() {
        return this._relatedMethodIdentifiers;
    }
}

export class HistoryAggregateStatistics extends Statistics {
    private readonly _classMap: Map<string, ClassHistoryStatistics>;
    private readonly _classIdMap: Map<string, string>;
    private readonly _methodMap: Map<string, HistoricalMethod>;
    private readonly _aggregate: HistoryAggregate;
    private readonly _historyIndex: number;

    constructor(historyEntry: HistoryAggregate) {
        super();
        this._aggregate = historyEntry;
        this._classMap = new Map();
        this._classIdMap = new Map();
        this._methodMap = new Map();
        this._historyIndex = historyEntry.historyIndex;

        Object.values(historyEntry.classContexts).forEach(classContext => {
            const classIdentifier = classContext.testContextName || classContext.contextValues.name
            this._classIdMap.set(classContext.contextValues.id, classIdentifier);
        });

        new Set(this._classIdMap.values()).forEach(cls => {
            this._classMap.set(cls, new ClassHistoryStatistics(cls));
        });

        Object.values(historyEntry.methodContexts).forEach(method => {
            this._methodMap.set(method.contextValues.id, new HistoricalMethod(method));
            if (method.methodType === MethodType.TEST_METHOD) {
                this.addResultStatus(method.resultStatus);
            }
        });

        this._methodMap.forEach(method => {
            method.context.relatedMethodContextIds.forEach(relatedMethod => {
                method.addRelatedMethods(this._methodMap.get(relatedMethod).identifier);
            });

            const classId = method.context.classContextId;
            const classIdentifier = this._classIdMap.get(classId);
            const classStats = this._classMap.get(classIdentifier);
            if (classStats) {
                classStats.addMethod(method);
            }
        });
    }

    get classes() {
        return this._classMap;
    }

    get historyIndex() {
        return this._historyIndex;
    }

    get historyAggregate() {
        return this._aggregate;
    }
}

export class HistoricalMethodRun {
    private readonly _historyIndex: number = 0;
    private readonly _context: IMethodContext;
    private readonly _failureAspects: string[];
    private readonly _combinedErrorMessage: string;

    constructor(historicalMethod: HistoricalMethod, index: number) {
        this._historyIndex = index;
        this._context = historicalMethod.context;
        this._failureAspects = historicalMethod.getFailureAspects();
        this._combinedErrorMessage = historicalMethod.getCombinedErrorMessage();
    }

    // Returns the overall status for flakiness calculation
    getParsedResultStatus(): ResultStatusType {
        let status = this._context.resultStatus;
        if (status === ResultStatusType.FAILED_RETRIED) {
            status = ResultStatusType.FAILED;
        } else if (status === ResultStatusType.PASSED_RETRY || status === ResultStatusType.REPAIRED) {
            status = ResultStatusType.PASSED;
        }
        return status;
    }

    get historyIndex() {
        return this._historyIndex;
    }

    get context() {
        return this._context;
    }

    get failureAspects() {
        return this._failureAspects;
    }

    get combinedErrorMessage() {
        return this._combinedErrorMessage;
    }
}

export class ClassHistory {
    private readonly _identifier: string;
    private _methods: MethodHistoryStatistics[] = [];

    constructor(
        identifier: string
    ) {
        this._identifier = identifier;
    }

    addMethod(method: MethodHistoryStatistics) {
        this._methods.push(method);
    }

    get availableFinalStatuses() {
        let availableFinalStatuses: Set<ResultStatusType> = new Set();
        this._methods
            .filter(method => method.isTestMethod())
            .forEach(method => {
                method.availableStatuses.forEach(status => {
                    if (status === ResultStatusType.FAILED_RETRIED) {
                        return;
                    }
                    availableFinalStatuses.add(
                        status === ResultStatusType.REPAIRED || status === ResultStatusType.PASSED_RETRY
                            ? ResultStatusType.PASSED
                            : status
                    );
                });
            });
        return Array.from(availableFinalStatuses);
    }

    get identifier() {
        return this._identifier;
    }

    get methods() {
        return this._methods;
    }
}

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
            let currentResultStatus: ResultStatusType = runs[i].getParsedResultStatus();
            let previousResultStatus: ResultStatusType = runs[i - 1].getParsedResultStatus();

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
        let runsInRange: HistoricalMethodRun[] = [];
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

    private _getFailingStreak(runs: HistoricalMethodRun[]): number {
        if ((runs.length < 2) || (runs[runs.length - 1].context.resultStatus === ResultStatusType.FAILED_RETRIED) || (runs[runs.length - 1].getParsedResultStatus() === ResultStatusType.PASSED)) {
            return 0;
        }

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
        let avg = 0;
        let durations: number[] = [];
        runs.forEach(run => {
            durations.push(run.context.contextValues.endTime - run.context.contextValues.startTime);
        });
        if (durations.length > 0) {
            let sum = durations.reduce((accumulator, currentValue) => accumulator + currentValue, 0);
            avg = Math.round(sum / durations.length);
        }
        return avg;
    }

    isTestMethod(): boolean {
        return this._getContextOfLatestRun().methodType === MethodType.TEST_METHOD;
    }

    addRun(historicalMethod: HistoricalMethod, historyIndex: number) {
        this._runs.push(new HistoricalMethodRun(historicalMethod, historyIndex));
        this.addResultStatus(historicalMethod.context.resultStatus);
    }

    getRunCount() {
        return this._runs.length;
    }

    getFailingStreakInRange(startIndex: number, endIndex: number): number {
        if (this._runs.map(run => run.historyIndex).includes(endIndex)) {
            return this._getFailingStreak(this._getMethodRunsInRange(startIndex, endIndex));
        }
        return 0;
    }

    getFlakinessInRange(startIndex: number, endIndex: number): number {
        return this._getFlakiness(this._getMethodRunsInRange(startIndex, endIndex));
    }

    getAverageDurationInRange(startIndex: number, endIndex: number): number {
        return this._getAverageDuration(this._getMethodRunsInRange(startIndex, endIndex));
    }

    getErrorCount() {
        let errorCount = new Map<string, number>();
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
        if (foundRun) {
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

export class ClassStatistics extends Statistics {
    private _configStatistics = new Statistics();
    private _methodContexts: IMethodContext[] = [];
    readonly classIdentifier;

    constructor(
        readonly classContext: IClassContext
    ) {
        super();
        this.classIdentifier = classContext.testContextName || classContext.contextValues.name;
    }

    addMethodContext(methodContext: IMethodContext) {
        if (methodContext.methodType == MethodType.CONFIGURATION_METHOD) {
            this._configStatistics.addResultStatus(methodContext.resultStatus);
        } else {
            this.addResultStatus(methodContext.resultStatus);
        }
        this._methodContexts.push(methodContext);
    }

    get methodContexts() {
        return this._methodContexts;
    }

    get configStatistics() {
        return this._configStatistics;
    }
}


export class FailureAspectStatistics extends Statistics {
    private _irrelevantClassNameNeedles = ["TimeoutException"];
    private _methodContexts: IMethodContext[] = [];
    readonly identifier: string;
    readonly relevantCause: IStackTraceCause;
    readonly message: string;
    public index: number;

    constructor(
        readonly errorContext: IErrorContext
    ) {
        super();
        this.relevantCause = this._findRelevantCause(this.errorContext.stackTrace);
        if (this.errorContext.description) {
            this.identifier = this.errorContext.description;
            this.message = this.errorContext.description;
        } else if (this.relevantCause) {
            // Replace all occurring line-breaks with a space-character
            this.message = this.relevantCause.message.replaceAll('\n', ' ');
            this.message = this.message.trim();
            this.identifier = this.relevantCause.className + this.message;
        }
    }

    /**
     * This method finds a relevant cause from the stack trace.
     * If it could not find any, it takes the first cause from the stack.
     */
    private _findRelevantCause(causes: IStackTraceCause[]): IStackTraceCause {
        let relevantCause = causes.find(cause => {
            return !this._irrelevantClassNameNeedles.find(needle => cause.className.indexOf(needle) >= 0);
        })
        if (!relevantCause) {
            relevantCause = causes.find(value => true);
        }
        return relevantCause;
    }

    addMethodContext(methodContext: IMethodContext) {
        if (this._methodContexts.indexOf(methodContext) == -1) {
            this._methodContexts.push(methodContext);
            this.addResultStatus(methodContext.resultStatus);
        }
    }

    /**
     * A failure aspect is minor when it has no statuses in failed state
     */
    get isMinor() {
        return this.getStatusCount(data.ResultStatusType.FAILED) == 0;
    }

    get methodContexts() {
        return this._methodContexts;
    }
}
