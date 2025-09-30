/*
 * Testerra
 *
 * (C) 2025, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
import "./method-table.scss";
import {IComparableMethod} from "./run-comparison";
import {ClassName, ClassNameValueConverter} from "../../../value-converters/class-name-value-converter";

@autoinject()
export class MethodTable {

    @bindable({defaultBindingMode: bindingMode.toView})
    private methods: IComparableMethod[];
    @bindable({defaultBindingMode: bindingMode.toView})
    private pastRun: number;
    @bindable({defaultBindingMode: bindingMode.toView})
    private currentRun: number;

    constructor() {
    }

    methodsChanged() {
        if (this.methods) {
            let converter = new ClassNameValueConverter();
            this.methods = this.methods.sort((a, b) => {
                const aClass = converter.toView(a.classIdentifier, ClassName.simpleName);
                const bClass = converter.toView(b.classIdentifier, ClassName.simpleName);
                if (aClass < bClass) return -1;
                if (aClass > bClass) return 1;
                return 0;
            });
        }

    }
}
