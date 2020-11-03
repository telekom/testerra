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
import {autoinject} from "aurelia-framework";
import ExecStatusType = data.ExecStatusType;
import ResultStatusType = data.ResultStatusType;

class GraphColors {
  static readonly PASSED = '#3c8f64'; // $success
  static readonly SKIPPED = '#8a929a'; // $dark
  static readonly FAILED = '#e63946'; // $danger
  static readonly CRASHED = '#5d6f81'; // $secondary
  static readonly RUNNING = '#0089b6'; // $info

  /*old color schme*/
  /*static readonly PASSED = '#00c853'; // $success
  static readonly SKIPPED = '#8a929a'; // $dark
  static readonly FAILED = '#D50000'; // $danger
  static readonly CRASHED = '#5d6f81'; // $secondary
  static readonly RUNNING = '#0089b6'; // $info */
}

@autoinject()
export class StatusConverter {

  private static resultStatusValues: Array<ResultStatusType | string> = Object.values(ResultStatusType);
  private static resultStatusKeys = Object.keys(ResultStatusType);
  private static execStatusValues = Object.values(ExecStatusType);
  private static execStatusKeys = Object.keys(ExecStatusType);

  resultStatusToString(value: ResultStatusType | number): string {
    const index = StatusConverter.resultStatusValues.indexOf(value);
    return StatusConverter.resultStatusKeys[index];
  }

  resultStatusFromString(value: string): ResultStatusType {
    const index = StatusConverter.resultStatusKeys.indexOf(value.toUpperCase());
    return <ResultStatusType>StatusConverter.resultStatusValues[index];
  }

  execStatusToString(value: ExecStatusType): string {
    const index = StatusConverter.execStatusValues.indexOf(value);
    return StatusConverter.execStatusKeys[index];
  }

  /**
   * @deprecated
   */
  groupStatisticStatus(status: ResultStatusType): ResultStatusType {
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

  cssClassFor(status: ResultStatusType): string {
    switch (status) {
      case ResultStatusType.FAILED:
      case ResultStatusType.FAILED_MINOR:
        return 'report-failed';
      case ResultStatusType.PASSED:
      case ResultStatusType.MINOR:
        return 'report-passed';
      case ResultStatusType.NO_RUN:
        return 'report-running';
      default:
        return 'report-skipped';
    }
  }

  i18nKeyForResultStatus(status: ResultStatusType): string {
    return "RESULT_STATUS_" + this.resultStatusToString(status);
  }

  i18nKeyForExecStatus(status: ExecStatusType): string {
    return "EXEC_STATUS_" + this.execStatusToString(status);
  }

  i18nKeyFor(
    execStatus?: ExecStatusType,
    resultStatus?: ResultStatusType
  ) {
    if (execStatus !== ExecStatusType.FINISHED) {
      return this.i18nKeyForExecStatus(execStatus);
    } else {
      if (resultStatus) {
        return this.i18nKeyForResultStatus(resultStatus);
      } else {
        return this.i18nKeyForExecStatus(execStatus);
      }
    }
  }

  iconClassesFor(
    execStatus?: ExecStatusType,
    resultStatus?: ResultStatusType
  ) {
    switch (execStatus) {
      case ExecStatusType.NEW:
      case ExecStatusType.PENDING:
        return 'clock';
      case ExecStatusType.PROVISIONING:
      case ExecStatusType.RUNNING:
        return '.mdi .mdi-spin .mdi-loading'; // looks like cube
      case ExecStatusType.FINISHED:
        // If finished, the result status will be checked for a detailed status
        switch (resultStatus) {
          case ResultStatusType.PASSED:
            return "done";
          case ResultStatusType.FAILED:
            return "error_outline";
          //case ResultStatusType.NO_RUN:
          default:
            return 'radio_button_unchecked'; // looks like outline circle
        }
      case ExecStatusType.ABORTED:
        return 'highlight_off';
      case ExecStatusType.CRASHED:
        return 'close';
      default:
        return '.mdi .mdi-skull-outline';
    }
  }

  colorClassFor(
    execStatus?: ExecStatusType,
    resultStatus?: ResultStatusType
  ) {
    switch (execStatus) {
      case ExecStatusType.NEW:
      case ExecStatusType.PENDING:
      case ExecStatusType.PROVISIONING:
      case ExecStatusType.RUNNING:
        return 'info';
      case ExecStatusType.FINISHED:
        // If finished, the result status will be checked for a detailed status
        switch (resultStatus) {
          case ResultStatusType.PASSED:
            return "success";
          case ResultStatusType.FAILED:
          case ResultStatusType.FAILED_EXPECTED:
            return "danger";
          default:
            // unknown or skipped
            return 'dark';
        }
      case ExecStatusType.ABORTED:
        return 'dark';
      case ExecStatusType.CRASHED:
        return 'secondary';
      // INVALID_STATUS_TYPE
      default:
        return "danger";
    }
  }

  colorFor(status: ResultStatusType): string {
    switch (status) {
      case ResultStatusType.PASSED:
      case ResultStatusType.PASSED_RETRY:
      case ResultStatusType.MINOR:
        return GraphColors.PASSED;
      case ResultStatusType.FAILED:
      case ResultStatusType.FAILED_RETRIED:
      case ResultStatusType.FAILED_MINOR:
      case ResultStatusType.FAILED_EXPECTED:
        return GraphColors.FAILED;
      case ResultStatusType.SKIPPED:
        return GraphColors.SKIPPED;
      case ResultStatusType.NO_RUN:
        return GraphColors.RUNNING;
    }
  }
}
