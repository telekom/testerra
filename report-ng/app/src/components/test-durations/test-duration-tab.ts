/*
 * Testerra
 *
 * (C) T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
import {AbstractViewModel} from "../abstract-view-model";
import * as echarts from 'echarts';
import {EChartsOption} from 'echarts';
import "./test-duration-tab.scss";
import {ExecutionStatistics} from "services/statistic-models";
import {MethodDetails, StatisticsGenerator} from "services/statistics-generator";
import moment from "moment";


@autoinject()
export class TestDurationTab extends AbstractViewModel {
    private _chart: echarts.ECharts;
    private _executionStatistics: ExecutionStatistics
    private _option: EChartsOption;
    private _attached = false;
    private _hasEnded = false;
    private _methodDetails: MethodDetails[];
    private _durationOptions: IDurationOptions;
    private _showConfigurationMethods = false;

    constructor(
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }

    attached() {
        this._methodDetails = [];

        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;

            executionStatistics.classStatistics
                .forEach(classStatistic => {
                    let methodContexts = classStatistic.methodContexts;

                    let methodDetails = methodContexts.map(methodContext => {
                        return new MethodDetails(methodContext, classStatistic);
                    });
                    methodDetails.forEach(methodDetails => {
                        this._methodDetails.push(methodDetails);
                    })
                })

            const names = [];
            const ids = [];
            const durations = [];
            const methodTypes = [];

            this._methodDetails.forEach(method => {
                names.push(method.methodContext.contextValues.name);
                ids.push(method.methodContext.contextValues.id);
                durations.push(this._updateDuration(method.methodContext.contextValues.startTime, method.methodContext.contextValues.endTime));
                methodTypes.push((method.methodContext.methodType))
            })

            this._prepareData(durations,names,ids, methodTypes);

        }).finally(() => {
            this._option = {
                tooltip: {
                    trigger: 'axis',
                    axisPointer: {
                        type: 'shadow'
                    },
                    // this enables that the y coordinate stays the same => should facilitate clicking inside later on
                    // position: function (point) {
                    //     var x = point[0]-100;
                    //     return [x.toString(), '20%'];
                    // },
                    formatter: function (params) {
                        if (params.length > 0) {
                            const dataIndex = params[0].dataIndex; // gives the index of the data point for bar chart
                            const testNumber = this._durationOptions.durationAmount[dataIndex];
                            const testNames = this._durationOptions.testNames;

                            let tooltipString = testNumber + ` test cases: <br>`;
                            tooltipString += "<ul>";

                            testNames[dataIndex].forEach(testCase => {
                                tooltipString += `<li>${testCase}</li>`;
                            });

                            tooltipString += "</ul>";
                            return tooltipString;
                        }
                        return ""; // Return an empty string if no data points are hovered on
                    }.bind(this), // Binding the current context to the formatter function to access this._durationOptions
                },
                xAxis: {
                    type: 'category',
                    data: this._durationOptions.labels, //TODO: add dynamic scaling to the axis
                },
                yAxis: {
                    type: 'value',
                    minInterval: 1, //allows only integer values
                },
                series: [
                    {
                        data: this._durationOptions.durationAmount,
                        type: 'bar',
                        itemStyle: {
                            color: '#6897EA',
                        }
                    }
                ]
            };

            this._attached = true;
            if (this._option) {
                this._createChart();
            }
        })
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
    }

    private _createChart() {
        this._option && this._chart.setOption(this._option)
    }

    private _prepareData(durations: number[], names: string[], ids: string[], methodTypes: number[]) {
        const nameMap: { [duration: number]: string[] } = {};
        const idMap: { [duration: number]: string[] } = {};
        const methodTypeMap: { [duration: number]: number[] } = {};
        const dataCountMap: { [duration: number]: number } = {};

        durations.forEach((datum, index) => {
            if (!dataCountMap.hasOwnProperty(datum)) {
                dataCountMap[datum] = 0;
                nameMap[datum] = [];
                idMap[datum] = [];
                methodTypeMap[datum] = [];
            }

            // if(methodTypes[index] === 1){
                dataCountMap[datum]++;
                nameMap[datum].push(names[index]);
                idMap[datum].push(ids[index]);
                methodTypeMap[datum].push(methodTypes[index]);
            // } else if(methodTypes[index] === 2){
            //     //fix
            // }
        });

        this._durationOptions = {
            labels: Object.keys(dataCountMap).map(count => `${count}s`),
            durationAmount: Object.values(dataCountMap),
            testIds: Object.values(idMap),
            testNames: Object.values(nameMap),
            methodType: Object.values(methodTypeMap),
        };
    }

    private _updateDuration(startTime, endTime) {
        if (!endTime) {
            this._hasEnded = false;
            endTime = new Date().getMilliseconds();
        } else {
            this._hasEnded = true;
        }
        return Math.ceil(moment.duration(endTime - startTime, 'milliseconds').asSeconds());
    }

    private _showConfigurationMethodsChanged(){
        if(this._showConfigurationMethods){
            console.log("on")
        } else {
            console.log("off")
        }
    }
}

export interface IDurationOptions {
    labels: string[];
    durationAmount: number[];
    testNames;
    testIds;
    methodType;
}
