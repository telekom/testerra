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
import {StatisticsGenerator} from "../../services/statistics-generator";
import {Container} from "aurelia-dependency-injection";
import "./method-history-chart.scss"

@autoinject()
export class MethodHistoryChart extends AbstractViewModel {
    private _dateFormatter: IntlDateFormatValueConverter;
    private _durationFormatter: DurationFormatValueConverter;
    @observable() private _chart: ECharts;
    private _option: EChartsOption;
    @bindable() private _methodHistory: MethodHistoryStatistics;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
        this._option = {};
    }

    async attached() {
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

        console.log(this._methodHistory);

        this._option = {
            grid: {
                top: '20%',
                bottom: '40%',
                left: '2%',
                right: '2%'
            },
            tooltip: {
                trigger: 'item',
                formatter: (params) => `Position: (${params.data.value[0]}, ${params.data.value[1]})<br/>Farbe: ${params.color}`
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
                    symbolSize: 20,
                    data: [
                        {value: [0, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [1, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [2, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [3, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [4, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [5, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [6, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [7, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [8, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [9, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [10, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [11, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [12, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [13, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [14, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [15, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [16, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [17, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [18, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [19, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [20, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [21, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [22, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [23, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [24, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [25, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [26, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [27, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [28, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [29, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [30, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [31, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [32, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [33, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [34, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [35, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [36, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [37, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [38, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [39, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [40, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [41, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [42, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [43, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [44, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [45, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [46, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [47, 0], itemStyle: {color: '#F7AF3E', opacity: 1}},
                        {value: [48, 0], itemStyle: {color: '#417336', opacity: 1}},
                        {value: [49, 0], itemStyle: {color: '#E63946', opacity: 1}},
                        {value: [50, 0], itemStyle: {color: '#417336', opacity: 1}},
                    ],
                    z: 2
                },
                {
                    type: 'line',
                    data: [
                        [0, 0], [50, 0]
                    ],
                    lineStyle: {
                        color: '#A0A0A0',
                        width: 1
                    },
                    markLine: {
                        symbol: 'none'
                    },
                    z: 1
                }
            ]
        };
    }
}
