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

import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../../abstract-view-model";
import * as echarts from "echarts";
import {ECharts, EChartsOption} from "echarts";

@autoinject()
export class PageTimingsTab extends AbstractViewModel {

    private _echart_page_timings: HTMLDivElement = undefined;
    private _myChart: ECharts = undefined;

    private _pageData = [
        [0, 1700, 1400, 1200, 300, 0],
        [2900, 1200, 300, 200, 900, 500],
        [1400, 2000, 1500, 1300, 500, 50],
        [10, 10, 10, 10, 10, 10]]
    private _pageLabels = ['Page Name', 'Page Name', 'Page Name', 'Page Name', 'Page Name', 'Page Name']
    private _componentData = [
        [0, 1700, 1400, 1200, 300, 0, 300, 400],
        [2900, 1200, 300, 200, 900, 500, {value: 300, itemStyle: {color: '#75C6CB'}}, {value: 400, itemStyle: {color: '#75C6CB'}}],
        [1400, 2000, 1500, 1300, 500, 50, 400, 500],
        [10, 10, 10, 10, 10, 10, 10, 10]]
    private _componentLabels = ['Page Name', 'Page Name', 'Page Name', 'Page Name', 'Page Name', 'Page Name', 'Component Name', 'Component Name']
    private _option: EChartsOption;
    private _attached = false;
    private _showComponents = false;

    constructor() {
        super();
    }

    attached() {
        this._attached = true;
        this._updateOption(this._pageLabels, this._pageData);
        if (this._option) {
            this._createChart();
        }
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
    }

    private _createChart() {
        this._myChart = echarts.init(this._echart_page_timings);
        this._option && this._myChart.setOption(this._option);
    }

    private _updateOption(labels, data) {
        this._option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                },
            },
            xAxis: {
                type: 'value'
            },
            yAxis: {
                type: 'category',
                data: labels,
            },
            series: [
                {
                    name: 'Placeholder Page Timing',
                    type: 'bar',
                    itemStyle: {
                        borderColor: 'transparent',
                        color: 'transparent'
                    },
                    stack: 'Total2',
                    data: data[0], //start position of the bars
                },
                {
                    name: 'Page Timing',
                    type: 'bar',
                    itemStyle: {
                        color: '#6897EA',
                    },
                    stack: 'Total2',
                    data: data[1], //length of the bars
                },
                {
                    name: 'Placeholder Average Value',
                    type: 'bar',
                    itemStyle: {
                        borderColor: 'transparent',
                        color: 'transparent'
                    },
                    stack: 'Total1',
                    data: data[2], //start position of the bars
                },
                {
                    name: 'Average Value',
                    type: 'bar',
                    stack: 'Total1',
                    itemStyle: {
                        color: '#4b4b4b',
                    },
                    data: data[3], //length of the bars
                    barGap: '-100%',
                },
            ]
        };
    }

    private _showComponentsChanged() {
        if (this._showComponents) {
            this._updateOption(this._componentLabels, this._componentData);
        } else {
            this._updateOption(this._pageLabels, this._pageData);
        }
        this._myChart.setOption(this._option, false);
    }
}
