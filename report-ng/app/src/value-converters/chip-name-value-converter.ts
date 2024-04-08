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
import {ChipType} from "../services/common-models";
import {StatusConverter} from "../services/status-converter";
import {ClassName, ClassNameValueConverter} from "./class-name-value-converter";

@autoinject()
export class ChipNameValueConverter {

    constructor(
        private _statusConverter: StatusConverter,
        private _classNameValueConverter: ClassNameValueConverter
    ) {
    }

    toView(chipType: ChipType, element, converterType: ChipConverterType) {

        if (converterType == ChipConverterType.TOOLTIP) {

            switch (chipType) {
                case ChipType.STATUS:
                    return "Status";
                case ChipType.CLASS:
                    return "Test Class";
                case ChipType.CUSTOM_TEXT:
                    return "Search Text";
                case ChipType.CUSTOM_FILTER_TIMINGS:
                    return "Custom test filter according to filter in previous view";
                case ChipType.CUSTOM_FILTER_FAILURE_ASPECTS:
                    return "Custom test filter according to filter in previous view";
                case ChipType.CLEAR_ALL:
                    return "Remove all Filters";
            }

        } else if (converterType == ChipConverterType.LABEL) {

            switch (chipType) {
                case ChipType.STATUS:
                    return this._statusConverter.getLabelForStatus(this._statusConverter.getStatusForClass(element));
                case ChipType.CLASS:
                    return this._classNameValueConverter.toView(String(element), ClassName.simpleName);
                case ChipType.CUSTOM_TEXT:
                    return element;
                case ChipType.CUSTOM_FILTER_TIMINGS:
                    return "Custom Filter";
                case ChipType.CUSTOM_FILTER_FAILURE_ASPECTS:
                    return "Custom Filter";
                case ChipType.CLEAR_ALL:
                    return "Clear All";
            }

        }
    }
}

enum ChipConverterType {
    TOOLTIP,
    LABEL
}
