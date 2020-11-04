import {data} from "../../services/report-model";
import './classes.scss'
import {DataLoader} from "../../services/data-loader";
import {StatusConverter} from "../../services/status-converter";
import {autoinject} from "aurelia-framework";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ClassStatistics} from "../../services/statistic-models";

@autoinject()
export class Classes {

  private _classStatistics: ClassStatistics[] = [];
  constructor(
    private _dataLoader: DataLoader,
    private _statusConverter: StatusConverter,
    private _statisticsGenerator: StatisticsGenerator,
  ) {
  }
  attached() {
    this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
      executionStatistics.classStatistics.forEach(classStatistics => {
        console.log(classStatistics);
        this._prepareClasses(this._classStatistics.push(classStatistics));
        //classStatistics.classAggregate.methodContexts.forEach(methodContext=> {
        // this._methods.errorFingerprint.push(methodContext);
        // })
      })
    });
  }

  private _prepareClasses(classStatistics) {
  }

}

