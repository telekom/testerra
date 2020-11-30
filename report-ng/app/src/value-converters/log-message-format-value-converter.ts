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
import {StatusConverter} from "../services/status-converter";
import {data} from "../services/report-model";

@autoinject()
export class LogMessageFormatValueConverter {
    constructor(
        private _statusConverter:StatusConverter
    ) {
    }

    toView(value: data.PLogMessage) {
        let string = "[";
        switch (value.type) {
            case data.PLogMessageType.LMT_DEBUG: string += "DEBUG"; break;
            case data.PLogMessageType.LMT_ERROR: string += "ERROR"; break;
            case data.PLogMessageType.LMT_INFO: string += "INFO"; break;
            case data.PLogMessageType.LMT_WARN: string += "WARN"; break;
        }
        string += "] " + this._statusConverter.separateNamespace(value.loggerName).class + ": " + value.message;
        return string;
    }
}
