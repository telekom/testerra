import './test-classes-card.scss';
import {data} from "../../services/report-model";
import {autoinject, bindable} from "aurelia-framework";
import {EventAggregator} from 'aurelia-event-aggregator';
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import ResultStatusType = data.ResultStatusType;

@autoinject
export class TestClassesCard {
  private _apexBarOptions: any = undefined;


  constructor(
    private _statusConverter: StatusConverter,
    private _statisticsGenerator: StatisticsGenerator,
    private _eventAggregator: EventAggregator
  ) {
  }

  attached() {
    this._eventAggregator.subscribe('executionStatistics', payload => {
      this._prepareHorizontalBarChart(payload.classStatistics);
    });
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
        fontFamily: 'Roboto',
        stacked:true,
        toolbar: {
          show: false,
        }
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
      grid: {
        show: false,
      },
      plotOptions:{
        bar: {
          horizontal: true,
          barHeight: '75%',
        }
      },
      noData: {
        text: "There is no data available at the moment. Please be patient!"
      },
      dataLabels:{
        enable:false,
      },
      legend: {
        position: 'top',
        horizontalAlign: 'center'
      }
    }
  }
}
