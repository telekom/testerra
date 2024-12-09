/*
 * Testerra
 *
 * (C) 2024, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {autoinject, bindable, observable} from "aurelia-framework";
import {AbstractViewModel} from "../abstract-view-model";
import {ECharts, EChartsOption} from "echarts";
import "./status-share-chart.scss"

@autoinject()
export class StatusShareChart extends AbstractViewModel {
    @observable() private _chart: ECharts;
    private _option: EChartsOption;
    @bindable status_data: any[] = [];

    constructor() {
        super();
        this._option = {};
    }

    attached() {
        this._setChartOption();
    };

    private _setChartOption() {
        this._option = {
            grid: {
                top: 0,
                bottom: 0,
                left: 0,
                right: 0,
            },
            tooltip: {
                formatter: function (params) {
                    return '<div class="header" style="background-color: ' +
                        params.color + ';"> ' + params.data.statusName + ': ' + params.value + '</div>'
                }
            },
            legend: {
                show: false
            },
            series: [
                {
                    name: 'Status Share',
                    type: 'pie',
                    radius: ['40%', '130%'],
                    center: ['50%', '90%'],
                    startAngle: 180,
                    endAngle: 360,
                    data: this.status_data,
                    cursor: 'default',
                    label: {
                        show: true,
                        silent: true,
                        position: 'inner',
                        color: '#ffffff',
                        formatter: (params) => {
                            const percentage = params.percent;
                            return `${percentage.toFixed(1)}%`;
                        }
                    },
                    labelLine: {
                        length: 10,
                        length2: 10,
                        lineStyle: {
                            color: '#000000',
                            width: 1
                        }
                    }
                }
            ]
        };
    }
}
