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

import type {MethodHistoryStatistics} from "./MethodHistoryStatistics.ts";
import {ResultStatusType} from "./report-model/framework_pb.ts";

export class ClassHistory {
    private readonly _identifier: string;
    private _methods: MethodHistoryStatistics[] = [];

    constructor(
        identifier: string
    ) {
        this._identifier = identifier;
    }

    addMethod(method: MethodHistoryStatistics) {
        this._methods.push(method);
    }

    get availableFinalStatuses() {
        const availableFinalStatuses: Set<ResultStatusType> = new Set();
        this._methods
            .filter(method => method.isTestMethod())
            .forEach(method => {
                method.availableStatuses.forEach(status => {
                    if (status === ResultStatusType.FAILED_RETRIED) {
                        return;
                    }
                    availableFinalStatuses.add(
                        status === ResultStatusType.REPAIRED || status === ResultStatusType.PASSED_RETRY
                            ? ResultStatusType.PASSED
                            : status
                    );
                });
            });
        return Array.from(availableFinalStatuses);
    }

    get identifier() {
        return this._identifier;
    }

    get methods() {
        return this._methods;
    }
}
