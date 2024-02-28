/*
 * Testerra
 *
 * (C) 2023, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
import {MetricType} from "../../../services/report-model/framework_pb";
import {ExecutionStatistics} from "../../../services/statistic-models";
import {IntlDateFormatValueConverter} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter";

@autoinject()
export class SessionTimings extends AbstractViewModel {
    private static readonly SESSION_COLOR = '#6897EA';
    private static readonly BASEURL_COLOR = '#75C6CB';
    private _chart: ECharts;
    private _option: EChartsOption;
    private _executionStatistics: ExecutionStatistics;
    private _methodDetails: MethodDetails[];
    private _testDuration: Duration | null = null;
    private _dots: IDots[];

    constructor(
        private readonly _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private readonly _dateFormatter: IntlDateFormatValueConverter,
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
                    .map(methodContext => new MethodDetails(methodContext, classStatistic));
                this._methodDetails.push(...filteredMethodDetails);
            })
        });

        const sessionInformationArray = [];

        this._statisticsGenerator.getSessionMetrics().then(sessionMetrics => {
            sessionMetrics.forEach(metric => {
                const sessionData = metric.metricsValues.find(value => value.metricType === MetricType.SESSION_LOAD);

                const baseurlData = metric.metricsValues
                    .filter(value => value.metricType === MetricType.BASEURL_LOAD)
                    .filter(value => value.endTimestamp > 0) // if there is no baseurl endTimestamp the baseurl data will not be displayed
                    .find(() => true)

                if (!(sessionData?.endTimestamp > 0)){ // if there is no session endTimestamp the related metric will be skipped
                    return;
                }

                const sessionContext = this._executionStatistics.executionAggregate.sessionContexts[metric.sessionContextId];
                const methodList = this._methodDetails.filter(method => method.methodContext.sessionContextIds.includes(sessionContext.contextValues.id));
                const sessionInformation: ISessionInformation = {
                    sessionName: sessionContext.contextValues.name,
                    sessionId: sessionContext.sessionId,
                    browserName: sessionContext.browserName,
                    browserVersion: sessionContext.browserVersion,
                    methodList: methodList,
                    sessionDuration: (sessionData.endTimestamp - sessionData.startTimestamp) / 1000,
                    baseurlDuration: (baseurlData?.endTimestamp - baseurlData?.startTimestamp) / 1000,
                    sessionStartTime: sessionData.startTimestamp,
                    baseurlStartTime: baseurlData?.startTimestamp,
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
        const dateFormatter = this._dateFormatter;
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
                        right: "4%", // align slider with toolbox
                    },
            ],
            toolbox: {
                itemSize: 25,
                feature: {
                    dataZoom: {},
                    restore: {}
                }
            },
            grid: {
                bottom: 100,
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
                        tooltipString += `<b>Session name:</b> ${this._dots[seriesIndex].information.sessionName} <br>
                            <b>Session id:</b> ${this._dots[seriesIndex].information.sessionId} <br>
                            <hr>
                            <b>Session start duration:</b> ${this._dots[seriesIndex].information.sessionDuration}s <br>
                            <b>Session start time:</b> ${this._dateFormatter.toView(Number(this._dots[seriesIndex].information.sessionStartTime), 'time')} <br>`

                        if(this._dots[seriesIndex].information.baseurlStartTime){ // only show baseurl information if it is available
                            tooltipString += `<b>Base URL start duration:</b> ${this._dots[seriesIndex].information.baseurlDuration}s <br>
                            <b>Base URL start time:</b> ${this._dateFormatter.toView(Number(this._dots[seriesIndex].information.baseurlStartTime), 'time')} <br>`
                        }

                        if (testNames.length > 1) {
                            tooltipString += `
                                <hr>
                                <b>Test case(s):</b>
                                <ul style="margin-top: 4px; margin-bottom: 4px; padding-left: 20px;">`;

                            testNames.forEach(testName => {
                                tooltipString += `<li style="margin-bottom: 2px;">${testName}</li>`;
                            });
                            tooltipString += '</ul>';
                        } else {
                            tooltipString += `
                                <hr>
                                <b>Test case(s):</b> ${testNames}`;
                        }
                        return tooltipString;
                    }
                    return "";
                }.bind(this),
            },
            xAxis: {
                type: 'time',
                min: this._testDuration.startTime,
                max: this._testDuration.endTime,
                axisLabel: {
                    formatter: function (val) {
                        return dateFormatter.toView(Number(val), 'time') + '\n\n' + dateFormatter.toView(Number(val), 'date');
                    },
                }
            },
            yAxis: {
                type: 'value',
                name: 'Load duration in seconds',
            },
            series: this._createSeriesForValuePairs(),
        };
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
    baseurlDuration?: number;
    sessionStartTime: number;
    baseurlStartTime?: number;
}

interface Duration {
    startTime: number;
    endTime: number;
}
