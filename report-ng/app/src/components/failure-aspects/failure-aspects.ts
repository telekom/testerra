import {DataLoader} from "../../services/data-loader";
import {data} from "../../services/report-model";
import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import IContextClip = data.IContextClip;

@autoinject()
export class FailureAspects extends AbstractViewModel{

    private _failureAspects: IContextClip[];
    private _searchQuery:string;
    private _searchRegexp:RegExp;
    private _queryParams:any = {};

    constructor(
        private _dataLoader: DataLoader
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        if (params.q) {
            this._searchQuery = params.q;
        }
        this._filter();
    }

    private _filter() {
        if (this._searchQuery?.length > 0) {
            this._searchRegexp = new RegExp("(" + this._searchQuery + ")", "ig");
            this._queryParams.q = this._searchQuery;
        } else {
            this._searchRegexp = null;
            delete this._queryParams.q;
        }
        this._dataLoader.getExecutionAggregate().then(executionAggregate => {
            if (this._searchRegexp) {
                this._failureAspects = executionAggregate.executionContext.failureAscpects.filter(failureAspect => {
                    return failureAspect.key.match(this._searchRegexp);
                });
            } else {
                this._failureAspects = executionAggregate.executionContext.failureAscpects;
            }
        })

        this.updateUrl(this._queryParams);


    }
}
