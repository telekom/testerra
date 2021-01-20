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
import {autoinject, PLATFORM, useView} from 'aurelia-framework';
import {data} from "../../services/report-model";
import {MdcDialog} from '@aurelia-mdc-web/dialog';
import './screenshot-dialog.scss'
import IFile = data.IFile;

@autoinject
export class ScreenshotsDialog {
    private _screenshots:IFile[];
    private _current:IFile;
    private _index = 0;
    private _dialog: MdcDialog;
    constructor(

    ) {

    }

    activate(params:any) {
        this._screenshots = params.screenshots;
        this._current = params.current;
        this._index = this._screenshots.indexOf(this._current);
    }

    attached(){
        this._dialog.listen('MDCDialog:opened', () => {
            if (document.activeElement instanceof HTMLElement){
                document.activeElement.blur()
            }
        });
    }

    private _showScreenshot(file:IFile) {
        this._current = file;
    }

    private _left() {
        this._index--;
        if (this._index < 0) {
            this._index = this._screenshots.length-1;
        }
        this._current = this._screenshots[this._index];
    }

    private _right() {
        this._index++;
        if (this._index >= this._screenshots.length) {
            this._index = 0;
        }
        this._current = this._screenshots[this._index];
    }

   /** https://stackoverflow.com/questions/27798126/how-to-open-the-newly-created-image-in-a-new-tab**/
    private _fullscreen(){
        let image = new Image();
        let w = window.open('about:blank');
        image.src =  this._current.relativePath;

           w.document.write(image.outerHTML);
           w.document.title = this._current.meta.Title;
   }
}

