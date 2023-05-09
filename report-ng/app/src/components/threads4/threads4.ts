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

import {autoinject} from "aurelia-framework";
import {IFilter, StatusConverter} from "services/status-converter";
import {StatisticsGenerator} from "services/statistics-generator";
import {ExecutionStatistics} from "services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import "./threads4.scss"
import * as echarts from 'echarts';
import {ECBasicOption} from "echarts/types/dist/shared";
import {NavigationInstruction, RouteConfig} from "aurelia-router";


@autoinject()
export class Threads4 extends AbstractViewModel {
    private _executionStatistics: ExecutionStatistics;
    private _filter: IFilter;
    private _loading = true;

    private _container: HTMLDivElement;
    options: ECBasicOption;
    class:string;

    chart: echarts.ECharts|null;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
        this._fillOutOptions();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        if (this.queryParams.status) {
            this._filter = {
                status: this._statusConverter.getStatusForClass(this.queryParams.status)
            }
        }
    }

    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            this._loading = false;
            console.log(this._executionStatistics.getUpmostStatus());
            if (this.options) {
                this._createChart();
            }
            window.addEventListener("resize", this.onResize);
        });
    };

    detached() {
        if (this.chart) {
            this.chart.dispose();
            this.chart = null;
        }
        window.removeEventListener("resize", this.onResize);
    }

    optionsChanged(newOptions:ECBasicOption) {
        if (this.chart) {
            this.chart.setOption(newOptions, true);
        } else {
            this._createChart();
        }
    }

    private _fillOutOptions() {
        this.options = {
            xAxis: {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    data: [150, 230, 224, 218, 135, 147, 260],
                    type: 'line'
                }
            ]
        };
    }

    private readonly onResize= () => {
        this.chart?.resize();
    }

    private _createChart() {
        if (!this._container) {
            return;
        }
        console.log("Chart created ",  this.options);
        this.chart = echarts.init(this._container);
        this.chart.setOption(this.options);
    }
}

