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
import ClassContextAggregate = data.ClassContextAggregate;
import MethodType = data.MethodType;
import IMethodContext = data.IMethodContext;

class Statistics {
    private _statusConverter: StatusConverter
    constructor() {
        this._statusConverter = Container.instance.get(StatusConverter);
    }

    private _resultStatuses: { [key: number]: number } = {};
    private _testsTotal: number = 0;

    addResultStatus(status: ResultStatusType) {
        if (!this._resultStatuses[status]) {
            this._resultStatuses[status] = 0;
        }
        this._resultStatuses[status]++;
        this._testsTotal++;
    }

    get availableStatuses() {
        const statuses = [];
        for (const status in this._resultStatuses) {
            statuses.push(status);
        }
        return statuses;
    }

    get testsTotal() {
        return this._testsTotal;
    }

    get overallPassed() {
        let count = 0;
        this._statusConverter.passedStatuses.forEach(value => {
            count += this.getStatusCount(value);
        })
        return count;
    }

    get overallSkipped() {
        return this.getStatusCount(ResultStatusType.SKIPPED);
    }

    get overallFailed() {
        let count = 0;
        this._statusConverter.failedStatuses.forEach(value => {
            count += this.getStatusCount(value);
        })
        return count;
    }

    getStatusCount(status: ResultStatusType) {
        return this._resultStatuses[status] | 0;
    }

    protected addStatistics(statistics: Statistics) {
        for (const status in statistics._resultStatuses) {
            if (!this._resultStatuses[status]) {
                this._resultStatuses[status] = 0;
            }
            this._resultStatuses[status] += statistics._resultStatuses[status];
        }
        this._testsTotal += statistics._testsTotal;
    }

    get statusConverter() {
        return this._statusConverter;
    }
}

export class ExecutionStatistics extends Statistics {
    private _executionAggregate: ExecutionAggregate;
    private _classStatistics: ClassStatistics[] = [];
    private _failureAspectStatistics:FailureAspectStatistics[] = [];
    private _exitPointStatistics:ExitPointStatistics[] = [];

    setExecutionAggregate(executionAggregate: ExecutionAggregate) {
        this._executionAggregate = executionAggregate;
        return this;
    }

    addClassStatistics(statistics: ClassStatistics) {
        this._classStatistics.push(statistics);
        statistics.classAggregate.methodContexts
            .filter(methodContext => {
                return this.statusConverter.failedStatuses.indexOf(methodContext.contextValues.resultStatus) >= 0;
            })
            .forEach(methodContext => {

                methodContext.testSteps.forEach(testStep => {
                    testStep.testStepActions.forEach(testStepAction => {
                        if (testStepAction.screenshotIds?.length > 0) {
                            console.log("has screenshots", methodContext.contextValues.name)
                        }
                    })
                })


                const failureAspectStatistics = new FailureAspectStatistics().addMethodContext(methodContext);

                const foundFailureAspectStatistics = this._failureAspectStatistics.find(existingFailureAspectStatistics => {
                    return existingFailureAspectStatistics.name == failureAspectStatistics.name;
                });
                if (foundFailureAspectStatistics) {
                    foundFailureAspectStatistics.addMethodContext(failureAspectStatistics.methodContext);
                } else {
                    this._failureAspectStatistics.push(failureAspectStatistics);
                }

                const exitPointStatistics = new ExitPointStatistics().addMethodContext(methodContext);

                const foundExitPointStatistics = this._exitPointStatistics.find(existingExitPointStatistics => {
                    return existingExitPointStatistics.fingerprint == exitPointStatistics.fingerprint;
                });
                if (foundExitPointStatistics) {
                    foundExitPointStatistics.addMethodContext(exitPointStatistics.methodContext);
                } else {
                    this._exitPointStatistics.push(exitPointStatistics);
                }
            })
    }

    updateStatistics() {
        this._classStatistics.forEach(value => this.addStatistics(value));
    }

    get executionAggregate() {
        return this._executionAggregate;
    }

    get classStatistics() {
        return this._classStatistics;
    }

    get exitPointStatistics() {
        return this._exitPointStatistics;
    }

    get failureAspectStatistics() {
        return this._failureAspectStatistics;
    }
}

export class ClassStatistics extends Statistics {
    private _classAggregate: ClassContextAggregate;
    private _configStatistics = new Statistics();

    addClassAggregate(classAggregate: ClassContextAggregate) {
        if (!this._classAggregate) {
            this._classAggregate = classAggregate;
        } else {
            this._classAggregate.methodContexts = this._classAggregate.methodContexts.concat(classAggregate.methodContexts);
        }
        classAggregate.methodContexts
            .forEach(methodContext => {
                if (methodContext.methodType == MethodType.CONFIGURATION_METHOD) {
                    this._configStatistics.addResultStatus(methodContext.contextValues.resultStatus);
                } else {
                    this.addResultStatus(methodContext.contextValues.resultStatus);
                }
            });
        return this;
    }

    get classAggregate() {
        return this._classAggregate;
    }

    get configStatistics() {
        return this._configStatistics;
    }
}


export class FailureAspectStatistics extends Statistics {
    private _methodContext:IMethodContext;
    private _name:string;

    constructor() {
        super();
    }

    addMethodContext(methodContext:IMethodContext) {
        if (!this._methodContext) {
            this._methodContext = methodContext;
            const packageRegexp = new RegExp(".*\\.");
            this._name = (
                methodContext.errorContext?.description
                || methodContext.errorContext?.cause.className.replace(packageRegexp,"") + (methodContext.errorContext?.cause?.message ? ": " + methodContext.errorContext?.cause?.message.trim() : "")
            );
        }
        this.addResultStatus(methodContext.contextValues.resultStatus);
        return this;
    }

    get methodContext() {
        return this._methodContext;
    }

    get name() {
        return this._name;
    }
}

export class ExitPointStatistics extends Statistics {
    private _methodContext:IMethodContext;
    private _fingerprint:string;

    constructor() {
        super();
    }

    addMethodContext(methodContext:IMethodContext) {
        if (!this._methodContext) {
            this._methodContext = methodContext;
            this._fingerprint = (
                methodContext.errorContext?.scriptSource?.lines.map(line => line.line).join("\n")
                || methodContext.errorContext.cause.stackTraceElements.join("\n")
                || "undefined"
            );
        }
        this.addResultStatus(methodContext.contextValues.resultStatus);
        return this;
    }

    get methodContext() {
        return this._methodContext;
    }

    get fingerprint() {
        return this._fingerprint;
    }
}
