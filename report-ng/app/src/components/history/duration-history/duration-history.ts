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
import {ExecutionStatistics, HistoryStatistics} from "../../../services/statistic-models";
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
    private _dateFormatter: IntlDateFormatValueConverter;
    private _durationFormatter: DurationFormatValueConverter;
    @observable() private _chart: ECharts;
    private _option: EChartsOption;
    private _historyStatistics: HistoryStatistics;
    private static readonly TEST_COLOR = '#6897EA';
    private _chartData: any[] = [];

    constructor(
        private _statisticsGenerator: StatisticsGenerator,
        private _router: Router
    ) {
        super();
    }

    async activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;

        this._historyStatistics = await this._statisticsGenerator.getHistoryStatistics();

        this._historyStatistics.getHistoryAggregateStatistics().forEach(aggregate => {
            const context = aggregate.historyAggregate.executionContext.contextValues;
            const startTime = context.startTime;
            const endTime = context.endTime;
            const duration = endTime - startTime;

            const failed = aggregate.getStatusCount(ResultStatusType.FAILED);
            const expectedFailed = aggregate.getStatusCount(ResultStatusType.FAILED_EXPECTED);
            const skipped = aggregate.getStatusCount(ResultStatusType.SKIPPED);
            const passed = aggregate.overallPassed;

            this._chartData.push({
                value: [aggregate.historyAggregate.historyIndex, duration],
                itemStyle: {
                    color: DurationHistory.TEST_COLOR
                },
                startTime: startTime,
                endTime: endTime,
                testcases: passed + failed + skipped + expectedFailed
            });
        });

        this._initDateFormatter();
        this._initDurationFormatter();
        this._setChartOption();
    }

    private _initDurationFormatter() {
        const container = new Container();
        this._durationFormatter = container.get(DurationFormatValueConverter);
        this._durationFormatter.setDefaultFormat("h[h] m[min] s[s] S[ms]");
    }

    private _initDateFormatter() {
        const container = new Container();
        this._dateFormatter = container.get(IntlDateFormatValueConverter);
        this._dateFormatter.setLocale('en-GB');
        this._dateFormatter.setOptions('date', {year: 'numeric', month: 'short', day: 'numeric'});
        this._dateFormatter.setOptions('time', {hour: 'numeric', minute: 'numeric', second: 'numeric', hour12: false});
        this._dateFormatter.setOptions('full', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: 'numeric',
            minute: 'numeric',
            second: 'numeric',
            hour12: false
        });
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
                borderColor: DurationHistory.TEST_COLOR,
                formatter: function (params) {
                    const valueData = params[0];
                    const testcases = valueData.data.testcases;

                    return `<div class="duration-history-chart-tooltip-header">Run ${valueData.axisValue} (${durationFormatter.toView(valueData.value[1])})</div>
                        <br>Testcases: ${testcases}
                        <br><br>Start time: ${dateFormatter.toView(valueData.data.startTime, 'full')}
                        <br>End time: ${dateFormatter.toView(valueData.data.endTime, 'full')}`;
                }
            },
            grid: {
                left: '5%',
                right: '2%'
            },
            xAxis: {
                type: 'category'
            },
            yAxis: {
                type: 'value',
                axisLabel: {
                    formatter: function (val) {
                        return durationFormatter.toView(val, "h[h] m[min] s[s]");
                    },
                }
            },
            dataZoom:
                {
                    type: 'slider',
                    xAxisIndex: [0],
                    start: 0,
                    end: 100,
                },
            series: [{
                type: 'line',
                data: this._chartData,
                lineStyle: {
                    color: DurationHistory.TEST_COLOR
                }
            }]
        };
    }
}
