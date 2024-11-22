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
import ResultStatusType = data.ResultStatusType;
import History = data.History;
import HistoryAggregate = data.HistoryAggregate;
import ExecutionAggregate = data.ExecutionAggregate;
import MethodType = data.MethodType;
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

export class HistoryAggregateStatistics extends Statistics {
    private _classStatistics: ClassStatistics[] = [];
    private _methods: IMethodContext[] = [];

    constructor(
        readonly historyAggregate: HistoryAggregate
    ) {
        super();

        const classStatistics = {}

        for (const id in this.historyAggregate.methodContexts) {
            const methodContext = this.historyAggregate.methodContexts[id];
            const classContext = this.historyAggregate.classContexts[methodContext.classContextId];

            this._methods.push(methodContext);

            let currentClassStatistics: ClassStatistics = new ClassStatistics(classContext);
            if (!classStatistics[currentClassStatistics.classIdentifier]) {
                classStatistics[currentClassStatistics.classIdentifier] = currentClassStatistics;
            } else {
                currentClassStatistics = classStatistics[currentClassStatistics.classIdentifier];
            }

            currentClassStatistics.addMethodContext(methodContext);
        }
        this.setClassStatistics(Object.values(classStatistics));
    }

    setClassStatistics(classStatistics: ClassStatistics[]) {
        this._classStatistics = classStatistics;
        this._classStatistics.forEach(classStatistics => this.addStatistics(classStatistics));
    }

    getAllMethods() {
        return this._methods;
    }

    getClassStatistics() {
        return this._classStatistics;
    }
}

export class MethodRun {

    public historyIndex: number = 0;
    public context: IMethodContext;

    constructor(context: IMethodContext, index: number) {
        this.historyIndex = index;
        this.context = context;
    }
}

export class MethodHistoryStatistics extends Statistics {
    private _name: string = null;
    private _testname: string = null;
    private _parameters: { [key: string]: string; } = {};
    private _runs: MethodRun[] = [];
    private _durations: number[] = [];
    private _errorCount = new Map<string, number>();

    constructor(
        currentMethod: IMethodContext, history: HistoryStatistics
    ) {
        super();
        history.getHistoryAggregateStatistics().forEach(historicalRun => {
            const methodInRun = historicalRun.getAllMethods().find(method =>
                method.contextValues.name === currentMethod.contextValues.name &&
                method.testName === currentMethod.testName &&
                this._compareParameters(method.parameters, currentMethod.parameters)
            );
            if (methodInRun !== undefined) {
                this._runs.push(new MethodRun(methodInRun, historicalRun.historyAggregate.historyIndex));
                this.addResultStatus(methodInRun.resultStatus);
                this._durations.push(methodInRun.contextValues.endTime - methodInRun.contextValues.startTime);

                methodInRun.testSteps.forEach(step => {
                    step.actions.forEach(action => {
                        action.entries.forEach(entry => {
                            entry.errorContext.stackTrace.forEach(stackTrace => {
                                const error = stackTrace.className + ": " + stackTrace.message;
                                const currentErrorCount = this._errorCount.get(error) || 0;
                                this._errorCount.set(error, currentErrorCount + 1);
                            });
                        });
                    });
                });
            }
        });
        this._name = currentMethod.contextValues.name;
        this._testname = currentMethod.testName;
        this._parameters = currentMethod.parameters;
    }

    getAverageDuration(): number {
        let avg = 0;
        if (this._durations.length > 0) {
            let sum = this._durations.reduce((accumulator, currentValue) => accumulator + currentValue, 0);
            avg = Math.round(sum / this._durations.length);
        }
        return avg;
    }

    getSuccessRate(): number {
        let runCount = this.getMethodRunCount();
        let passedRuns = 0;

        const validStatuses = [
            ResultStatusType.PASSED,
            ResultStatusType.PASSED_RETRY,
            ResultStatusType.REPAIRED
        ];

        this._runs.forEach(methodRun => {
            if (validStatuses.includes(methodRun.context.resultStatus)) {
                passedRuns++;
            }
        });

        if (runCount != 0) {
            return (passedRuns / runCount) * 100
        }
        return 0;
    }

    getMethodRunCount(): number {
        return this._runs.length;
    }

    isMatchingMethod(methodContext: IMethodContext): boolean {
        return (this._name === methodContext.contextValues.name &&
            this._testname === methodContext.testName &&
            this._compareParameters(this._parameters, methodContext.parameters))
    }

    getRuns(): MethodRun[] {
        return this._runs;
    }

    private _compareParameters(parameters1: { [p: string]: string }, parameters2: { [p: string]: string }): boolean {
        const keys1 = Object.keys(parameters1);
        const keys2 = Object.keys(parameters2);
        if (keys1.length !== keys2.length) {
            return false;
        }

        for (const key of keys1) {
            if (parameters1[key] !== parameters2[key]) {
                return false;
            }
        }

        return true;
    }

    getErrorCount(): Map<string, number> {
        return this._errorCount;
    }
}

export class HistoryStatistics {
    protected historyAggregateStatistics: HistoryAggregateStatistics[] = [];
    protected methodHistoryStatistics: MethodHistoryStatistics[] = [];
    private _totalRuns: number = 0;

    constructor(
        readonly history: History
    ) {
        this.history.entries.forEach(entry => {
            this.historyAggregateStatistics.push(new HistoryAggregateStatistics(entry));
        })

        const lastEntry = this.historyAggregateStatistics[this.historyAggregateStatistics.length - 1];

        lastEntry.getAllMethods().forEach(currentMethod => {
            this.methodHistoryStatistics.push(new MethodHistoryStatistics(currentMethod, this));
        });

        this._totalRuns = this.history.entries.length;
    }

    getHistoryAggregateStatistics(): HistoryAggregateStatistics[] {
        return this.historyAggregateStatistics;
    }

    getMethodHistoryStatistics(): MethodHistoryStatistics[] {
        return this.methodHistoryStatistics;
    }

    getTotalRuns(): number {
        return this._totalRuns;
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
