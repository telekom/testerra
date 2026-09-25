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

import type {MethodContext} from "./report-model/framework_pb.ts";

export class HistoricalMethod {
    private readonly _methodContext: MethodContext;
    private readonly _methodIdentifier: string;
    private _relatedMethodIdentifiers: string[] = [];

    constructor(methodContext: MethodContext) {
        this._methodContext = methodContext;
        this._methodIdentifier = this._getParsedMethodIdentifier();
    }

    private _getParsedMethodIdentifier() {
        let methodName: string;
        if (this._methodContext.testName) {
            methodName = this._methodContext.testName;
        } else {
            methodName = this._methodContext.contextValues?.name ?? "";
            const params = [];
            for (const name in this._methodContext.parameters) {
                params.push(name + ": " + this._methodContext.parameters[name]);
            }
            if (params.length > 0) {
                methodName += "(" + params.join(", ") + ")";
            }
        }
        return methodName;
    }

    getFailureAspects(): string[] {
        return (this._methodContext.testSteps ?? [])
            .flatMap(step => step.actions ?? [])
            .flatMap(action =>
                (action.entries ?? []).flatMap(entry => {
                    const cause = entry.errorContext?.stackTrace?.[0];
                    if (!cause) {
                        return [];
                    }

                    const className = cause.className ?? "";
                    const shortClassName = className.substring(className.lastIndexOf(".") + 1);
                    const message = cause.message ?? "";
                    return [`${shortClassName}: ${message}`.trim().replaceAll("\n", " ")];
                })
            );
    }

    getCombinedErrorMessage(): string {
        let combinedErrorMessage = "";
        const failureAspects = this.getFailureAspects();
        if (failureAspects.length > 0) {
            failureAspects.forEach(error => {
                combinedErrorMessage += "\n" + error;
            });
            combinedErrorMessage = combinedErrorMessage.trim();
        }
        return combinedErrorMessage;
    }

    addRelatedMethods(relatedMethod: string) {
        this._relatedMethodIdentifiers.push(relatedMethod);
    }

    get context() {
        return this._methodContext;
    }

    get identifier() {
        return this._methodIdentifier;
    }

    get relatedMethods() {
        return this._relatedMethodIdentifiers;
    }
}
