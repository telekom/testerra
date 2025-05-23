/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

import {autoinject, bindable} from "aurelia-framework";
import {IFilter, StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics} from "../../services/statistic-models";
import {ApexOptions} from "apexcharts";
import {data} from "../../services/report-model";
import ResultStatusType = data.ResultStatusType;
import {ISelection} from "../apex-chart/apex-chart";
import {bindingMode} from "aurelia-binding";

export interface IPieceClickedDetails {
    mouseEvent: MouseEvent,
    filter: IFilter,
}

export class PieceClickedEvent extends CustomEvent<IPieceClickedDetails> {
    constructor(
        details: IPieceClickedDetails
    ) {
        super("piece-clicked", {
            detail: details,
            bubbles: true
        });
    }
}

@autoinject
export class TestResultsChart {
    @bindable({defaultBindingMode: bindingMode.toView})
    filter: IFilter;

    @bindable({defaultBindingMode: bindingMode.toView})
    executionStatistics: ExecutionStatistics;

    private _apexPieOptions: ApexOptions = undefined;
    private _skeletonOptions: ApexOptions = undefined;
    private _selection: ISelection;

    @bindable({defaultBindingMode: bindingMode.toView})
    class: string;

    @bindable({defaultBindingMode: bindingMode.toView})
    animationsEnabled = true;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _element: Element
    ) {
    }

    async attached() {

        this.executionStatistics = await this._statisticsGenerator.getExecutionStatistics();

        this._skeletonOptions = {
            chart: {
                type: 'pie',
                animations: {
                    enabled: false,
                },
            },
            dataLabels: {
                enabled: false,
            },
            legend: {
                show: false,
            },
            tooltip: {
                enabled: false,
            },
            plotOptions: {
                pie: {
                    expandOnClick: false,
                },
            },
            series: [1, 1, 2, 3, 5],
            colors: ['#c4c4c4']
        }
    };

    private _getSeriesByStatus(status:ResultStatusType) {
        return this._statusConverter.relevantStatuses.indexOf(status);
    }

    private _getStatusByIndex(index:number) {
        return this._statusConverter.relevantStatuses[index];
    }

    filterChanged(newValue, oldValue) {
        // console.log("filter changed", newValue);
        if (newValue?.status) {
            this._selection = {
                series: this._getSeriesByStatus(newValue.status),
            };
        } else {
            this._selection = null;
        }
    }

    executionStatisticsChanged() {
        this._preparePieChart(this.executionStatistics);
    }

    private _preparePieChart(executionStatistics: ExecutionStatistics): void {
        const series = [];
        const labels = [];
        //const labelStatus = [];
        const colors = [];

        for (const status of this._statusConverter.relevantStatuses) {
            const statusGroup = this._statusConverter.groupStatus(status);
            series.push(executionStatistics.getSummarizedStatusCount(statusGroup));
            labels.push(this._statusConverter.getLabelForStatus(status));
            //labelStatus.push(status)
            colors.push(this._statusConverter.getColorForStatus(status));
        }

        this._apexPieOptions = {
            chart: {
                animations: {
                    enabled: this.animationsEnabled
                },
                type: 'pie',
                fontFamily: 'Roboto',
                events: {
                    dataPointMouseEnter: (event) => {
                        event.target.style.cursor = 'pointer'
                    },
                    dataPointSelection: (event, chartContext, config) => {
                        this._pieceToggled(event,chartContext,config)
                    }
                },
            },
            legend: {
                show: false,
            },
            dataLabels: {
                style: {
                    fontSize: '12px',
                    fontFamily: 'Roboto',
                    fontWeight: 400,
                    colors: colors
                },
                background: {
                    enabled: true,
                    dropShadow: {
                        enabled:false
                    },
                    foreColor: '#fff',
                    borderWidth: 0,
                    opacity: 0.6
                },
                dropShadow: {
                    enabled: false
                }
            },
            stroke: {
                show: false
            },
            series: series,
            colors: colors,
            labels: labels
        };
    }

    private _pieceToggled(event:Event, chartContext, config) {
        event?.stopPropagation();

        if (event instanceof MouseEvent) {
            let filter:IFilter = null;

            if (config.selectedDataPoints[0]?.length > 0) {
                filter = {
                    status: this._getStatusByIndex(config.selectedDataPoints[0][0])
                };
            }
            this._element.dispatchEvent(new PieceClickedEvent({
                mouseEvent: event,
                filter: filter
            }));
        }
    }
}
