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
import hljs from "highlight.js/lib/core";
import java from 'highlight.js/lib/languages/java';
import 'highlight.js/styles/base16/darcula.css';
import IScriptSource = data.ScriptSource;
import './code-view.scss'

@autoinject()
export class CodeView {
    private _hljs = hljs;

    @bindable({defaultBindingMode: bindingMode.toView})
    source:IScriptSource;

    @bindable({defaultBindingMode: bindingMode.toView})
    markClass:string = "error";

    constructor() {
        this._hljs.registerLanguage("java", java);
    }
    //
    // sourceChanged() {
    //     console.log(this.source);
    // }
}
