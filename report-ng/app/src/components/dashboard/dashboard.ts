import {autoinject} from "aurelia-framework";
import {StatusConverter} from "services/status-converter";
import {StatisticsGenerator} from "services/statistics-generator";
import {ExecutionStatistics} from "services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";

@autoinject()
export class Dashboard extends AbstractViewModel {
    private _executionStatistics: ExecutionStatistics;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }


    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            console.log(executionStatistics);
        })

    };

    private _pieceClicked(ev:CustomEvent) {
        console.log("piece clicked", ev);
        this.queryParams = ev.detail
    }

    private _gotoTests(params:any) {
        this.navInstruction.router.navigateToRoute("tests", params);
    }
}
