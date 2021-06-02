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

import {autoinject, Container} from 'aurelia-framework';
import {StatisticsGenerator} from "../../services/statistics-generator";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {data} from "../../services/report-model";
import IFile = data.IFile;
import {ObjectStorage} from "t-systems-aurelia-components/src/utils/object-storage";
import ISessionContext = data.ISessionContext;

interface IVideoInfo {
    time:number;
}

interface IVideo {
    element?:HTMLVideoElement,
    file:IFile;
}

class VideoSessionContext {
    video: IVideo;
    constructor(
        readonly sessionContext:ISessionContext,
    ) {
        const statisticsGenerator = Container.instance.get(StatisticsGenerator);
        const videoStorage = Container.instance.get(VideoStorage);
        if (sessionContext.videoId) {
            statisticsGenerator.getFilesForIds([sessionContext.videoId]).then(videoFiles => {
                this.video = <IVideo>{
                    file: videoFiles[0]
                }

                // Set the start time of the video
                if (this.video.element) {
                    const videoInfo = videoStorage.getVideoInfo(this.video.file.id);
                    if (videoInfo) {
                        this.video.element.currentTime = videoInfo.time;
                    }
                }
            });
        }

    }
}

@autoinject()
class VideoStorage {
    private _videoInfos:{[key:string]:IVideoInfo};

    constructor(
        private _objectStorage:ObjectStorage
    ) {
        this._objectStorage.setStorage(localStorage);
        this._videoInfos = this._objectStorage.getItem("videos");
        if (!this._videoInfos) {
            this._videoInfos = {};
        }
    }

    getVideoInfo(videoFileId:string) {
        return this._videoInfos[videoFileId];
    }

    persist(videoSessionContexts:VideoSessionContext[]) {
        videoSessionContexts
            .filter(videoSessionContexts => videoSessionContexts.video)
            .forEach(videoSessionContext => {
                this._videoInfos[videoSessionContext.video.file.id] = {
                    time: videoSessionContext.video.element.currentTime,
                };
        });
        this._objectStorage.setItem("videos", this._videoInfos);
    }
}

@autoinject()
export class Sessions {
    private _videoSessionContexts:VideoSessionContext[];

    constructor(
        private _statistics: StatisticsGenerator,
        private _videoStorage: VideoStorage,
    ) {
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        this._videoSessionContexts = [];
        this._statistics.getMethodDetails(params.methodId).then(methodDetails => {
            methodDetails.sessionContexts.forEach(sessionContext => {
                this._videoSessionContexts.push(new VideoSessionContext(sessionContext))
            })
        });
    }

    detached() {
        this._videoStorage.persist(this._videoSessionContexts);
    }
}
