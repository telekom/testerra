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
import {MethodType} from "./report-model/framework_pb";
import ResultStatusType = data.ResultStatusType;
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

    getErrorMessage() {
        let errorMessage = "";
        if (this.context.resultStatus != ResultStatusType.PASSED) {
            this.context.testSteps.flatMap(value => value.actions)
                .forEach(actionDetails => {
                    actionDetails.entries.forEach(entry => {
                        const errorContext = entry.errorContext;
                        errorContext.stackTrace.forEach(stackTrace => {
                            // TODO: How to handle multiple errorMessages
                            const errorClassName = stackTrace.className.substring(stackTrace.className.lastIndexOf(".") + 1);
                            errorMessage = errorClassName + ": " + errorMessage.concat(stackTrace.message + " ");
                        })
                    })
                });
        }
        return errorMessage.trim();
    }
}

export class MethodHistoryStatistics extends Statistics {
    private _name: string = null;
    private _testname: string = null;
    private _parameters: { [key: string]: string; } = {};
    private _runs: MethodRun[] = [];
    private _durations: number[] = [];
    private _errorCount = new Map<string, number>();
    private _flakyness: number = 0;
    private _relatedMethods: string[] = [];
    private _identifier: string = null;
    private _classIdentifier: string = null;

    constructor(
        currentMethod: IMethodContext, history: HistoryStatistics
    ) {
        super();

        this._name = currentMethod.contextValues.name;
        this._testname = currentMethod.testName;
        this._parameters = currentMethod.parameters;
        this._identifier = this.getIdentifier();
        const currentIdArray = currentMethod.relatedMethodContextIds;
        if (currentIdArray) {
            this._relatedMethods = currentIdArray
                .map(id => {
                    history.getHistoryAggregateStatistics()[history.getHistoryAggregateStatistics().length - 1].getAllMethods().find(obj => obj.contextValues.id === id)
                    const obj = history.getHistoryAggregateStatistics()[history.getHistoryAggregateStatistics().length - 1].getAllMethods().find(o => o.contextValues.id === id);
                    if (obj) {
                        return this._getMethodIdentifier(obj);
                    }
                    return undefined;
                })
                .filter(entry => entry !== undefined);
        }

        this._classIdentifier = history.getHistoryAggregateStatistics()[history.getHistoryAggregateStatistics().length - 1].getClassStatistics().find(classStats => {
            return classStats.classContext.contextValues.id === currentMethod.classContextId;
        }).classIdentifier;

        history.getHistoryAggregateStatistics().forEach(historicalRun => {
            const methodInRun = historicalRun.getAllMethods().find(method => {
                    const idArray = method.relatedMethodContextIds;
                    let relatedMethods: string[] = [];

                    if (idArray) {
                        relatedMethods = idArray
                            .map(id => {
                                historicalRun.getAllMethods().find(obj => obj.contextValues.id === id)
                                const obj = historicalRun.getAllMethods().find(o => o.contextValues.id === id);
                                if (obj) {
                                    return this._getMethodIdentifier(obj);
                                }
                                return undefined;
                            })
                            .filter(entry => entry !== undefined);
                    }
                    const methodIdentifier = this._getParsedMethodIdentifier(method.testName, method.contextValues.name, method.parameters);

                    return (
                        methodIdentifier === this._identifier &&
                        this._compareRelatedMethods(relatedMethods, this._relatedMethods));
                }
            );
            if (methodInRun !== undefined) {
                const currentRun = new MethodRun(methodInRun, historicalRun.historyAggregate.historyIndex);
                this._runs.push(currentRun);
                this.addResultStatus(methodInRun.resultStatus);
                this._durations.push(methodInRun.contextValues.endTime - methodInRun.contextValues.startTime);

                const error = currentRun.getErrorMessage();
                if (error) {
                    const currentErrorCount = this._errorCount.get(error) || 0;
                    this._errorCount.set(error, currentErrorCount + 1);
                }
            }
        });
        this._flakyness = this._calculateFlakyness();
    }

    get classIdentifier() {
        return this._classIdentifier;
    }

    get relatedMethods() {
        return this._relatedMethods;
    }

    private _calculateFlakyness(): number {
        if (this._runs.length < 2) {
            return 0;
        }

        let switchCount = 0;
        for (let i = 1; i < this._runs.length; i++) {
            if (this._runs[i].context.resultStatus !== this._runs[i - 1].context.resultStatus) {
                switchCount++;
            }
        }
        const maxSwitches = this._runs.length - 1;
        return (switchCount / maxSwitches);
    }

    getFlakyness(): number {
        return this._flakyness;
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

    isMatchingMethod(methodContext: IMethodContext, latestRun: HistoryAggregateStatistics): boolean {
        const methodIdentifier = this._getParsedMethodIdentifier(methodContext.testName, methodContext.contextValues.name, methodContext.parameters);
        const idArray = methodContext.relatedMethodContextIds;
        let relatedMethods: string[] = [];

        if (idArray) {
            relatedMethods = idArray
                .map(id => {
                    latestRun.getAllMethods().find(obj => obj.contextValues.id === id)
                    const obj = latestRun.getAllMethods().find(o => o.contextValues.id === id);
                    if (obj) {
                        return this._getParsedMethodIdentifier(obj.testName, obj.contextValues.name, obj.parameters);
                    }
                    return undefined;
                })
                .filter(entry => entry !== undefined);
        }

        return (
            methodIdentifier === this._identifier &&
            this._compareRelatedMethods(relatedMethods, this._relatedMethods));
    }

    getRuns(): MethodRun[] {
        return this._runs;
    }

    private _compareRelatedMethods(arr1: string[], arr2: string[]): boolean {
        if (arr1.length !== arr2.length) {
            return false;
        }
        const sortedArr1 = [...arr1].sort();
        const sortedArr2 = [...arr2].sort();

        return sortedArr1.every((value, index) => value === sortedArr2[index]);
    }

    getErrorCount(): Map<string, number> {
        return this._errorCount;
    }

    private _getParsedMethodIdentifier(testname: string, name: string, parameters: { [key: string]: string; }) {
        let methodName: string;
        if (testname) {
            methodName = testname;
        } else {
            methodName = name;
            const params = [];
            for (const name in parameters) {
                params.push(name + ": " + parameters[name]);
            }
            if (params.length > 0) {
                methodName += "(" + params.join(", ") + ")";
            }
        }
        return methodName;
    }

    private _getMethodIdentifier(context: IMethodContext) {
        return this._getParsedMethodIdentifier(context.testName, context.contextValues.name, context.parameters);
    }

    getIdentifier() {
        return this._getParsedMethodIdentifier(this._testname, this._name, this._parameters);
    }

    getIdOfLatestRun(): string {
        return this._runs[this._runs.length - 1].context.contextValues.id;
    }

    isConfigurationMethod(): boolean {
        return this.getRuns()[this.getRuns().length - 1].context.methodType === MethodType.CONFIGURATION_METHOD;

    }
}

export class HistoryStatistics {
    protected historyAggregateStatistics: HistoryAggregateStatistics[] = [];
    protected methodHistoryStatistics: MethodHistoryStatistics[] = [];
    private _totalRuns: number = 0;

    constructor(
        readonly history: History
    ) {
        if (history.entries.length === 0) {
            return;
        }
        this.history.entries.forEach(entry => {
            this.historyAggregateStatistics.push(new HistoryAggregateStatistics(entry));
        });

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
