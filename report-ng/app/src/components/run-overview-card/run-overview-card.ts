/*
 * Testerra
 *
 * (C) 2025, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {autoinject, bindable, observable} from 'aurelia-framework';
import "./run-overview-card.scss";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ECharts, EChartsOption} from "echarts";
import {HistoryAggregateStatistics} from "../../services/statistic-models";
import {StatusConverter} from "../../services/status-converter";

@autoinject()
export class RunOverviewCard extends AbstractViewModel {
    @observable() private _chart: ECharts;
    @bindable history_aggregate_statistics: HistoryAggregateStatistics;
    private _option: EChartsOption;
    private _data: any[] = [];
    @bindable history_index: number;
    @bindable date_time_started: number;
    @bindable run_duration: number;
    @bindable executed_test_cases: number;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }

    history_aggregate_statisticsChanged() {
        if (this.history_aggregate_statistics) {
            this._data = [];
            let overallTestcases = 0;
            for (const status of this._statusConverter.relevantStatuses) {
                const statusGroup = this._statusConverter.groupStatus(status);
                const statusCount = this.history_aggregate_statistics.getSummarizedStatusCount(statusGroup);
                overallTestcases += statusCount;

                if (statusCount) {
                    this._data.push({
                        status: status,
                        name: this._statusConverter.getLabelForStatus(status),
                        value: statusCount,
                        itemStyle: {
                            color: this._statusConverter.getColorForStatus(status),
                            opacity: 1
                        }
                    });
                }
            }
            this.history_index = this.history_aggregate_statistics.historyIndex;
            this.date_time_started = this.history_aggregate_statistics.historyAggregate.executionContext.contextValues.startTime;
            this.run_duration = this.history_aggregate_statistics.historyAggregate.executionContext.contextValues.endTime - this.date_time_started;
            this.executed_test_cases = overallTestcases;
            this.setOption();
        }
    }

    private _prepareChartData() {
    }

    private setOption() {
        this._option = {
            tooltip: {
                formatter: function (params) {
                    return '<div class="header" style="background-color: ' +
                        params.color + ';"> ' + params.data.name + ': ' + params.value + '</div>';
                }
            },
            series: [
                {
                    name: 'Access From',
                    type: 'pie',
                    cursor: 'default',
                    radius: '90%',
                    label: {
                        position: 'inner',
                        show: true,
                        silent: true,
                        color: '#ffffff',
                        formatter: (params: any) => `${params.percent.toFixed(1)}%`,
                        distance: 0.8
                    },
                    data: this._data
                }
            ]
        };
    }
}
