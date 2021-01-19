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
import {autoinject, bindable} from "aurelia-framework";
import {IComparison, ScreenshotComparison} from "../screenshot-comparison/screenshot-comparison";
import {MdcDialogService} from '@aurelia-mdc-web/dialog';
import pixelmatch from 'pixelmatch';
import {bindingMode} from "aurelia-binding";
import {Config} from "../../services/config-dev";

export interface ICompareImages {
    actual:IImage,
    expected:IImage,
    diff:IImage
}

export interface IImage {
    src:string,
    title:string;
}

@autoinject
export class LayoutComparison {
    @bindable({bindingMode:bindingMode.toView}) context;
    private _images:ICompareImages;
    private _actualImageElement:HTMLImageElement;
    private _expectedImageElement:HTMLImageElement;

    constructor(
        private _dialogService: MdcDialogService,
        private _config:Config,
    ) {
    }

    bind() {
        this._prepareComparison();
    }

    private _loadImage(image:HTMLImageElement) {
        return new Promise(resolve => {
            image.addEventListener("load", () => {
                resolve(image);
            });
        })
    }

    private _prepareComparison() {
        this._images = {
            actual: {
                src: this._config.correctRelativePath('test-report/screenshots/' + this.context.actualScreenshot.filename),
                title: "Actual"
            },
            diff: {
                src: "",
                title: "Difference"
            },
            expected: {
                src: this._config.correctRelativePath('test-report/screenshots/' + this.context.expectedScreenshot.filename),
                title: "Expected"
            }
        }

        const allImagesLoaded = Promise.all([this._loadImage(this._actualImageElement), this._loadImage(this._expectedImageElement)]);
        allImagesLoaded.then(() => {
            const canvas: HTMLCanvasElement = document.createElement("canvas");
            const maxWidth = Math.max(this._actualImageElement.naturalWidth, this._expectedImageElement.naturalWidth);
            const maxHeight = Math.max(this._actualImageElement.naturalHeight, this._expectedImageElement.naturalHeight);

            //get Image data of actual screenshot via canvas
            canvas.width = maxWidth;
            canvas.height = maxHeight;
            let canvasContext = canvas.getContext("2d");
            canvasContext.drawImage(this._actualImageElement, 0, 0);
            const imgData1 = canvasContext.getImageData(0, 0, maxWidth, maxHeight);

            //get Image data of expected screenshot via canvas
            canvasContext = canvas.getContext("2d");
            canvasContext.clearRect(0, 0, canvas.width, canvas.height);
            canvasContext.drawImage(this._expectedImageElement, 0, 0);
            const imgData2 = canvasContext.getImageData(0, 0, maxWidth, maxHeight);
            const diff = canvasContext.createImageData(maxWidth, maxHeight);

            // @ts-ignore
            pixelmatch(imgData1.data, imgData2.data, diff.data, maxWidth, maxHeight, {threshold: 0.2, includeAA: true, alpha: 0.9, diffColor:[246, 168, 33]});

            canvasContext = canvas.getContext("2d");
            canvasContext.putImageData(diff, 0, 0);
            this._images.diff.src = canvas.toDataURL();
        });
    }

    private _imageClicked(image:IImage) {
        const left = image;
        let right:IImage;
        if (image == this._images.diff) {
            right = this._images.expected;
        } else {
            right = this._images.diff
        }

        this._dialogService.open({
            viewModel: ScreenshotComparison,
            model: <IComparison> {
                images: Object.values(this._images),
                left: left,
                right: right
            }
        });
    }
}
