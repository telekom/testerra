import {StatusConverter} from "../services/status-converter";
import {autoinject} from "aurelia-framework";

@autoinject()
export class StatusClassValueConverter {
    constructor(
        private _statusConverter: StatusConverter
    ) {
    }

    toView(value: string|number) {
        return this._statusConverter.getClassForStatus(value);
    }
}
