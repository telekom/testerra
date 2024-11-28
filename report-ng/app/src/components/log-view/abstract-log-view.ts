/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
import {StatusConverter} from "services/status-converter";
import {ILogEntry} from "services/statistics-generator";
import {bindable} from "aurelia-templating";
import {bindingMode} from "aurelia-binding";
import {
    IntlDateFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter";

@autoinject()
export abstract class AbstractLogView {

    @bindable({defaultBindingMode: bindingMode.toView})
    class:string;

    @bindable({defaultBindingMode: bindingMode.toView})
    logMessages:ILogEntry[];

    @bindable({defaultBindingMode: bindingMode.toView})
    searchRegexp:RegExp;

    //improve performance
    //The cache uses the timestamp as the key and the formatted timestamp as the value.
    private timestampCache: Map<number, string> = new Map<number, string>();

    constructor(
        readonly statusConverter:StatusConverter,
        protected dateFormat: IntlDateFormatValueConverter
    ) {
    }

    protected formatTimestamp(timestamp: number): string {
        //check timestamp exists in cache, we do not need to reload timestamp every time
        if (this.timestampCache.has(timestamp)) {
            return this.timestampCache.get(timestamp)!;
        } else {
            const formattedTimestamp = this.dateFormat.toView(new Date(timestamp), 'log');
            this.timestampCache.set(timestamp, formattedTimestamp);
            return formattedTimestamp;
        }
    }

    protected toggleCause(logMessage:ILogEntry) {
        if (logMessage.cause) {
            logMessage.cause = null;
        } else {
            this.open(logMessage);
        }
    }

    protected open(logMessage:ILogEntry) {
        let msg = "";
        logMessage.stackTrace.forEach(cause => {
            if (msg.length > 0) {
                msg += "<br>"
            }
            msg += (cause.message?.length>0?cause.message + "<br>":'') + cause.stackTraceElements.join("<br>");
        });
        logMessage.cause = msg;
    }
}
