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
    private _highlightedData: string = undefined;
    private _opacityOfInactiveElements = 0.38;  // Default opacity of disabled elements https://m2.material.io/design/interaction/states.html#disabled
    private _maxXAxisLabelLength = 28;          // Maximum shown length of x-axis label
    private _maxErrorMessageLength = 400;

    constructor(
        private _statusConverter: StatusConverter
    ) {
        super();
        this._option = {};
    }

    attached() {
        if (this.failure_aspects_data.length > 0) {
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
        }
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

    private _truncateErrorMessage(errorMessage: string): string {
        if (errorMessage.length <= this._maxErrorMessageLength) {
            return errorMessage;
        }
        return errorMessage.slice(0, this._maxErrorMessageLength - 3) + '...';
    }

    private _setChartOption() {
        const maxTextLength = this._maxXAxisLabelLength;
        const chartData: any[] = [];
        const self = this;
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
                    return `<div class="tooltip-content">${self._truncateErrorMessage(params.name)}</div>
                        <br> Occurrences: ${params.value[1]}`
                }
            },
            xAxis: {
                type: 'category',
                axisLabel: {
                    formatter: function (value) {
                        if (value.length > maxTextLength) {
                            return value.substring(0, maxTextLength) + '...';
                        }
                        return value;
                    }
                }
            },
            yAxis: {
                type: 'value',
                minInterval: 1
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
