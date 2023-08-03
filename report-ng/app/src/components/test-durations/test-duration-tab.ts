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
import {data} from "../../services/report-model";
import MethodType = data.MethodType;
import IContextValues = data.IContextValues;
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

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);

        if (params.config) {
            if (params.config.toLowerCase()) {
                this._showConfigurationMethods = true;
            } else {
                this._showConfigurationMethods = false;
            }
        }
        this._filter();
    }

    private _getLookupOptions = async (filter: string, methodId: string): Promise<IContextValues[]>  => {
        return this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            let methodContexts:IMethodContext[];
            if (methodId) {
                methodContexts = [executionStatistics.executionAggregate.methodContexts[methodId]];
                this._searchRegexp = null;
                delete this.queryParams.methodName;
                this._focusOn(methodId);
                this.updateUrl({methodId: methodId});
            } else if (filter?.length > 0) {
                this._searchRegexp = this._statusConverter.createRegexpFromSearchString(filter);
                delete this.queryParams.methodId;
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts).filter(methodContext => methodContext.contextValues.name.match(this._searchRegexp));
            } else {
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts);
            }
            return methodContexts.map(methodContext => methodContext.contextValues);
        });
    };

    private _focusOn(methodId: string) {

        // Finds the index of the bar that contains the method that is searched
        const dataIndex = this._bars.findIndex(value => value.methodList.find(value => value.id === methodId));

        const totalDataPoints = this._chart.getOption().series[0].data.length;

        // Create an array of dataIndex representing all bars except for the one that should be highlighted
        const otherDataIndices = Array.from({ length: totalDataPoints }, (_, index) => index);
        otherDataIndices.splice(dataIndex, 1);

        // Downplay all the other bars by changing their color
        this._chart.dispatchAction({
            type: 'highlight',
            seriesIndex: 0, // Assuming the bar chart is the first series
            dataIndex: otherDataIndices,
        });
    }

    selectionChanged(){
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
                .map(classStatistics => {
                    // Determine if we need to enable showing config methods by default if there has any error occurred
                    if (this._showConfigurationMethods === null) {
                        this._showConfigurationMethods = classStatistics.configStatistics.overallFailed > 0;
                    }
                    return classStatistics;
                })
                .forEach(classStatistic => {
                    let methodContexts = classStatistic.methodContexts;

                    if (this._showConfigurationMethods === false) {
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

            this._loading = false;
        }).finally(() => {
            this._attached = true;
            this._updateOption()
        })
    }

    private _updateOption(){
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
                enterable: true,
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
                    emphasis: {
                        itemStyle :{
                            color: '#c8d4f4',
                        }
                    },
                }
            ]
        };

        this._option && this._chart.setOption(this._option)
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
        durations.sort((a, b) => a - b); // sort array in ascending order

        const maxDuration = durations[durations.length - 1];

        // Determine the possible section ranges (multiples of 3 and 5)
        const possibleSectionRanges: number[] = [];
        for (let i = 3; i <= 5; i += 2) {
            for (let j = 1; j <= 10; j++) {
                const sectionRange = i * j;
                if (sectionRange <= maxDuration) {
                    possibleSectionRanges.push(sectionRange);
                } else {
                    break;
                }
            }
        }

        // Choose the section range that results in fewer sections (but at least 5)
        let numSections = 10;
        let chosenSectionRange = possibleSectionRanges[possibleSectionRanges.length - 1];
        for (const sectionRange of possibleSectionRanges) {
            const sections = Math.ceil(maxDuration / sectionRange);
            if (sections >= 5 && sections <= 10 && sections < numSections) {
                numSections = sections;
                chosenSectionRange = sectionRange;
            }
        }

        const resultDurations: number[] = Array(numSections).fill(0);
        const resultSections: string[] = Array(numSections).fill("");

        for (let i = 0; i < numSections; i++) {
            const start = i * chosenSectionRange;
            const end = (i + 1) * chosenSectionRange - 1;
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


