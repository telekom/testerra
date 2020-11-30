import {autoinject} from "aurelia-framework";
import {StatusConverter} from "services/status-converter";
import {StatisticsGenerator} from "services/statistics-generator";
import {ExecutionStatistics} from "services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";

@autoinject()
export class Dashboard extends AbstractViewModel {
    private _executionStatistics: ExecutionStatistics;
    private _selectedResult: any = {status: ""};
    private _failedRetried = 0;
    private _passedRetried = 0;

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
            this._failedRetried = this._executionStatistics.getStatusCount(9);
            this._passedRetried = this._executionStatistics.getStatusesCount([11,12]);
        })

    };

    private _resultClicked(result) {
        this._selectedResult= {status: result};
    }

    private _pieceClicked(ev:CustomEvent) {
        this.queryParams = ev.detail;
    }

    private _gotoTests(params:any) {
        this.navInstruction.router.navigateToRoute("tests", params);
        console.log(params);
    }
}
