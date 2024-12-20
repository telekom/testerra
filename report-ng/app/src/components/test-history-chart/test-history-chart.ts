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
import {HistoryStatistics} from "../../services/statistic-models";
import {ECharts, EChartsOption} from "echarts";
import {IFilter, StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {Container} from "aurelia-dependency-injection";
import "./test-history-chart.scss"
import {bindingMode} from "aurelia-binding";
import {ResultStatusType} from "../../services/report-model/framework_pb";

@autoinject()
export class TestHistoryChart extends AbstractViewModel {
    private _historyStatistics: HistoryStatistics;
    private _historyAvailable: boolean;
    private _initialChartLoading = true;
    @observable() private _chart: ECharts;
    private _option: EChartsOption;
    @bindable({defaultBindingMode: bindingMode.toView}) filter: IFilter;
    @bindable is_history_view: boolean;
    private _chartData: any[] = [];
    @bindable viewport: number[] = [];
    @bindable({defaultBindingMode: bindingMode.twoWay}) status_data: any[] = [];

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _dateFormatter: IntlDateFormatValueConverter,
        private _durationFormatter: DurationFormatValueConverter
    ) {
        super();
        this._option = {};
    }

    filterChanged() {
        if (this._initialChartLoading || !this._historyAvailable) {
            return;
        }

        const defaultLegend = {
            'Failed': false,
            'Expected Failed': false,
            'Skipped': false,
            'Passed': false,
        };

        const statusLegendMap = {
            [ResultStatusType.FAILED]: {...defaultLegend, 'Failed': true},
            [ResultStatusType.FAILED_EXPECTED]: {...defaultLegend, 'Expected Failed': true},
            [ResultStatusType.SKIPPED]: {...defaultLegend, 'Skipped': true},
            [ResultStatusType.PASSED]: {...defaultLegend, 'Passed': true},
        };

        this._option.legend = {
            show: false,
            selected: this.filter?.status
                ? statusLegendMap[this.filter.status] || defaultLegend
                : {
                    'Failed': true,
                    'Expected Failed': true,
                    'Skipped': true,
                    'Passed': true,
                },
        };

        this._chart.setOption(this._option);
    }

    async attached() {
        this._historyStatistics = await this._statisticsGenerator.getHistoryStatistics();
        this._historyAvailable = this._historyStatistics.getTotalRunCount() > 1;
        this._initDurationFormatter();

        if (this._historyAvailable) {
            this._setChartOption();
        } else {
            this._setChartPlaceholderOption();
        }
        this._initialChartLoading = false;
        if (this.filter?.status) {
            this.filterChanged();
        }
    };

    private _handleZoomEvent(event: any) {
        const options = this._chart.getOption();
        const start = options.dataZoom[0].startValue;
        const end = options.dataZoom[0].endValue;
        const historyIndexes = this._historyStatistics.availableRuns;
        const startIndex = historyIndexes[start];
        const endIndex = historyIndexes[end];

        if (this.viewport[0] != startIndex || this.viewport[1] != endIndex) {
            this.viewport = [startIndex, endIndex];

            let statusCount = new Map<ResultStatusType, number>();
            for (let i = start; i <= end; i++) {
                const currentFailed = statusCount.get(ResultStatusType.FAILED) || 0;
                const currentExpectedFailed = statusCount.get(ResultStatusType.FAILED_EXPECTED) || 0;
                const currentSkipped = statusCount.get(ResultStatusType.SKIPPED) || 0;
                const currentPassed = statusCount.get(ResultStatusType.PASSED) || 0;

                statusCount.set(ResultStatusType.FAILED, currentFailed + this._chartData[i].value[1]);
                statusCount.set(ResultStatusType.FAILED_EXPECTED, currentExpectedFailed + this._chartData[i].value[2]);
                statusCount.set(ResultStatusType.SKIPPED, currentSkipped + this._chartData[i].value[3]);
                statusCount.set(ResultStatusType.PASSED, currentPassed + this._chartData[i].value[4]);
            }

            let newStatusData = [];
            statusCount.forEach((count, status) => {
                if (count) {
                    newStatusData.push({
                        status: status,
                        statusName: this._statusConverter.getLabelForStatus(status),
                        value: count,
                        itemStyle: {color: this._statusConverter.getColorForStatus(status)}
                    });
                }
            });

            this.status_data = newStatusData;
        }
    }

    private _chartChanged() {
        this._chart.on('datazoom', event => this._handleZoomEvent(event));
    }

    private _initDurationFormatter() {
        const container = new Container();
        this._durationFormatter = container.get(DurationFormatValueConverter);
        this._durationFormatter.setDefaultFormat("h[h] m[min] s[s] S[ms]");
    }

    private _setChartPlaceholderOption() {
        this._option = {
            grid: {
                top: '3%',
                left: '3%',
                right: '3%',
                bottom: '3%',
                containLabel: true
            },
            xAxis: [
                {
                    type: 'category',
                    axisLabel: {
                        show: false
                    },
                    boundaryGap: false,
                    splitLine: {
                        show: true
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    axisLine: {
                        show: true
                    },
                    axisLabel: {
                        show: false
                    },
                    splitLine: {
                        show: false
                    }
                }
            ],
            series: [{
                data: [1000, 1100, 1100, 1200, 1290, 1330, 1320],
                type: 'line',
                areaStyle: {
                    color: 'rgba(20,20,20,0.05)'
                },
                lineStyle: {
                    color: 'rgba(255,255,255,0)',
                    width: 0
                },
                silent: true,
                symbol: 'none',
                emphasis: {
                    focus: 'none'
                },
                tooltip: {
                    show: false
                }
            }],
            graphic: {
                type: 'text',
                left: 'center',
                top: 'center',
                silent: true,
                z: 10,
                style: {
                    text: 'No history available',
                    font: '28px Roboto',
                    fill: '#55555'
                }
            }
        };
    }

    private _setChartOption() {
        const style = new Map<number, string>();
        style.set(ResultStatusType.PASSED, this._statusConverter.getColorForStatus(ResultStatusType.PASSED));
        style.set(ResultStatusType.SKIPPED, this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED));
        style.set(ResultStatusType.FAILED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED));
        style.set(ResultStatusType.FAILED_EXPECTED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED));

        const dateFormatter = this._dateFormatter;
        const durationFormatter = this._durationFormatter;

        this._historyStatistics.getHistoryAggregateStatistics().forEach(entry => {
            const startTime = entry.historyAggregate.executionContext.contextValues.startTime;
            const endTime = entry.historyAggregate.executionContext.contextValues.endTime;

            this._chartData.push({
                startTime: startTime,
                endTime: endTime,
                duration: endTime - startTime,
                value: [
                    entry.historyAggregate.historyIndex,
                    entry.getStatusCount(ResultStatusType.FAILED),
                    entry.getStatusCount(ResultStatusType.FAILED_EXPECTED),
                    entry.getStatusCount(ResultStatusType.SKIPPED),
                    entry.overallPassed,
                ]
            });
        });

        this._option = {
            grid: {
                top: '5%',
                left: '1%',
                right: '3%',
                bottom: '0%',
                containLabel: true
            },
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'line',
                },
                confine: true,
                borderWidth: 1,
                borderColor: 'rgba(221,221,221,1)',
                formatter: function (params) {
                    const data = params[0].data;
                    const values = params[0].value;

                    const failed = values[1];
                    const expectedFailed = values[2];
                    const skipped = values[3];
                    const passed = values[4];
                    const testcases = passed + failed + skipped + expectedFailed;

                    return `<div class="history-chart-tooltip-header">Run ${params[0].axisValue}</div>
                        <br>Testcases: ${testcases}
                        ${failed > 0 ? `<br><span class="status-dot status-failed"></span> Failed: ${failed}` : ''}
                        ${expectedFailed > 0 ? `<br><span class="status-dot status-failed-expected"></span> Expected Failed: ${expectedFailed}` : ''}
                        ${skipped > 0 ? `<br><span class="status-dot status-skipped"></span> Skipped: ${skipped}` : ''}
                        ${passed > 0 ? `<br><span class="status-dot status-passed"></span> Passed: ${passed}` : ''}
                        <br><br>Start time: ${dateFormatter.toView(data.startTime, 'full')}
                        <br>End time: ${dateFormatter.toView(data.endTime, 'full')}
                        <br>Duration: ${durationFormatter.toView(data.duration)}`;
                }
            },
            xAxis: [
                {
                    type: 'category',
                    axisLine: {
                        show: true
                    },
                    axisLabel: {
                        show: true
                    },
                    boundaryGap: false,
                    splitLine: {
                        show: true
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    axisLabel: {
                        show: true
                    },
                    splitLine: {
                        show: false
                    }
                }
            ],
            series: [
                {
                    name: 'Passed',
                    type: 'line',
                    stack: 'Total',
                    silent: true,
                    lineStyle: {
                        width: 0
                    },
                    symbol: 'none',
                    areaStyle: {
                        color: style.get(ResultStatusType.PASSED),
                        opacity: 1
                    },
                    encode: {
                        x: 0,
                        y: 4
                    },
                    emphasis: {
                        disabled: true
                    },
                    data: this._chartData
                },
                {
                    name: 'Skipped',
                    type: 'line',
                    stack: 'Total',
                    silent: true,
                    lineStyle: {
                        width: 0
                    },
                    symbol: 'none',
                    areaStyle: {
                        color: style.get(ResultStatusType.SKIPPED),
                        opacity: 1
                    },
                    encode: {
                        x: 0,
                        y: 3
                    },
                    emphasis: {
                        disabled: true
                    },
                    data: this._chartData
                },
                {
                    name: 'Expected Failed',
                    type: 'line',
                    stack: 'Total',
                    silent: true,
                    areaStyle: {
                        color: style.get(ResultStatusType.FAILED_EXPECTED),
                        opacity: 1
                    },
                    lineStyle: {
                        width: 0
                    },
                    symbol: 'none',
                    encode: {
                        x: 0,
                        y: 2
                    },
                    emphasis: {
                        disabled: true
                    },
                    data: this._chartData
                },
                {
                    name: 'Failed',
                    type: 'line',
                    stack: 'Total',
                    silent: true,
                    lineStyle: {
                        width: 0
                    },
                    symbol: 'none',
                    areaStyle: {
                        color: style.get(ResultStatusType.FAILED),
                        opacity: 1
                    },
                    encode: {
                        x: 0,
                        y: 1
                    },
                    emphasis: {
                        disabled: true
                    },
                    data: this._chartData
                },
            ]
        };

        if (this.is_history_view) {
            this._option.dataZoom = [
                {
                    type: 'inside',
                    start: 0,
                    end: 100,
                    minValueSpan: 1
                },
                {
                    start: 0,
                    end: 100,
                    minValueSpan: 1
                }
            ]

            // Adapt grid for data-zoom slider
            this._option.grid = {
                top: '5%',
                left: '1%',
                right: '3%',
                bottom: '50px',
                containLabel: true
            }
        }
    }
}
