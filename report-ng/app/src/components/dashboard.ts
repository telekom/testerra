// @ts-ignore
import {DataLoader} from "../services/data-loader";
import {data} from "../services/report-model";
import {autoinject} from "aurelia-framework";
import {StatusConverter} from "../services/status-converter";
import IExecutionContext = data.IExecutionContext;
import ResultStatusType = data.ResultStatusType;

@autoinject()
export class Dashboard {
  _chartData : object;
  _executionContext:IExecutionContext;
  _testsPassed:number = 0;

  constructor(
    private _dataLoader:DataLoader,
    private _statusConverter:StatusConverter
  ) {
  }
  attached() {
    this._dataLoader.getExecutionAggregate().then(executionAggregate => {
      console.log(executionAggregate);
      executionAggregate.testContexts.forEach(testContext => {
        testContext.classContextIds.forEach(classContextId => {
          this._dataLoader.getClassContextAggregate(classContextId).then(classContextAggregate => {
            console.log(classContextAggregate);

            classContextAggregate.methodContexts.forEach(methodContext => {
              const status = this._statusConverter.groupStatisticStatus(methodContext.contextValues.resultStatus);
              if (status == ResultStatusType.PASSED) {
                this._testsPassed++;
              }
            });
          });
        })
      });
    });

    this._chartData = {
      chart: {
        type: 'bar'
      },
      series: [
        {
          name: 'sales',
          data: [30, 40, 35, 50, 49, 60, 70, 91, 125]
        }
      ],
      xaxis: {
        categories: [1991, 1992, 1993, 1994, 1995, 1996, 1997, 1998, 1999]
      }
    }
  }
}
