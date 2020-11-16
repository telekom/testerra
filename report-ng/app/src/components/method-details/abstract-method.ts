import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import {AbstractViewModel} from "../abstract-view-model";
import {ClassStatistics, ExecutionStatistics} from "../../services/statistic-models";
import IMethodContext = data.IMethodContext;

@autoinject()
export class AbstractMethod extends AbstractViewModel {

    private _methodContext:IMethodContext;
    private _executionStatistic:ExecutionStatistics;
    private _classStatistic:ClassStatistics;
    private _loading = false;

    constructor(
        private _statistics: StatisticsGenerator,
    ) {
        super();
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        super.activate(params, routeConfig, navInstruction);
        this._loadMethodContext(this.queryParams.id);
    }

    private _loadMethodContext(methodId:string) {
        this._loading = true;
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            for (const classStatistic of executionStatistics.classStatistics) {
                this._methodContext = classStatistic.classAggregate.methodContexts
                    .find(methodContext => methodContext.contextValues.id == methodId);

                if (this._methodContext) {
                    this._executionStatistic = executionStatistics;
                    this._classStatistic = classStatistic;
                    console.log(this._methodContext);
                    this.loaded();
                    break;
                }
            }
            this._loading = false;
        });
    }

    protected loaded() {
    }

    get loading() {
        return this._loading;
    }

    get methodContext() {
        return this._methodContext;
    }

    get executionStatistic() {
        return this._executionStatistic;
    }

    get classStatistic() {
        return this._classStatistic;
    }
}
