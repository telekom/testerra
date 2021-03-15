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

    private _leftImageElement:HTMLDivElement;
    private _rightImageElement:HTMLDivElement;
    private _compContainer: HTMLDivElement;
    private _slider: HTMLDivElement;
    private _width: number;
    private _clicked: boolean;
    private _ratio: number = 0.5;
    private _mouseMoveHandler: EventListenerObject;
    private _resizeListener = () => {
        this._setImageSizes(this._leftImageElement, this._rightImageElement, this._ratio);
    };

    constructor(
        private _dialog: MdcDialog
    ) {

    }

    activate(params:IComparison) {
        this._comparison = params;
        this._updateCompareLists();
    }

    unbind() {
        window.removeEventListener('resize', this._resizeListener);
    }

    private _leftLoaded(event:Event) {
        this._prepareImageComparison();
    }

    private _updateCompareLists() {
        this._left = this._comparison.images.filter(value => value != this._comparison.right);
        this._right = this._comparison.images.filter(value => value != this._comparison.left);
    }

    private _leftChanged() {
        this._updateCompareLists();
    }

    private _rightChanged() {
        this._updateCompareLists();
    }

    private _prepareImageComparison(){
        if (this._slider) return;

        console.log("prepare image comparison");

        this._clicked = false;
        //Create slider
        this._slider = document.createElement("div");
        this._slider.setAttribute("class", "img-comp-slider secondary-bg");
        this._leftImageElement.parentElement.insertBefore(this._slider, this._leftImageElement);

        //adjust image sizes
        this._setImageSizes(this._leftImageElement, this._rightImageElement, this._ratio);
        window.addEventListener('resize', this._resizeListener);

        //Function handlers for mouse
        this._slider.addEventListener("mousedown", this._slideReady.bind(this));
        window.addEventListener("mouseup", this._slideFinish.bind(this));
        //Function handlers for touchscreen devices
        this._slider.addEventListener("touchstart", this._slideReady.bind(this));
        window.addEventListener("touchend", this._slideFinish.bind(this));
    }

    private _setImageSizes(leftImg:HTMLDivElement, rightImg: HTMLDivElement, sliderRatio: number) {
        let height;

        this._width = Math.max(leftImg.firstElementChild.clientWidth, rightImg.firstElementChild.clientWidth);
        height = Math.max(leftImg.firstElementChild.clientHeight, rightImg.firstElementChild.clientHeight);

        if (this._width > window.innerWidth * 0.8) {
            this._width = window.innerWidth * 0.8; //80vw
        }

        this._compContainer.style.height = height + "px";
        this._compContainer.style.width = (this._width + 20) + "px";

        //Set the width of the img element to ratio
        leftImg.style.width = this._width * sliderRatio + "px";
        leftImg.style.zIndex = "2";

        //Position the slider by ratio
        this._slider.style.top = (height / 2) - (this._slider.offsetHeight / 2) + "px";
        this._slider.style.left = (this._width * sliderRatio)  - (this._slider.offsetWidth / 2) + "px";
    }

    // Function handlers for slider and image resizes:
    private _slideReady(e) {
        e.preventDefault();
        this._clicked = true;
        /*
        * binding function to variable to allow removing it later since binding the this-context to the handler returns a new function everytime
        * @see: https://stackoverflow.com/questions/33859113/javascript-removeeventlistener-not-working-inside-a-class
        */
        this._mouseMoveHandler = this._slideMove.bind(this);
        window.addEventListener("mousemove", this._mouseMoveHandler);
        window.addEventListener("touchmove", this._mouseMoveHandler);
    }

    private _slideFinish() {
        //The slider is no longer clicked:
        this._clicked = false;
        //remember ratio for correct slider positioning
        this._ratio = this._leftImageElement.offsetWidth / this._width;
        window.removeEventListener("mousemove", this._mouseMoveHandler);
        window.removeEventListener("touchmove", this._mouseMoveHandler);
    }

    public _slideMove(e) {
        let position;

        if (this._clicked == false) return false;

        position = this._getCursorPos(e);
        //Prevent the slider from being positioned outside the image:
        if (position < 0) {
            position = 0;
        }

        if (position > this._width){
            position = this._width;
        }

        this._slide(position);
    }

    private _slide(x) {
        //Resize the image
        this._leftImageElement.style.width = x + "px";
        //Position the slider
        this._slider.style.left = this._leftImageElement.offsetWidth - (this._slider.offsetWidth / 2) + "px";
    }

    private _getCursorPos(e) {
        let a, x: number;
        e = e || window.event;
        //Get the imageElements positions of the image
        a = this._leftImageElement.getBoundingClientRect();
        //Calculate the cursor's imageElements coordinate, relative to the image
        x = e.pageX - a.left;
        //Consider any page scrolling
        x = x - window.pageXOffset;

        return x;
    }
}
