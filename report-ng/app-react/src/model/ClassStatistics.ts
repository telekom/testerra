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
import type {ClassContext as IClassContext} from "./report-model/framework_pb.ts";
import {type MethodContext as IMethodContext, MethodType} from "./report-model/framework_pb.ts";

export class ClassStatistics extends Statistics {

    private _configStatistics = new Statistics();
    private _methodContexts: IMethodContext[] = [];
    readonly classIdentifier: string;

    readonly classContext: IClassContext;

    constructor(
        classContext: IClassContext
    ) {
        super();
        this.classContext = classContext;
        this.classIdentifier = (classContext.testContextName || classContext.contextValues?.name) as string;
    }

    addMethodContext(methodContext: IMethodContext) {
        if (!methodContext.resultStatus) {
            return;
        }
        if (methodContext.methodType == MethodType.CONFIGURATION_METHOD) {
            this._configStatistics.addResultStatus(methodContext.resultStatus);
        } else {
            this.addResultStatus(methodContext.resultStatus);
        }
        this._methodContexts.push(methodContext);
    }

    get methodContexts() {
        return this._methodContexts;
    }

    get configStatistics() {
        return this._configStatistics;
    }

}
