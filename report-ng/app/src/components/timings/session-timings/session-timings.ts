/*
 * Testerra
 *
 * (C) 2023, Telekom MMS GmbH, Deutsche Telekom AG
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
import * as echarts from 'echarts';
import {EChartsOption} from 'echarts';
import "./session-timings.scss";
import {MethodDetails, StatisticsGenerator} from "services/statistics-generator";
import {StatusConverter} from "../../../services/status-converter";
import moment from "moment";
import {MethodType} from "../../../services/report-model/framework_pb";

@autoinject()
export class SessionTimings extends AbstractViewModel {
    private _chart: echarts.ECharts;
    private _option: EChartsOption;
    private _sessionMetrics;
    private _executionStatistics;
    private _methodDetails;
    private _baseURLData;
    private _sessionData;
    private _hasEnded = false;
    private _testDuration;
    private _bars;


    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
        this._sessionMetrics = [];
        this._executionStatistics = [];
        this._methodDetails = [];
        this._bars = [];
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);

        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;

            this._testDuration = [this._executionStatistics.executionAggregate.executionContext.contextValues.startTime,
                this._executionStatistics.executionAggregate.executionContext.contextValues.endTime]

            executionStatistics.classStatistics
                .forEach(classStatistic => {
                    let methodContexts = classStatistic.methodContexts;
                    methodContexts = methodContexts.filter(methodContext => methodContext.methodType == MethodType.TEST_METHOD);

                    let methodDetails = methodContexts.map(methodContext => {
                        return new MethodDetails(methodContext, classStatistic);
                    });
                    methodDetails.forEach(methodDetails => {
                        this._methodDetails.push(methodDetails);
                    })
                })
        });

        const sessionInformationArray = [];

        this._statisticsGenerator.getSessionMetrics().then(sessionMetrics => {
            this._sessionMetrics = sessionMetrics;
            sessionMetrics.forEach(metric => {
                const methodList = [];
                this._methodDetails.forEach(method => {
                    if (method.methodContext.sessionContextIds.includes(metric.sessionContext.contextValues.id)) {
                        methodList.push(method);
                    }
                });

                const sessionInformation: ISessionInformation = {
                    sessionName: metric.sessionContext.contextValues.name,
                    sessionId: metric.sessionContext.sessionId,
                    browserName: metric.sessionContext.browserName,
                    browserVersion: metric.sessionContext.browserVersion,
                    server: metric.sessionContext.serverUrl,
                    node: metric.sessionContext.nodeUrl,
                    methodList: methodList,
                    sessionDuration: this._calculateDuration(metric.metricsValues[0].startTimestamp, metric.metricsValues[0].endTimestamp),
                    baseurlDuration: this._calculateDuration(metric.metricsValues[1].startTimestamp, metric.metricsValues[1].endTimestamp),
                    startTime: metric.metricsValues[0].startTimestamp,
                }
                sessionInformationArray.push(sessionInformation);
            })
        }).finally(() => {
            this._prepareData(sessionInformationArray)
            this._setChartOption();
        })
    }

    private _prepareData(sessionInformationArray: ISessionInformation[]){
        this._sessionData = [];
        this._baseURLData = [];
        sessionInformationArray.forEach(info => {
            const bar: ISessionBar = {
                x: info.startTime,
                ySession: info.sessionDuration,
                yBaseurl: info.baseurlDuration, //the two durations are stacking on top of each other
                sessionInformation: info,
            }
            this._bars.push(bar);
            this._sessionData.push([bar.x, bar.ySession]);
            this._baseURLData.push([bar.x, bar.yBaseurl]);
        })
    }

    private _setChartOption(){
        this._option = {
            barMinWidth: 5,
            dataZoom: [
                {
                    type: 'inside'
                },
                {
                    type: 'slider'
                }
            ],
            legend: {},
            tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                    if (params.length > 0) {
                        const dataIndex = params[0].dataIndex;

                        const testNames = this._bars[dataIndex].sessionInformation.methodList.map(method => method.methodContext.contextValues.name);
                        let tooltipString = `<b>session name:</b> ${this._bars[dataIndex].sessionInformation.sessionName} <br>`;
                        tooltipString += `<b>session id:</b> ${this._bars[dataIndex].sessionInformation.sessionId} <br>`;
                        tooltipString += `<b>browser name:</b> ${this._bars[dataIndex].sessionInformation.browserName} <br>`;
                        tooltipString += `<b>browser version:</b> ${this._bars[dataIndex].sessionInformation.browserVersion} <br>`;
                        tooltipString += `<b>server:</b> ${this._bars[dataIndex].sessionInformation.server} <br>`;
                        tooltipString += `<b>node:</b> ${this._bars[dataIndex].sessionInformation.node} <br>`;
                        tooltipString += `<b>session start duration:</b> ${this._bars[dataIndex].sessionInformation.sessionDuration}s <br>`;
                        tooltipString += `<b>base url start duration:</b> ${this._bars[dataIndex].sessionInformation.baseurlDuration}s <br>`;
                        tooltipString += `<b>test case(s):</b> ` + testNames.join(', ');


                        return tooltipString;
                    }

                    return "";
                }.bind(this),

            },
            xAxis: {
                type: 'time',
                min: this._testDuration[0],
                max: this._testDuration[1],
                name: 'total test duration',
            },
            yAxis: {
                type: 'value',
                name: 'load duration in seconds',
            },
            series: [
                {
                    name: 'Session Load',
                    type: 'bar',
                    stack: 'x',
                    data: this._sessionData,
                    itemStyle: {
                        color: '#6897EA',
                    }
                },
                {
                    name: 'Base URL Load',
                    type: 'bar',
                    stack: 'x',
                    data: this._baseURLData,
                    itemStyle: {
                        color: '#75C6CB',
                    }
                },
            ],
        };
    }

    private _calculateDuration(startTime, endTime) {
        if (!endTime) {
            this._hasEnded = false;
            endTime = new Date().getMilliseconds();
        } else {
            this._hasEnded = true;
        }
        return moment.duration(endTime - startTime, 'milliseconds').asSeconds();
    }
}

export interface ISessionBar {
    x: number,
    ySession: number,
    yBaseurl: number,
    sessionInformation: ISessionInformation,
}

export interface ISessionInformation {
    sessionName: string;
    sessionId: string;
    browserName: string;
    browserVersion: string;
    server: string;
    node: string;
    methodList: MethodDetails[];
    sessionDuration: number;
    baseurlDuration: number;
    startTime: number;
}
