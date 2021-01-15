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
import {MdcDialog} from '@aurelia-mdc-web/dialog';
import './screenshot-comparison.scss';
import {IImage} from "../layout-comparison/layout-comparison";

export interface IComparison {
    images: IImage[];
    left: IImage;
    right: IImage
}

@autoinject
export class ScreenshotComparison {
    private _comparison: IComparison;
    private _left:IImage[];
    private _right:IImage[];
    private _leftImageElement:HTMLImageElement;
    private _rightImageElement:HTMLImageElement;

    constructor(
        private _dialog: MdcDialog
    ) {

    }

    activate(params:IComparison) {
        this._comparison = params;
        // this._srcActual = params.actual.src;
        // this._srcExpected = params.expected.src;
        // this._comparison = params.comparison.src;
        console.log("activate", params)
    }

    attached() {
        console.log("attached");
        this._initComparisons();
        this._updateCompareLists();
    }

    private _updateCompareLists() {
        this._left = this._comparison.images;
        this._right = this._comparison.images.filter(value => value != this._comparison.left);
    }

    private _leftChanged() {
        this._updateCompareLists();
    }

    private _rightChanged() {
        this._updateCompareLists();
    }

    private _initComparisons() {
        // var imageElements, i;
        // // /* Find all elements with an "overlay" class: */
        // imageElements = document.getElementsByClassName("img-comp-overlay");
        // for (i = 0; i < imageElements.length; i++) {
        //     /* Once for each "overlay" element:
        //     pass the "overlay" element as a parameter when executing the compareImages function: */
        //     compareImages(imageElements[i]);
        // }

        [this._leftImageElement, this._rightImageElement].forEach(value => compareImages(value))

        //compareImages([this._leftImageElement, this._rightImageElement])

        function compareImages(img:HTMLImageElement) {
            console.log(img);
            var slider, clicked = 0, width, height;
            /* Get the width and height of the img element */
            width = img.offsetWidth;
            height = img.offsetHeight;
            let container = document.querySelector(".img-comp-container") as HTMLDivElement;
            container.style.height = height + "px";
            container.style.width = (width + 20) + "px";

            /* Set the width of the img element to 50%: */
            img.style.width = width + "px";
            /* Create slider: */
            slider = document.createElement("div");
            slider.setAttribute("class", "img-comp-slider secondary-bg");
            /* Insert slider */
            img.parentElement.insertBefore(slider, img);
            /* Position the slider in the middle: */
            slider.style.top = (height / 2) - (slider.offsetHeight / 2) + "px";
            slider.style.left = width - slider.offsetWidth + "px";

            window.addEventListener('resize', () => {
                container.style.height = height + "px";
                container.style.width = (width + 20) + "px";
            })

            /* Execute a function when the mouse button is pressed: */
            slider.addEventListener("mousedown", slideReady);
            /* And another function when the mouse button is released: */
            window.addEventListener("mouseup", slideFinish);
            /* Or touched (for touch screens: */
            slider.addEventListener("touchstart", slideReady);
            /* And released (for touch screens: */
            window.addEventListener("touchend", slideFinish);

            function slideReady(e) {
                /* Prevent any other actions that may occur when moving over the image: */
                e.preventDefault();
                /* The slider is now clicked and ready to move: */
                clicked = 1;
                /* Execute a function when the slider is moved: */
                window.addEventListener("mousemove", slideMove);
                window.addEventListener("touchmove", slideMove);
            }
            function slideFinish() {
                /* The slider is no longer clicked: */
                clicked = 0;
            }
            function slideMove(e) {
                var pos;
                /* If the slider is no longer clicked, exit this function: */
                if (clicked == 0) return false;
                /* Get the cursor's imageElements position: */
                pos = getCursorPos(e)
                /* Prevent the slider from being positioned outside the image: */
                if (pos < 0) pos = 0;
                if (pos > width) pos = width;
                /* Execute a function that will resize the overlay image according to the cursor: */
                slide(pos);
            }
            function getCursorPos(e) {
                var a, x = 0;
                e = e || window.event;
                /* Get the imageElements positions of the image: */
                a = img.getBoundingClientRect();
                /* Calculate the cursor's imageElements coordinate, relative to the image: */
                x = e.pageX - a.left;
                /* Consider any page scrolling: */
                x = x - window.pageXOffset;
                return x;
            }
            function slide(x) {
                /* Resize the image: */
                img.style.width = x + "px";
                /* Position the slider: */
                slider.style.left = img.offsetWidth - (slider.offsetWidth / 2) + "px";
            }
        }
    }
}
