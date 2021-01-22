/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
import {StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import IFile = data.IFile;

@autoinject()
export class LazyImage {

    @bindable({bindingMode: bindingMode.toView})
    fileId:string;

    @bindable({bindingMode: bindingMode.toView})
    class:string;

    private _file:IFile;

    constructor(
        private _statistics: StatisticsGenerator,
        private _element: Element,
    ) {
    }

    bind() {
        console.log("load lazy image", this.fileId);
        this._statistics.getFilesForIds([this.fileId]).then(file => {
            this._file = file[0];
        })
    }

    private _bubbleFile(ev:MouseEvent) {
        ev.stopPropagation();

        const e = new CustomEvent("click", {
            detail: this._file,
            bubbles: true
        });
        this._element.dispatchEvent(e);
    }
}
