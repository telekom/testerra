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

@autoinject()
export class StatusShareChart extends AbstractViewModel {
    @observable() private _chart: ECharts;
    private _option: EChartsOption;

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
        this._option = {
            grid: {
                top: '0%',
                bottom: '0%',
                left: '0%',
                right: '0%'
            },
            tooltip: {
                trigger: 'item'
            },
            legend: {
                show: false
            },
            series: [
                {
                    name: 'Access From',
                    type: 'pie',
                    radius: ['40%', '70%'],
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
