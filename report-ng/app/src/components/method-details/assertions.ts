import {autoinject} from 'aurelia-framework';
import {StatisticsGenerator} from "../../services/statistics-generator";
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import {data} from "../../services/report-model";
import IErrorContext = data.IErrorContext;

@autoinject()
export class Assertions {
    private _optionalAssertions:IErrorContext[];
    private _collectedAssertions:IErrorContext[];

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
            this._collectedAssertions = [];
            this._optionalAssertions = [];
            methodDetails.methodContext.testSteps
                .flatMap(value => value.actions)
                .flatMap(value => value.entries)
                .filter(value => value.assertion)
                .map(value => value.assertion)
                .forEach(value => {
                    if (value.optional) this._optionalAssertions.push(value);
                    else this._collectedAssertions.push(value);
                })
        });
    }
}
