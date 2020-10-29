import {data} from "../../services/report-model";
import {autoinject} from "aurelia-framework";
import {EventAggregator} from 'aurelia-event-aggregator';
import "./test-results-card.css";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics} from "../../services/statistic-models";
import ApexOptions = ApexCharts.ApexOptions;
import ResultStatusType = data.ResultStatusType;

@autoinject
export class TestResultsCard {
  private _apexPieOptions: ApexOptions = undefined;
  private _executionStatistics;

  constructor(
    private _statusConverter: StatusConverter,
    private _statisticsGenerator: StatisticsGenerator,
    private _eventAggregator: EventAggregator
  ) {
  }

  attached() {
    this._eventAggregator.subscribe('executionStatistics', payload => {
      this._executionStatistics = payload;
      this._preparePieChart(payload);
    });
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
