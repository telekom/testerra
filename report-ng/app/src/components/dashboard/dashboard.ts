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
import ContextValues = data.ContextValues;


@autoinject()
export class Dashboard {
  private _apexPieOptions: any = undefined;
  private _apexBarOptions: any = undefined;
  private _testDuration: Duration = moment.duration(0);
  private _hasFinished: boolean = true;

  _executionContext: IExecutionContext;
  private _executionStatistics: ExecutionStatistics;
  //private _classStatistics:ClassStatistics[] = [];

  constructor(
    private _statusConverter: StatusConverter,
    private _statisticsGenerator: StatisticsGenerator,
  ) {
  }

  attached() {
    this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
      this._executionStatistics = executionStatistics;
      this._executionContext = executionStatistics.executionAggregate.executionContext;

      this._prepareDuration(this._executionContext.contextValues)
      this._preparePieChart(this._executionStatistics);
      this._prepareHorizontalBarChart(this._executionStatistics.classStatistics);
    })
  }

  private _prepareDuration(contextValues: data.IContextValues): void {
    if (contextValues.endTime == null) {
      this._hasFinished = false;
    } else {
      this._testDuration = moment.duration(<number>contextValues.endTime - <number>contextValues.startTime);
    }
  }

  private _preparePieChart(executionStatistics: ExecutionStatistics): void {
    this._apexPieOptions = {
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

  private _prepareHorizontalBarChart(classStatistics): void{
    let data: Map<string, Array<number>> = new Map();
    let xlabels: Array<string> = [];
    let displayedStatuses: Array<string> = ["passed", "failed", "skipped"];

    //Set statuses we need for the chart
    displayedStatuses.forEach (status => {
      data.set(status, []);
    })

    //Iterate through classStatistics array to fill map with data for series
    console.log(classStatistics);
    classStatistics.forEach(classStats => {
      data.get("passed").push(classStats.overallPassed);
      data.get("failed").push(classStats.overallFailed);

      if (classStats.overallSkipped){
        data.get("skipped").push(classStats.overallSkipped);
      } else {
        data.get("skipped").push(0);
      }

      //Push Class Names in array for x-axis labels
      xlabels.push(classStats.classAggregate.classContext.fullClassName);
    });

    //Display at least 10 rows in bar chart even if there are less classes
    if (xlabels.length < 10) {
      for (let i = xlabels.length; i <= 10; i++) {
        xlabels[i] = "";
        displayedStatuses.forEach(status => {
          data.get(status)[i] = 0;
        })
      }
    }


    this._apexBarOptions = {
      chart: {
        type: 'bar',
        stacked:true
      },
      series: [{
        name: "Passed",
        data: data.get("passed")
      } , {
        name: "Failed",
        data: data.get("failed")
      } , {
        name: "Skipped",
        data: data.get("skipped")
      }
      ],
      colors:[
        this._statusConverter.colorFor(ResultStatusType.PASSED),
        this._statusConverter.colorFor(ResultStatusType.FAILED),
        this._statusConverter.colorFor(ResultStatusType.SKIPPED)
      ],
      xaxis: {
        categories: xlabels
      },
      yaxis: {
        title: {
          text: undefined
        },
      },
      tooltip: {
        y: {
          formatter: function (val) {
            return val
          }
        }
      },
      plotOptions:{
        bar: {
          horizontal: true,
        }
      },
      noData: {
        text: "There is no data available at the moment. Please be patient!"
      },
      fill: {
        opacity: 1,
      },
      dataLabels:{
        enable:false,
      },
      legend: {
        position: 'top',
        horizontalAlign: 'left',
        offsetX: 40
      }
    }
  }
}
