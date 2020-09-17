// @ts-ignore
import ApexCharts from 'apexcharts'

export class Dashboard {
  message: string;
  _chartData : object;
  constructor() {
    this.message = 'This is the home URL and therefore loads the Dashboard component. More routes are implemented, take a look around. The old testing site can be found in >testing';


  }
  attached() {
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
