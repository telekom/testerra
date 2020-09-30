// @ts-ignore
import {DataLoader} from "../services/data-loader";
import {data} from "../services/report-model";
import {autoinject} from "aurelia-framework";
import IExecutionContext = data.IExecutionContext;

@autoinject()
export class Dashboard {
  _chartData : object;
  _executionContext:IExecutionContext;

  constructor(
    private _dataLoader:DataLoader
  ) {
  }
  attached() {

    this._dataLoader.getExecutionAggregate().then(executionAggregate => {
      this._executionContext = executionAggregate.executionContext;
    })

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

    //let chart = new ApexCharts(document.querySelector('#chart'), options);
    //chart.render();
  }
}
