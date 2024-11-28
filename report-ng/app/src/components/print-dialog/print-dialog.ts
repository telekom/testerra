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
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics} from "../../services/statistic-models";
import {
    IntlDateFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter";

@autoinject()
export class PrintDialog {
    // reference values adapted by hand - calculation base for transformation
    private static readonly REFERENCE_HEIGHT = 527.25;
    private static readonly REFERENCE_SCALE = 0.3125;
    private static readonly A4_SCALE_VALUE = 0.9125;    // value estimated througth testing

    private _title: string;
    private _iFrameSrc: string;
    private _iFrameDoc: Document | undefined;
    @observable private _loading = true;
    private _page = 1;
    private _totalPages = 1;
    private _pagesCalculated = false;   // necessary otherwise total pages will be added up if window is resized
    private _pageArray = [0, 0];
    private _executionStatistics: ExecutionStatistics;

    private readonly _checkboxOptions: ICheckBoxOption[] = [        // Note: id is always the html-id that can be checked in printable.html. It is used to display or hide the components.
        {label: 'Test Classes Table', checked: true, id: "test-classes-table"},
        {
            label: 'Failure Aspects Table', checked: true, id: "failure-aspects-card",
            filter: [
                {id: "failure-aspects-table", name: "All", tooltip: "Displays all Failure Aspects"},
                {id: "failure-aspects-table-major", name: "Major", tooltip: "Failure aspect affects end status"},
                {id: "failure-aspects-table-minor", name: "Minor", tooltip: "Failure aspect does not affect end status"}
            ]
        },
        {
            label: 'Test Case List', checked: true, id: "classes-table-card",
            filter: [
                {
                    id: "classes-table",
                    name: "All",
                    tooltip: "Included status: Passed, Failed, Expected Failed, Skipped"
                },
                {
                    id: "classes-table-failed",
                    name: "Failed",
                    tooltip: "Included status: Failed, Expected Failed, Skipped"
                },
            ]
        }
    ];

    private _dynamicFilter = {      // Needs to be used to bind the selected filter to the radio button. Default values are the first filter value per CheckboxOption.
        'failure-aspects-card': this._checkboxOptions[1].filter[0],
        'classes-table-card': this._checkboxOptions[2].filter[0]
    };

    constructor(
        private _dialog: MdcDialog,
        private _statisticsGenerator: StatisticsGenerator,
        private readonly _dateFormatter: IntlDateFormatValueConverter,
    ) {
    }

    activate(params: { title: string; iFrameSrc: string }) {
        this._title = params.title;
        this._iFrameSrc = params.iFrameSrc;
    }

    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
        })

        const iframe = document.getElementById('iframe') as HTMLIFrameElement;
        iframe.onload = () => {
            this._iFrameDoc = iframe.contentDocument || iframe.contentWindow.document;
            this._iFrameDoc.title = "Test report " + this._title;    // sets header in browser print preview
            this._iFrameDoc.addEventListener('scroll', this.handleScrollEvent.bind(this))

            setTimeout(() => {      // make sure iFrame is fully loaded
                this._resizeFrame();
            }, 50);

            window.addEventListener('resize', this._resizeFrame.bind(this));

            this._iFrameDoc.getElementById("header").style.pointerEvents = "none";
        };
    }

    detached() {
        window.removeEventListener('resize', this._resizeFrame.bind(this));
        this._iFrameDoc?.removeEventListener('scroll', this.handleScrollEvent.bind(this));
    }

    handleScrollEvent() {
        const pageOffset = this._iFrameDoc.scrollingElement.clientHeight / 2;   // use pixel offset to not use top of iframe as page indicator

        // Find the current page based on scroll position and page boundaries
        this._page = this._pageArray.findIndex((value, index) =>
            this._iFrameDoc.scrollingElement.scrollTop + pageOffset >= value && (index === this._pageArray.length - 1
                || this._iFrameDoc.scrollingElement.scrollTop + pageOffset < this._pageArray[index + 1])) + 1

        // Set page to last page if scrolled to the bottom
        if (Math.floor(this._iFrameDoc.scrollingElement.scrollTop) >= this._iFrameDoc.scrollingElement.scrollHeight - this._iFrameDoc.scrollingElement.clientHeight) {
            this._page = this._totalPages;
        }

        // Set page to 1 if scrolled to the top (even though page indicator could be already on page 2 - see page offset)
        if (this._iFrameDoc.scrollingElement.scrollTop == 0) {
            this._page = 1;
        }
    }

    private _resizeFrame() {
        const dialog = document.getElementById("print-dialog");
        if (!dialog || !this._iFrameDoc) return;

        const iFrameHeight = window.innerHeight / 100 * 90;     // calculate new iFrame size based on window size
        const iFrameWidth = iFrameHeight / Math.sqrt(2);        // A4 format

        const iframe = document.getElementById("iframe");
        if (!iframe) return;

        iframe.style.height = `${iFrameHeight}px`;
        iframe.style.width = `${iFrameWidth}px`;

        const printableBody = this._iFrameDoc.getElementById("printable-body");
        if (printableBody) {

            // workaround to use <style> tags to make sure the scaling is only applied to the preview and not the actual website
            const styleElement = this._iFrameDoc.createElement('style');
            styleElement.textContent = `
            @media screen {
                #printable-body {
                    transform: scale(${(PrintDialog.REFERENCE_SCALE / PrintDialog.REFERENCE_HEIGHT) * iFrameHeight});     /* scale content inside iFrame to fit new iFrame size */ 
                    transform-origin: top left;     /* necessary because scaling will always cause gaps */
                    margin-bottom: -100000px;       /* setting the margin high enough - it will detect the end of the last element automatically and is more reliable than estimated calculation */
                }
                body {
                    overflow-x: hidden;
                }
            }
            a:-webkit-any-link {            /* overwrites styling that indicate links/routes */
                color: black;
                text-decoration: none;
            }
            .badge {
                color: white !important;    /* overwrites styling specifically for badges */
            }`;
            this._iFrameDoc.head.appendChild(styleElement);
        }

        if (!this._pagesCalculated) {     // prevents that totalPages are calculated again and summed up if page is resized
            this._calculateTotalPages();
            this.handleScrollEvent()
        }

        this._loading = false;

        // sets new position of page overlay after resizing
        if (!this._loading && this._pagesCalculated) {
            const pageOverlayElement = document.getElementById("page-overlay");
            pageOverlayElement.style.marginTop = `${this._iFrameDoc.scrollingElement.clientHeight - pageOverlayElement.getBoundingClientRect().height - 16}px`;
        }
    }

    private _print() {
        const iframe = document.getElementById('iframe') as HTMLIFrameElement;
        const executionContext = this._executionStatistics.executionAggregate.executionContext;
        const date = this._dateFormatter.toView(executionContext.contextValues.startTime, "print").replaceAll(',', '_').replaceAll(':', '-').replaceAll('/', '-').replaceAll(' ', '')
        document.title = "Testreport_" + this._title + "_" + executionContext.runConfig.runcfg + "_" + date;       // modifies the file title that is proposed by the browser if we want to save the file as a PDF
        iframe.contentWindow.print();
    }

    private _loadingChanged() {
        // Workaround for if.bind and show.bind
        // if.bind cannot be used because it removes the elements from the DOM, but we need them for calculation
        // show.bind does not remove elements from the DOM, but it adds a class "aurelia-hide" which sets "display: none".
        // This causes getBoundingClientRect() to return 0 and crash all calculations (https://stackoverflow.com/questions/4576295/getboundingclientrect-is-returning-zero-in-xul).
        // Aurelia documentation proposes overwriting the "aurelia-hide" class, but it is added as <style> to the head which makes simply overwriting the class useless.
        // For some reason the class adding approach will not work for the page overlay and in return removing style will not work for the iFrame.

        const iFrameElement = document.getElementById("iframe");
        const pageOverlayElement = document.getElementById("page-overlay")

        if (iFrameElement && pageOverlayElement) {
            iFrameElement.classList.toggle("hide", this._loading);
            pageOverlayElement.style.visibility = this._loading ? "hidden" : "";
        }
    }

    private _checkboxSelectionChanged(option) {
        const iFrameElement = this._iFrameDoc.getElementById(option.id)
        iFrameElement.style.display = option.checked ? "" : "none";

        this._calculateTotalPages();
    }

    private _radioSelectionChanged(option: ICheckBoxOption, filter: IFilter) {
        option.filter.forEach(optionFilter => {
            const table = this._iFrameDoc.getElementById(optionFilter.id);
            table.style.display = "none"
        });      // Set all tables to hidden

        this._iFrameDoc.getElementById(filter.id).style.display = "";   // set only this table visible

        this._calculateTotalPages();
    }

    private _calculateTotalPages() {
        // Reset scroll position and initialize page count
        this._iFrameDoc.scrollingElement.scrollTop = 0;
        this._totalPages = 1;
        this._pageArray = [0, 0];

        // Calculate the height of an A4 page in pixels, considering margins/borders in browser print version
        const a4inPixels = document.getElementById("iframe").getBoundingClientRect().height * PrintDialog.A4_SCALE_VALUE;

        // Initialize pixel tracker and add space for header, headline and report information card
        let pixels = this._iFrameDoc.getElementById("print-card").getBoundingClientRect().bottom
        this._pageArray[this._totalPages] += this._iFrameDoc.getElementById("print-card").getBoundingClientRect().bottom;

        // Iterate through each checkbox option to calculate content height and check if they fill multiple pages
        this._checkboxOptions.forEach(option => {
            if (option.checked) {
                const elementHeight = this._iFrameDoc.getElementById(option.id).getBoundingClientRect().height;
                pixels += elementHeight;

                let pageElementCount = 0;

                // Check if the current content fits on the current page
                if (pixels < a4inPixels) {
                    this._pageArray[this._totalPages] += this._iFrameDoc.getElementById(option.id).getBoundingClientRect().height
                }

                // If content exceeds one page, split across multiple pages
                while (pixels > a4inPixels) {
                    this._totalPages++;
                    this._pageArray[this._totalPages] = this._pageArray[this._totalPages - 1] + a4inPixels;

                    if (pageElementCount == 0) {
                        pixels = elementHeight;     // Reset pixels to the current element's height
                    } else {
                        pixels -= a4inPixels;
                    }
                    pageElementCount++;
                }

                // Add remaining height to the last page if split across multiple pages
                if (pageElementCount > 0) {
                    this._pageArray[this._pageArray.length - 1] = this._pageArray[this._pageArray.length - 2] + this._iFrameDoc.getElementById(option.id).getBoundingClientRect().height % a4inPixels;
                }
            }
        })
        this._pagesCalculated = true;
    }
}

type ICheckBoxOption = {
    label: string,
    checked: boolean,
    id: string,
    filter?: IFilter[]
}

interface IFilter {
    id: string;
    name: string;
    tooltip?: string;
}
