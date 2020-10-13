// @ts-ignore
import './dashboard.scss';
import {data} from "../../services/report-model";
import {autoinject} from "aurelia-framework";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics} from "../../services/statistic-models";
import moment from 'moment';
import 'moment-duration-format';
import IExecutionContext = data.IExecutionContext;

@autoinject()
export class Dashboard {
  private _apexDonutOptions: any = undefined;
  private _testDuration: string = undefined;


  _executionContext: IExecutionContext;
  private _executionStatistics: ExecutionStatistics;

  constructor(
    private _statusConverter: StatusConverter,
    private _statisticsGenerator: StatisticsGenerator,
  ) {
  }

  attached() {

    this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
      this._executionStatistics = executionStatistics;
      this._executionContext = executionStatistics.executionAggregate.executionContext;
      this._testDuration = moment.duration(<number>this._executionContext.contextValues.endTime - <number>this._executionContext.contextValues.startTime).format();
      console.log(this._executionContext);
      this._prepareDonutChart(executionStatistics);
    })
  }

  private _prepareDonutChart(executionStatistics:ExecutionStatistics): void {
    this._apexDonutOptions = {
      chart: {
        type: 'donut',
        width: '400px'
      },
      series: [executionStatistics.overallPassed, executionStatistics.overallFailed, executionStatistics.overallSkipped],
      labels: ["passed", "failed", "skipped"],
      colors:['#1abc9c', '#f1556c', '#495561']
    }
  }
}



