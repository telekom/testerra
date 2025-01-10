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

import {autoinject, observable} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import {AbstractViewModel} from "../../abstract-view-model";
import {ECharts, EChartsOption} from 'echarts';
import "./duration-history.scss";
import {HistoryStatistics, MethodHistoryStatistics} from "../../../services/statistic-models";
import {StatisticsGenerator} from "../../../services/statistics-generator";
import {Container} from "aurelia-dependency-injection";
import {
    DurationFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/duration-format-value-converter";
import {
    IntlDateFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter";
import {ResultStatusType} from "../../../services/report-model/framework_pb";

@autoinject()
export class DurationHistory extends AbstractViewModel {
    @observable() private _chart: ECharts;
    @observable() viewport: number[] = [];
    private _option: EChartsOption;
    private _historyStatistics: HistoryStatistics;
    private static readonly DURATION_COLOR = '#6897EA';
    private _chartData: any[] = [];
    private _historyAvailable = false;
    private _topLongestRuns: any[] = [];
    private _topLongestTestCases: any[] = [];

    constructor(
        private _statisticsGenerator: StatisticsGenerator,
        private _router: Router,
        private _dateFormatter: IntlDateFormatValueConverter,
        private _durationFormatter: DurationFormatValueConverter
    ) {
        super();
    }

    async activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;

        this._historyStatistics = await this._statisticsGenerator.getHistoryStatistics();
        if (this._historyStatistics.getTotalRunCount() > 1) {
            this._historyAvailable = true;
        }

        const availableRuns = this._historyStatistics.availableRuns;
        this.viewport = [Math.min(...availableRuns), Math.max(...availableRuns)];

        this._historyStatistics.getHistoryAggregateStatistics().forEach(aggregate => {
            const context = aggregate.historyAggregate.executionContext.contextValues;
            const startTime = context.startTime;
            const endTime = context.endTime;
            const duration = endTime - startTime;

            const failed = aggregate.getStatusCount(ResultStatusType.FAILED);
            const expectedFailed = aggregate.getStatusCount(ResultStatusType.FAILED_EXPECTED);
            const skipped = aggregate.getStatusCount(ResultStatusType.SKIPPED);
            const passed = aggregate.overallPassed;
            const testcases = passed + failed + skipped + expectedFailed;

            this._chartData.push({
                value: [aggregate.historyAggregate.historyIndex, duration, testcases],
                startTime: startTime,
                endTime: endTime
            });
        });

        this._initDurationFormatter();
        this._setChartOption();
        this.viewportChanged();
    }

    private _handleZoomEvent(event: any) {
        const options = this._chart.getOption();
        const start = options.dataZoom[0].startValue;
        const end = options.dataZoom[0].endValue;
        const historyIndexes = this._historyStatistics.availableRuns;
        const startIndex = historyIndexes[start];
        const endIndex = historyIndexes[end];

        if (this.viewport[0] != startIndex || this.viewport[1] != endIndex) {
            this.viewport = [startIndex, endIndex];
        }
    }

    private _chartChanged() {
        this._chart.on('datazoom', event => this._handleZoomEvent(event));
    }

    viewportChanged() {
        if (this.viewport.length > 1) {
            this._updateLongestRuns();
            this._updateLongestTestCases();
        }
    }

    private _calculateAverageDurations() {
        return this._historyStatistics.getClassHistory()
            .flatMap(classItem => classItem.methods)
            .map(method => {
                return {
                    statistics: method,
                    averageDuration: method.getAverageDurationInRange(this.viewport[0], this.viewport[1])
                };
            });
    }

    private _updateLongestTestCases() {
        let methodAverages = this._calculateAverageDurations();
        this._topLongestTestCases = methodAverages.sort((a, b) => b.averageDuration - a.averageDuration).slice(0, 3);
    }

    private _updateLongestRuns() {
        this._topLongestRuns = this._chartData
            .filter(item => {
                const historyIndex = item.value[0];
                return historyIndex >= this.viewport[0] && historyIndex <= this.viewport[1];
            })
            .sort((a, b) => b.value[1] - a.value[1])
            .slice(0, 3);
    }

    private _initDurationFormatter() {
        const container = new Container();
        this._durationFormatter = container.get(DurationFormatValueConverter);
        this._durationFormatter.setDefaultFormat("h[h] m[min] s[s] S[ms]");
    }

    private _setChartOption() {
        const dateFormatter = this._dateFormatter;
        const durationFormatter = this._durationFormatter;

        this._option = {
            tooltip: {
                trigger: 'axis',
                axisPointer: {
                    type: 'line',
                },
                borderWidth: 1,
                borderColor: DurationHistory.DURATION_COLOR,
                formatter: function (params) {
                    const valueData = params[0];
                    const testcases = valueData.value[2];
                    return `<div class="duration-history-chart-tooltip-header">Run ${valueData.axisValue}</div>
                        <br>Duration: ${durationFormatter.toView(valueData.value[1])}
                        <br>Testcases: ${testcases}
                        <br><br>Start time: ${dateFormatter.toView(valueData.data.startTime, 'full')}
                        <br>End time: ${dateFormatter.toView(valueData.data.endTime, 'full')}`;
                }
            },
            grid: {
                top: '18%',
                left: '1%',
                right: '1%',
                containLabel: true
            },
            xAxis: {
                type: 'category'
            },
            yAxis: [
                {
                    type: 'value',
                    axisLabel: {
                        formatter: function (val) {
                            return durationFormatter.toView(val, "h[h] m[min] s[s]");
                        },
                    },
                    name: 'Duration'
                },
                {
                    type: 'value',
                    splitLine: {
                        show: false
                    },
                    name: 'Testcases'
                }
            ],
            dataZoom: [
                {
                    type: 'inside',
                    xAxisIndex: [0],
                    start: 0,
                    end: 100,
                },
                {
                    type: 'slider',
                    xAxisIndex: [0],
                    start: 0,
                    end: 100
                }
            ],
            legend: {
                data: ['Duration', 'Testcases'],
                itemGap: 30,
                itemStyle: {
                    color: '#6897EA',
                    opacity: 1
                },
                selectedMode: false
            },
            series: [
                {
                    name: 'Duration',
                    type: 'line',
                    data: this._chartData,
                    z: 2,
                    cursor: 'default',
                    lineStyle: {
                        color: DurationHistory.DURATION_COLOR
                    },
                    symbol: 'circle',
                    symbolSize: 5,
                    encode: {
                        x: 0,
                        y: 1
                    },
                    itemStyle: {
                        color: DurationHistory.DURATION_COLOR
                    }
                },
                {
                    name: 'Testcases',
                    type: 'bar',
                    yAxisIndex: 1,
                    z: 1,
                    cursor: 'default',
                    data: this._chartData,
                    encode: {
                        x: 0,
                        y: 2
                    },
                    itemStyle: {
                        color: DurationHistory.DURATION_COLOR,
                        opacity: 0.1
                    }
                },
            ]
        };
        this._addResetZoomButton();
    }

    private _addResetZoomButton() {
        const resetZoomIconSvgPath = "M 4,1 V 5 H 0 M 3.9865238,4.9219293 C 1.602752,3.5367838 0,0.95556327 0,-2 c 0,-4.418278 3.581722,-8 8,-8 4.418278,0 8,3.581722 8,8 0,4.418278 -3.581722,8 -8,8";

        this._option.toolbox = {
            itemSize: 20,
            feature: {
                myRestore: {
                    show: true,
                    title: 'Reset Zoom',
                    icon: `path://${resetZoomIconSvgPath}`,
                    onclick: () => {
                        this._chart.dispatchAction({
                            type: 'restore',
                        });
                        this._handleZoomEvent(null);
                    }
                }
            }
        };
    }

    private _navigateToMethodHistory(methodHistoryStatistics: MethodHistoryStatistics) {
        this._router.navigateToRoute('method', {
            methodId: methodHistoryStatistics.getIdOfRun(this._historyStatistics.getLastEntry().historyIndex),
            subPage: "method-history"
        });
    }
}
