// @ts-ignore
import './dashboard.scss';
import {data} from "../../services/report-model";
import {autoinject} from "aurelia-framework";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics} from "../../services/statistic-models";
import IExecutionContext = data.IExecutionContext;
import moment from "moment";

@autoinject()
export class Dashboard {
  private _apexDonutOptions: any = undefined;
  // dur =moment.duration(5000);
  // dur =moment.duration(1,"day");
//dur= moment.duration(2,"day").add(1,"week").humanize(); //9day
//dur= moment.duration(2,"day").subtract(1,"week").humanize(); //5days
  //private dur =moment().format('MMMM Do YYYY, h:mm:ss a');

  private _executionContext:IExecutionContext;
  private _executionStatistics:ExecutionStatistics;
  private _startDate;
  private _endDate;
  private _duration;

  constructor(
    private _statusConverter:StatusConverter,
    private _statisticsGenerator:StatisticsGenerator,
  ) {
  }

  attached() {

    this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
      this._executionStatistics = executionStatistics;
      this._executionContext = executionStatistics.executionAggregate.executionContext;
      console.log(executionStatistics);
      this._prepareDonutChart(executionStatistics);

      //duration
      this._startDate= this._executionContext.contextValues.startTime;
      this._endDate= this._executionContext.contextValues.endTime;
      this._duration=moment(this._endDate).from(this._startDate);
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



