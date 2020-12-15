import {autoinject} from "aurelia-framework";
import {bindable} from "aurelia-templating";
import './test-duration-card.scss';

@autoinject
export class TestDurationCard {
    @bindable start: number;
    @bindable end: number;
    private _duration:number;
    private _hasEnded = false;

    startChanged() {
        this._updateDuration();
    }

    endChanged() {
        this._updateDuration();
    }

    private _updateDuration() {
        if (!this.end) {
            this._hasEnded = false;
            this.end = new Date().getMilliseconds();
        } else {
            this._hasEnded = true;
        }
        this._duration = this.end-this.start;
    }
}
