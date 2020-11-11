import {autoinject} from "aurelia-framework";
import {StatusConverter} from "services/status-converter";
import {StatisticsGenerator} from "services/statistics-generator";
import {ExecutionStatistics} from "services/statistic-models";

@autoinject()
export class Dashboard {
    private _executionStatistics: ExecutionStatistics;
    private _filter: any = {status: ""};

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
    }

    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            console.log(executionStatistics);
        })

    };

    private _pieceClicked(ev:CustomEvent) {
        console.log("piece clicked", ev);
        this._filter = ev.detail
    }
}
