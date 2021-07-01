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
import {ObjectStorage} from "t-systems-aurelia-components/src/utils/object-storage";

interface IVideoInfo {
    time:number;
}

@autoinject()
export class LazyVideo {

    @bindable({defaultBindingMode: bindingMode.toView})
    fileId:string;

    // @bindable({bindingMode: bindingMode.toView})
    // class:string;

    private _videoElement:HTMLVideoElement;

    constructor(
        private _statistics: StatisticsGenerator,
        private _element: Element,
        private _objectStorage: ObjectStorage
    ) {
    }

    attached() {
        this._statistics.getFilesForIds([this.fileId]).then(file => {
            const videoInfo = this._objectStorage.getItem(`video-${this.fileId}`);
            this._videoElement.src = file[0].relativePath;
            if (videoInfo) {
                this._videoElement.currentTime = videoInfo.time;
            }
        });
    }

    detached() {
        const videoInfo:IVideoInfo = {
            time: this._videoElement.currentTime
        }
        this._objectStorage.setItem(`video-${this.fileId}`, videoInfo);
    }
}
