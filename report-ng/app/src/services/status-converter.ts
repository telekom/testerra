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
    static readonly FAILED = '#e63946'; // $danger
    static readonly CRASHED = '#5d6f81'; // $secondary
    static readonly RUNNING = '#0089b6'; // $info
    static readonly FAILED_MINOR = '#f7af3e'; // $failedMinor
    static readonly FAILED_EXPECTED = '#4f031b'; // $failedExpected
}

@autoinject()
export class StatusConverter {
    /**
     * Status adapted from {@link TestStatusController.java}
     */
    get failedStatuses() {
        return [
            ResultStatusType.FAILED,
            ResultStatusType.FAILED_MINOR,
            ResultStatusType.FAILED_EXPECTED,
            ResultStatusType.FAILED_RETRIED
        ]
    }

    get passedStatuses() {
        return [
            ResultStatusType.PASSED,
            ResultStatusType.PASSED_RETRY,
            ResultStatusType.MINOR,
            ResultStatusType.MINOR_RETRY
        ]
    }

    get relevantStatuses() {
        return [
            ResultStatusType.PASSED,
            ResultStatusType.FAILED,
            ResultStatusType.FAILED_EXPECTED,
            ResultStatusType.SKIPPED,
            ResultStatusType.FAILED_MINOR,
        ];
    }

    getLabelForStatus(value: ResultStatusType | number): string {
        switch (value) {
            case ResultStatusType.SKIPPED:
                return "Skipped";
            case ResultStatusType.PASSED:
                return "Passed";
            case ResultStatusType.MINOR:
                return "Minor Passed";
            case ResultStatusType.PASSED_RETRY:
                return "Passed Retried";
            case ResultStatusType.MINOR_RETRY:
                return "Minor Passed Retried";
            case ResultStatusType.FAILED_RETRIED:
                return "Failed Retried";
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
            case ResultStatusType.MINOR_RETRY:
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

    getClassForStatus(status: number): string {
        switch (status) {
            case ResultStatusType.PASSED:
                return "passed";
            case ResultStatusType.PASSED_RETRY:
                return "passed-retry";
            case ResultStatusType.MINOR_RETRY:
                return "minor-retry";
            case ResultStatusType.MINOR:
                return "minor";
            case ResultStatusType.FAILED:
                return "failed";
            case ResultStatusType.FAILED_RETRIED:
                return "failed-retry";
            case ResultStatusType.FAILED_MINOR:
                return "failed-minor";
            case ResultStatusType.FAILED_EXPECTED:
                return "failed-expected";
            case ResultStatusType.SKIPPED:
                return "skipped";
            case ResultStatusType.NO_RUN:
                return "running";
        }
    }

    getStatusForClass(status: string): ResultStatusType {
        switch (status) {
            case "passed":
                return ResultStatusType.PASSED;
            case "running":
                return ResultStatusType.NO_RUN;
            case "skipped":
                return ResultStatusType.SKIPPED;
            case "failed-expected":
                return ResultStatusType.FAILED_EXPECTED;
            case "failed-minor":
                return ResultStatusType.FAILED_MINOR;
            case "failed-retry":
                return ResultStatusType.FAILED_RETRIED;
            case "failed":
                return ResultStatusType.FAILED;
            case "minor":
                return ResultStatusType.MINOR;
            case "minor-retry":
                return ResultStatusType.MINOR_RETRY;
            case "passed-retry":
                return ResultStatusType.PASSED_RETRY;
        }
    }

}
