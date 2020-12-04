import {autoinject} from 'aurelia-framework';
import {StatisticsGenerator} from "../../services/statistics-generator";
import {NavigationInstruction, RouteConfig} from "aurelia-router";

@autoinject()
export class Videos {
    private _methodDetails;

    constructor(
        private _statistics: StatisticsGenerator,
    ) {
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        this._statistics.getMethodDetails(params.methodId).then(methodDetails => {
            this._methodDetails = methodDetails;
        });
    }
}
