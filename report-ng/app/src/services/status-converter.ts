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
    static readonly SKIPPED = '#f7af3e'; // $dark #8a929a
    static readonly FAILED = '#e63946'; // $danger
    static readonly CRASHED = '#5d6f81'; // $secondary
    //static readonly RUNNING = '#0089b6'; // $info
    //static readonly FAILED_MINOR = '#f7af3e'; // $failedMinor
    static readonly FAILED_EXPECTED = '#4f031b'; // $failedExpected
}

export interface IFilter {
    status?:data.ResultStatusType,
    class?:string,
}

@autoinject()
export class StatusConverter {
    private readonly _packageRegexp = new RegExp("^(.+)\\.(\\w+)$");

    /**
     * Status adapted from {@link TestStatusController.java}
     * @deprecated
     */
    get failedStatuses() {
        return [
            ResultStatusType.FAILED,
            ResultStatusType.FAILED_MINOR,
            /**
             * Expected failed is no actual failed status and explicitly featured
             */
            //ResultStatusType.FAILED_EXPECTED,
            /**
             * {@link ResultStatusType.FAILED_RETRIED} is not a statistical relevant status
             */
            ResultStatusType.FAILED_RETRIED
        ]
    }

    /**
     * @deprecated
     */
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
            ResultStatusType.FAILED,
            ResultStatusType.FAILED_EXPECTED,
            ResultStatusType.SKIPPED,
            ResultStatusType.PASSED,
        ];
    }

    /**
     * Groups a status to relevant combined statuses
     * @param status
     * @deprecated
     */
    groupStatus(status:ResultStatusType):ResultStatusType[] {
        switch (status) {
            case ResultStatusType.PASSED: return this.passedStatuses;
            case ResultStatusType.FAILED: return this.failedStatuses
            default: return [status];
        }
    }

    normalizeStatus(status:ResultStatusType|string):number {
        if (typeof status === "string") {
            status = Number.parseInt(status);
        }
        return status;
    }

    getLabelForStatus(status: ResultStatusType|string): string {
        switch (this.normalizeStatus(status)) {
            case ResultStatusType.SKIPPED:
                return "Skipped";
            case ResultStatusType.MINOR:
            case ResultStatusType.PASSED:
                return "Passed";
            case ResultStatusType.MINOR_RETRY:
            case ResultStatusType.PASSED_RETRY:
                return "Recovered";
            case ResultStatusType.FAILED_RETRIED:
                return "Retried";
            case ResultStatusType.FAILED:
                return "Failed";
            case ResultStatusType.FAILED_EXPECTED:
                return "Expected Failed";
            case ResultStatusType.REPAIRED:
                return "Repaired"
        }
    }

    getIconNameForStatus(status: ResultStatusType|string) {
        switch (this.normalizeStatus(status)) {
            case ResultStatusType.SKIPPED:
                return "radio_button_unchecked";
            case ResultStatusType.FAILED_RETRIED:
            case ResultStatusType.FAILED_MINOR:
            case ResultStatusType.FAILED_EXPECTED:
            case ResultStatusType.FAILED:
                return "highlight_off";
            case ResultStatusType.PASSED:
            case ResultStatusType.MINOR:
            case ResultStatusType.MINOR_RETRY:
                return "check";
        }
    }

    getColorForStatus(status: ResultStatusType): string {
        switch (this.normalizeStatus(status)) {
            case ResultStatusType.PASSED:
            case ResultStatusType.PASSED_RETRY:
            case ResultStatusType.MINOR_RETRY:
            case ResultStatusType.MINOR:
                return GraphColors.PASSED;
            case ResultStatusType.FAILED_MINOR:
            case ResultStatusType.FAILED:
            case ResultStatusType.FAILED_RETRIED:
                return GraphColors.FAILED;
            case ResultStatusType.FAILED_EXPECTED:
                return GraphColors.FAILED_EXPECTED
            case ResultStatusType.SKIPPED:
                return GraphColors.SKIPPED;
            case ResultStatusType.NO_RUN:
                return GraphColors.CRASHED;
        }
    }

    getClassForStatus(status: ResultStatusType|string): string {
        switch (this.normalizeStatus(status)) {
            case ResultStatusType.MINOR:
            case ResultStatusType.PASSED:
                return "passed";
            case ResultStatusType.MINOR_RETRY:
            case ResultStatusType.PASSED_RETRY:
                return "recovered";
            case ResultStatusType.FAILED_MINOR:
            case ResultStatusType.FAILED:
                return "failed";
            case ResultStatusType.FAILED_RETRIED:
                return "retried";
            case ResultStatusType.FAILED_EXPECTED:
                return "failed-expected";
            case ResultStatusType.SKIPPED:
                return "skipped";
            case ResultStatusType.NO_RUN:
                return "running";
            case data.ResultStatusType.REPAIRED:
                return "repaired";
            default:
                return "unknown-status";
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
            case "retried":
                return ResultStatusType.FAILED_RETRIED;
            case "failed":
                return ResultStatusType.FAILED;
            case "recovered":
                return ResultStatusType.PASSED_RETRY;
            case "repaired":
                return data.ResultStatusType.REPAIRED;
        }
    }

    createRegexpFromSearchString(searchQuery:string) {
        const specialCharacters = new RegExp("([\\[\\]])", "g");
        searchQuery = searchQuery.replace(specialCharacters, "\\$1");
        return new RegExp("(" + searchQuery + ")", "ig");
    }

    separateNamespace(namespace:string) {
        const match = namespace.match(this._packageRegexp);
        if (match) {
            return {
                package: match[1],
                class: match[2],
            }
        } else {
            return {
                class: namespace
            }
        }
    }

    /**
     * Correct the deprecated result status type of the method context
     */
    correctStatus(status:ResultStatusType) {
        switch (status) {
            case ResultStatusType.FAILED_MINOR: return ResultStatusType.FAILED;
            case ResultStatusType.MINOR_RETRY: return ResultStatusType.PASSED_RETRY;
            case ResultStatusType.MINOR: return ResultStatusType.PASSED;
            default: return status;
        }
    }
}
