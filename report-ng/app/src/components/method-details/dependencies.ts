import {autoinject} from 'aurelia-framework';
import {IMethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import {FailureAspectStatistics} from "../../services/statistic-models";

@autoinject()
export class Dependencies {
    private _beforeMethods:IMethodDetails[];
    private _dependsOnMethods:IMethodDetails[];
    private _router:Router;

    constructor(
        private _statistics: StatisticsGenerator,
    ) {
    }

    private _generateRoute(methodDetails:IMethodDetails) {
        return this._router?.generate("method", {methodId:methodDetails.methodContext.contextValues.id})+"/dependencies";
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        this._router = navInstruction.router;
        this._statistics.getMethodDetails(params.methodId).then(methodDetails => {
            const addMethodDetails = (methodId:string, targetList:IMethodDetails[]) => {
                const methodContext = methodDetails.classStatistics.methodContexts.find(methodContext => methodContext.contextValues.id == methodId);
                if (methodContext) {
                    targetList.push({
                        methodContext: methodContext,
                        failureAspectStatistics: (methodContext.errorContext?new FailureAspectStatistics().setErrorContext(methodContext.errorContext):null)
                    });
                }
            }

            this._dependsOnMethods = [];
            methodDetails.methodContext.dependsOnMethodContextIds.forEach(methodId => {
                addMethodDetails(methodId, this._dependsOnMethods);
            })

            this._beforeMethods = [];
            methodDetails.methodContext.relatedMethodContextIds.forEach(methodId => {
                addMethodDetails(methodId, this._beforeMethods);
            });
        });
    }
}
