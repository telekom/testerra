import {StatusConverter} from "../services/status-converter";
import {autoinject} from "aurelia-framework";
import {data} from "../services/report-model";
import ResultStatusType = data.ResultStatusType;

@autoinject()
export class StatusIconNameValueConverter {
    constructor(
        private _statusConverter: StatusConverter
    ) {
    }

    toView(value: ResultStatusType) {
        return this._statusConverter.getIconNameForStatus(value);
    }
}
