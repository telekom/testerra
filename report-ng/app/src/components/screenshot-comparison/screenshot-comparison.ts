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
    private _mouseMoveHandler: EventListenerObject;



    constructor(
        private _dialog: MdcDialog
    ) {

    }

    activate(params:IComparison) {
        this._comparison = params;
        console.log("activate", params)
    }

    attached() {
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
        this._prepareImageComparison(this._leftImageElement, this._rightImageElement);
        /*window.addEventListener('resize', () => {
            this._prepareImageComparison(this._leftImageElement, this._rightImageElement);
        });*/
    }

    private _prepareImageComparison(leftImg:HTMLDivElement, rightImg: HTMLDivElement){
        this._clicked = false;

        //Create slider
        this._slider = document.createElement("div");
        this._slider.setAttribute("class", "img-comp-slider secondary-bg");
        //Insert slider
        leftImg.parentElement.insertBefore(this._slider, leftImg);

        this._setImageSizes(this._leftImageElement, this._rightImageElement);

        /*window.addEventListener('resize', () => {
            console.log(window.innerWidth, window.innerHeight);
            if (leftImg.offsetWidth > (window.innerWidth * 0.8 + 56) || rightImg.offsetWidth > (window.innerWidth * 0.8 + 56) ) {
                console.log ("triggered resize listener");
                width = window.innerWidth * 0.8 + 56; //80vw
            }
        }) */

        //Function handlers for mouse
        this._slider.addEventListener("mousedown", this._slideReady.bind(this));
        window.addEventListener("mouseup", this._slideFinish.bind(this));
        //Function handlers for touchscreen devices
        this._slider.addEventListener("touchstart", this._slideReady.bind(this));
        window.addEventListener("touchend", this._slideFinish.bind(this));
    }

    private _setImageSizes(leftImg:HTMLDivElement, rightImg: HTMLDivElement) {
        let height;

        // Get the width and height of the img element
        if (leftImg.offsetWidth > window.innerWidth || rightImg.offsetWidth > window.innerWidth) {
            this._width = window.innerWidth * 0.8; //80vw
        }

        this._width = leftImg.offsetWidth;
        height = leftImg.offsetHeight;
        console.log(this._width, " x ", height)
        this._compContainer.style.height = height + "px";
        this._compContainer.style.width = (this._width + 20) + "px";

        //Set the width of the img element to 50%
        leftImg.style.width = this._width / 2 + "px";
        leftImg.style.zIndex = "2";

        //Position the slider in the middle
        this._slider.style.top = (height / 2) - (this._slider.offsetHeight / 2) + "px";
        this._slider.style.left = (this._width / 2) - (this._slider.offsetWidth / 2)+ "px";
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
        window.removeEventListener("mousemove", this._mouseMoveHandler);
        window.removeEventListener("touchmove", this._mouseMoveHandler);
    }

    public _slideMove(e) {
        console.log("slideMove");
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
        console.log("getCursorPos");
        let a, x = 0;
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
