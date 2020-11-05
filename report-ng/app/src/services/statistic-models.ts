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
import ResultStatusType = data.ResultStatusType;
import ExecutionAggregate = data.ExecutionAggregate;
import ClassContextAggregate = data.ClassContextAggregate;

abstract class AbstractStatistics {

  constructor() {
  }

  private _resultStatuses:{[key: number]:number} = {};
  private _testsTotal:number = 0;
  private _overallPassed:number = 0;
  private _overallFailed:number = 0;

  addResultStatus(status:ResultStatusType) {
    if (!this._resultStatuses[status]) {
      this._resultStatuses[status] = 0;
    }
    this._resultStatuses[status]++;

    const groupedStatus = this.groupStatisticStatus(status);
    if (groupedStatus == ResultStatusType.PASSED) {
      this._overallPassed++;
    } else if (groupedStatus == ResultStatusType.FAILED) {
      this._overallFailed++;
    }

    this._testsTotal++;
  }

  private groupStatisticStatus(status: ResultStatusType): ResultStatusType {
    switch (status) {
      case ResultStatusType.FAILED_EXPECTED:
      case ResultStatusType.FAILED_RETRIED:
      case ResultStatusType.FAILED_MINOR:
      case ResultStatusType.FAILED:
        return ResultStatusType.FAILED;
      case ResultStatusType.MINOR_RETRY:
      case ResultStatusType.PASSED_RETRY:
      case ResultStatusType.MINOR:
      case ResultStatusType.PASSED:
        return ResultStatusType.PASSED;
      default:
        return status;
    }
  }

  get testsTotal() {
    return this._testsTotal;
  }
  get overallPassed() {
    return this._overallPassed;
  }
  get overallSkipped() {
    return this._resultStatuses[ResultStatusType.SKIPPED]|0;
  }
  get overallFailed() {
    return this._overallFailed;
  }

  protected addStatistics(statistics:AbstractStatistics) {
    for (const status in statistics._resultStatuses) {
      if (!this._resultStatuses[status]) {
        this._resultStatuses[status] = 0;
      }
      this._resultStatuses[status] += statistics._resultStatuses[status];
    }
    this._overallPassed += statistics._overallPassed;
    this._overallFailed += statistics._overallFailed;
    this._testsTotal += statistics._testsTotal;
  }
}

export class ExecutionStatistics extends AbstractStatistics{
  private _executionAggregate:ExecutionAggregate;
  private _classStatistics:ClassStatistics[] = [];

  setExecutionAggregate(executionAggregate:ExecutionAggregate) {
    this._executionAggregate = executionAggregate;
    return this;
  }

  addClassStatistics(statistics:ClassStatistics) {
    this.addStatistics(statistics);
    this._classStatistics.push(statistics);
  }

  get executionAggregate() {
    return this._executionAggregate;
  }

  get classStatistics() {
    return this._classStatistics;
  }
}

export class ClassStatistics extends AbstractStatistics {
  private _classAggregate:ClassContextAggregate;

  setClassAggregate(classAggregate:ClassContextAggregate) {
    this._classAggregate = classAggregate;
    return this;
  }

  get classAggregate() {
    return this._classAggregate;
  }
}
