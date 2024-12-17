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
    private _methodHistoryStatistics: MethodHistoryStatistics[] = [];

    constructor(
        readonly history: History
    ) {
        if (history.entries.length === 0) {
            return;
        }
        this.history.entries.forEach(entry => {
            this._historyAggregateStatistics.push(new HistoryAggregateStatistics(entry));
        });

        this.getLastEntry().methods.forEach(currentMethod => {
            this._methodHistoryStatistics.push(new MethodHistoryStatistics(currentMethod));
        });

        this._methodHistoryStatistics.forEach(methodHistory => {
            this._historyAggregateStatistics.forEach(entry => {
                const foundMethod = Array.from(entry.methods.values()).find(method => {
                    return method.identifier === methodHistory.identifier &&
                        method.classIdentifier === methodHistory.classIdentifier &&
                        this._compareRelatedMethods(method.relatedMethods, methodHistory.relatedMethods)
                });

                if (foundMethod) {
                    methodHistory.addRun(foundMethod, entry.historyIndex);
                }
            });
        });
    }

    getLastEntry(): HistoryAggregateStatistics {
        return this._historyAggregateStatistics[this._historyAggregateStatistics.length - 1];
    }

    getHistoryAggregateStatistics(): HistoryAggregateStatistics[] {
        return this._historyAggregateStatistics;
    }

    getMethodHistoryStatistics(): MethodHistoryStatistics[] {
        return this._methodHistoryStatistics;
    }

    getTotalRunCount(): number {
        return this.history.entries.length;
    }

    private _compareRelatedMethods(arr1: string[], arr2: string[]): boolean {
        if (arr1.length !== arr2.length) {
            return false;
        }
        const sortedArr1 = [...arr1].sort();
        const sortedArr2 = [...arr2].sort();

        return sortedArr1.every((value, index) => value === sortedArr2[index]);
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
    private readonly _classIdentifier: string;
    private _relatedMethodIdentifiers: string[] = [];

    constructor(methodContext: IMethodContext, classIdentifier: string) {
        this._methodContext = methodContext;
        this._methodIdentifier = this._getParsedMethodIdentifier();
        this._classIdentifier = classIdentifier;
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

    getErrorMessage() {
        let errorMessage = "";
        if (this._methodContext.resultStatus != ResultStatusType.PASSED) {
            this._methodContext.testSteps.flatMap(value => value.actions)
                .forEach(actionDetails => {
                    actionDetails.entries.forEach(entry => {
                        const errorContext = entry.errorContext;
                        const errorClassName = errorContext.stackTrace[0].className.substring(errorContext.stackTrace[0].className.lastIndexOf(".") + 1);
                        errorMessage += " " + (errorClassName + ": " + errorContext.stackTrace[0].message);
                    });
                });
        }
        return errorMessage.trim();
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

    get classIdentifier() {
        return this._classIdentifier;
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

        Object.values(historyEntry.classContexts).forEach(cls => {
            const classIdentifier = cls.testContextName || cls.contextValues.name
            this._classIdMap.set(cls.contextValues.id, classIdentifier);
        });

        const uniqueClasses = Array.from(new Set(this._classIdMap.values()));
        uniqueClasses.forEach(cls => {
            let classStats = new ClassHistoryStatistics(cls);
            this._classMap.set(cls, classStats);
        });

        Object.values(historyEntry.methodContexts).forEach(method => {
            this._methodMap.set(method.contextValues.id, new HistoricalMethod(method, this._classIdMap.get(method.classContextId)));
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

    get methods() {
        return this._methodMap;
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
    private readonly _errorMessage: string;

    constructor(historicalMethod: HistoricalMethod, index: number) {
        this._historyIndex = index;
        this._context = historicalMethod.context;
        this._errorMessage = historicalMethod.getErrorMessage();
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

    get errorMessage() {
        return this._errorMessage;
    }
}

export class MethodHistoryStatistics extends Statistics {
    private readonly _identifier: string;
    private readonly _relatedMethods: string[] = [];
    private readonly _classIdentifier: string;
    private readonly _idOfLatestRun: string;
    private _runs: HistoricalMethodRun[] = [];
    private _flakinessFullWeightRunCount = 10;
    private _flakinessDecayFactor = 0.9;

    constructor(method: HistoricalMethod) {
        super();
        this._identifier = method.identifier;
        this._relatedMethods = method.relatedMethods;
        this._classIdentifier = method.classIdentifier;
        this._idOfLatestRun = method.context.contextValues.id;
    }

    addRun(historicalMethod: HistoricalMethod, historyIndex: number) {
        this._runs.push(new HistoricalMethodRun(historicalMethod, historyIndex));
        this.addResultStatus(historicalMethod.context.resultStatus);
    }

    getRunCount() {
        return this._runs.length;
    }

    get flakiness(): number {
        const runCount = this._runs.length;
        if (runCount < 2) {
            return 0;
        }

        let weightedSwitchSum = 0;
        let totalWeight = 0;

        for (let i = 1; i < runCount; i++) {
            let currentResultStatus: ResultStatusType = this._runs[i].getParsedResultStatus();
            let previousResultStatus: ResultStatusType = this._runs[i - 1].getParsedResultStatus();

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

    isConfigurationMethod(): boolean {
        return this._runs[this.getRunCount() - 1].context.methodType === MethodType.CONFIGURATION_METHOD;
    }

    getAverageDuration(): number {
        let avg = 0;
        let durations: number[] = [];
        this._runs.forEach(run => {
            durations.push(run.context.contextValues.endTime - run.context.contextValues.startTime);
        });
        if (durations.length > 0) {
            let sum = durations.reduce((accumulator, currentValue) => accumulator + currentValue, 0);
            avg = Math.round(sum / durations.length);
        }
        return avg;
    }

    getErrorCount() {
        let errorCount = new Map<string, number>();
        this._runs.forEach(run => {
            const error = run.errorMessage;
            if (error) {
                const currentErrorCount = errorCount.get(error) || 0;
                errorCount.set(error, currentErrorCount + 1);
            }
        });
        return errorCount;
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

    get classIdentifier() {
        return this._classIdentifier;
    }

    get idOfLatestRun() {
        return this._idOfLatestRun;
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
