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

import {autoinject} from "aurelia-framework";
import {bindable} from "aurelia-templating";
import {bindingMode} from "aurelia-binding";
import {data} from "../../services/report-model";
import ILogMessage = data.ILogMessage;
import {StatusConverter} from "../../services/status-converter";

@autoinject()
export class LogView {

    private _causes:{[key:number]:string} = {};

    constructor(
        private _statusConverter:StatusConverter,
    ) {

    }

    @bindable({bindingMode: bindingMode.toView})
    logMessages:ILogMessage[];

    @bindable({bindingMode: bindingMode.toView})
    showThreads;

    @bindable({bindingMode: bindingMode.toView})
    search:RegExp;

    private _toggleCause(logMessage:ILogMessage) {
        const index = this.logMessages.indexOf(logMessage);
        if (this._causes[index]) {
            this._causes[index] = null;
        } else {
            let msg = "";
            const stackTrace = this._statusConverter.flattenStackTrace(logMessage.cause);
            stackTrace.forEach(cause => {
                if (msg.length > 0) {
                    msg += "<br>"
                }
                msg += cause.message + "<br>" + cause.stackTraceElements.join("<br>");
            });
            this._statusConverter.flattenStackTrace(logMessage.cause)
            this._causes[index] = msg;
        }
    }
}
