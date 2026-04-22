/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import type {
    MethodContext,
    TestContext,
    SuiteContext,
    SessionContext,
    LogMessage, ErrorContext, /*ErrorContext*/
} from "./report-model/framework_pb";

import {ExecutionStatistics} from "./ExecutionStatistics";
import {ClassStatistics} from "./ClassStatistics";
// import {ClassStatistics} from "./ClassStatistics";
import {FailureAspectStatistics} from "./FailureAspectStatistics";

export class MethodDetails {

    executionStatistics!: ExecutionStatistics;
    testContext!: TestContext;
    suiteContext!: SuiteContext;
    sessionContexts!: SessionContext[];
    promptLogs!: LogMessage[];
    private _identifier: string | null = null;
    static readonly FAILS_ANNOTATION_NAME = "eu.tsystems.mms.tic.testframework.annotations.Fails";
    private _decodedAnnotations = {};
    private _failureAspects: FailureAspectStatistics[] | null = null;


    constructor(
        readonly methodContext: MethodContext,
        readonly classStatistics: ClassStatistics
    ) {}

    get failsAnnotation() {
        return this.decodeAnnotation(MethodDetails.FAILS_ANNOTATION_NAME);
    }

    get identifier() {
        if (!this._identifier) {
            if (this.methodContext.testName) {
                this._identifier = this.methodContext.testName;
            } else {
                this._identifier = this.methodContext.contextValues?.name ?? "";
                const params = [];
                for (const name in this.methodContext.parameters) {
                    params.push(name + ": " + this.methodContext.parameters[name]);
                }
                if (params.length > 0) {
                    this._identifier += "(" + params.join(", ") + ")";
                }
            }
        }
        return this._identifier;
    }

    private _decode(from: { [p: string]: string } | undefined, name: string, to: { [p: string]: any }): any {
        if (to[name] === undefined) {
            if (from?.[name]) {
                to[name] = JSON.parse(from[name]);
            } else {
                to[name] = null;
            }
        }

        return to[name];
    }

    decodeAnnotation(name: string): any {
        return this._decode(this.methodContext.annotations, name, this._decodedAnnotations);
    }

    get errorContexts() {
        return (
            this.methodContext.testSteps
                ?.flatMap(step => step.actions ?? [])
                .flatMap(action => action.entries ?? [])
                .filter(value => value.errorContext)
                .map(value => value.errorContext)
                .filter(
                    (ec): ec is ErrorContext => ec !== undefined    // makes sure there are no undefined errorContexts if something is optional
                ) ?? []
        );
    }

    get failureAspects(): FailureAspectStatistics[] {
        if (this._failureAspects == null) {
            this._failureAspects = this.errorContexts.map(errorContext => {
                const failureAspect = new FailureAspectStatistics(errorContext);
                failureAspect.addMethodContext(this.methodContext);
                return failureAspect;
            });
        }
        return this._failureAspects;
    }
}
