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


import {ResultStatusType} from "../provider/report-model/framework_pb.ts";
import type {data} from "../provider/report-model";

class GraphColors {
    static readonly PASSED = '#417336'; // $success
    static readonly SKIPPED = '#f7af3e'; // $dark #8a929a
    static readonly FAILED = '#e63946'; // $danger
    static readonly CRASHED = '#5d6f81'; // $secondary

    static readonly FAILED_EXPECTED = '#4f031b'; // $failedExpected

    static readonly NO_STATUS = 'black';
}

export interface IFilter {
    status?: data.ResultStatusType,
    class?: string,
}

export class StatusConverter {
    private static readonly _packageRegexp = new RegExp("^(.+)\\.(\\w+)$");

    static get passedStatuses() {
        return [
            ResultStatusType.PASSED,
            ResultStatusType.PASSED_RETRY, // Recovered
            ResultStatusType.REPAIRED,
        ]
    }

    static get relevantStatuses() {
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
     */
    static groupStatus(status: ResultStatusType): ResultStatusType[] {
        switch (status) {
            case ResultStatusType.PASSED:
                return this.passedStatuses;
            default:
                return [status];
        }
    }

    // TODO should be always int, not string
    static normalizeStatus(status: ResultStatusType | string): number {
        if (typeof status === "string") {
            status = Number.parseInt(status);
        }
        return status;
    }

    static getLabelForStatus(status: ResultStatusType | string): string {
        switch (this.normalizeStatus(status)) {
            case ResultStatusType.SKIPPED:
                return "Skipped";
            case ResultStatusType.PASSED:
                return "Passed";
            case ResultStatusType.PASSED_RETRY:
                return "Recovered";
            case ResultStatusType.FAILED_RETRIED:
                return "Retried";
            case ResultStatusType.FAILED:
                return "Failed";
            case ResultStatusType.FAILED_EXPECTED:
                return "Expected Failed";
            case ResultStatusType.REPAIRED:
                return "Repaired";
            default:
                return "na";
        }
    }

    static getIconNameForStatus(status: ResultStatusType | string) {
        switch (this.normalizeStatus(status)) {
            case ResultStatusType.SKIPPED:
                return "radio_button_unchecked";
            case ResultStatusType.FAILED_RETRIED:
            case ResultStatusType.FAILED_EXPECTED:
            case ResultStatusType.FAILED:
                return "highlight_off";
            case ResultStatusType.PASSED:
                return "check";
        }
    }

    static getColorForStatus(status: ResultStatusType): string {
        switch (this.normalizeStatus(status)) {
            case ResultStatusType.PASSED:
            case ResultStatusType.PASSED_RETRY:
            case ResultStatusType.REPAIRED:
                return GraphColors.PASSED;
            case ResultStatusType.FAILED:
            case ResultStatusType.FAILED_RETRIED:
                return GraphColors.FAILED;
            case ResultStatusType.FAILED_EXPECTED:
                return GraphColors.FAILED_EXPECTED
            case ResultStatusType.SKIPPED:
                return GraphColors.SKIPPED;
            case ResultStatusType.NO_RUN:
                return GraphColors.CRASHED;
            default:
                return GraphColors.NO_STATUS;
        }
    }

    static getClassForStatus(status: ResultStatusType | string): string {
        switch (this.normalizeStatus(status)) {
            case ResultStatusType.PASSED:
                return "passed";
            case ResultStatusType.PASSED_RETRY:
                return "recovered";
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
            case ResultStatusType.REPAIRED:
                return "repaired";
            default:
                return "na";
        }
    }

    static getStatusForClass(status: string): ResultStatusType {
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
                return ResultStatusType.REPAIRED;
            default:
                return ResultStatusType.NO_RUN;
        }
    }

    static createRegexpFromSearchString(searchQuery: string) {
        const specialCharacters = new RegExp('([.*+?^${}()|[\\]\\\\"])', "g");
        searchQuery = searchQuery.replace(specialCharacters, "\\$1");
        return new RegExp("(" + searchQuery + ")", "ig");
    }

    static separateNamespace(namespace: string) {
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
}
