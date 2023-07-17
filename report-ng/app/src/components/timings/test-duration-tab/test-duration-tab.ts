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
import * as echarts from 'echarts';
import {ECharts, EChartsOption} from 'echarts';
import "./test-duration-tab.scss";
import {ExecutionStatistics} from "services/statistic-models";
import {StatisticsGenerator} from "services/statistics-generator";


@autoinject()
export class TestDurationTab extends AbstractViewModel {

    private _echart_test_duration: HTMLDivElement = undefined;
    private _myChart: ECharts = undefined;
    private _executionStatistics: ExecutionStatistics
    private _option: EChartsOption = {
        xAxis: {
            type: 'category',
            data: ['0s', '2s', '4s', '6s', '8s', '10s', '12s']
        },
        yAxis: {
            type: 'value',
        },
        series: [
            {
                data: [12, 2, 1, 1, 0, 1, 2],
                type: 'bar',
                itemStyle: {
                    color: '#6897EA',
                }
            }
        ]
    };
    private _attached = false;

    constructor(
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();

    }

    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
        })
        this._attached = true;
        if (this._option) {
            this._createChart();
        }
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
    }

    private _createChart() {
        this._myChart = echarts.init(this._echart_test_duration);
        this._option && this._myChart.setOption(this._option)
    }
}
