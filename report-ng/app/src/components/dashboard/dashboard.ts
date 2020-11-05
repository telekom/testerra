import './dashboard.scss';
import {data} from "../../services/report-model";
import {autoinject} from "aurelia-framework";
import {EventAggregator} from 'aurelia-event-aggregator';
import {StatusConverter} from "services/status-converter";
import {StatisticsGenerator} from "services/statistics-generator";
import {ExecutionStatistics} from "services/statistic-models";
import IExecutionContext = data.IExecutionContext;

@autoinject()
export class Dashboard {
  _executionContext: IExecutionContext = undefined;
  private _executionStatistics: ExecutionStatistics;

  constructor(
    private _statusConverter: StatusConverter,
    private _statisticsGenerator: StatisticsGenerator,
    private _eventAggregator: EventAggregator
  ) {
  }

  attached() {
    this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
      this._executionStatistics = executionStatistics;
      console.log(executionStatistics);
      this._executionContext = executionStatistics.executionAggregate.executionContext;
      this._eventAggregator.publish('executionStatistics', this._executionStatistics);
      this._eventAggregator.publish('executionContext', this._executionContext);
    })

  };

}
