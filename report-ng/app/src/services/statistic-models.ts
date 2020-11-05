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

abstract class AbstractStatistics {
    private _statusConverter: StatusConverter
    constructor() {
        this._statusConverter = Container.instance.get(StatusConverter);
    }

    private _resultStatuses: { [key: number]: number } = {};
    private _testsTotal: number = 0;

    addResultStatus(status: ResultStatusType) {
        status = this.groupStatisticStatus(status);
        if (!this._resultStatuses[status]) {
            this._resultStatuses[status] = 0;
        }
        this._resultStatuses[status]++;
        this._testsTotal++;
    }

    private groupStatisticStatus(status: ResultStatusType): ResultStatusType {
        return this._statusConverter.groupStatus(status);
    }

    get testsTotal() {
        return this._testsTotal;
    }

    get overallPassed() {
        return this.getStatusCount(ResultStatusType.PASSED);
    }

    get overallSkipped() {
        return this.getStatusCount(ResultStatusType.SKIPPED);
    }

    get overallFailed() {
        return this.getStatusCount(ResultStatusType.FAILED);
    }

    getStatusCount(status: ResultStatusType) {
        return this._resultStatuses[status] | 0;
    }

    protected addStatistics(statistics: AbstractStatistics) {
        for (const status in statistics._resultStatuses) {
            if (!this._resultStatuses[status]) {
                this._resultStatuses[status] = 0;
            }
            this._resultStatuses[status] += statistics._resultStatuses[status];
        }
        this._testsTotal += statistics._testsTotal;
    }
}

export class ExecutionStatistics extends AbstractStatistics {
    private _executionAggregate: ExecutionAggregate;
    private _classStatistics: ClassStatistics[] = [];

    setExecutionAggregate(executionAggregate: ExecutionAggregate) {
        this._executionAggregate = executionAggregate;
        return this;
    }

    addClassStatistics(statistics: ClassStatistics) {
        this._classStatistics.push(statistics);
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
}

export class ClassStatistics extends AbstractStatistics {
    private _classAggregate: ClassContextAggregate;

    addClassAggregate(classAggregate: ClassContextAggregate) {
        if (!this._classAggregate) {
            this._classAggregate = classAggregate;
        } else {
            this._classAggregate.methodContexts = this._classAggregate.methodContexts.concat(classAggregate.methodContexts);
        }
        classAggregate.methodContexts.forEach(methodContext => {
            this.addResultStatus(methodContext.contextValues.resultStatus);
        });
        return this;
    }

    get classAggregate() {
        return this._classAggregate;
    }
}
