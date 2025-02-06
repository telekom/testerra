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
import {
    IntlDateFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter";
import {
    DurationFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/duration-format-value-converter";
import {MethodHistoryStatistics} from "../../services/statistic-models";
import {ECharts, EChartsOption} from "echarts";
import {StatusConverter} from "../../services/status-converter";
import {Container} from "aurelia-dependency-injection";
import "./method-history-chart.scss"
import {ResultStatusType} from "../../services/report-model/framework_pb";

@autoinject()
export class MethodHistoryChart extends AbstractViewModel {
    @observable() private _chart: ECharts;
    @bindable method_history_statistics: MethodHistoryStatistics;
    private _option: EChartsOption;
    private _data: any[] = [];
    private _lineStart: number[] = [];
    private _lineEnd: number[] = [];
    private _initialChartLoading = true;        // To prevent the access of _option in the first call of sharedDataChanged()
    private _opacityOfInactiveElements = 0.38;  // Default opacity of disabled elements https://m2.material.io/design/interaction/states.html#disabled
    private _chartSymbolSize = 20;
    private _maxErrorMessageLength = 400;
    @bindable() sharedData;

    constructor(
        private _statusConverter: StatusConverter,
        private _dateFormatter: IntlDateFormatValueConverter,
        private _durationFormatter: DurationFormatValueConverter
    ) {
        super();
        this._option = {};
    }

    async attached() {
        this._initDurationFormatter();
        this._prepareChartData();
        this._setChartOption();
        this._initialChartLoading = false;
    };

    private _initDurationFormatter() {
        const container = new Container();
        this._durationFormatter = container.get(DurationFormatValueConverter);
        this._durationFormatter.setDefaultFormat("h[h] m[min] s[s] S[ms]");
    }

    sharedDataChanged(failureAspect: string) {
        const inactiveOpacity = this._opacityOfInactiveElements;

        if (failureAspect) {
            this._option.series[0].data.forEach(function (methodRunObject: any) {
                if (methodRunObject.errorMessage.toString().includes(failureAspect)) {
                    methodRunObject.itemStyle.opacity = 1;
                } else {
                    methodRunObject.itemStyle.opacity = inactiveOpacity;
                }
            });
        } else {
            if (this._initialChartLoading) {
                return;
            }
            this._option.series[0].data.forEach(function (methodRunObject: any) {
                methodRunObject.itemStyle.opacity = 1;
            });
        }
        this._chart.setOption(this._option);
    }

    private _prepareChartData() {
        const style = new Map<number, string>();
        style.set(ResultStatusType.PASSED, this._statusConverter.getColorForStatus(ResultStatusType.PASSED));
        style.set(ResultStatusType.REPAIRED, this._statusConverter.getColorForStatus(ResultStatusType.REPAIRED));
        style.set(ResultStatusType.PASSED_RETRY, this._statusConverter.getColorForStatus(ResultStatusType.PASSED_RETRY));
        style.set(ResultStatusType.SKIPPED, this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED));
        style.set(ResultStatusType.FAILED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED));
        style.set(ResultStatusType.FAILED_EXPECTED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED));
        style.set(ResultStatusType.FAILED_MINOR, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_MINOR));
        style.set(ResultStatusType.FAILED_RETRIED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_RETRIED));

        this.method_history_statistics.runs.forEach(run => {
            const startTime = run.context.contextValues.startTime;
            const endTime = run.context.contextValues.endTime;
            const status = run.context.resultStatus;

            this._data.push({
                status: status,
                statusName: this._statusConverter.getLabelForStatus(status),
                errorMessage: run.combinedErrorMessage,
                itemStyle: {
                    color: style.get(status),
                    opacity: 1
                },
                startTime: startTime,
                endTime: endTime,
                duration: endTime - startTime,
                value: [run.historyIndex, 0]
            });
        });

        this._completeArray();

        this._lineStart = this._data[0].value;
        this._lineEnd = this._data[this._data.length - 1].value;
        if (this._data.length > 50) {
            this._chartSymbolSize = 14;
        }
    }

    private _completeArray() {
        const values = this._data.map(item => item.value[0]);

        const min = Math.min(...values);
        const max = Math.max(...values);

        const fullRange = Array.from({length: max - min + 1}, (_, i) => min + i);
        const missingValues = fullRange.filter(value => !values.includes(value));

        const missingRuns = missingValues.map(value => ({
            status: ResultStatusType.NO_RUN,
            statusName: "No Run",
            errorMessage: "",
            itemStyle: {
                color: "#808080",
                opacity: 1,
            },
            startTime: 0,
            endTime: 0,
            duration: 0,
            value: [value, 0],
        }));

        this._data = [...this._data, ...missingRuns].sort((a, b) => a.value[0] - b.value[0]);
    }

    private _truncateErrorMessage(str: string): string {
        if (str.length <= this._maxErrorMessageLength) {
            return str;
        }
        return str.slice(0, this._maxErrorMessageLength - 3) + '...';
    }

    private _setChartOption() {
        const dateFormatter = this._dateFormatter;
        const durationFormatter = this._durationFormatter;
        const self = this;

        this._option = {
            grid: {
                top: '20%',
                bottom: '40%',
                left: '2%',
                right: '2%'
            },
            tooltip: {
                trigger: 'item',
                formatter: function (params) {
                    if (params.data.status === ResultStatusType.NO_RUN) {
                        return '<div class="header" style="background-color: ' +
                            params.color + ';">' + params.data.statusName + '</div>';
                    }

                    let tooltip = '<div class="header" style="background-color: ' +
                        params.color + ';">Run ' + params.value[0] + ": " + params.data.statusName + '</div>'

                    if (params.data.errorMessage) {
                        tooltip += '<br><div class="tooltip-content">' + self._truncateErrorMessage(params.data.errorMessage) + '</div>';
                    }

                    tooltip += '<br>Start time: ' + dateFormatter.toView(params.data.startTime, 'full')
                        + '<br>End time: ' + dateFormatter.toView(params.data.endTime, 'full')
                        + '<br>Duration: ' + durationFormatter.toView(params.data.duration);

                    return tooltip;
                }
            },
            xAxis: {
                type: 'category',
                show: true
            },
            yAxis: {
                type: 'category',
                show: false
            },
            series: [
                {
                    type: 'scatter',
                    symbolSize: this._chartSymbolSize,
                    data: this._data,
                    cursor: 'default',
                    z: 2
                },
                {
                    // This series represents a static line in the background for visual reference, with no interaction or tooltip.
                    type: 'line',
                    symbol: 'none',
                    data: [
                        this._lineStart, this._lineEnd
                    ],
                    lineStyle: {
                        color: '#A0A0A0',
                        width: 1
                    },
                    markLine: {
                        symbol: 'none'
                    },
                    z: 1,
                    silent: true
                }
            ]
        };
    }
}
