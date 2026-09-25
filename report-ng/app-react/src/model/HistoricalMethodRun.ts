/*
 * Testerra
 *
 * (C) 2026, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {MethodContext, ResultStatusType} from "./report-model/framework_pb.ts";
import type {HistoricalMethod} from "./HistoricalMethod.ts";

export class HistoricalMethodRun {
    private readonly _historyIndex: number = 0;
    private readonly _context: MethodContext;
    private readonly _failureAspects: string[];
    private readonly _combinedErrorMessage: string;

    constructor(historicalMethod: HistoricalMethod, index: number) {
        this._historyIndex = index;
        this._context = historicalMethod.context;
        this._failureAspects = historicalMethod.getFailureAspects();
        this._combinedErrorMessage = historicalMethod.getCombinedErrorMessage();
    }

    // Returns the overall status for flakiness calculation
    getParsedResultStatus(): ResultStatusType {
        let status = this._context.resultStatus ?? ResultStatusType.NO_RUN;
        if (status === ResultStatusType.FAILED_RETRIED) {
            status = ResultStatusType.FAILED;
        } else if (status === ResultStatusType.PASSED_RETRY || status === ResultStatusType.REPAIRED) {
            status = ResultStatusType.PASSED;
        }
        return status;
    }

    get historyIndex() {
        return this._historyIndex;
    }

    get context() {
        return this._context;
    }

    get failureAspects() {
        return this._failureAspects;
    }

    get combinedErrorMessage() {
        return this._combinedErrorMessage;
    }
}
