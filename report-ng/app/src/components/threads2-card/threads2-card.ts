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
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {ExecutionStatistics} from "../../services/statistic-models";
import {ApexOptions} from "apexcharts";
import {bindingMode} from "aurelia-binding";
import {Router} from "aurelia-router";

@autoinject
export class Threads2Card {
    private _apexTimelineOptions: ApexOptions = undefined;
    private _classNamesMap: { [key: string]: string };
    @bindable({defaultBindingMode: bindingMode.toView})
    executionStatistics: ExecutionStatistics;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _statistics: StatisticsGenerator,
        private _router: Router
    ) {
    }

    executionStatisticsChanged() {
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            this._classNamesMap = {};
            executionStatistics.classStatistics.forEach(classStatistic => {
                this._classNamesMap[classStatistic.classContext.contextValues.id] = classStatistic.classIdentifier;
            });
            this._prepareTimelineChart(executionStatistics);
        });
    }

    private _prepareTimelineChart(executionStatistics: ExecutionStatistics): void {
        const yLabels: string[] = [];
        const series = [];
        const data = [];

        Object.values(executionStatistics.executionAggregate.methodContexts).forEach(methodContext => {

            data.push({
                fillColor: this._statusConverter.getColorForStatus(methodContext.resultStatus),
                x: methodContext.threadName,
                y: [
                    methodContext.contextValues.startTime,
                    methodContext.contextValues.endTime
                ],
                methodName: methodContext.contextValues.name,
                methodRunIndex: methodContext.methodRunIndex,
                className: this._classNamesMap[methodContext.classContextId],
                methodID: methodContext.contextValues.id
            });
            yLabels.push(methodContext.threadName);
        });

        series.push(data);

        // Break yLabels
        const yLabelsMaxWidth = 400;
        const dataAvailable = yLabels.length > 0;

        // const height: string = yLabels.length * 20 + 67.65 + 'px';
        const height: string = 150 + 'px';

        this._apexTimelineOptions = {
            series: [{
                data: data
            }],
            chart: {
                height: height,
                type: 'rangeBar',
                toolbar: {
                    show: true,
                    tools: {
                        download: false
                    }
                },
                events: {
                    dataPointSelection: (event, chartContext, config) => {
                        this._barClicked(event, chartContext, config);
                    }
                },
            },
            plotOptions: {
                bar: {
                    horizontal: true,
                    barHeight: '60%',
                    dataLabels: {
                        hideOverflowingLabels: true
                    }
                },
            },
            xaxis: {
                type: 'datetime',
                // labels: {
                //     show: true,
                //     datetimeUTC: false
                // }
                labels: {
                    show: true,
                    hideOverlappingLabels: true,
                    datetimeUTC: false,
                    formatter: function (value) {
                        let dateTime = new Date(value).toLocaleString();
                        let date = dateTime.split(' ')[1];
                        return ('0' + date.split(':')[0]).slice(-2) + ':' + ('0' + date.split(':')[1]).slice(-2) + ':' + ('0' + date.split(':')[2]).slice(-2);
                    }
                }
            },
            yaxis: {
                labels: {
                    show: dataAvailable,
                    maxWidth: yLabelsMaxWidth
                },
            },
            dataLabels: {
                enabled: true,
                textAnchor: 'middle',
                formatter: function (val, obj) {
                    return obj.w.config.series[obj.seriesIndex].data[obj.dataPointIndex].methodName
                },
            },
            stroke: {
                width: 1,
                // colors: ['#f0f0f0']
                colors: ['#6E8192']
            },
            fill: {
                type: 'solid',
                opacity: 1.0
            },
            noData: {
                text: "There is no data for this filter."
            },
            legend: {
                show: false
            },
            tooltip: {
                followCursor: true,
                fillSeriesColor: true,
                custom: function ({series, seriesIndex, dataPointIndex, w}) {
                    let dateStart = new Date(w.config.series[seriesIndex].data[dataPointIndex].y[0]).toLocaleString().split(' ')[1];
                    let dateEnd = new Date(w.config.series[seriesIndex].data[dataPointIndex].y[1]).toLocaleString().split(' ')[1];

                    return  '<div class="header" style="background-color: ' +
                        w.config.series[seriesIndex].data[dataPointIndex].fillColor + ';"> ' +
                        w.config.series[seriesIndex].data[dataPointIndex].methodName +
                        " (" + w.config.series[seriesIndex].data[dataPointIndex].methodRunIndex + ")" + ' </div>' +
                        '<div class="tooltip_box">' +
                        w.config.series[seriesIndex].data[dataPointIndex].className + '<br>' +
                        ('0' + dateStart.split(':')[0]).slice(-2) + ':' + ('0' + dateStart.split(':')[1]).slice(-2) + ':' + ('0' + dateStart.split(':')[2]).slice(-2) + " - " +
                        ('0' + dateEnd.split(':')[0]).slice(-2) + ':' + ('0' + dateEnd.split(':')[1]).slice(-2) + ':' + ('0' + dateEnd.split(':')[2]).slice(-2) +

                        '</div>'
                }
            }
        }
    }

    private _barClicked(event: MouseEvent, chartContext, config): void {
        event?.stopPropagation();

        const methodId = config.w.config.series[config.seriesIndex].data[config.dataPointIndex].methodID;
        this._router.navigateToRoute('method', {methodId: methodId})
    }
}
