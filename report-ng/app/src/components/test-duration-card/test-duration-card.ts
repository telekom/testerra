import './test-duration-card.scss';
import {data} from "../../services/report-model";
import {autoinject} from "aurelia-framework";
import {EventAggregator} from 'aurelia-event-aggregator';
import moment, {Duration} from "moment";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";

@autoinject
export class TestDurationCard {
  private _testDuration: Duration = moment.duration(0);
  private _hasFinished: boolean = true;

  constructor(
    private _statusConverter: StatusConverter,
    private _statisticsGenerator: StatisticsGenerator,
    private _eventAggregator: EventAggregator
  ) {
  }

  attached() {
    this._eventAggregator.subscribe('executionContext', payload => {
      this._prepareDuration(payload.contextValues);
    });
  }

  private _prepareDuration(contextValues: data.IContextValues): void {
    if (contextValues.endTime == null) {
      this._hasFinished = false;
    } else {
      this._testDuration = moment.duration(<number>contextValues.endTime - <number>contextValues.startTime);
    }
  }
}
