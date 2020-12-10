import {bindable} from "aurelia-templating";
// @ts-ignore
import ApexCharts from 'apexcharts';
import ApexOptions = ApexCharts.ApexOptions;

export class ApexChart {
    private _apex: HTMLDivElement;
    private _myApexChart: ApexCharts;

    @bindable data: ApexOptions;
    @bindable selection: any;

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

    selectionChanged() {
        this._myApexChart.toggleDataPointSelection(this.selection.dataPointIndex);
    }

    private _createChart() {
        if (this.data && this._apex) {
            this._myApexChart = new ApexCharts(this._apex, this.data);
            this._myApexChart.render();
        }
    }
}
