/*
 * Testerra
 *
 * (C) 2023, Selina Natschke, Telekom MMS GmbH, Deutsche Telekom AG
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

import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../../abstract-view-model";
import {ECharts, EChartsOption} from 'echarts';
import "./session-timings.scss";
import {MethodDetails, StatisticsGenerator} from "services/statistics-generator";
import {StatusConverter} from "../../../services/status-converter";
import moment from "moment";
import {MethodType} from "../../../services/report-model/framework_pb";
import {ExecutionStatistics} from "../../../services/statistic-models";

@autoinject()
export class SessionTimings extends AbstractViewModel {
    private static readonly SESSION_COLOR = '#6897EA';
    private static readonly BASEURL_COLOR = '#75C6CB';
    private _chart: ECharts;
    private _option: EChartsOption;
    private _executionStatistics: ExecutionStatistics;
    private _methodDetails: MethodDetails[];
    private _hasEnded = false;
    private _testDuration: Duration | null = null;
    private _dots: IDots[];

    constructor(
        private readonly _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
        this._methodDetails = [];
        this._dots = [];
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);

        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;

            this._testDuration = {
                startTime: this._executionStatistics.executionAggregate.executionContext.contextValues.startTime,
                endTime: this._executionStatistics.executionAggregate.executionContext.contextValues.endTime,
            }

            executionStatistics.classStatistics.forEach(classStatistic => {
                const filteredMethodDetails = classStatistic.methodContexts
                    .filter(methodContext => methodContext.methodType == MethodType.TEST_METHOD)
                    .map(methodContext => new MethodDetails(methodContext, classStatistic));
                this._methodDetails.push(...filteredMethodDetails);
            })
        });

        const sessionInformationArray = [];

        this._statisticsGenerator.getSessionMetrics().then(sessionMetrics => {
            sessionMetrics.forEach(metric => {
                const sessionContext = this._executionStatistics.executionAggregate.sessionContexts[metric.sessionContextId];
                const methodList = this._methodDetails.filter(method => method.methodContext.sessionContextIds.includes(sessionContext.contextValues.id));
                const sessionInformation: ISessionInformation = {
                    sessionName: sessionContext.contextValues.name,
                    sessionId: sessionContext.sessionId,
                    browserName: sessionContext.browserName,
                    browserVersion: sessionContext.browserVersion,
                    methodList: methodList,
                    sessionDuration: this._calculateDuration(metric.metricsValues[1].startTimestamp, metric.metricsValues[1].endTimestamp),
                    baseurlDuration: this._calculateDuration(metric.metricsValues[0].startTimestamp, metric.metricsValues[0].endTimestamp),
                    sessionStartTime: metric.metricsValues[1].startTimestamp,
                    baseurlStartTime: metric.metricsValues[0].startTimestamp,
                }
                sessionInformationArray.push(sessionInformation);
            })
        }).finally(() => {
            this._prepareData(sessionInformationArray);
            this._setChartOption();
        })
    }

    private _prepareData(sessionInformationArray: ISessionInformation[]) {
        sessionInformationArray.forEach(info => {
            const dots: IDots = {
                sessionValues: [info.sessionStartTime, info.sessionDuration],
                baseUrlValues: [info.baseurlStartTime, info.baseurlDuration],
                information: info,
            }
            this._dots.push(dots);
            this._createSeriesForValuePairs();
        })
    }

    private _createSeriesForValuePairs() {
        const seriesList = [];
        // each series has two colors, but echarts usually uses the series colors to generate the legend
        // => create two placeholder series with uniform colors and use these for legend generating
        const legendPlaceholder = [{
            name: 'Session load',
            type: 'scatter',
            data: [],
            itemStyle: {
                color: SessionTimings.SESSION_COLOR,
            },
            showInLegend: true,
        },
        {
            name: 'Base URL load',
            type: 'scatter',
            data: [],
            itemStyle: {
                color: SessionTimings.BASEURL_COLOR,
            },
            showInLegend: true,
        }]
        seriesList.push(...legendPlaceholder);

        this._dots.forEach((dots) => {
            const seriesData = [
                {
                    name: 'Session',
                    type: 'scatter',
                    data: [{
                        value: dots.sessionValues,
                        itemStyle: {
                            color: SessionTimings.SESSION_COLOR,
                        }
                    }, {
                        value: dots.baseUrlValues,
                        itemStyle: {
                            color: SessionTimings.BASEURL_COLOR,
                        }
                    },
                    ],
                    // enables highlighting only items of the series (session load and connected baseurl load) when hovering
                    emphasis: {
                        focus: 'series',
                    },
                    itemStyle: {
                    },
                    showInLegend: false,
                },
            ];
            seriesList.push(...seriesData);
        });
        return seriesList;
    }

    private _setChartOption() {
        this._option = {
            legend: {
                data: [
                    {
                        name: 'Session load',
                        itemStyle: {
                            color: SessionTimings.SESSION_COLOR,
                        }
                    },
                    {
                        name: 'Base URL load',
                        itemStyle: {
                            color: SessionTimings.BASEURL_COLOR,
                        }
                    },
                ]
            },
            dataZoom: [
                {
                    type: 'inside',
                    yAxisIndex: [0],
                },
                {
                        type: 'slider',
                        xAxisIndex: [0],
                    },
                    {
                        type: 'slider',
                        yAxisIndex: [0],
                    },
            ],
            toolbox: {
                itemSize: 25,
                feature: {
                    dataZoom: {},
                    restore: {}
                }
            },

            tooltip: {
                textStyle: {
                    fontSize: 13,
                },
                formatter: function (params) {
                    if (params.data) {
                        const seriesIndex = (params.seriesIndex - 2); // two series as offset for the legend placeholder
                        const testNames = this._dots[seriesIndex].information.methodList.map(method => method.methodContext.contextValues.name);
                        let tooltipString = '<div class="header" style="background-color: ' +
                            params.color + ';"> ' + this._dots[seriesIndex].information.browserName + ', Version: ' +
                            this._dots[seriesIndex].information.browserVersion + '</div> <br>';
                        tooltipString += `<b>Session name:</b> ${this._dots[seriesIndex].information.sessionName} <br>`;
                        tooltipString += `<b>Session id:</b> ${this._dots[seriesIndex].information.sessionId} <br>`;
                        tooltipString += `<b>Session start duration:</b> ${this._dots[seriesIndex].information.sessionDuration}s <br>`;
                        tooltipString += `<b>Base URL start duration:</b> ${this._dots[seriesIndex].information.baseurlDuration}s <br>`;
                        tooltipString += `<b>Start time session:</b> ${this._dots[seriesIndex].information.sessionStartTime} <br>`;
                        tooltipString += `<b>Start time base URL:</b> ${this._dots[seriesIndex].information.baseurlStartTime} <br>`;

                        if (testNames.length > 1) {
                            tooltipString += `<b>Test case(s):</b><ul style="margin-top: 4px; margin-bottom: 4px; padding-left: 20px;">`;
                            testNames.forEach(testName => {
                                tooltipString += `<li style="margin-bottom: 2px;">${testName}</li>`;
                            });
                            tooltipString += '</ul>';
                        } else {
                            tooltipString += `<b>Test case(s):</b> ${testNames}`;
                        }
                        return tooltipString;
                    }
                    return "";
                }.bind(this),
                backgroundColor: 'rgba(255,255,255,0.5)',
            },
            xAxis: {
                type: 'time',
                min: this._testDuration.startTime,
                max: this._testDuration.endTime,
                name: 'Total test duration',
                nameTextStyle: {
                    align: 'right',
                    verticalAlign: 'top',
                    padding: [8, -40, 0, 0], // prevents that data zoom slider and label overlap
                }
            },
            yAxis: {
                type: 'value',
                name: 'Load duration in seconds',
            },
            series: this._createSeriesForValuePairs(),
        };
    }

    private _calculateDuration(startTime: number, endTime: number) {
        if (!endTime) {
            this._hasEnded = false;
            endTime = new Date().getMilliseconds();
        } else {
            this._hasEnded = true;
        }
        return moment.duration(endTime - startTime, 'milliseconds').asSeconds();
    }
}

interface IDots {
    sessionValues: number[];
    baseUrlValues: number[];
    information: ISessionInformation;
}

interface ISessionInformation {
    sessionName: string;
    sessionId: string;
    browserName: string;
    browserVersion: string;
    methodList: MethodDetails[];
    sessionDuration: number;
    baseurlDuration: number;
    sessionStartTime: number;
    baseurlStartTime: number;
}

interface Duration {
    startTime: number;
    endTime: number;
}
