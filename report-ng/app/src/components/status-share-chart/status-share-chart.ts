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
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import "./status-share-chart.scss"
import {MethodHistoryStatistics} from "../../services/statistic-models";
import {ResultStatusType} from "../../services/report-model/framework_pb";
import {ClassName} from "../../value-converters/class-name-value-converter";

@autoinject()
export class StatusShareChart extends AbstractViewModel {
    @observable() private _chart: ECharts;
    private _option: EChartsOption;
    private _data: any[] = [];
    @bindable method_history_statistics: MethodHistoryStatistics;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
        this._option = {};
    }

    async attached() {
        this._prepareChartData();
        this._setChartOption();
    };

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

        for (const status of this._statusConverter.relevantStatuses) {
            const statusCount = this.method_history_statistics.getStatusCount(status);
            if (statusCount) {
                this._data.push({
                    status: status,
                    statusName: this._statusConverter.getLabelForStatus(status),
                    value: statusCount,
                    itemStyle: {color: style.get(status)}
                })
            }
        }
    }

    private _setChartOption() {

        this._option = {
            tooltip: {
                formatter: function (params) {
                    console.log(params);
                    return '<div class="header" style="background-color: ' +
                        params.color + ';"> ' + params.data.statusName + ': ' + params.value + '</div>'
                }
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
                    data: this._data,
                    label: {
                        show: true,
                        formatter: function (params) {
                            return `${params.value}`;
                        }
                    },
                    labelLine: {
                        length: 10,
                        length2: 10,
                        lineStyle: {
                            color: '#000000',
                            width: 1
                        }
                    }
                }
            ]
        };
    }
}
