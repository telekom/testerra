/*
 * Testerra
 *
 * (C) 2024, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {autoinject, bindable} from "aurelia-framework";
import {FailureAspectStatistics} from "../../services/statistic-models";
import {bindingMode} from "aurelia-binding";
import {StatusConverter} from "../../services/status-converter";

@autoinject()
export class FailureAspectsTable {
    @bindable({defaultBindingMode: bindingMode.toView})
    private filteredFailureAspects: FailureAspectStatistics[];


    @bindable({defaultBindingMode: bindingMode.toView})
    private searchRegexp: RegExp;

    constructor(
        private _statusConverter:StatusConverter
    ) {
    }

    private _calcFontSize(index: number) {
        const min = 1;
        const max = 3;
        const count = Math.min(10, this.filteredFailureAspects.length);
        let size = ((count - index) / count) * max;
        if (size < min) size = min;
        return size;
    }

}
