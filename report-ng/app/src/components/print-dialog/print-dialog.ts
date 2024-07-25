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
    private _loading = true;
    private _failureAspectFilters: IFailureAspectFilter[] = [
        {id: 0, name: "All"},
        {id: 1, name: "Major"},
        {id: 2, name: "Minor"}
    ];
    @observable private _selectedFailureAspectFilter = 0;

    private readonly _checkboxOptions: ICheckBoxOption[] = [
        {label: 'Test Classes Chart', checked: true, id: "test-classes-card"},
        {label: 'Failure Aspects Table', checked: true, id: "failure-aspects-card"},
        {label: 'Test Case List', checked: true, id: "classes-table-card"}
    ];

    constructor(private _dialog: MdcDialog) {
    }

    activate(params: { title: string; iFrameSrc: string }) {
        this._title = params.title;
        this._iFrameSrc = params.iFrameSrc;
    }

    attached() {
        const iframe = document.getElementById('iframe') as HTMLIFrameElement;
        iframe.onload = () => {
            this._iFrameDoc = iframe.contentDocument || iframe.contentWindow.document;

            setTimeout(() => {
                this._resizeFrame();
            }, 50);

            window.addEventListener('resize', this._resizeFrame.bind(this));

            this._loading = false;
        };
    }

    private _resizeFrame() {
        const dialog = document.getElementById("print-dialog");
        if (!dialog || !this._iFrameDoc) return;

        const iFrameHeight = window.innerHeight / 100 * 80;
        const iFrameWidth = iFrameHeight / Math.sqrt(2);

        const iframe = document.getElementById("iframe");
        if (!iframe) return;

        iframe.style.height = `${iFrameHeight}px`;
        iframe.style.width = `${iFrameWidth}px`;

        const {scale, marginBottom} = this._calculateScaleAndMargin(iFrameHeight);

        const printableBody = this._iFrameDoc.getElementById("printable-body");
        if (printableBody) {

            // workaround to use <style> tags to make sure the scaling is only applied to the screen version
            // scaling necessary to display whole page in the iframe; origin-transform necessary because scaling will always cause gaps
            const styleElement = this._iFrameDoc.createElement('style');
            styleElement.textContent = `
            @media screen {
                #printable-body {
                    transform: scale(${scale});     
                    transform-origin: top left;
                    margin-bottom: ${marginBottom}px;
                }
                body {
                    overflow-x: hidden;
                }
            }`;
            this._iFrameDoc.head.appendChild(styleElement);
        }
    }

    private _print() {
        const iframe = document.getElementById('iframe') as HTMLIFrameElement;
        iframe.contentWindow.print();
    }

    private _selectionChanged(item) {
        const iFrameElement = this._iFrameDoc.getElementById(item.id)

        if (!item.checked) {
            iFrameElement.setAttribute("style", "display: none;")
        } else {
            iFrameElement.removeAttribute("style");
        }
    }

    // function to calculate scaling and margin-bottom
    private _calculateScaleAndMargin(height: number): { scale: number, marginBottom: number } {

        // reference values adapted by hand - calculation base for transformation
        const referenceHeight = 527.25;
        const referenceScale = 0.3;
        const referenceMarginBottom = -5500;

        // calculate scaling and margin-bottom based on height
        const scale = (referenceScale / referenceHeight) * height;
        const marginBottom = (referenceMarginBottom / referenceHeight) * height;

        return {scale, marginBottom};
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

            Object.values(tables).forEach(tbl => tbl.setAttribute("style", "display: none;"));      // Set all tables to hidden

            if (tables[newValue]) {
                tables[newValue].removeAttribute("style");      // Remove style attribute from the selected table to show it
            }
        }
    }
}

type ICheckBoxOption = {
    label: string,
    checked: boolean,
    id: string
}

export interface IFailureAspectFilter {
    id: number;
    name: string;
}
