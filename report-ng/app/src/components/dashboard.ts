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
    this._prepareStatisticsData().then(value => {
      this._testsPassed = value[ResultStatusType.PASSED];
      this._testsFailed = value[ResultStatusType.FAILED];
      this._testsSkipped = value[ResultStatusType.SKIPPED];
      this._prepareDonutChart(value);
    });
  }

  private _prepareStatisticsData() {
    return this._dataLoader.getExecutionAggregate()
      .then(executionAggregate => {
        const overallStatistics = {}
        const loadingPromises = [];
        executionAggregate.testContexts.forEach(testContext => {
          testContext.classContextIds.forEach(classContextId => {
            const loadingPromise = this._dataLoader.getClassContextAggregate(classContextId).then(classContextAggregate => {
              classContextAggregate.methodContexts.forEach(methodContext => {
                const status = this._statusConverter.groupStatisticStatus(methodContext.contextValues.resultStatus);
                if (!overallStatistics[status]) {
                  overallStatistics[status] = 1;
                } else {
                  overallStatistics[status]++;
                }
              });
            });

            loadingPromises.push(loadingPromise);
          })
        });

        return Promise.all(loadingPromises).then(() => {
          return Promise.resolve(overallStatistics);
        })
      });
  }

  private _prepareDonutChart(value): void {
    this._apexDonutOptions = {
      chart: {
        type: 'donut',
        width: '400px'
      },
      series: [value[ResultStatusType.PASSED], value[ResultStatusType.FAILED], value[ResultStatusType.SKIPPED]],
      labels: ["passed", "failed", "skipped"]
    }
  }


}
