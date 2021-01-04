/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
