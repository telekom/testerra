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

import {data} from "../../services/report-model";
import {autoinject, bindable} from "aurelia-framework";
import {IFilter, StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ClassStatistics} from "../../services/statistic-models";
import {ApexOptions} from "apexcharts";
import {bindingMode} from "aurelia-binding";
import ResultStatusType = data.ResultStatusType;

@autoinject
export class TestClassesCard {
    @bindable({bindingMode: bindingMode.toView}) filter: IFilter;
    @bindable classStatistics: ClassStatistics[];
    private _apexBarOptions: ApexOptions = undefined;
    private _filteredStatuses: number[];

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _element: Element
    ) {
    }

    classStatisticsChanged() {
        this._prepareHorizontalBarChart(this.classStatistics);
    }

    filterChanged() {
        if (this.classStatistics?.length > 0) {
            this._prepareHorizontalBarChart(this.classStatistics);
        }
    }

    private _prepareHorizontalBarChart(classStatistics: ClassStatistics[]): void {
        let data: Map<ResultStatusType, Array<number>> = new Map();
        let yLabels: Array<string> = [];

        const series = [];
        this._filteredStatuses = this._statusConverter.relevantStatuses.filter(status => (!this.filter?.status || status === this.filter.status));

        this._filteredStatuses.forEach(status => {
            data.set(status, []);
            series.push({
                name: this._statusConverter.getLabelForStatus(status),
                data: data.get(status),
                color: this._statusConverter.getColorForStatus(status)
            })
        })

        //Iterate through classStatistics array to fill map with data for series
        classStatistics
            .filter(classStatistic => {
                return this._filteredStatuses.find(status => classStatistic.getStatusesCount(this._statusConverter.groupStatus(status)) > 0);
            })
            .sort((a, b) => {
                return b.getStatusesCount(this._filteredStatuses) - a.getStatusesCount(this._filteredStatuses)
            })
            .forEach(classStats => {
                for (const status of this._filteredStatuses) {
                    if (status == ResultStatusType.PASSED) {
                        // For PASSED we need all passed states
                        data.get(status).push(classStats.getStatusesCount(this._statusConverter.passedStatuses));
                    } else {
                        data.get(status).push(classStats.getStatusCount(status));
                    }

                }
                const className = classStats.classIdentifier;
                //Push Class Names in array for y-axis labels
                yLabels.push(className);
            });


        // Break yLabels
        const yLabelsMaxWidth = 400;
        const dataAvailable = yLabels.length > 0;

        //set size by amount of bars to have consistent bar height
        //amount of classes * 60px + offset due to legend and labels
        let height: string = yLabels.length * 60 + 67.65 + 'px'

        this._apexBarOptions = {
            chart: {
                type: 'bar',
                fontFamily: 'Roboto',
                stacked: true,
                height: height,
                toolbar: {
                    show: false,
                },
                events: {
                    dataPointSelection: (event, chartContext, config) => {
                        this._barClicked(event, chartContext, config);
                    }
                },
            },
            dataLabels: {
                style: {
                    fontSize: '12px',
                    fontFamily: 'Roboto',
                    fontWeight: 400
                },
                dropShadow: {
                    enabled: false
                }
            },
            fill: {
                opacity: 1.0
            },
            series: series,
            xaxis: {
                labels: {
                    show: dataAvailable,
                    trim: false,    //ignored apparently, documentation: https://apexcharts.com/docs/options/xaxis/#trim
                    maxHeight: undefined,
                },
                categories: yLabels,
                axisBorder: {
                    show: dataAvailable,
                },
                axisTicks: {
                    show: dataAvailable,
                }
            },
            yaxis: {

                labels: {
                    show: dataAvailable,
                    //minWidth: 200,    // Does not work
                    maxWidth: yLabelsMaxWidth
                },
            },
            grid: {
                show: false,
            },
            plotOptions: {
                bar: {
                    horizontal: true,
                    barHeight: '60%',
                }
            },
            noData: {
                text: "There is no data for this filter."
            },
            legend: {
                show: false
                // position: 'top',
                // horizontalAlign: 'center'
            }
        }
    }

    private _barClicked(event, chartContext, config): void {
        event?.stopPropagation();

        const params: IFilter = {
            class: this._apexBarOptions.xaxis.categories[config.dataPointIndex],
            status: this._filteredStatuses[config.seriesIndex]
        }

        const customEvent = new CustomEvent("filter-changed", {
            detail: params,
            bubbles: true
        });
        // console.log(params, this._element);
        this._element.dispatchEvent(customEvent)
    }
}
