import {autoinject} from "aurelia-framework";
import {StatusConverter} from "services/status-converter";
import {StatisticsGenerator} from "services/statistics-generator";
import {ExecutionStatistics} from "services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import {data} from "../../services/report-model";
import ResultStatusType = data.ResultStatusType;

@autoinject()
export class Dashboard extends AbstractViewModel {
    private _executionStatistics: ExecutionStatistics;
    private _selectedResult: any = {status: ""};
    private _failedRetried = 0;
    private _passedRetried = 0;
    private _majorFailures = 0;
    private _minorFailures = 0;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }


    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            this._failedRetried = this._executionStatistics.getStatusCount(ResultStatusType.FAILED_RETRIED);
            this._passedRetried = this._executionStatistics.getStatusesCount([ResultStatusType.PASSED_RETRY,ResultStatusType.MINOR_RETRY]);

            this._executionStatistics.failureAspectStatistics.forEach(failureAspectStatistics => {
                if (failureAspectStatistics.isMinor) {
                    ++this._minorFailures;
                } else {
                    ++this._majorFailures
                }
            })

        });
    };

    private _resultClicked(result) {
        this._selectedResult = {status: result};
    }

    private _pieceClicked(ev:CustomEvent) {
        this.queryParams = ev.detail;
    }

    private _gotoTests(params:any) {
        this.navInstruction.router.navigateToRoute("tests", params);
    }
}
