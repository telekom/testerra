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

    resultStatusToString(value: ResultStatusType | number): string {
        const index = StatusConverter.resultStatusValues.indexOf(value);
        return StatusConverter.resultStatusKeys[index];
    }

    resultStatusFromString(value: string): ResultStatusType {
        const index = StatusConverter.resultStatusKeys.indexOf(value.toUpperCase());
        return <ResultStatusType>StatusConverter.resultStatusValues[index];
    }

    iconNameForStatus(status: ResultStatusType) {
        switch (status) {
            case ResultStatusType.SKIPPED:
                return "radio_button_unchecked";
            case ResultStatusType.FAILED:
                return "highlight_off";
            case ResultStatusType.PASSED:
                return "check";
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
