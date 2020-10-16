// @ts-ignore
import './dashboard.scss';
import {data} from "../../services/report-model";
import {autoinject} from "aurelia-framework";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ClassStatistics, ExecutionStatistics} from "../../services/statistic-models";
import moment, {Duration} from 'moment';
import IExecutionContext = data.IExecutionContext;
import ResultStatusType = data.ResultStatusType;


@autoinject()
export class Dashboard {
  private _apexDonutOptions: any = undefined;
  private _apexBarOptions: any = undefined;
  private _testDuration: Duration = moment.duration(0);

  _executionContext: IExecutionContext;
  private _executionStatistics: ExecutionStatistics;
  private _classStatistics:ClassStatistics[] = [];

  constructor(
    private _statusConverter: StatusConverter,
    private _statisticsGenerator: StatisticsGenerator,
  ) {
  }

  attached() {
    this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
      this._executionStatistics = executionStatistics;
      this._executionContext = executionStatistics.executionAggregate.executionContext;
      this._testDuration = moment.duration(<number>this._executionContext.contextValues.endTime - <number>this._executionContext.contextValues.startTime);
      console.log(this._testDuration);
      this._prepareDonutChart(executionStatistics);
      this._prepareHorizontalBarChart();
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
      colors:[this._statusConverter.colorFor(ResultStatusType.PASSED),
              this._statusConverter.colorFor(ResultStatusType.FAILED),
              this._statusConverter.colorFor(ResultStatusType.SKIPPED)]
    }
  }

  private _prepareHorizontalBarChart():void{
    this._apexBarOptions={
      chart: {
        type: 'bar',
        stacked:true,
      },
      series: [{
        name: 'Marine Sprite',
        data: [44, 55, 41, 37, 22, 43, 21]
      }, {
        name: 'Striking Calf',
        data: [53, 32, 33, 52, 13, 43, 32]
      }, {
        name: 'Tank Picture',
        data: [12, 17, 11, 9, 15, 11, 20]
      }],
      xaxis: {
        categories: [2008, 2009, 2010, 2011, 2012, 2013, 2014],
        labels: {
          formatter: function (val) {
            return val + "K"
          }
        }
      },
      yaxis: {
        title: {
          text: undefined
        },
      },
      tooltip: {
        y: {
          formatter: function (val) {
            return val + "K"
          }
        }
      },
      plotOptions:{
        bar: {
          horizontal: true,
        }
      },
      fill: {
        opacity: 1,
      },
      dataLabels:{
        enable:false,
      },
      title: {
        text: 'Test Classes',
        align: 'center',
        margin: 20,
        offsetY: 20,
        style:{
          fontSize: '20px',
        }
      },
      legend: {
        position: 'top',
        horizontalAlign: 'left',
        offsetX: 40
      }
    }
  }
}
