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
import ResultStatusType = data.ResultStatusType;
import ExecutionAggregate = data.ExecutionAggregate;
import MethodType = data.MethodType;
import IMethodContext = data.IMethodContext;
import IClassContext = data.IClassContext;
import IErrorContext = data.IErrorContext;
import IStackTraceCause = data.IStackTraceCause;
import {MethodDetails} from "./statistics-generator";

class Statistics {
    private _statusConverter: StatusConverter
    constructor() {
        this._statusConverter = Container.instance.get(StatusConverter);
    }

    private _resultStatuses: { [key: number]: number } = {};

    addResultStatus(status: ResultStatusType) {
        if (!this._resultStatuses[status]) {
            this._resultStatuses[status] = 0;
        }
        this._resultStatuses[status]++;
    }

    get availableStatuses():ResultStatusType[] {
        const statuses = [];
        for (const status in this._resultStatuses) {
            statuses.push(status);
        }
        return statuses;
    }

    /**
     * Returns the number of test cases including passed retried
     */
    get overallTestCases() {
        return this.getStatusesCount([
            ResultStatusType.PASSED,
            ResultStatusType.SKIPPED,
            ResultStatusType.FAILED,
            ResultStatusType.FAILED_EXPECTED,
            ResultStatusType.PASSED_RETRY
        ]);
    }

    get overallPassed() {
        return this.getStatusesCount(this._statusConverter.passedStatuses);
    }

    get overallSkipped() {
        return this.getStatusCount(ResultStatusType.SKIPPED);
    }

    get overallFailed() {
       return this.getStatusesCount(this._statusConverter.failedStatuses);
    }

    getStatusCount(status: ResultStatusType) {
        return this._resultStatuses[status] | 0;
    }

    getStatusesCount(statuses:number[]) {
        let count = 0;
        statuses.forEach(value => {
            count += this.getStatusCount(value);
        })
        return count;
    }

    getUpmostStatus():number {
        let count = 0;
        let upmostStatus:any;
        for (let status in this._resultStatuses) {
            if (this._resultStatuses[status] > count) {
                upmostStatus = status;
            }
        }
        return upmostStatus;
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
}

export class ExecutionStatistics extends Statistics {
    private _classStatistics: ClassStatistics[] = [];
    private _uniqueFailureAspects:FailureAspectStatistics[] = [];
    private _repairedTests = 0;

    constructor(
        readonly executionAggregate: ExecutionAggregate
    ) {
        super();
    }

    setClassStatistics(classStatistics:ClassStatistics[]) {
        this._classStatistics = classStatistics;
        this._classStatistics.forEach(classStatistics => this.addStatistics(classStatistics));
    }

    private _addUniqueFailureAspect(failureAspect: FailureAspectStatistics, methodContext: data.IMethodContext) {
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

    get repairedTests() {
        return this._repairedTests;
    }

    protected addStatistics(classStatistics: ClassStatistics) {
        super.addStatistics(classStatistics);
        classStatistics.methodContexts
            .forEach(methodContext => {

                if (methodContext.resultStatus == data.ResultStatusType.PASSED && methodContext.annotations[MethodDetails.FAILS_ANNOTATION_NAME]) {
                    this._repairedTests++;
                }

                const methodDetails = new MethodDetails(methodContext, classStatistics);
                methodDetails.failureAspects.forEach(failureAspect => {
                    this._addUniqueFailureAspect(failureAspect, methodContext);
                })
            })

        // Sort failure aspects by fail count
        this._uniqueFailureAspects = this._uniqueFailureAspects.sort((a, b) => b.overallFailed-a.overallFailed);

        for (let i = 0; i < this._uniqueFailureAspects.length; ++i) {
            this._uniqueFailureAspects[i].index = i;
        }
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

export class ClassStatistics extends Statistics {
    private _configStatistics = new Statistics();
    private _methodContexts:IMethodContext[] = [];
    readonly classIdentifier;

    constructor(
        readonly classContext:IClassContext
    ) {
        super();
        this.classIdentifier = classContext.testContextName || classContext.contextValues.name;
    }

    addMethodContext(methodContext : IMethodContext) {
        if (methodContext.methodType == MethodType.CONFIGURATION_METHOD) {
            this._configStatistics.addResultStatus(methodContext.resultStatus);
        } else {
            this.addResultStatus(methodContext.resultStatus);
        }
        this._methodContexts.push(methodContext);
        return this;
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
    private _methodContexts:IMethodContext[] = [];
    readonly identifier:string;
    readonly relevantCause:IStackTraceCause;
    readonly message:string;
    public index:number;

    constructor(
        readonly errorContext:IErrorContext
    ) {
        super();
        this.relevantCause = this._findRelevantCause(this.errorContext.stackTrace);
        if (this.errorContext.description) {
            this.identifier = this.errorContext.description;
            this.message = this.errorContext.description;
        } else if (this.relevantCause) {
            this.message = this.relevantCause.message?.trim();
            this.identifier = this.relevantCause.className + this.message;
        }
    }

    /**
     * This method finds a relevant cause from the stack trace.
     * If it could not find any, it takes the first cause from the stack.
     */
    private _findRelevantCause(causes:IStackTraceCause[]):IStackTraceCause {
        let relevantCause = causes.find(cause => {
            return !this._irrelevantClassNameNeedles.find(needle => cause.className.indexOf(needle) >= 0);
        })
        if (!relevantCause) {
            relevantCause = causes.find(value => true);
        }
        return relevantCause;
    }

    addMethodContext(methodContext:IMethodContext) {
        if (this._methodContexts.indexOf(methodContext) == -1) {
            this._methodContexts.push(methodContext);
            this.addResultStatus(methodContext.resultStatus);
        }
    }

    /**
     * A failure aspect is minor when it has no statuses in failed state
     */
    get isMinor() {
        return this.getStatusesCount(this.statusConverter.failedStatuses) == 0;
    }

    get methodContexts() {
        return this._methodContexts;
    }
}
