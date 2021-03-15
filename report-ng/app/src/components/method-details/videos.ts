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
import {StatisticsGenerator} from "../../services/statistics-generator";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {data} from "../../services/report-model";
import IFile = data.IFile;
import {ObjectStorage} from "t-systems-aurelia-components/src/utils/object-storage";

interface IVideoInfo {
    time:number;
}

interface IVideo {
    element?:HTMLVideoElement,
    file:IFile;
}

@autoinject()
export class Videos {
    private _videos:IVideo[];
    private _videoStorage:{[key:string]:IVideoInfo};

    constructor(
        private _statistics: StatisticsGenerator,
        private _objectStorage:ObjectStorage
    ) {
        this._objectStorage.setStorage(localStorage);
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        this._statistics.getMethodDetails(params.methodId).then(methodDetails => {
            this._videoStorage = this._objectStorage.getItem("videos");
            if (!this._videoStorage) {
                this._videoStorage = {};
            }
            const videoFileIds = methodDetails.sessionContexts.filter(value => value.videoId).map(value => value.videoId);
            this._statistics.getFilesForIds(videoFileIds).then(videoFiles => {
                this._videos = videoFiles.map(file => <IVideo>{
                    file: file
                });

                window.setTimeout(() => {
                    this._setStartTimes();
                }, 100);
            });

        });
    }

    private _setStartTimes() {
        this._videos.forEach(video => {
           const videoInfo = this._videoStorage[video.file.id];
           if (videoInfo) {
               video.element.currentTime = videoInfo.time;
           }
        });
    }

    detached() {
        this._videos.forEach(video => {
            this._videoStorage[video.file.id] = {
                time: video.element.currentTime,
            };
        });
        this._objectStorage.setItem("videos", this._videoStorage);
    }
}
