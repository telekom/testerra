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
import {HistoricalMethod} from "./HistoricalMethod.ts";
import type {HistoryAggregate} from "./report-model/report_pb.ts";
import {MethodType, ResultStatusType} from "./report-model/framework_pb.ts";
import {ClassHistoryStatistics} from "./ClassHistoryStatistics.ts";

export class HistoryAggregateStatistics extends Statistics {
    private readonly _classMap: Map<string, ClassHistoryStatistics>;
    private readonly _classIdMap: Map<string, string>;
    private readonly _methodMap: Map<string, HistoricalMethod>;
    private readonly _aggregate: HistoryAggregate;
    private readonly _historyIndex: number;

    constructor(historyEntry: HistoryAggregate) {
        super();
        this._aggregate = historyEntry;
        this._classMap = new Map();
        this._classIdMap = new Map();
        this._methodMap = new Map();
        this._historyIndex = historyEntry.historyIndex ?? 0;

        Object.values(historyEntry.classContexts ?? {}).forEach(classContext => {
            const classIdentifier = classContext.testContextName || (classContext.contextValues?.name ?? "");
            const classId = classContext.contextValues?.id;
            if (classId) {
                this._classIdMap.set(classId, classIdentifier);
            }
        });

        new Set(this._classIdMap.values()).forEach(cls => {
            this._classMap.set(cls, new ClassHistoryStatistics(cls));
        });

        Object.values(historyEntry.methodContexts ?? {}).forEach(method => {
            const methodId = method.contextValues?.id;
            if (methodId) {
                this._methodMap.set(methodId, new HistoricalMethod(method));
            }
            if (method.methodType === MethodType.TEST_METHOD) {
                this.addResultStatus(method.resultStatus ?? ResultStatusType.NO_RUN);
            }
        });

        this._methodMap.forEach(method => {
            (method.context.relatedMethodContextIds ?? []).forEach(relatedMethod => {
                const related = this._methodMap.get(relatedMethod);
                if (related) {
                    method.addRelatedMethods(related.identifier);
                }
            });

            const classId = method.context.classContextId;
            const classIdentifier = classId ? this._classIdMap.get(classId) : undefined;
            const classStats = classIdentifier ? this._classMap.get(classIdentifier) : undefined;
            if (classStats) {
                classStats.addMethod(method);
            }
        });
    }

    get classes() {
        return this._classMap;
    }

    get historyIndex() {
        return this._historyIndex;
    }

    get historyAggregate() {
        return this._aggregate;
    }
}
