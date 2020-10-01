// @ts-ignore
import {DataLoader} from "../services/data-loader";
import {data} from "../services/report-model";
import {autoinject} from "aurelia-framework";
import {StatusConverter} from "../services/status-converter";
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
    this._dataLoader.getExecutionAggregate().then(executionAggregate => {
      console.log(executionAggregate);
      executionAggregate.testContexts.forEach(testContext => {
        testContext.classContextIds.forEach(classContextId => {
          this._dataLoader.getClassContextAggregate(classContextId).then(classContextAggregate => {
            classContextAggregate.methodContexts.forEach(methodContext => {
              const status = this._statusConverter.groupStatisticStatus(methodContext.contextValues.resultStatus);
              switch(status) {
                case ResultStatusType.PASSED: this._testsPassed++; break;
                case ResultStatusType.FAILED: this._testsFailed++; break;
                case ResultStatusType.SKIPPED: this._testsSkipped++; break;
              }
            });
          });
        })
      });
    });
    setTimeout(() => { this._prepareDonutChart(); }, 1000);
  }

  private _prepareDonutChart(): void {
    this._apexDonutOptions = {
      chart: {
        type: 'donut'
      },
      series: [this._testsPassed, this._testsFailed, this._testsSkipped],
      labels: ["passed", "failed", "skipped"]
    }
  }


}
