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

import {AbstractLogView} from "./abstract-log-view";
import {bindable} from "aurelia-templating";
import {bindingMode} from "aurelia-binding";
import {customElement} from "aurelia-framework";

/**
 * A virtual log view with search support
 */
@customElement("virtual-log-view")
export class VirtualLogView extends AbstractLogView {

    @bindable({defaultBindingMode: bindingMode.fromView})
    readonly logView:VirtualLogView = this;

    private _lastFoundIndex = -1;
    private _initialScrollOffset:number;
    private _logView:HTMLDivElement;

    private _scrollToNextFound() {
        if (this.searchRegexp && this._logView) {
            const firstItem = this._logView.children.item(1) as HTMLDivElement;
            const logMessagesToSearch = this.logMessages.slice(this._lastFoundIndex+1, -1);

            const foundLogMessage = logMessagesToSearch.find(logMessage => {
                const foundInMessage = logMessage.message.match(this.searchRegexp);
                const foundInStackTrace = logMessage.stackTrace
                    .flatMap(stackTrace => stackTrace.stackTraceElements)
                    .filter(line => line.match(this.searchRegexp));
                const foundInLoggerName = logMessage.loggerName.match(this.searchRegexp);

                if (foundInStackTrace.length>0) {
                    this.open(logMessage);
                }

                return foundInMessage || foundInStackTrace.length>0 || foundInLoggerName;
            });

            if (foundLogMessage) {
                this._lastFoundIndex = this.logMessages.indexOf(foundLogMessage);
                // console.log("found at index", this._lastFoundIndex);
                const scrollY = this._initialScrollOffset + (firstItem.clientHeight * this._lastFoundIndex);
                // console.log(this._initialScrollOffset);
                // console.log("scroll to ", this._initialScrollOffset, " + found index", this._lastFoundIndex, " * ", firstItem.clientHeight);
                const options = {top: scrollY};

                window.setTimeout(() => {
                    window.scroll(options);
                }, 1);
            }
        }
    }


    attached() {
        this._initialScrollOffset = this._logView.getBoundingClientRect().top;
    }

    searchNext() {
        this._scrollToNextFound();
    }

    resetSearch() {
        // console.log("reset search");
        this._lastFoundIndex = -1;
    }
}
