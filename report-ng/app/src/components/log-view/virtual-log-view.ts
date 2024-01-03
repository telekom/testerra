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
import {autoinject, customElement} from "aurelia-framework";
import {ILogEntry} from "../../services/statistics-generator";
import "./virtual-log-view.scss"

/**
 * A virtual log view with search support
 */
@autoinject()
@customElement("virtual-log-view")
export class VirtualLogView extends AbstractLogView {

    private static readonly SCROLL_OFFSET = 500;
    @bindable({defaultBindingMode: bindingMode.fromView})
    readonly logView: VirtualLogView = this;
    private _lastFoundIndex = -1;
    private _logView: HTMLDivElement;
    private _visibleMessages: ILogEntry[] = []; // array of messages that are visible in the DOM
    private _reloadSize = 200; // number of messages that are reloaded

    // variables for search helper text
    @bindable({defaultBindingMode: bindingMode.twoWay}) helperText: string;
    @bindable searchRegexp: RegExp;
    private _occurrences: number;

    bind() {
        // initialize visible messages
        const endIndex = Math.min(this._visibleMessages.length + 2 * this._reloadSize, this.logMessages.length);
        this._visibleMessages = this.logMessages.slice(0, endIndex);
    }

    handleScrollEvent() {
        const isAtTop = this._logView.scrollTop < VirtualLogView.SCROLL_OFFSET;
        const isAtBottom = this._logView.scrollHeight - this._logView.scrollTop <= this._logView.clientHeight + 1;

        if (isAtTop) {
            this._getMoreUp();
        } else if (isAtBottom && this._visibleMessages[this._visibleMessages.length - 1] != this.logMessages[this.logMessages.length - 1]) { // check if we already reached end of logMessages
            this._getMoreDown();
        }
    }

    resetSearch() {
        // console.log("reset search");
        this._lastFoundIndex = -1;
        this._occurrences = 0;
    }

    searchRegexpChanged() {
        this._scrollToNextFound();
        this._calculateOccurrences();
    }

    private _calculateOccurrences() {
        if (this.searchRegexp) {
            let totalOccurrences = 0;

            this.logMessages.forEach(logMessage => {
                const foundInMessage = logMessage.message.match(this.searchRegexp);
                const foundInStackTrace = logMessage.stackTrace
                    .flatMap(stackTrace => stackTrace.stackTraceElements)
                    .filter(line => line.match(this.searchRegexp));
                const foundInLoggerName = this.statusConverter.separateNamespace(logMessage.loggerName).class.match(this.searchRegexp);

                if(foundInMessage || foundInStackTrace.length > 0 || foundInLoggerName) {
                    if (foundInStackTrace.length > 0) {
                        this.open(logMessage);
                    }
                    totalOccurrences += 1;
                }
            });

            this.helperText = (this._occurrences == 0 && totalOccurrences == 0)
            ? "No results found"
            : "Results found: " + this._occurrences + "/" + totalOccurrences;
        }
    }

    private _scrollToNextFound() {
        if (this.searchRegexp && this._logView) {
            const logMessagesToSearch = this.logMessages.slice(this._lastFoundIndex + 1, -1);

            const foundLogMessage = logMessagesToSearch.find(logMessage => {
                const foundInMessage = logMessage.message.match(this.searchRegexp);
                const foundInStackTrace = logMessage.stackTrace
                    .flatMap(stackTrace => stackTrace.stackTraceElements)
                    .filter(line => line.match(this.searchRegexp));
                const foundInLoggerName = this.statusConverter.separateNamespace(logMessage.loggerName).class.match(this.searchRegexp);

                if (foundInStackTrace.length > 0) {
                    this.open(logMessage);
                }

                return foundInMessage || foundInStackTrace.length > 0 || foundInLoggerName;
            });

            if (foundLogMessage) {
                this._occurrences += 1;
                this._lastFoundIndex = this.logMessages.indexOf(foundLogMessage);

                const start = Math.max(this._lastFoundIndex - this._reloadSize / 2, 0);
                const end = this._lastFoundIndex > this._reloadSize / 2 ? this._lastFoundIndex + this._reloadSize / 2 : this._lastFoundIndex + this._reloadSize;

                this._visibleMessages = this.logMessages.slice(start, end);

                window.setTimeout(() => {
                    // getting the DOM element that we found in logMessages
                    const element = document.getElementById(`id-${foundLogMessage.message}-${foundLogMessage.timestamp}-${foundLogMessage.type}-${foundLogMessage.loggerName}-${foundLogMessage.stackTrace}-${foundLogMessage.threadName}`)
                    element.scrollIntoView();
                }, 1);

            }
        }
    }

    private _getMoreDown() {
        if (this._visibleMessages.length < this.logMessages.length) {
            // add next block of messages from logMessages to visibleMessages
            const index = this.logMessages.indexOf(this._visibleMessages[this._visibleMessages.length - 1])
            this._visibleMessages.push(...this.logMessages.slice(index, index + this._reloadSize))
        }

        // remove messages that are not displayed anymore
        if (this._visibleMessages.length > 2 * this._reloadSize) {
            this._visibleMessages.splice(0, this._reloadSize)
        }
    }

    private _getMoreUp() {
        if (this._visibleMessages.length < this.logMessages.length) {

            // add next block of messages from logMessages to visibleMessages
            const index = this.logMessages.indexOf(this._visibleMessages[0])
            const start = index - this._reloadSize < 0 ? 0 : index - this._reloadSize;
            this._visibleMessages.unshift(...this.logMessages.slice(start, index))
        }

        // remove messages that are not displayed anymore
        if (this._visibleMessages.length > 2 * this._reloadSize) {
            this._visibleMessages.splice(-this._reloadSize, this._reloadSize)
        }
    }
}
