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
import {HistoryStatistics} from "../../../services/statistic-models";
import {StatusConverter} from "../../../services/status-converter";
import {StatisticsGenerator} from "../../../services/statistics-generator";
import {ClassName, ClassNameValueConverter} from "../../../value-converters/class-name-value-converter";
import {ResultStatusType} from "../../../services/report-model/framework_pb";
import {MdcSelect} from "@aurelia-mdc-web/select";

@autoinject()
export class ClassesHistory extends AbstractViewModel {
    private _selectedClass: string = null;
    private _previousSelectedClass: string;           // Helper variable to prevent unnecessary option building
    private _historyStatistics: HistoryStatistics;
    @observable() private _chart: ECharts;
    private _option: EChartsOption;
    private _data: any[] = [];
    private _singleClassData: any[] = [];
    private _categoryHeight = 80;             // Height of y-axis categories in pixel
    private _categoryWidth = 70;              // Width of y-axis categories in pixel
    private _symbolSize = 60;                 // Width and height of chart symbol in pixel
    private _chartHeaderHeight = 50;          // Height of the top spacing including the scrollbar in pixel
    private _maxYCategoryLength = 35;         // Maximum number of characters for y-category names before linebreak
    private _gridLeftValue: number;
    @observable private _uniqueClasses: any[] = [];
    private _numberOfMethodsInClass = 0;
    private _cardHeight: number;
    private _gridHeight: number;
    private _gridWidth: number;
    private _numberOfRuns: number;
    private _visibleRuns: number;
    private _cardHeadline: string = null;
    private _classSelect: MdcSelect;
    private _historyAvailable = false;

    constructor(
        private _router: Router,
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _classNameValueConverter: ClassNameValueConverter,
        private _dateFormatter: IntlDateFormatValueConverter,
        private _durationFormatter: DurationFormatValueConverter
    ) {
        super();
        this._option = {};
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;

        const self = this;
        if (this.queryParams.class || params.class) {
            const classInParams = params.class;
            window.setTimeout(() => {
                self._selectedClass = classInParams;
                self._classSelect.value = self._uniqueClasses.find(classId => classId == classInParams);      // necessary to keep selection after refreshing the page
            }, 200);
        } else {
            this._selectedClass = null;
        }
    }

    attached() {
        this._gridLeftValue = this._maxYCategoryLength * 7;

        this._statisticsGenerator.getHistoryStatistics().then(historyStatistics => {
            this._historyStatistics = historyStatistics;
            if (historyStatistics.getTotalRunCount() > 1) {
                this._historyAvailable = true;
                // Calculate the number of visible runs based on the container width
                const dataGridWidth = document.getElementById('classes-history-chart-container').clientWidth - this._gridLeftValue - 10;
                this._visibleRuns = Math.floor(dataGridWidth / this._categoryWidth);
            } else {
                this._cardHeadline = "No history available"
                return;
            }
            this._initDurationFormatter();
            this._prepareChartData();

            this._classChanged();
        });
    }

    private _classChanged() {
        if (this._selectedClass) {
            this.updateUrl({class: this._selectedClass});
            this._cardHeadline = "History of all testcases in class: " + this._classNameValueConverter.toView(this._selectedClass.toString(), ClassName.simpleName);
            this._prepareSingleClassChartData();
            this._adaptChartSize(this._numberOfMethodsInClass);
            this._setChartOptionForSingleClass();
        } else {
            this.updateUrl({});
            this._selectedClass = null;
            delete this.queryParams.class;
            this._cardHeadline = "History of all test classes";
            this._numberOfRuns = this._historyStatistics.getTotalRunCount();
            this._adaptChartSize(this._uniqueClasses.length);
            this._setChartOption();
        }

        let startValue = Math.max(0, ((this._numberOfRuns - this._visibleRuns) / this._numberOfRuns) * 100);

        this._option.dataZoom = [
            {
                type: 'slider',
                xAxisIndex: [0],
                start: startValue,
                height: 20,
                end: 100,
                brushSelect: false,
                zoomLock: true,
                showDataShadow: false,
                top: 'top'
            }
        ]
    }

    private _adaptChartSize(yItems: number) {
        this._gridHeight = (yItems * this._categoryHeight);
        this._cardHeight = this._gridHeight + 20 + this._chartHeaderHeight;
        this._gridWidth = ((Math.min(this._visibleRuns, this._numberOfRuns)) * this._categoryWidth);
    }

    private _initDurationFormatter() {
        const container = new Container();
        this._durationFormatter = container.get(DurationFormatValueConverter);
        this._durationFormatter.setDefaultFormat("h[h] m[min] s[s] S[ms]");
    }

    private _prepareSingleClassChartData() {
        if (this._previousSelectedClass === this._selectedClass) {
            return;
        }

        this._singleClassData = [];
        this._previousSelectedClass = this._selectedClass;
        let numberOfClassRuns = 0;

        const style = new Map<number, string>();
        style.set(ResultStatusType.PASSED, this._statusConverter.getColorForStatus(ResultStatusType.PASSED));
        style.set(ResultStatusType.REPAIRED, this._statusConverter.getColorForStatus(ResultStatusType.REPAIRED));
        style.set(ResultStatusType.PASSED_RETRY, this._statusConverter.getColorForStatus(ResultStatusType.PASSED_RETRY));
        style.set(ResultStatusType.SKIPPED, this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED));
        style.set(ResultStatusType.FAILED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED));
        style.set(ResultStatusType.FAILED_EXPECTED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED));
        style.set(ResultStatusType.FAILED_MINOR, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_MINOR));
        style.set(ResultStatusType.FAILED_RETRIED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_RETRIED));

        const methodsInClass = this._historyStatistics.getMethodHistoryStatistics().filter(method =>
            method.classIdentifier === this._selectedClass &&
            method.isConfigurationMethod() === false
        );

        methodsInClass.forEach(method => {
            numberOfClassRuns = Math.max(numberOfClassRuns, method.getRunCount());
            const methodIdentifier = method.identifier;
            method.runs.forEach(methodRun => {
                const historyIndex = methodRun.historyIndex;
                const status = methodRun.context.resultStatus;
                const startTime = methodRun.context.contextValues.startTime;
                const endTime = methodRun.context.contextValues.endTime;

                this._singleClassData.push({
                    value: [historyIndex, methodIdentifier],
                    itemStyle: {
                        color: style.get(status),
                        opacity: 1
                    },
                    status: status,
                    statusName: this._statusConverter.getLabelForStatus(status),
                    errorMessage: methodRun.errorMessage,
                    startTime: startTime,
                    endTime: endTime,
                    duration: endTime - startTime
                });
            });
        });

        // Sort the data by historyIndex and methodIdentifier
        this._singleClassData.sort((a, b) => {
            if (a.value[0] !== b.value[0]) {
                return a.value[0] - b.value[0];
            }
            const identifierA = a.value[1];
            const identifierB = b.value[1];
            if (identifierA < identifierB) return -1;
            if (identifierA > identifierB) return 1;
            return 0;
        });

        const uniqueMethodNames = new Set(this._singleClassData.map(entry => entry.value[1]));
        this._numberOfMethodsInClass = uniqueMethodNames.size;
        this._numberOfRuns = numberOfClassRuns;
    }

    private _setChartOptionForSingleClass() {
        const dateFormatter = this._dateFormatter;
        const durationFormatter = this._durationFormatter;
        let maxYCategoryLength = this._maxYCategoryLength;

        this._setCommonChartOptions(maxYCategoryLength);
        this._option.tooltip = {
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
        };
        this._option.series = [
            {
                type: 'scatter',
                symbolSize: this._symbolSize,
                symbol: 'rect',
                data: this._singleClassData,
                z: 2,
                cursor: 'default'
            }
        ];
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

            const classes = Array.from(aggregate.classes.values());

            classes.forEach(testClass => {
                const className = this._classNameValueConverter.toView(testClass.identifier, ClassName.simpleName);
                const relevantStatuses = testClass.statusConverter.relevantStatuses;
                let classStatuses: any[] = [];
                let testcases = 0;

                relevantStatuses.forEach(status => {
                    if (status === ResultStatusType.PASSED) {
                        classStatuses.push(testClass.overallPassed);
                        testcases += testClass.overallPassed;
                    } else {
                        classStatuses.push(testClass.getStatusCount(status));
                        testcases += testClass.getStatusCount(status);
                    }
                });

                // Skip classes with no testcases
                if (testcases === 0) {
                    return;
                }

                if (!this._uniqueClasses.includes(testClass.identifier)) {
                    this._uniqueClasses.push(testClass.identifier);
                }

                const startTime = testClass.classContext.contextValues.startTime;
                const endTime = testClass.classContext.contextValues.endTime;

                this._data.push({
                    value: [
                        historyIndex,
                        className,
                        classStatuses[3],
                        classStatuses[0],
                        classStatuses[1],
                        classStatuses[2]
                    ],
                    itemStyle: {
                        color: 'rgb(221,221,221)',
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

    private _setChartOption() {
        const dateFormatter = this._dateFormatter;
        const durationFormatter = this._durationFormatter;

        // Variables to construct the custom chart elements
        const maxYCategoryLength = this._maxYCategoryLength;
        const subQuadWidth = Math.sqrt((this._symbolSize * this._symbolSize) / 4);
        const largeSubQuadLength = subQuadWidth * 2;
        const subQuadHeight = subQuadWidth * 2 / 3;

        this._setCommonChartOptions(maxYCategoryLength);

        this._option.tooltip = {
            trigger: 'item',
            formatter: function (params) {
                const statuses = params.value.slice(2);
                const failed = statuses[1];
                const expectedFailed = statuses[2];
                const skipped = statuses[3];
                const passed = statuses[0];

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
        };
        this._option.series = [
            {
                type: 'custom',
                cursor: 'default',
                renderItem: function (params, api) {
                    const statuses = [api.value(2), api.value(3), api.value(4), api.value(5)];
                    const x = api.coord([api.value(0), api.value(1)])[0];
                    const y = api.coord([api.value(0), api.value(1)])[1];

                    const children = [];

                    const statusCounts = [
                        Number(api.value(2)),
                        Number(api.value(3)),
                        Number(api.value(4)),
                        Number(api.value(5))
                    ];

                    // invisible rect as background
                    if (statusCounts[0] === 0 || (statusCounts[1] === 0 && statusCounts[2] === 0) || statusCounts[3] === 0) {
                        children.push(
                            {
                                type: 'rect',
                                shape: {
                                    x: x - subQuadWidth,
                                    y: y - (subQuadHeight * 3 / 2),
                                    width: largeSubQuadLength,
                                    height: largeSubQuadLength
                                },
                                style: {fill: '#00000000'}
                            });
                    }

                    // passed-rect
                    if (statusCounts[0] > 0) {
                        children.push(
                            {
                                type: 'rect',
                                shape: {
                                    x: x - subQuadWidth,
                                    y: y - (subQuadHeight * 3 / 2),
                                    width: largeSubQuadLength,
                                    height: subQuadHeight
                                },
                                style: {fill: '#417336'}
                            },
                            {
                                type: 'text',
                                style: {
                                    x: x,
                                    y: y - subQuadHeight,
                                    text: statuses[0].toString(),
                                    fill: '#fff',
                                    fontSize: 12,
                                    align: 'center',
                                    verticalAlign: 'middle'
                                }
                            }
                        );
                    }

                    // failed-rect
                    if (statusCounts[1] > 0) {
                        let failedWidth = subQuadWidth;
                        let failedTextPos = (subQuadWidth / 2);
                        if (statusCounts[2] === 0) {
                            failedWidth = largeSubQuadLength;
                            failedTextPos = 0;
                        }
                        children.push(
                            {
                                type: 'rect',
                                shape: {
                                    x: x - subQuadWidth,
                                    y: y - (subQuadHeight / 2),
                                    width: failedWidth,
                                    height: subQuadHeight
                                },
                                style: {fill: '#e63946'}
                            },
                            {
                                type: 'text',
                                style: {
                                    x: x - failedTextPos,
                                    y: y,
                                    text: statuses[1].toString(),
                                    fill: '#fff',
                                    fontSize: 12,
                                    align: 'center',
                                    verticalAlign: 'middle'
                                }
                            }
                        );
                    }

                    // failed_expected-rect
                    if (statusCounts[2] > 0) {
                        let expectedFailedWidth = subQuadWidth;
                        let expectedFailedTextPos = (subQuadWidth / 2);
                        let expectedFailedPosX = 0;
                        if (statusCounts[1] === 0) {
                            expectedFailedWidth = largeSubQuadLength;
                            expectedFailedTextPos = 0;
                            expectedFailedPosX = subQuadWidth;
                        }
                        children.push(
                            {
                                type: 'rect',
                                shape: {
                                    x: x - expectedFailedPosX,
                                    y: y - (subQuadHeight / 2),
                                    width: expectedFailedWidth,
                                    height: subQuadHeight
                                },
                                style: {fill: '#4f031b'}
                            },
                            {
                                type: 'text',
                                style: {
                                    x: x + expectedFailedTextPos,
                                    y: y,
                                    text: statuses[2].toString(),
                                    fill: '#fff',
                                    fontSize: 12,
                                    align: 'center',
                                    verticalAlign: 'middle'
                                }
                            }
                        );
                    }

                    // skipped-rect
                    if (statusCounts[3] > 0) {
                        children.push(
                            {
                                type: 'rect',
                                shape: {
                                    x: x - subQuadWidth,
                                    y: y + (subQuadHeight / 2),
                                    width: largeSubQuadLength,
                                    height: subQuadHeight
                                },
                                style: {fill: '#f7af3e'}
                            },
                            {
                                type: 'text',
                                style: {
                                    x: x,
                                    y: y + subQuadHeight,
                                    text: statuses[3].toString(),
                                    fill: '#fff',
                                    fontSize: 12,
                                    align: 'center',
                                    verticalAlign: 'middle'
                                }
                            }
                        );
                    }

                    return {
                        type: 'group',
                        children: children
                    };
                },
                encode: {
                    x: 0,
                    y: 1
                },
                data: this._data
            }
        ];
    }

    private _setCommonChartOptions(maxYCategoryLength: number) {
        this._option = {
            grid: {
                height: this._gridHeight,
                width: this._gridWidth,
                top: this._chartHeaderHeight,
                bottom: 0,
                left: this._gridLeftValue,
                right: 0
            },
            yAxis: {
                type: 'category',
                show: true,
                axisLabel: {
                    formatter: function (value) {
                        const regex = new RegExp(`.{1,${maxYCategoryLength}}`, 'g');
                        return value.match(regex)?.join('\n');
                    }
                },
                splitLine: {
                    show: true,
                    lineStyle: {
                        color: '#c3c3c3',
                        type: 'solid',
                        width: 1
                    }
                },
                splitArea: {
                    show: true,
                    areaStyle: {
                        color: ['rgb(255,255,255)', 'rgb(239,239,239)'],
                        opacity: 1
                    }
                }
            },
            xAxis: {
                type: 'category',
                position: 'top',
                show: true
            }
        }
    }
}
