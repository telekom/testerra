/*
 * Testerra
 *
 * (C) 2024, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
import "./classes-history.scss";
import {Container} from "aurelia-dependency-injection";
import {
    DurationFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/duration-format-value-converter";
import {
    IntlDateFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter";
import {ExecutionStatistics, HistoryStatistics} from "../../../services/statistic-models";
import {StatusConverter} from "../../../services/status-converter";
import {StatisticsGenerator} from "../../../services/statistics-generator";
import {ClassName, ClassNameValueConverter} from "../../../value-converters/class-name-value-converter";
import {ResultStatusType} from "../../../services/report-model/framework_pb";

@autoinject()
export class ClassesHistory extends AbstractViewModel {
    private _dateFormatter: IntlDateFormatValueConverter;
    private _durationFormatter: DurationFormatValueConverter;
    private _selectedClass: string;
    private _previousSelectedClass: string = null;
    private _historyStatistics: HistoryStatistics;
    private _executionStatistics: ExecutionStatistics;
    @observable() private _chart: ECharts;
    private _option: EChartsOption;
    private _data: any[] = [];
    private _singleClassData: any[] = [];
    private _categorySize = 70;                 // Height of y-axis categories in pixel
    private _chartHeaderHeight = 30;
    private _symbolSize = 60;
    private _uniqueClasses: any[] = [];
    private _numberOfMethodsInClass = 0;
    private _cardHeight: number;
    private _gridHeight: number;
    private _gridWidth: number;
    private _numberOfRuns: number;
    private _sliderVisible = false;
    private _visibleRuns = 18;

    constructor(
        private _router: Router,
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _classNameValueConverter: ClassNameValueConverter
    ) {
        super();
        this._option = {};
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;
    }

    async attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            this._executionStatistics.classStatistics.sort((a, b) => this._classNameValueConverter.toView(a.classIdentifier, 1).localeCompare(this._classNameValueConverter.toView(b.classIdentifier, 1)))
        });
        this._historyStatistics = await this._statisticsGenerator.getHistoryStatistics();
        this._numberOfRuns = this._historyStatistics.getTotalRuns();
        this._initDateFormatter();
        this._initDurationFormatter();
        this._prepareChartData();

        this._classChanged();
    };

    private _classChanged() {
        this._sliderVisible = (this._historyStatistics.getTotalRuns() > (this._visibleRuns - 1));

        if (this._selectedClass) {
            this._prepareSingleClassChartData();
            this._adaptChartSize(this._numberOfMethodsInClass);
            this._setChartOptionForSingleClass();
        } else {
            this._adaptChartSize(this._uniqueClasses.length);
            this._setChartOption();
        }

        if(this._sliderVisible) {
            let endValue = ((this._visibleRuns - 1) / this._historyStatistics.getTotalRuns()) * 100;

            this._option.dataZoom = [
                {
                    type: 'slider',
                    xAxisIndex: [0],
                    start: 0,
                    end: endValue,
                    brushSelect: false,
                    zoomLock: true
                }
            ]

            // this._chart.dispatchAction({
            //     type: 'dataZoom',
            //     id: 'threadZoom',
            //     startValue: endValue - this._visibleRuns,
            //     endValue: endValue
            // });
        }
    }

    private _initDurationFormatter() {
        const container = new Container();
        this._durationFormatter = container.get(DurationFormatValueConverter);
        this._durationFormatter.setDefaultFormat("h[h] m[min] s[s] S[ms]");
    }

    private _initDateFormatter() {
        const container = new Container();
        this._dateFormatter = container.get(IntlDateFormatValueConverter);
        this._dateFormatter.setLocale('en-GB');
        this._dateFormatter.setOptions('date', {year: 'numeric', month: 'short', day: 'numeric'});
        this._dateFormatter.setOptions('time', {hour: 'numeric', minute: 'numeric', second: 'numeric', hour12: false});
        this._dateFormatter.setOptions('full', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: 'numeric',
            minute: 'numeric',
            second: 'numeric',
            hour12: false
        });
    }

    private _prepareChartData() {

        const style = new Map<number, string>();
        style.set(ResultStatusType.PASSED, this._statusConverter.getColorForStatus(ResultStatusType.PASSED));
        style.set(ResultStatusType.REPAIRED, this._statusConverter.getColorForStatus(ResultStatusType.REPAIRED));
        style.set(ResultStatusType.PASSED_RETRY, this._statusConverter.getColorForStatus(ResultStatusType.PASSED_RETRY));
        style.set(ResultStatusType.SKIPPED, this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED));
        style.set(ResultStatusType.FAILED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED));
        style.set(ResultStatusType.FAILED_EXPECTED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED));
        style.set(ResultStatusType.FAILED_MINOR, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_MINOR));
        style.set(ResultStatusType.FAILED_RETRIED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_RETRIED));

        this._historyStatistics.getHistoryAggregateStatistics().forEach(aggregate => {
            const historyIndex = aggregate.historyAggregate.historyIndex;

            const classes = aggregate.getClassStatistics();

            classes.forEach(testClass => {
                const className = this._classNameValueConverter.toView(testClass.classIdentifier, ClassName.simpleName);
                const relevantStatuses = testClass.statusConverter.relevantStatuses;
                let classStatuses: any[] = [];
                let testcases = 0;

                relevantStatuses.forEach(status => {
                    classStatuses.push(testClass.getStatusCount(status));
                    testcases += testClass.getStatusCount(status);
                });

                if (testcases === 0) {
                    return;
                }

                let color: string;
                switch (testcases) {
                    case classStatuses[3]:
                        color = style.get(ResultStatusType.PASSED)
                        break;
                    case classStatuses[2]:
                        color = style.get(ResultStatusType.SKIPPED)
                        break;
                    case classStatuses[1]:
                        color = style.get(ResultStatusType.FAILED_EXPECTED)
                        break;
                    default:
                        color = style.get(ResultStatusType.FAILED)
                        break;
                }

                if (!this._uniqueClasses.includes(testClass.classIdentifier)) {
                    this._uniqueClasses.push(testClass.classIdentifier);
                }

                const startTime = testClass.classContext.contextValues.startTime;
                const endTime = testClass.classContext.contextValues.endTime;

                this._data.push({
                    value: [
                        historyIndex,
                        className,
                        classStatuses
                    ],
                    itemStyle: {
                        color: color,
                        opacity: 1
                    },
                    testcases: testcases,
                    startTime: startTime,
                    endTime: endTime,
                    duration: endTime - startTime
                });
            });
        });
        this._adaptChartSize(this._uniqueClasses.length);
    }

    private _prepareSingleClassChartData() {
        if (this._previousSelectedClass === this._selectedClass) {
            return;
        }

        this._singleClassData = [];
        this._previousSelectedClass = this._selectedClass;

        const style = new Map<number, string>();
        style.set(ResultStatusType.PASSED, this._statusConverter.getColorForStatus(ResultStatusType.PASSED));
        style.set(ResultStatusType.REPAIRED, this._statusConverter.getColorForStatus(ResultStatusType.REPAIRED));
        style.set(ResultStatusType.PASSED_RETRY, this._statusConverter.getColorForStatus(ResultStatusType.PASSED_RETRY));
        style.set(ResultStatusType.SKIPPED, this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED));
        style.set(ResultStatusType.FAILED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED));
        style.set(ResultStatusType.FAILED_EXPECTED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED));
        style.set(ResultStatusType.FAILED_MINOR, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_MINOR));
        style.set(ResultStatusType.FAILED_RETRIED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_RETRIED));

        // let numberOfMethodsInClass = 0;
        const relevantStatuses = this._statusConverter.relevantStatuses;

        this._historyStatistics.getHistoryAggregateStatistics().forEach(aggregate => {
            const historyIndex = aggregate.historyAggregate.historyIndex;
            const foundClass = aggregate.getClassStatistics().find(currentClass =>
                currentClass.classIdentifier === this._selectedClass
            );
            if (foundClass) {
                // let methodCount = 0;

                foundClass.methodContexts.forEach(method => {
                    const status = method.resultStatus;

                    if (!(relevantStatuses.includes(status))) {
                        return;
                    }

                    let methodName: string = null;
                    // methodCount++;

                    if (method.testName) {
                        methodName = method.testName;
                    } else {
                        methodName = method.contextValues.name;
                        const params = [];
                        for (const name in method.parameters) {
                            params.push(name + ": " + method.parameters[name]);
                        }
                        if (params.length > 0) {
                            methodName += "(" + params.join(", ") + ")";
                        }
                    }

                    let errorMessage = "";

                    if (status != ResultStatusType.PASSED) {
                        method.testSteps.flatMap(value => value.actions)
                            .forEach(actionDetails => {
                                actionDetails.entries.forEach(entry => {
                                    const errorContext = entry.errorContext;
                                    errorContext.stackTrace.forEach(stackTrace => {
                                        // TODO: How to handle multiple errorMessages
                                        errorMessage = errorMessage.concat(stackTrace.message + " ");
                                    })
                                })
                            });
                    }

                    const color = style.get(status);

                    const startTime = method.contextValues.startTime;
                    const endTime = method.contextValues.endTime;

                    this._singleClassData.push({
                        value: [historyIndex, methodName],
                        itemStyle: {
                            color: color,
                            opacity: 1
                        },
                        status: status,
                        statusName: this._statusConverter.getLabelForStatus(status),
                        errorMessage: errorMessage,
                        startTime: startTime,
                        endTime: endTime,
                        duration: endTime - startTime
                    });
                });
            }
        });
        const uniqueMethodNames = new Set(this._singleClassData.map(entry => entry.value[1]));
        this._numberOfMethodsInClass = uniqueMethodNames.size;
    }

    private _adaptChartSize(yItems: number) {
        let topBottomPlaceholder = 16;
        if (this._sliderVisible) {
            topBottomPlaceholder += 60;
        }

        this._gridHeight = (yItems * this._categorySize);
        this._cardHeight = this._gridHeight + this._chartHeaderHeight + topBottomPlaceholder;
        this._gridWidth = (this._numberOfRuns * this._categorySize);
    }

    private _setChartOptionForSingleClass() {
        const dateFormatter = this._dateFormatter;
        const durationFormatter = this._durationFormatter;

        let maxYCategoryLength = 30;
        let gridLeftValue = maxYCategoryLength * 7;   // Calculate the value for grid:left

        this._option = {
            grid: {
                height: this._gridHeight,
                width: this._gridWidth,
                top: this._chartHeaderHeight,
                bottom: 0,
                left: gridLeftValue,
                right: 0
            },
            tooltip: {
                trigger: 'item',
                formatter: function (params) {
                    let tooltip = '<div class="header" style="background-color: ' +
                        params.color + ';">Run ' + params.value[0] + ": " + params.data.statusName + '</div>'

                    if (params.data.errorMessage) {
                        tooltip += '<br><div class="tooltip-content">' + params.data.errorMessage + '</div>';
                    }

                    tooltip += '<br>Start time: ' + dateFormatter.toView(params.data.startTime, 'full')
                        + '<br>End time: ' + dateFormatter.toView(params.data.endTime, 'full')
                        + '<br>Duration: ' + durationFormatter.toView(params.data.duration);

                    return tooltip;
                }
            },
            xAxis: {
                type: 'category',
                position: 'top',
                show: true
            },
            yAxis: {
                type: 'category',
                show: true,
                axisLabel: {
                    formatter: function (value) {
                        const regex = new RegExp(`.{1,${maxYCategoryLength}}`, 'g');
                        return value.match(regex)?.join('\n');
                    }
                }
            },
            series: [
                {
                    type: 'scatter',
                    symbolSize: this._symbolSize,
                    symbol: 'rect',
                    data: this._singleClassData,
                    z: 2
                }
            ]
        };
    }

    private _setChartOption() {
        const dateFormatter = this._dateFormatter;
        const durationFormatter = this._durationFormatter;

        let maxYCategoryLength = 30;
        let gridLeftValue = maxYCategoryLength * 7;   // Calculate the value for grid:left

        this._option = {
            grid: {
                height: this._gridHeight,
                width: this._gridWidth,
                top: this._chartHeaderHeight,
                bottom: 0,
                left: gridLeftValue,
                right: 0
            },
            tooltip: {
                trigger: 'item',
                formatter: function (params) {
                    const statuses = params.value[2];
                    const failed = statuses[0];
                    const expectedFailed = statuses[1];
                    const skipped = statuses[2];
                    const passed = statuses[3];

                    return `<div class="class-history-chart-tooltip-header">Run ${params.value[0]}</div>
                        <br>Testcases: ${params.data.testcases}
                        ${failed > 0 ? `<br><span class="status-dot status-failed"></span> Failed: ${failed}` : ''}
                        ${expectedFailed > 0 ? `<br><span class="status-dot status-failed-expected"></span> Expected Failed: ${expectedFailed}` : ''}
                        ${skipped > 0 ? `<br><span class="status-dot status-skipped"></span> Skipped: ${skipped}` : ''}
                        ${passed > 0 ? `<br><span class="status-dot status-passed"></span> Passed: ${passed}` : ''}
                        <br><br>Start time: ${dateFormatter.toView(params.data.startTime, 'full')}
                        <br>End time: ${dateFormatter.toView(params.data.endTime, 'full')}
                        <br>Duration: ${durationFormatter.toView(params.data.duration)}`;
                }
            },
            xAxis: {
                type: 'category',
                position: 'top',
                show: true
            },
            yAxis: {
                type: 'category',
                show: true,
                axisLabel: {
                    formatter: function (value) {
                        const regex = new RegExp(`.{1,${maxYCategoryLength}}`, 'g');
                        return value.match(regex)?.join('\n');
                    }
                }
            },
            series: [
                {
                    type: 'scatter',
                    symbolSize: this._symbolSize,
                    symbol: 'rect',
                    encode: {
                        x: 0,
                        y: 1
                    },
                    data: this._data,
                    z: 2
                }
            ]
        };
    }
}
