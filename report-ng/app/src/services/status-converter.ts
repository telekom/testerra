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
import ResultStatusType = data.ResultStatusType;

class GraphColors {
    static readonly PASSED = '#417336'; // $success
    static readonly SKIPPED = '#8a929a'; // $dark
    static readonly FAILED = '#9f0737'; // $danger
    static readonly CRASHED = '#5d6f81'; // $secondary
    static readonly RUNNING = '#0089b6'; // $info
    static readonly FAILED_MINOR = '#f7af3e'; // $failedMinor
    static readonly FAILED_EXPECTED = '#4f031b'; // $failedExpected
}

@autoinject()
export class StatusConverter {

    // private static resultStatusValues: Array<ResultStatusType | string> = Object.values(ResultStatusType);
    // private static resultStatusKeys = Object.keys(ResultStatusType);

    get relevantStatuses() {
        return [
            ResultStatusType.PASSED,
            ResultStatusType.FAILED,
            ResultStatusType.FAILED_EXPECTED,
            ResultStatusType.FAILED_MINOR,
            ResultStatusType.SKIPPED,
        ];
    }

    getLabelForStatus(value: ResultStatusType | number): string {
        switch (value) {
            case ResultStatusType.SKIPPED:
                return "Skipped";
            case ResultStatusType.PASSED:
            case ResultStatusType.MINOR:
                return "Passed";
            case ResultStatusType.FAILED_RETRIED:
            case ResultStatusType.FAILED:
                return "Failed";
            case ResultStatusType.FAILED_EXPECTED:
                return "Expected Failed";
            case ResultStatusType.FAILED_MINOR:
                return "Minor Failed";
        }
    }

    getIconNameForStatus(status: ResultStatusType) {
        switch (status) {
            case ResultStatusType.SKIPPED:
                return "radio_button_unchecked";
            case ResultStatusType.FAILED_RETRIED:
            case ResultStatusType.FAILED_MINOR:
            case ResultStatusType.FAILED_EXPECTED:
            case ResultStatusType.FAILED:
                return "highlight_off";
            case ResultStatusType.PASSED:
            case ResultStatusType.MINOR:
                return "check";
        }
    }

    getColorForStatus(status: ResultStatusType): string {
        switch (status) {
            case ResultStatusType.PASSED:
            case ResultStatusType.PASSED_RETRY:
            case ResultStatusType.MINOR:
                return GraphColors.PASSED;
            case ResultStatusType.FAILED:
            case ResultStatusType.FAILED_RETRIED:
                return GraphColors.FAILED;
            case ResultStatusType.FAILED_MINOR:
                return GraphColors.FAILED_MINOR
            case ResultStatusType.FAILED_EXPECTED:
               return GraphColors.FAILED_EXPECTED
            case ResultStatusType.SKIPPED:
                return GraphColors.SKIPPED;
            case ResultStatusType.NO_RUN:
                return GraphColors.RUNNING;
        }
    }

    groupStatus(status: ResultStatusType): ResultStatusType {
        switch (status) {
            case ResultStatusType.FAILED_RETRIED:
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
}
