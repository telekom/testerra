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

import {Statistics} from "./Statistics.ts";
import type {HistoricalMethod} from "./HistoricalMethod.ts";
import {MethodType, ResultStatusType} from "./report-model/framework_pb.ts";

export class ClassHistoryStatistics extends Statistics {
    private readonly _identifier: string;
    private _methods: HistoricalMethod[] = [];

    constructor(
        identifier: string
    ) {
        super();
        this._identifier = identifier;
    }

    addMethod(method: HistoricalMethod) {
        if (method.context.contextValues?.endTime != 0) {
            this._methods.push(method);
            if (method.context.methodType === MethodType.TEST_METHOD) {
                this.addResultStatus(method.context.resultStatus ?? ResultStatusType.NO_RUN);
            }
        }
    }

    get methods() {
        return this._methods;
    }

    get identifier() {
        return this._identifier;
    }
}
