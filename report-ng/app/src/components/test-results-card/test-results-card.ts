import {data} from "../../services/report-model";
import {autoinject} from "aurelia-framework";
import "./test-results-card.css";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics} from "../../services/statistic-models";
import ApexOptions = ApexCharts.ApexOptions;
import IExecutionContext = data.IExecutionContext;
import ResultStatusType = data.ResultStatusType;



@autoinject
export class TestResultsCard {
  private _apexPieOptions: ApexOptions = undefined;

  _executionContext: IExecutionContext;
  private _executionStatistics: ExecutionStatistics;

  constructor(
    private _statusConverter: StatusConverter,
    private _statisticsGenerator: StatisticsGenerator) {

  }

  attached() {
    this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
      this._executionStatistics = executionStatistics;
      this._executionContext = executionStatistics.executionAggregate.executionContext;

      this._preparePieChart(this._executionStatistics);
    })
  }

  private _preparePieChart(executionStatistics: ExecutionStatistics): void {
    this._apexPieOptions = {
      chart: {
        type: 'pie',
        width: '400px',
        fontFamily: 'Roboto'
      },
      series: [executionStatistics.overallPassed, executionStatistics.overallFailed, executionStatistics.overallSkipped],
      labels: ["passed", "failed", "skipped"],
      colors:[this._statusConverter.colorFor(ResultStatusType.PASSED),
        this._statusConverter.colorFor(ResultStatusType.FAILED),
        this._statusConverter.colorFor(ResultStatusType.SKIPPED)]
    }
  }
}
