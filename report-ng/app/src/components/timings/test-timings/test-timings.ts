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

import {autoinject, observable} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import {AbstractViewModel} from "../../abstract-view-model";
import {ECharts, EChartsOption} from 'echarts';
import "./test-timings.scss";
import {ExecutionStatistics} from "services/statistic-models";
import {MethodDetails, StatisticsGenerator} from "services/statistics-generator";
import {data} from "../../../services/report-model";
import {StatusConverter} from "../../../services/status-converter";
import MethodType = data.MethodType;
import * as echarts from "echarts";
import {ResultStatusType} from "../../../services/report-model/framework_pb";

@autoinject()
export class TestTimings extends AbstractViewModel {
    private static readonly TEST_NUMBER_LIMIT = 10;
    private static readonly TEST_COLOR = '#6897EA';
    private static readonly TEST_COLOR_PALE = '#c8d4f4';
    @observable() private _chart: ECharts;
    private _executionStatistics: ExecutionStatistics;
    private _option: EChartsOption;
    private _methodDetails: MethodDetails[];
    private _labels: string[];
    private _sectionValues: number[];
    private _data: number[];
    private _bars: IDurationBar[];
    private _loading = false;
    private _searchRegexp: RegExp;
    private _inputValue = '';
    private _rangeOptions = ['5', '10', '15', '20']; // ranges of time in seconds that the test durations can be sorted in
    private _showConfigurationMethods: boolean = null;
    private _lookUpOptions;
    @observable private selectedOptionId;
    private _methodNameInput;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _router: Router
    ) {
        super();
        this._bars = [];
        this._lookUpOptions = [];
        this._option = {};
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        if (!this.queryParams.rangeNum){
            this.queryParams.rangeNum = '10';   // only set range 10 if there is no range at all (e.g. when navigation from another view)
        }
        this._router = navInstruction.router;

        if (!this.queryParams.rangeNum){
            this.queryParams.rangeNum = '10';// only set range 10 if there is no range at all (e.g. when navigation from another view)
        }
        if (params.config) {
            this._showConfigurationMethods = !!params.config.toLowerCase();
        }
        this._getLookUpOptions()
        this._filter();
    }

    selectedOptionIdChanged(methodId: string) {
        if (methodId) {
            this.queryParams.methodId = methodId
            this.updateUrl(this.queryParams);
            this._highlightData()
        }
    }

    selectionChanged() { // called when input from lookup text field is deleted manually
        this._setChartOption(); // overwrites color highlighting (reset)
        if (this._inputValue.length == 0) {
            delete this.queryParams.methodId;
            this.updateUrl(this.queryParams);
            this._getLookUpOptions();
        }
    }

    private _getLookUpOptions() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            let methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts)
            if (this._inputValue?.length > 0) {
                this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this._inputValue);
                methodContexts = methodContexts.filter(methodContext => methodContext.contextValues.name.match(this._searchRegexp))
            }
            if (!this._showConfigurationMethods) {
                methodContexts = methodContexts.filter(methodContext => methodContext.methodType == MethodType.TEST_METHOD);
            }

            this._lookUpOptions = methodContexts
                .map(methodContext => methodContext.contextValues)
                .filter(data => data && data.name?.trim().length > 0)
                .sort((a, b) => a.name.localeCompare(b.name));
        });
    };

    private _filter() {
        this._loading = true;

        this._methodDetails = [];

        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;

            executionStatistics.classStatistics.forEach(classStatistic => {
                const filteredMethodDetails = classStatistic.methodContexts
                    .filter(methodContext => this._showConfigurationMethods || methodContext.methodType == MethodType.TEST_METHOD)
                    .map(methodContext => new MethodDetails(methodContext, classStatistic));
                this._methodDetails.push(...filteredMethodDetails);
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
                    duration: (method.methodContext.contextValues.endTime - method.methodContext.contextValues.startTime)/1000,
                    methodType: method.methodContext.methodType,
                    status: method.methodContext.resultStatus
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
        }).finally(() => {
            this._loading = false;
            this._setChartOption()
            if (this.queryParams.methodId != undefined) {
                this._highlightData()
            }
        })
    }

    private _showConfigurationChanged() {
        if (!this._showConfigurationMethods
            && this.queryParams.methodId
            && this._methodDetails.find(method => method.methodContext.contextValues.id === this.queryParams.methodId).methodContext.methodType == MethodType.CONFIGURATION_METHOD) {
            delete this.queryParams.methodId
        }
        this._getLookUpOptions();
        this._filterOnce();
    }

    private _filterOnce() {
        if (!this._loading) {
            this._filter();
        }
    }

    private _setChartOption() {
        this._option = {
            tooltip: {
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

                        let tooltipString = `${testNumber} test case(s): <br/><br/>`;

                        this._bars[dataIndex].methodList.slice(0,TestTimings.TEST_NUMBER_LIMIT).map(method => {     // if methodList has less than TEST_NUMBER_LIMIT entries it remains unchanged
                            tooltipString += `<div class="mb1"> 
                                                <span class="ml1 mr1 badge status-${this._statusConverter.getClassForStatus(method.status)}">
                                                ${this._statusConverter.getLabelForStatus(method.status)} </span>`

                            tooltipString += `${method.name}`

                            if(method.methodType==MethodType.CONFIGURATION_METHOD){
                                tooltipString += `<span class="ml1 mr1 config-badge badge__dense tag tag__dense"> Configuration </span>`;
                            }
                            tooltipString += `</div>`
                        });

                        if (testNumber > TestTimings.TEST_NUMBER_LIMIT) {
                            const remainingTestCount = this._bars[dataIndex].methodList.length - TestTimings.TEST_NUMBER_LIMIT;
                            tooltipString += ` and ${remainingTestCount} more`
                        }

                        return tooltipString;
                    }

                    return ""; // Return an empty string if no data points are hovered on
                }.bind(this), // Binding the current context to the formatter function to access this._durationOptions
            },
            xAxis: {
                type: 'category',
                data: this._labels,
                name: 'Duration'
            },
            yAxis: {
                type: 'value',
                minInterval: 1, //allows only integer values
                name: 'Number of test cases'
            },
            series: [
                {
                    data: this._data,
                    type: 'bar',
                    itemStyle: {
                        color: TestTimings.TEST_COLOR,
                    },
                }
            ]
        };
    }

    private _highlightData() {
        if (this._bars.length > 0) {
            const dataIndex = this._bars.findIndex(value => value.methodList.find(value => value.id === this.queryParams.methodId));
            this._option.series[0].data = this._data.map((item, index) => {
                return {
                    value: item,
                    itemStyle: {
                        color: (index === dataIndex) ? TestTimings.TEST_COLOR : TestTimings.TEST_COLOR_PALE
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
                if (method.duration <= sectionValue
                    && (method.duration > this._sectionValues[i - 1]
                        || this._sectionValues[i - 1] === undefined)) {
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
        const rangeNumInt = parseInt(this.queryParams.rangeNum)
        durations.sort((a, b) => a - b)

        let maxDuration = durations[durations.length - 1];
        const remainder = maxDuration % rangeNumInt;
        maxDuration = remainder === 0 ? maxDuration : maxDuration + (rangeNumInt - remainder);

        const sectionRange = maxDuration / rangeNumInt;

        const resultDurations: number[] = Array(rangeNumInt).fill(0);
        const resultSections: string[] = Array(rangeNumInt).fill("");

        // calculate ranges
        for (let i = 0; i < rangeNumInt; i++) {
            const start = i * sectionRange;
            const end = (i + 1) * sectionRange - 1;
            resultSections[i] = `${start}-${end}s`;
            resultDurations[i] = end;
        }

        this._sectionValues = resultDurations;
        this._labels = resultSections;
    }

    private _chartChanged() {
        this._chart.on('click', event => this._handleClickEvent(event));
    }

    private _handleClickEvent(event: echarts.ECElementEvent) {
        const methodList = this._bars[event.dataIndex].methodList.map(method => method.id);
        this._router.navigateToRoute('tests',  {config: this._showConfigurationMethods, methods: methodList.join("~")});
    }
}

interface IDurationBar {
    label: string,
    durationAmount: number;
    methodList: ITestDurationMethod[];
}

interface ITestDurationMethod {
    id: string;
    name: string;
    duration: number;
    methodType: MethodType;
    status: ResultStatusType;
}


