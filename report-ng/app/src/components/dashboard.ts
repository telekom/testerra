// @ts-ignore
import {DataLoader} from "../services/data-loader";
import {data} from "../services/report-model";
import {autoinject} from "aurelia-framework";
import {StatusConverter} from "../services/status-converter";
import 'apexcharts'
import IExecutionContext = data.IExecutionContext;
import ResultStatusType = data.ResultStatusType;

@autoinject()
export class Dashboard {
  private _apexDonutOptions: any = undefined;
  private _testsPassed: number = 0;
  private _testsFailed: number = 0;
  private _testsSkipped: number = 0;

  _executionContext:IExecutionContext;

  constructor(
    private _dataLoader:DataLoader,
    private _statusConverter:StatusConverter
  ) {
  }


  attached() {
    this._prepareDonutChart();

    this._dataLoader.getExecutionAggregate().then(executionAggregate => {
      console.log(executionAggregate);
      executionAggregate.testContexts.forEach(testContext => {
        testContext.classContextIds.forEach(classContextId => {
          this._dataLoader.getClassContextAggregate(classContextId).then(classContextAggregate => {
            classContextAggregate.methodContexts.forEach(methodContext => {
              const status = this._statusConverter.groupStatisticStatus(methodContext.contextValues.resultStatus);
              //console.log("status:" + status + "result status" + resultStatus);
              if (status == ResultStatusType.PASSED) {
                this._testsPassed++;
              }
            });
          });
        })
      });
    });
    console.log(this._testsPassed);
  }

  private _prepareData(resultStatus: ResultStatusType): number {
    let testsInState: number = 0;

    this._dataLoader.getExecutionAggregate().then(executionAggregate => {
      console.log(executionAggregate);
      executionAggregate.testContexts.forEach(testContext => {
        testContext.classContextIds.forEach(classContextId => {
          this._dataLoader.getClassContextAggregate(classContextId).then(classContextAggregate => {
            classContextAggregate.methodContexts.forEach(methodContext => {
              const status = this._statusConverter.groupStatisticStatus(methodContext.contextValues.resultStatus);
              //console.log("status:" + status + "result status" + resultStatus);
              if (status == resultStatus) {
                testsInState++;
              }
            });
          });
        })
      });
    });
    console.log(testsInState);
    return testsInState;
  }

  private _prepareDonutChart(): void {
    let options: any = undefined;

    options = {
      chart: {
        type: 'donut'
      },
      series: [10,10,20],
      labels: ["passed", "failed", "skipped"]
    }

    this._apexDonutOptions = options;
  }


}
