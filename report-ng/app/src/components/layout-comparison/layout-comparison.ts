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
import {bindingMode} from "aurelia-binding";
import {Config} from "../../services/config-dev";
import {data} from "services/report-model";
import {StatisticsGenerator} from "../../services/statistics-generator";

export interface ICompareImages {
    actual: IImage,
    expected: IImage,
    diff: IImage
}

export interface IImage {
    src: string,
    id: string,
    title: string;
}

export interface ILayoutComparisonContext {
    image: string,
    mode: string,
    distance: number,
    actualScreenshotPath: string,
    distanceScreenshotPath: string,
    expectedScreenshotPath: string
}

@autoinject
export class LayoutComparison {
    // @bindable({defaultBindingMode:bindingMode.toView}) context:ILayoutComparisonContext;
    @bindable({defaultBindingMode: bindingMode.toView}) context: data.LayoutCheckContext;
    private _images: ICompareImages;

    constructor(
        private _dialogService: MdcDialogService,
        private _config: Config,
        private _statistics: StatisticsGenerator,
    ) {
    }

    bind() {
        this._prepareComparison();
    }

    private _loadImage(image: HTMLImageElement) {
        return new Promise(resolve => {
            image.addEventListener("load", () => {
                resolve(image);
            });
        })
    }

    private _prepareComparison() {
        this._images = {
            actual: {
                src: "",
                id: this.context.actualScreenshotId,
                title: "Actual"
            },
            diff: {
                src: "",
                id: this.context.distanceScreenshotId,
                title: "Difference"
            },
            expected: {
                src: "",
                id: this.context.expectedScreenshotId,
                title: "Expected"
            }
        }
        // Needed relative paths for <screenshot-comparison>, does not really work with <lazy-image>
        this._statistics.getFilesForIds([this._images.actual.id, this._images.diff.id, this._images.expected.id]).then(file => {
            this._images.actual.src = file[0].relativePath;
            this._images.diff.src = file[1].relativePath;
            this._images.expected.src = file[2].relativePath;
        });
    }

    private _imageClicked(image: IImage) {
        const left = image;
        let right: IImage;
        if (image == this._images.diff) {
            right = this._images.expected;
        } else {
            right = this._images.diff
        }

        this._dialogService.open({
            viewModel: ScreenshotComparison,
            model: <IComparison>{
                images: Object.values(this._images),
                left: left,
                right: right
            }
        });
    }
}
