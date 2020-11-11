import {autoinject} from "aurelia-framework";
import {bindable} from "aurelia-templating";

@autoinject
export class TestDurationCard {
    @bindable start: number;
    @bindable end: number;
    private _duration:number;
    private _hasEnded = false;

    startChanged() {
        console.log("start" , this.start);
        this._updateDuration();
    }

    endChanged() {
        console.log("end" , this.end);
        this._updateDuration();
    }

    private _updateDuration() {
        if (!this.end) {
            this._hasEnded = false;
            this.end = new Date().getDate();
        } else {
            this._hasEnded = true;
        }
        this._duration = this.end-this.start;
    }
}
