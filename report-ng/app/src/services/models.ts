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
import IMethodContext = data.IMethodContext;

export class FailureAspect {
    private _methodContext:IMethodContext;
    private _methodCount:number = 0;
    private _name:string;

    constructor() {
    }

    setMethodContext(methodContext:IMethodContext) {
        this._methodContext = methodContext;
        this._name = (methodContext.errorContext?.description
            || methodContext.errorContext?.stackTrace?.cause.className + (methodContext.errorContext?.stackTrace?.cause?.message?": "+methodContext.errorContext?.stackTrace?.cause?.message.trim():""));
        this._methodCount = 1;
        return this;
    }

    get methodContext() {
        return this._methodContext;
    }

    get name() {
        return this._name;
    }

    get methodCount() {
        return this._methodCount;
    }

    incrementMethodCount() {
        this._methodCount++;
        return this;
    }

}
