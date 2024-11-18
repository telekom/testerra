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
import "./classes-history.scss";
import {Container} from "aurelia-dependency-injection";
import {
    DurationFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/duration-format-value-converter";
import {
    IntlDateFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter";
import {ExecutionStatistics, HistoryStatistics} from "../../../services/statistic-models";
import {StatusConverter} from "../../../services/status-converter";
import {StatisticsGenerator} from "../../../services/statistics-generator";
import {ClassNameValueConverter} from "../../../value-converters/class-name-value-converter";

@autoinject()
export class ClassesHistory extends AbstractViewModel {
    private _dateFormatter: IntlDateFormatValueConverter;
    private _durationFormatter: DurationFormatValueConverter;
    private _selectedClass: string;
    private _historyStatistics: HistoryStatistics;
    private _executionStatistics: ExecutionStatistics;
    @observable() private _chart: ECharts;
    private _option: EChartsOption;

    constructor(
        private _router: Router,
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _classNameValueConverter: ClassNameValueConverter
    ) {
        super();
        this._option = {};
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;
    }

    async attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            this._executionStatistics.classStatistics.sort((a, b) => this._classNameValueConverter.toView(a.classIdentifier, 1).localeCompare(this._classNameValueConverter.toView(b.classIdentifier, 1)))
        });
        this._historyStatistics = await this._statisticsGenerator.getHistoryStatistics();
        this._initDateFormatter();
        this._initDurationFormatter();

        this._setChartOption();
    };

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

        this._option = {
            grid: {
                top: '3%',
                bottom: '2%',
                containLabel: true,
                left: '2%',
                right: '2%'
            },
            tooltip: {
                trigger: 'item',
                formatter: (params) => `Position: (${params.data.value[0]}, ${params.data.value[1]})<br/>Color: ${params.color}`
            },
            xAxis: {
                type: 'category',
                show: true
            },
            yAxis: {
                type: 'category',
                show: true
            },
            series: [
                {
                    type: 'scatter',
                    symbolSize: 60,
                    symbol: 'rect',
                    data: [
                        {value: [1, 'testcase01'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [2, 'testcase01'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [3, 'testcase01'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [4, 'testcase01'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [5, 'testcase01'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [6, 'testcase01'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [7, 'testcase01'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [8, 'testcase01'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [9, 'testcase01'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [10, 'testcase01'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [11, 'testcase01'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [12, 'testcase01'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [13, 'testcase01'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [14, 'testcase01'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [15, 'testcase01'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [16, 'testcase01'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [17, 'testcase01'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [18, 'testcase01'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [19, 'testcase01'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [20, 'testcase01'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [1, 'testcase02'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [2, 'testcase02'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [3, 'testcase02'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [4, 'testcase02'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [5, 'testcase02'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [6, 'testcase02'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [7, 'testcase02'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [8, 'testcase02'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [9, 'testcase02'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [10, 'testcase02'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [11, 'testcase02'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [12, 'testcase02'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [13, 'testcase02'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [14, 'testcase02'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [15, 'testcase02'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [16, 'testcase02'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [17, 'testcase02'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [18, 'testcase02'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [19, 'testcase02'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [20, 'testcase02'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [1, 'testcase03'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [2, 'testcase03'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [3, 'testcase03'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [4, 'testcase03'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [5, 'testcase03'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [6, 'testcase03'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [7, 'testcase03'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [8, 'testcase03'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [9, 'testcase03'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [10, 'testcase03'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [11, 'testcase03'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [12, 'testcase03'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [13, 'testcase03'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [14, 'testcase03'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [15, 'testcase03'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [16, 'testcase03'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [17, 'testcase03'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [18, 'testcase03'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [19, 'testcase03'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [20, 'testcase03'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [1, 'testcase04'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [2, 'testcase04'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [3, 'testcase04'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [4, 'testcase04'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [5, 'testcase04'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [6, 'testcase04'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [7, 'testcase04'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [8, 'testcase04'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [9, 'testcase04'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [10, 'testcase04'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [11, 'testcase04'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [12, 'testcase04'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [13, 'testcase04'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [14, 'testcase04'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [15, 'testcase04'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [16, 'testcase04'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [17, 'testcase04'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [18, 'testcase04'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [19, 'testcase04'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [20, 'testcase04'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [1, 'testcase05'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [2, 'testcase05'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [3, 'testcase05'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [4, 'testcase05'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [5, 'testcase05'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [6, 'testcase05'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [7, 'testcase05'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [8, 'testcase05'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [9, 'testcase05'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [10, 'testcase05'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [11, 'testcase05'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [12, 'testcase05'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [13, 'testcase05'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [14, 'testcase05'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [15, 'testcase05'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [16, 'testcase05'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [17, 'testcase05'], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [18, 'testcase05'], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [19, 'testcase05'], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [20, 'testcase05'], itemStyle: {color: '#417336', opacity: 1}},
                    ],
                    z: 2
                }
            ]
        };
    }
}
