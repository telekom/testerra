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
import {autoinject} from 'aurelia-framework';
import {data} from "../../services/report-model";
import {MdcDialog} from '@aurelia-mdc-web/dialog';
import './screenshot-dialog.scss'
import {StatisticsGenerator} from "../../services/statistics-generator";
import ISessionContext = data.SessionContext;

export interface IScreenshotsDialogParams {
    screenshotIds:string[],
    current: data.File
}

@autoinject
export class ScreenshotsDialog {
    private _screenshots: data.File[];
    private _current: data.File;
    private _index = 0;
    private _sessionContext: ISessionContext;
    private _pageSourceFile: data.File;

    constructor(
        private _dialog: MdcDialog,
        private _statistics: StatisticsGenerator,
    ) {
        // this._dialog.listen('MDCDialog:opened', () => {
        //     console.log("dialog opened");
        //     if (document.activeElement instanceof HTMLElement){
        //         console.log("blur on open dialog");
        //         document.activeElement.blur();
        //     }
        // });
    }

    activate(params:IScreenshotsDialogParams) {
        this._showScreenshot(params.current);
        this._statistics.getFilesForIds(params.screenshotIds)
            .then(screenshots => {
                this._screenshots = screenshots;
                this._index = this._screenshots.indexOf(this._current);
            });
    }

    attached() {
        // if (document.activeElement instanceof HTMLElement){
        //     document.activeElement.blur();
        // }
    }

    private _showScreenshot(file: data.File) {
        this._current = file;

        const sessionKey = this._current.meta.SessionKey;
        if (sessionKey) {
            this._statistics.getExecutionStatistics().then(executionStatistics => {
                const sessionContexts = Object.values(executionStatistics.executionAggregate.sessionContexts);
                this._sessionContext = sessionContexts.find(value => value.contextValues.name === sessionKey);
            });
        }

        this._pageSourceFile = null;
        if (this._current.meta.sourcesRefId) {
            this._statistics.getFilesForIds([this._current.meta.sourcesRefId]).then(files => {
                this._pageSourceFile = files.find(value => true);
            })
        }

    }

    private _left() {
        this._index--;
        if (this._index < 0) {
            this._index = this._screenshots.length-1;
        }
        this._showScreenshot(this._screenshots[this._index]);
    }

    private _right() {
        this._index++;
        if (this._index >= this._screenshots.length) {
            this._index = 0;
        }
        this._showScreenshot(this._screenshots[this._index]);
    }

   /** https://stackoverflow.com/questions/27798126/how-to-open-the-newly-created-image-in-a-new-tab**/
    private _fullscreen(){
        const image = new Image();
        const newWindow = window.open('about:blank');
        image.src =  this._current.relativePath;
        newWindow.document.write(image.outerHTML);
        newWindow.document.title = this._current.meta.Title;
   }
}

