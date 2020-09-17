import {bindable} from "aurelia-templating";
// @ts-ignore
import ApexCharts from 'apexcharts';
import ApexOptions = ApexCharts.ApexOptions;

export class ApexChart {
    private _apex: HTMLDivElement = undefined;
    private _myApexChart: ApexCharts = undefined;

    @bindable data: ApexOptions;

    constructor() {

    }

    attached() {
        this._createChart();
    }

    dataChanged(newData) {
        if (this._myApexChart) {
            this._myApexChart.updateOptions(newData);
        } else {
            this._createChart();
        }
    }

    private _createChart() {
        if (this.data && this._apex) {
            this._myApexChart = new ApexCharts(this._apex, this.data);
            this._myApexChart.render();
        }
    }
}
