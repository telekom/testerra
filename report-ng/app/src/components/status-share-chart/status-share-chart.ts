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

import {autoinject, observable} from "aurelia-framework";
import {AbstractViewModel} from "../abstract-view-model";
import {ECharts, EChartsOption} from "echarts";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import "./status-share-chart.scss"
import {bindable} from "aurelia-templating";
import {MethodHistoryStatistics} from "../../services/statistic-models";

@autoinject()
export class StatusShareChart extends AbstractViewModel {
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
        this._setChartOption();
    };

    private _setChartOption() {

        console.log(this._methodHistory);

        this._option = {
            tooltip: {
                trigger: 'item'
            },
            legend: {
                show: false
            },
            series: [
                {
                    name: 'Status Share',
                    type: 'pie',
                    radius: ['30%', '80%'],
                    center: ['50%', '80%'],
                    startAngle: 180,
                    endAngle: 360,
                    data: [
                        { value: 2, name: '2' },
                        { value: 7, name: '7' },
                        { value: 5, name: '5' },
                        { value: 10, name: '10' }
                    ]
                }
            ]
        };
    }
}
