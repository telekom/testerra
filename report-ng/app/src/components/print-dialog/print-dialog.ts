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

import {autoinject, observable} from 'aurelia-framework';
import {MdcDialog} from '@aurelia-mdc-web/dialog';
import './print-dialog.scss';

@autoinject()
export class PrintDialog {
    private _title: string;
    private _iFrameSrc: string;
    private _iFrameDoc: Document | undefined;
    @observable private _loading = true;
    private _page = 1;
    private _totalPages = 1;
    private _pagesCalculated = false;   // necessary otherwise total pages will be added up if window is resized
    private _pageArray = [0, 0];
    private _failureAspectFilters: IFilter[] = [
        {id: 0, name: "All"},
        {id: 1, name: "Major"},
        {id: 2, name: "Minor"}
    ];
    private _testFilters: IFilter[] = [
        {id: 0, name: "All", tooltip: "Included status: Passed, Failed, Expected Failed, Skipped"},
        {id: 1, name: "Failed", tooltip: "Included status: Failed, Expected Failed, Skipped"},
    ];
    @observable private _selectedFailureAspectFilter = 0;
    @observable private _selectedTestFilter = 0;

    private readonly _checkboxOptions: ICheckBoxOption[] = [
        {label: 'Test Classes Chart', checked: true, id: "test-classes-card"},
        {label: 'Failure Aspects Table', checked: true, id: "failure-aspects-card"},
        {label: 'Test Case List', checked: true, id: "classes-table-card"}
    ];

    constructor(
        private _dialog: MdcDialog
    ) {
    }

    activate(params: { title: string; iFrameSrc: string }) {
        this._title = params.title;
        this._iFrameSrc = params.iFrameSrc;
    }

    attached() {
        const iframe = document.getElementById('iframe') as HTMLIFrameElement;
        iframe.onload = () => {
            this._iFrameDoc = iframe.contentDocument || iframe.contentWindow.document;
            this._iFrameDoc.addEventListener('scroll', this.handleScrollEvent.bind(this))

            setTimeout(() => {
                this._resizeFrame();
            }, 50);

            window.addEventListener('resize', this._resizeFrame.bind(this));
        };
    }

    private _resizeFrame() {
        const dialog = document.getElementById("print-dialog");
        if (!dialog || !this._iFrameDoc) return;

        const iFrameHeight = window.innerHeight / 100 * 90;
        const iFrameWidth = iFrameHeight / Math.sqrt(2);

        const iframe = document.getElementById("iframe");
        if (!iframe) return;

        iframe.style.height = `${iFrameHeight}px`;
        iframe.style.width = `${iFrameWidth}px`;

        const printableBody = this._iFrameDoc.getElementById("printable-body");
        if (printableBody) {

            // workaround to use <style> tags to make sure the scaling is only applied to the screen version
            const styleElement = this._iFrameDoc.createElement('style');
            styleElement.textContent = `
            @media screen {
                #printable-body {
                    transform: scale(${this._calculateScale(iFrameHeight)});     /* necessary to display whole page in the iframe*/ 
                    transform-origin: top left;     /* necessary because scaling will always cause gaps */
                    margin-bottom: -100000px;       /* setting the margin high enough - it will detect the end of the last element automatically and is more reliable than estimated calculation */
                }
                body {
                    overflow-x: hidden;
                }
            }`;
            this._iFrameDoc.head.appendChild(styleElement);
        }

        if(!this._pagesCalculated){     // prevents that totalPages are calculated again and summed up if page is resized
            this._calculateTotalPages();
            this.handleScrollEvent()
        }

        this._loading = false;

        if (!this._loading && this._pagesCalculated) {
            const pageOverlayElement = document.getElementById("page-overlay");
            pageOverlayElement.setAttribute("style", `margin-top: ${this._iFrameDoc.scrollingElement.clientHeight - pageOverlayElement.getBoundingClientRect().height - 16}px;`)
        }
    }

    private _print() {
        const iframe = document.getElementById('iframe') as HTMLIFrameElement;
        iframe.contentWindow.print();
    }

    private _loadingChanged(){
        // Workaround for if.bind and show.bind
        // if.bind cannot be used because it removes the elements from the DOM, but we need them for calculation
        // show.bind does not remove elements from the DOM, but it adds a class "aurelia-hide" which sets "display: none".
        // This causes getBoundingClientRect() to return 0 and crash all calculations (https://stackoverflow.com/questions/4576295/getboundingclientrect-is-returning-zero-in-xul).
        // Aurelia documentation proposes overwriting the "aurelia-hide" class, but it is added as <style> to the head which makes simply overwriting the class useless.
        // For some reason the class adding approach will not work for the page overlay and in return removing style will not work for the iFrame.

        const iFrameElement = document.getElementById("iframe");
        const pageOverlayElement = document.getElementById("page-overlay")

        if(iFrameElement && pageOverlayElement){
            if(this._loading){
                iFrameElement.classList.add("hide")
                pageOverlayElement.setAttribute("style","visibility: hidden;")
            } else {
                iFrameElement.classList.remove("hide")
                pageOverlayElement.removeAttribute("style")
            }
        }
    }

    private _selectionChanged(item) {
        const iFrameElement = this._iFrameDoc.getElementById(item.id)

        if (!item.checked) {
            iFrameElement.setAttribute("style", "display: none;")
        } else {
            iFrameElement.removeAttribute("style");
        }

        this._totalPages = 1;
        this._pageArray = [0, 0]
        this._calculateTotalPages();
    }

    private _calculateScale(height: number): number {
        // reference values adapted by hand - calculation base for transformation
        const referenceHeight = 527.25;
        const referenceScale = 0.3125;

        // calculate scaling based on height
        return (referenceScale / referenceHeight) * height;
    }

    private _selectedFailureAspectFilterChanged(newValue: string, oldValue: string) {

        if (oldValue == "0" || oldValue == "1" || oldValue == "2") {
            const table = this._iFrameDoc.getElementById("failure-aspects-table");
            const tableMajor = this._iFrameDoc.getElementById("failure-aspects-table-major");
            const tableMinor = this._iFrameDoc.getElementById("failure-aspects-table-minor");

            const tables = {
                "0": table,
                "1": tableMajor,
                "2": tableMinor
            };

            this._setTableVisibility(tables, newValue);
        }
    }

    private _selectedTestFilterChanged(newValue: string, oldValue: string) {
        if (oldValue == "0" || oldValue == "1") {
            const table = this._iFrameDoc.getElementById("classes-table");
            const tableFailed = this._iFrameDoc.getElementById("classes-table-failed");

            const tables = {
                "0": table,
                "1": tableFailed
            };

            this._setTableVisibility(tables, newValue);
        }
    }

    private _setTableVisibility(tables: Object, newValue){
        Object.values(tables).forEach(tbl => tbl.setAttribute("style", "display: none;"));      // Set all tables to hidden

        if (tables[newValue]) {
            tables[newValue].removeAttribute("style");      // Remove style attribute from the selected table to show it
        }
        this._calculateTotalPages();
    }

    private _calculateTotalPages(){
        this._iFrameDoc.scrollingElement.scrollTop = 0;
        this._totalPages = 1;
        this._pageArray = [0,0];

        const a4inPixels = document.getElementById("iframe").getBoundingClientRect().height * 0.9125;   // multiplied by 0.9125 because the standard print uses borders (value estimated through testing)

        let pixels = 0;
        pixels += this._iFrameDoc.getElementById("print-card").getBoundingClientRect().bottom     // add space for header, headline and report information card

        this._pageArray[this._totalPages] += this._iFrameDoc.getElementById("print-card").getBoundingClientRect().bottom;

        this._checkboxOptions.forEach(option => {
            if(option.checked){
                pixels = pixels + this._iFrameDoc.getElementById(option.id).getBoundingClientRect().height;

                let pageElementCount = 0;

                if(pixels < a4inPixels){
                    this._pageArray[this._totalPages] += this._iFrameDoc.getElementById(option.id).getBoundingClientRect().height
                }

                while(pixels > a4inPixels){
                    this._totalPages++;
                    this._pageArray[this._totalPages] = this._pageArray[this._totalPages-1] + a4inPixels;

                    if(pageElementCount == 0){
                        pixels = this._iFrameDoc.getElementById(option.id).getBoundingClientRect().height;
                    } else {
                        pixels -= a4inPixels;
                    }
                    pageElementCount++;
                }

                // use the last page of this option and add only the actual content length instead of the whole a4 page to calculate active page while scrolling
                if(pageElementCount > 0){
                    this._pageArray[this._pageArray.length-1] = this._pageArray[this._pageArray.length-2] + this._iFrameDoc.getElementById(option.id).getBoundingClientRect().height % a4inPixels;
                }
            }
        })
        this._pagesCalculated = true;
    }

    handleScrollEvent() {
        const pageOffset = this._iFrameDoc.scrollingElement.clientHeight / 2;   // use pixel offset to not use top of iframe as page indicator

        this._page = this._pageArray.findIndex((value, index) =>
            this._iFrameDoc.scrollingElement.scrollTop + pageOffset >= value && (index === this._pageArray.length - 1
                || this._iFrameDoc.scrollingElement.scrollTop + pageOffset < this._pageArray[index + 1])) + 1

        if(Math.floor(this._iFrameDoc.scrollingElement.scrollTop) >= this._iFrameDoc.scrollingElement.scrollHeight - this._iFrameDoc.scrollingElement.clientHeight){    // if we scrolled down to the bottom, the value should be set to the maximum
            this._page = this._totalPages;
        }
        if(this._iFrameDoc.scrollingElement.scrollTop == 0){     // if we scroll back to the top the page should be set to 1 (even if the page indicator is technically already in the second page (see pageOffset))
            this._page = 1;
        }
    }
}

type ICheckBoxOption = {
    label: string,
    checked: boolean,
    id: string
}

export interface IFilter {
    id: number;
    name: string;
    tooltip?: string;
}
