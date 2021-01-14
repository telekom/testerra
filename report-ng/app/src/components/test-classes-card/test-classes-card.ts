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
import ResultStatusType = data.ResultStatusType;
import {ApexOptions} from "apexcharts";
import {bindingMode} from "aurelia-binding";

@autoinject
export class TestClassesCard {
    @bindable({bindingMode: bindingMode.toView}) filter:IFilter;
    @bindable classStatistics: ClassStatistics[];
    private _apexBarOptions: ApexOptions = undefined;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _element: Element
    ) {
    }

    classStatisticsChanged() {
        this._prepareHorizontalBarChart(this.classStatistics);
    }

    filterChanged(){
        if (this.classStatistics?.length > 0 ) {
            this._prepareHorizontalBarChart(this.classStatistics);
        }
    }

    private _prepareHorizontalBarChart(classStatistics: ClassStatistics[]): void {
        let data: Map<ResultStatusType, Array<number>> = new Map();
        let xlabels: Array<string> = [];
        let noData : boolean = true;

        const series = [];
        const filteredStatuses = this._statusConverter.relevantStatuses.filter(status => (!this.filter?.status || status === this.filter.status) );

        // console.log("filtered statuses",filteredStatuses);

        for (const status of filteredStatuses) {
            data.set(status, []);
            series.push({
                name: this._statusConverter.getLabelForStatus(status),
                data: data.get(status),
                color: this._statusConverter.getColorForStatus(status)
            })
        }

        //Iterate through classStatistics array to fill map with data for series
        classStatistics
            .filter(classStatistic => {
                for (let status of filteredStatuses) {
                    if (classStatistic.getStatusCount(status) > 0) {
                        return true;
                    } else {
                       noData = false
                    }
                }
            })
            .forEach(classStats => {
                for (const status of filteredStatuses) {
                    data.get(status).push(classStats.getStatusCount(status));
                }
                //Push Class Names in array for x-axis labels
                xlabels.push(classStats.classIdentifier);
            });


        //set size by amount of bars to have consistent bar height
        //amount of classes * 60px + offset due to legend and labels
        let height: string = xlabels.length * 60 + 67.65 + 'px'

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
                    show: noData,
                    trim: false,    //ignored apparently, documentation: https://apexcharts.com/docs/options/xaxis/#trim
                    maxHeight: undefined,
                },
                categories: xlabels,
                axisBorder: {
                    show: noData,
                },
                axisTicks: {
                    show: noData,
                }
            },
            yaxis: {
                labels:{
                    show: noData,
                }
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
                text: "There is no data available at the moment. Please be patient!"
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

        const params:IFilter = {
            class: this._apexBarOptions.xaxis.categories[config.dataPointIndex],
            status: this._statusConverter.relevantStatuses[config.seriesIndex]
        }

        const customEvent = new CustomEvent("filter-changed", {
            detail: params,
            bubbles: true
        });
        // console.log(params, this._element);
        this._element.dispatchEvent(customEvent)
    }
}
