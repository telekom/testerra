import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {StatusConverter} from "../../services/status-converter";
import {FailureAspectStatistics} from "../../services/statistic-models";

@autoinject()
export class Threads extends AbstractViewModel {
    private _searchRegexp: RegExp;
    private _loading = false;

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter:StatusConverter
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._filter();
    }

    private _filter() {
        if (this.queryParams?.q?.trim().length > 0) {
            this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this.queryParams.q);
        } else {
            this._searchRegexp = null;
        }

        this._loading = true;
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            executionStatistics.executionAggregate.methodContexts.forEach(value => {
                console.log(value.threadName, value.contextValues.startTime, value.contextValues.endTime);
            })
            this._loading = false;
            this.updateUrl(this.queryParams);
        });
    }
}
