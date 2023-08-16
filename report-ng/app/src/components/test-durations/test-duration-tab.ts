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
import {AbstractViewModel} from "../abstract-view-model";
import * as echarts from 'echarts';
import {EChartsOption} from 'echarts';
import "./test-duration-tab.scss";
import {ExecutionStatistics} from "services/statistic-models";
import {MethodDetails, StatisticsGenerator} from "services/statistics-generator";
import moment from "moment";
import {data} from "../../services/report-model";
import MethodType = data.MethodType;
import IMethodContext = data.IMethodContext;
import {StatusConverter} from "../../services/status-converter";


@autoinject()
export class TestDurationTab extends AbstractViewModel {
    private _chart: echarts.ECharts;
    private _executionStatistics: ExecutionStatistics
    private _option: EChartsOption;
    private _attached = false;
    private _hasEnded = false;
    private _methodDetails: MethodDetails[];
    private _showConfigurationMethods: boolean = null;
    private _labels: string[];
    private _sectionValues: number[];
    private _data: number[];
    private _bars: IDurationBar[];
    private _loading = false;
    private _searchRegexp: RegExp;
    private _inputValue;
    private _lookUpOptions;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);

        if (params.config) {
            this._showConfigurationMethods = !!params.config.toLowerCase();
        }
        this._lookUpOptions = [];
        this._getLookUpOptions()
        this._filter();
    }

    private _getLookUpOptions() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            let methodContexts:IMethodContext[];
            if (this.queryParams.methodId) {
                methodContexts = [executionStatistics.executionAggregate.methodContexts[this.queryParams.methodId]];
                this._searchRegexp = null;
                delete this.queryParams.methodName;
                this._highlightData();
                this.updateUrl({methodId: this.queryParams.methodId});
            } else if (this._inputValue?.length > 0) {
                this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this._inputValue);
                delete this.queryParams.methodId;
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts).filter(methodContext => methodContext.contextValues.name.match(this._searchRegexp));
            } else {
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts);
            }
            if (this._showConfigurationMethods === false || this._showConfigurationMethods === null) {
                methodContexts = methodContexts.filter(methodContext => methodContext.methodType == MethodType.TEST_METHOD);
            }

            this._lookUpOptions = methodContexts.map(methodContext => methodContext.contextValues).sort(function (a, b) {
                if (a.name < b.name) {
                    return -1;
                }
                if (a.name > b.name) {
                    return 1;
                }
                return 0;
            });
        });
    };

    selectionChanged(){
        console.log("selection changed")
        this._setChartOption(); // overwrites color highlighting
        if (this._inputValue.length == 0){
            this.updateUrl({});
        }
    }

    private _filter(){
        this._loading = true;

        this._methodDetails = [];

        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;

            executionStatistics.classStatistics
                .forEach(classStatistic => {
                    let methodContexts = classStatistic.methodContexts;

                    if (this._showConfigurationMethods === false || this._showConfigurationMethods === null) {
                        methodContexts = methodContexts.filter(methodContext => methodContext.methodType == MethodType.TEST_METHOD);
                    }

                    let methodDetails = methodContexts.map(methodContext => {
                        return new MethodDetails(methodContext, classStatistic);
                    });
                    methodDetails.forEach(methodDetails => {
                        this._methodDetails.push(methodDetails);
                    })
                })

            const testDurationMethods = [];

            this._methodDetails.forEach(method => {
                const testDurationMethod: ITestDurationMethod = {
                    id: method.methodContext.contextValues.id,
                    name: method.methodContext.contextValues.name,
                    duration: this._calculateDuration(method.methodContext.contextValues.startTime, method.methodContext.contextValues.endTime),
                    methodType: method.methodContext.methodType
                }
                testDurationMethods.push(testDurationMethod)
            })
            this._prepareData(testDurationMethods);

            if (this._showConfigurationMethods) {
                this.queryParams.config = this._showConfigurationMethods;
            } else {
                delete this.queryParams.config;
            }
            this.updateUrl(this.queryParams);

            this._loading = false;
        }).finally(() => {
            this._attached = true;
            this._setChartOption()

            if(this.queryParams.methodId != undefined){
                this._highlightData()
            }
        })
    }

    private _setChartOption(){
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
                        const testNumber = this._bars[dataIndex].durationAmount;
                        const testNames = this._bars[dataIndex].methodList.map(method => method.name);

                        let tooltipString = testNumber + ` test cases: <br>`;
                        tooltipString += "<ul>";

                        testNames.forEach(testCase => {
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
                data: this._labels,
            },
            yAxis: {
                type: 'value',
                minInterval: 1, //allows only integer values
            },
            series: [
                {
                    data: this._data,
                    type: 'bar',
                    itemStyle: {
                        color: '#6897EA',
                    },
                }
            ]
        };
        this._option && this._chart.setOption(this._option)
    }

    private _highlightData() {
        const methodId = this.queryParams.methodId
        const dataIndex = this._bars.findIndex(value => value.methodList.find(value => value.id === methodId));

        this._option.series[0].data = this._data.map((item, index) => {
                return {
                    value: item,
                    itemStyle: {
                        color: (index === dataIndex) ? '#6897EA' : '#c8d4f4'
                    }
                };
        });
        this._chart.setOption(this._option)
    }

    private _prepareData(methods: ITestDurationMethod[]) {
        const durations = methods.map(method => method.duration);
        this._calculateDurationAxis(durations);

        this._bars = this._sectionValues.map((sectionValue, i) => {
            const methodList = methods.reduce((list, method) => {
                if (method.duration < sectionValue && (method.duration > this._sectionValues[i - 1] || this._sectionValues[i - 1] === undefined)) {
                    list.push(method);
                }
                return list;
            }, []);

            const bar: IDurationBar = {
                durationAmount: methodList.length,
                methodList: methodList,
                label: this._labels[i]
            };
            return bar;
        });

        this._data = this._bars.map(bar => bar.durationAmount);
    }

    private _calculateDurationAxis(durations: number[]) {
        durations.sort((a, b) => a - b)

        let maxDuration = durations[durations.length - 1];
        const remainder = maxDuration % 5;
        maxDuration = remainder === 0 ? maxDuration : maxDuration + (5 - remainder);

        const sectionRange = maxDuration / 5;

        const resultDurations: number[] = Array(5).fill(0);
        const resultSections: string[] = Array(5).fill("");

        // calculate ranges
        for (let i = 0; i < 5; i++) {
            const start = i * sectionRange;
            const end = (i + 1) * sectionRange - 1;
            resultSections[i] = `${start}-${end}s`;
            resultDurations[i] = end;
        }

        this._sectionValues = resultDurations;
        this._labels = resultSections;
    }

    private _calculateDuration(startTime, endTime) {
        if (!endTime) {
            this._hasEnded = false;
            endTime = new Date().getMilliseconds();
        } else {
            this._hasEnded = true;
        }
        return Math.ceil(moment.duration(endTime - startTime, 'milliseconds').asSeconds());
    }

    private _showConfigurationChanged() {
        this._getLookUpOptions();
        this._filterOnce();
    }

    private _filterOnce() {
        if (!this._loading) {
            this._filter();
        }
    }
}

export interface IDurationBar {
    label: string,
    durationAmount: number;
    methodList: ITestDurationMethod[];
}

export interface ITestDurationMethod {
    id: string;
    name: string;
    duration: number;
    methodType: MethodType;
}


