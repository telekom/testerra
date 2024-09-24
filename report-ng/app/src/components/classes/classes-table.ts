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

import {autoinject} from "aurelia-framework";
import {MethodDetails} from "../../services/statistics-generator";
import {bindable} from "aurelia-templating";
import {bindingMode} from "aurelia-binding";

@autoinject()
export class ClassesTable {

    @bindable({defaultBindingMode: bindingMode.twoWay})
    sortByRunIndex: () => void;
    @bindable({defaultBindingMode: bindingMode.twoWay})
    sortByClass: () => void;
    @bindable({defaultBindingMode: bindingMode.twoWay})
    sortByMethod: () => void;
    @bindable({defaultBindingMode: bindingMode.toView})
    private filteredMethodDetails: MethodDetails[];
    @bindable({defaultBindingMode: bindingMode.toView})
    private uniqueStatuses = 0;
    @bindable({defaultBindingMode: bindingMode.toView})
    private uniqueClasses = 0;
    @bindable({defaultBindingMode: bindingMode.toView})
    private searchInput = 0;

    constructor() {
    }
}
