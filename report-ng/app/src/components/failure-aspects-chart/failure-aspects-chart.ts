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
import "./failure-aspects-chart.scss"
import {StatusConverter} from "../../services/status-converter";
import {ResultStatusType} from "../../services/report-model/framework_pb";

@autoinject()
export class FailureAspectsChart extends AbstractViewModel {
    @observable() private _chart: ECharts;
    private _option: EChartsOption;
    @bindable failure_aspects_data: any[] = [];
    @bindable() onClick;
    private _highlightedData = undefined;
    private _opacityOfInactiveElements = 0.38;  // Default opacity of disabled elements https://m2.material.io/design/interaction/states.html#disabled

    constructor(
        private _statusConverter: StatusConverter
    ) {
        super();
        this._option = {};
    }

    async attached() {
        this._setChartOption();
        this._chart.on('click', (params) => {
            let selectedData = params.name;
            if (this._highlightedData === selectedData) {
                selectedData = undefined;
            }

            if (params.data && this.onClick) {
                this.onClick(selectedData);
                this._highlightData(selectedData);
                this._highlightedData = selectedData;
            }
        });
    };

    private _highlightData(failureAspect: string) {
        const inactiveOpacity = this._opacityOfInactiveElements;

        if (failureAspect === undefined) {
            this._option.series[0].data.forEach(function (bar) {
                bar.itemStyle.opacity = 1;
            });
        } else {
            this._option.series[0].data.forEach(function (bar) {
                if (bar.value[0] === failureAspect) {
                    bar.itemStyle.opacity = 1;
                } else {
                    bar.itemStyle.opacity = inactiveOpacity;
                }
            });
        }
        this._chart.setOption(this._option);
    }

    private _setChartOption() {
        const chartData: any[] = [];
        this.failure_aspects_data.forEach(failureAspect => {
            chartData.push({
                value: failureAspect,
                itemStyle: {
                    color: this._statusConverter.getColorForStatus(ResultStatusType.FAILED),
                    opacity: 1
                }
            });
        });

        this._option = {
            grid: {
                top: 30,
                bottom: 20,
                left: 50,
                right: 50
            },
            tooltip: {
                trigger: 'item',
                formatter: function (params) {
                    return `<div class="tooltip-content">${params.name}</div>
                        <br> (Occurences: ${params.value[1]})`
                }
            },
            xAxis: {
                type: 'category',
                axisLabel: {
                    formatter: function (value) {
                        const maxLength = 30;
                        if (value.length > maxLength) {
                            return value.substring(0, maxLength) + '...';
                        }
                        return value;
                    }
                }
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    data: chartData,
                    type: 'bar'
                }
            ]
        };
    }
}
