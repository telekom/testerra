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

import {autoinject, bindable, observable} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../../abstract-view-model";
import * as echarts from 'echarts';
import {EChartsOption} from 'echarts';
import "./test-timings.scss";
import {ExecutionStatistics} from "services/statistic-models";
import {MethodDetails, StatisticsGenerator} from "services/statistics-generator";
import moment from "moment";
import {data} from "../../../services/report-model";
import {StatusConverter} from "../../../services/status-converter";
import MethodType = data.MethodType;
import IMethodContext = data.MethodContext;

@autoinject()
export class TestTimings extends AbstractViewModel {
    private _chart: echarts.ECharts;
    private _executionStatistics: ExecutionStatistics
    private _option: EChartsOption;
    private _hasEnded = false;
    private _methodDetails: MethodDetails[];
    private _labels: string[];
    private _sectionValues: number[];
    private _data: number[];
    private _bars: IDurationBar[];
    private _loading = false;
    private _searchRegexp: RegExp;
    private _inputValue = '';
    private _methodId: string;
    @bindable private _rangeNum;
    private _rangeOptions = ['5','10','15','20'];
    private _showConfigurationMethods: boolean = null;
    private _lookUpOptions;
    @observable private selectedOptionId;
    private _methodNameInput;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
        this._bars = [];
        this._lookUpOptions = [];
        this._option = {};
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        if(!this.queryParams.rangeNum){
            this._rangeNum = '10';
            this.queryParams.rangeNum = parseInt(this._rangeNum);
        }
        if (params.config) {
            this._showConfigurationMethods = !!params.config.toLowerCase();
        }
        this._getLookUpOptions()
        this._filter();
    }

    private _getLookUpOptions() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            let methodContexts:IMethodContext[];
            if (this.queryParams.methodId) {
                methodContexts = [executionStatistics.executionAggregate.methodContexts[this.queryParams.methodId]];
                this._highlightData();
                this.updateUrl({methodId: this.queryParams.methodId});
                this._methodId = this.queryParams.methodId;
            } else if (this._inputValue?.length > 0) {
                this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this._inputValue);
                delete this.queryParams.methodId;
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts)
                    .filter(methodContext => methodContext.contextValues.name.match(this._searchRegexp))
            } else {
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts);
            }
            if (!this._showConfigurationMethods) {
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

    selectedOptionIdChanged(n: string){
        if(n){
            this._methodId = n;
            this.queryParams.methodId = n
            this.updateUrl(this.queryParams);
            this._highlightData()
        }
    }

    selectionChanged() { // called when input from lookup text field is deleted manually
        this._setChartOption(); // overwrites color highlighting (reset)
        if (this._inputValue.length == 0) {
            this._methodId = undefined;
            this.queryParams.methodId = undefined;
            this.updateUrl({rangeNum: this._rangeNum, methodId: this._methodId});
            this._getLookUpOptions();
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

                    if (!this._showConfigurationMethods) {
                        methodContexts = methodContexts.filter(methodContext => methodContext.methodType == MethodType.TEST_METHOD);
                    }

                    let methodDetails = methodContexts.map(methodContext => {
                        return new MethodDetails(methodContext, classStatistic);
                    });
                    methodDetails.forEach(methodDetail => {
                        this._methodDetails.push(methodDetail);
                    })
                })

            const testDurationMethods = [];

            if (this.queryParams.methodId && this._methodDetails) { //prevents that input disappears after refreshing the page
                const inputMethod = this._methodDetails.find(method => method.methodContext.contextValues.id === this.queryParams.methodId);
                const inputValueFromQueryParam = inputMethod.methodContext.contextValues.name;
                if (inputValueFromQueryParam) {
                    this._inputValue = inputValueFromQueryParam;
                }
            }

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
            this._setChartOption()
            this._methodId = this.queryParams.methodId;
            this._rangeNum = this.queryParams.rangeNum
            if(this.queryParams.methodId != undefined){
                this._highlightData()
            }
        })
    }

    private _showConfigurationChanged() {
        if(!this._showConfigurationMethods && this.queryParams.methodId && this._methodDetails.find(method => method.methodContext.contextValues.id === this.queryParams.methodId).methodContext.methodType == MethodType.CONFIGURATION_METHOD){
            delete this.queryParams.methodId
            this._methodId = null;
        }
        this._getLookUpOptions();
        this._filterOnce();
    }

    private _filterOnce() {
        if (!this._loading) {
            this._filter();
        }
    }

    private _setChartOption(){
        this._option = {
            tooltip: {
                position: function(point){
                    return {top: 0, left: point[0]};
                },
                trigger: 'axis',
                axisPointer: {
                    type: 'shadow'
                },
                formatter: function (params) {
                    if (params.length > 0) {
                        const dataIndex = params[0].dataIndex;
                        const testNumber = this._bars[dataIndex].durationAmount;

                        if (testNumber === 0) {
                            return "";
                        }

                        const testNames = this._bars[dataIndex].methodList.map(method => method.name);
                        let tooltipString = `${testNumber} test case(s): <br>`;

                        if (testNumber < 20) {
                            tooltipString += "<ul>";
                            testNames.forEach(testCase => {
                                tooltipString += `<li>${testCase}</li>`;
                            });
                            tooltipString += "</ul>";
                        } else {
                            const displayedTestNames = testNames.slice(0, 19);
                            const remainingTestCount = testNames.length - 20;

                            tooltipString += "<ul>";
                            displayedTestNames.forEach(testCase => {
                                tooltipString += `<li>${testCase}</li>`;
                            });
                            tooltipString += "</ul>";
                            tooltipString += ` and ${remainingTestCount} more`;
                        }

                        return tooltipString;
                    }

                    return ""; // Return an empty string if no data points are hovered on
                }.bind(this), // Binding the current context to the formatter function to access this._durationOptions
            },
            xAxis: {
                type: 'category',
                data: this._labels,
                name: 'duration'
            },
            yAxis: {
                type: 'value',
                minInterval: 1, //allows only integer values
                name: 'number of test cases'
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
    }

    private _highlightData() {
        if (this._bars.length > 0) {
            const dataIndex = this._bars.findIndex(value => value.methodList.find(value => value.id === this._methodId));
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
    }

    private _prepareData(methods: ITestDurationMethod[]) {
        const durations = methods.map(method => method.duration);
        this._calculateDurationAxis(durations);

        this._bars = this._sectionValues.map((sectionValue, i) => {
            const methodList = methods.reduce((list, method) => {
                if (method.duration <= sectionValue && (method.duration > this._sectionValues[i - 1] || this._sectionValues[i - 1] === undefined)) {
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
        this._rangeNum = this.queryParams.rangeNum;
        durations.sort((a, b) => a - b)

        let maxDuration = durations[durations.length - 1];
        const remainder = maxDuration % this._rangeNum;
        maxDuration = remainder === 0 ? maxDuration : maxDuration + (this._rangeNum - remainder);

        const sectionRange = maxDuration / this._rangeNum;

        const resultDurations: number[] = Array(this._rangeNum).fill(0);
        const resultSections: string[] = Array(this._rangeNum).fill("");

        // calculate ranges
        for (let i = 0; i < this._rangeNum; i++) {
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

    private _rangeNumChanged(){
        if(this._rangeNum != this.queryParams.rangeNum) {
            this._filter();
            this.queryParams.rangeNum = this._rangeNum
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


