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
import {filter} from "minimatch";

@autoinject()
export class LogView {

    private _causes:{[key:number]:string} = {};

    @bindable({bindingMode: bindingMode.toView})
    logMessages:ILogMessage[];

    private _filteredLogMessages:ILogMessage[];

    @bindable({bindingMode: bindingMode.toView})
    showThreads;

    @bindable({bindingMode: bindingMode.toView})
    search:RegExp;

    private _toggleCause(logMessage:ILogMessage) {
        const index = this.logMessages.indexOf(logMessage);
        if (this._causes[index]) {
            this._causes[index] = null;
        } else {
            this._open(logMessage, index);
        }
    }

    private _open(logMessage:ILogMessage, index?:number) {
        if (!index) index = this.logMessages.indexOf(logMessage);
        let msg = "";
        logMessage.stackTrace.forEach(cause => {
            if (msg.length > 0) {
                msg += "<br>"
            }
            msg += (cause.message?.length>0?cause.message + "<br>":'') + cause.stackTraceElements.join("<br>");
        });
        this._causes[index] = msg;
    }

    logMessagesChanged() {
        if (this.search) {
            this._filteredLogMessages = this.logMessages.filter(logMessage => {
                const foundInMessage = logMessage.message.match(this.search);
                const foundInStackTrace = logMessage.stackTrace.flatMap(stackTrace => stackTrace.stackTraceElements).filter(line => line.match(this.search));

                if (foundInStackTrace) {
                    this._open(logMessage);
                }

                return foundInMessage || foundInStackTrace;
            });
        } else {
            this._filteredLogMessages = this.logMessages;
        }
    }
}
