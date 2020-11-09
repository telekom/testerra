import {data} from "services/report-model";
import {autoinject} from "aurelia-framework";
import moment, {Duration} from "moment";
import {StatusConverter} from "services/status-converter";
import {StatisticsGenerator} from "services/statistics-generator";

@autoinject
export class TestDurationCard {
  private _testDuration: Duration = moment.duration(0);
  private _hasFinished: boolean = true;

  constructor(
    private _statusConverter: StatusConverter,
    private _statisticsGenerator: StatisticsGenerator,
  ) {
  }

  attached() {
      this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
          this._prepareDuration(executionStatistics.executionAggregate.executionContext.contextValues);
      })
  }

  private _prepareDuration(contextValues: data.IContextValues): void {
    if (contextValues.endTime == null) {
      this._hasFinished = false;
    } else {
      this._testDuration = moment.duration(<number>contextValues.endTime - <number>contextValues.startTime);
    }
  }
}
