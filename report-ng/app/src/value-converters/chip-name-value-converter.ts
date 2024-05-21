/*
 * Testerra
 *
 * (C) 2023, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
import {ClassName, ClassNameValueConverter} from "./class-name-value-converter";
import {FilterType, IFilterChip} from "../components/classes/classes";

@autoinject()
export class ChipNameValueConverter {

    constructor(
        private _statusConverter: StatusConverter,
        private _classNameValueConverter: ClassNameValueConverter
    ) {
    }

    toView(filterChip: IFilterChip) {

        switch (filterChip.filter.type) {
            case FilterType.STATUS:
                return this._statusConverter.getLabelForStatus(this._statusConverter.getStatusForClass(filterChip.value));
            case FilterType.CLASS:
                return this._classNameValueConverter.toView(String(filterChip.value), ClassName.simpleName);
            case FilterType.CUSTOM_TEXT:
                return filterChip.value;
            case FilterType.CUSTOM_FILTER_TIMINGS:
                return "Custom Filter";
            case FilterType.CUSTOM_FILTER_FAILURE_ASPECTS:
                return "Custom Filter";
        }
    }

}
