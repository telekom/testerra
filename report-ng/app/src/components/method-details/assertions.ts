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
                .flatMap(testStep => testStep.testStepActions)
                .forEach(testStepAction => {
                    this._collectedAssertions = this._collectedAssertions.concat(testStepAction.collectedAssertions);
                    this._optionalAssertions = this._optionalAssertions.concat(testStepAction.optionalAssertions);
                })
        });
    }
}
