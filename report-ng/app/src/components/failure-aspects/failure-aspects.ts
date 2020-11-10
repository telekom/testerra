import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {StatusConverter} from "../../services/status-converter";
import {FailureAspectStatistics} from "../../services/statistic-models";

@autoinject()
export class FailureAspects extends AbstractViewModel {

    private _filteredFailureAspects: FailureAspectStatistics[];
    private _searchQuery: string;
    private _searchRegexp: RegExp;

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter:StatusConverter
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
        const queryParams: any = {};
        if (this._searchQuery?.length > 0) {
            this._searchRegexp = new RegExp("(" + this._searchQuery + ")", "ig");
            queryParams.q = this._searchQuery;
        } else {
            this._searchRegexp = null;
        }

        this._filteredFailureAspects = [];
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            this._filteredFailureAspects = executionStatistics.failureAspectStatistics
                .filter(failureAspectStatistics => {
                    return (!this._searchRegexp || failureAspectStatistics.name.match(this._searchRegexp));
                });
        });

        this.updateUrl(queryParams);
    }
}
