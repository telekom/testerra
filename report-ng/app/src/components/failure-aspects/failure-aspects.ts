import {DataLoader} from "../../services/data-loader";
import {data} from "../../services/report-model";
import {autoinject} from 'aurelia-framework';
import IContextClip = data.IContextClip;

@autoinject()
export class FailureAspects {

    private _failureAspects: IContextClip[];

    constructor(
        private _dataLoader: DataLoader
    ) {
        this._dataLoader.getExecutionAggregate().then(value => {
            this._failureAspects = value.executionContext.failureAscpects;
            this._failureAspects[0].methodContextIds.length
        })
    }
}
