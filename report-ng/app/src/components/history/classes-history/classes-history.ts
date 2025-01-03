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
import {HistoryStatistics, MethodHistoryStatistics} from "../../../services/statistic-models";
import {StatusConverter} from "../../../services/status-converter";
import {StatisticsGenerator} from "../../../services/statistics-generator";
import {ClassName, ClassNameValueConverter} from "../../../value-converters/class-name-value-converter";
import {MethodType, ResultStatusType} from "../../../services/report-model/framework_pb";
import {MdcSelect} from "@aurelia-mdc-web/select";

@autoinject()
export class ClassesHistory extends AbstractViewModel {
    private _historyStatistics: HistoryStatistics;
    private _uniqueClasses: any[] = [];
    @observable() private _chart: ECharts;
    private _availableClasses: any[] = [];
    private _availableStatuses: ResultStatusType[] = [];
    private _availableOverviewStatuses: ResultStatusType[] = [];
    private _option: EChartsOption;
    private _data: any[] = [];
    private _selectedClass: string = null;
    private _selectedStatus: ResultStatusType = null;
    private _categoryHeight = 80;             // Height of y-axis categories in pixel
    private _categoryWidth = 62;              // Width of y-axis categories in pixel
    private _symbolSize = 60;                 // Width and height of chart symbol in pixel
    private _chartHeaderHeight = 50;          // Height of the top spacing including the scrollbar in pixel
    private _maxYCategoryLength = 45;         // Maximum number of characters for y-category names before linebreak
    private _gridLeftValue: number;
    private _numberOfMethodsInClass = 0;
    private _cardHeight: number;
    private _gridHeight: number;
    private _gridWidth: number;
    private _numberOfRuns: number;
    private _visibleRuns: number;
    private _cardHeadline: string = null;
    private classSelect: MdcSelect;
    private statusSelect: MdcSelect;
    private _historyAvailable = false;
    private _initialChartLoading = true;
    private _skipChartReloading = false;
    private _maxErrorMessageLength = 400;

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

    attached() {
        this._gridLeftValue = this._maxYCategoryLength * 7;

        this._statisticsGenerator.getHistoryStatistics().then(historyStatistics => {
            this._historyStatistics = historyStatistics;
            this._numberOfRuns = this._historyStatistics.getTotalRunCount();
            if (this._numberOfRuns > 1) {
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
            this._availableClasses = this._uniqueClasses;
            this._updateChart();

            this._chart.on('click', params => {
                if (params.targetType === 'axisLabel') {
                    if (this._selectedClass) {
                        const foundMethod = this._historyStatistics.getMethodHistoryStatistics().find(method =>
                            method.identifier === params.value &&
                            method.classIdentifier === this._selectedClass &&
                            method.getStatusOfLatestRun() != ResultStatusType.FAILED_RETRIED
                        );
                        if (foundMethod) {
                            this._navigateToMethodHistory(foundMethod)
                        }
                    } else {
                        this._selectedClass = this._uniqueClasses.find(clsName => clsName.endsWith(params.value));
                    }
                }
            });
        });
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;

        const self = this;
        if ((this.queryParams.class || params.class) && (this.queryParams.status || params.status)) {
            // Prevent that the chart is updated twice if both filters are set
            this._skipChartReloading = true;
        }
        if (this.queryParams.class || params.class) {
            window.setTimeout(() => {
                self._selectedClass = params.class;
                self.classSelect.value = self._uniqueClasses.find(cls => cls === self._selectedClass);      // necessary to keep selection after refreshing the page
            }, 200);
        } else {
            this._selectedClass = null;
        }
        if (this.queryParams.status || params.status) {
            window.setTimeout(() => {
                self._selectedStatus = self._statusConverter.getStatusForClass(params.status);
                self.statusSelect.value = self._statusConverter.normalizeStatus(self._statusConverter.getStatusForClass(self.queryParams.status)).toString();       // necessary to keep selection after refreshing the page
            }, 200);
        } else {
            this._selectedStatus = null;
        }
        this._initialChartLoading = false;
    }

    private _classChanged() {
        this._filterChanged(true);
    }

    private _statusChanged() {
        this._filterChanged();
    }

    private _filterChanged(resetSelectedStatus?: boolean) {
        if (this._skipChartReloading) {
            this._skipChartReloading = false;
            return;
        }
        if (resetSelectedStatus) {
            this._selectedStatus = null;
        }

        if (this._selectedClass) {
            this.queryParams.class = this._selectedClass;
        } else {
            if (!this._initialChartLoading) {
                delete this.queryParams.class;
            }
        }
        if (this._selectedStatus) {
            this.queryParams.status = this._statusConverter.getClassForStatus(this._selectedStatus);
        } else {
            if (!this._initialChartLoading) {
                delete this.queryParams.status;
            }
        }
        this.updateUrl(this.queryParams);
        this._updateChart();
    }

    private _updateChart() {
        if (this._selectedClass) {
            this._cardHeadline = "History of all testcases in class: " + this._classNameValueConverter.toView(this._selectedClass.toString(), ClassName.simpleName);
            this._prepareSingleClassChartData();
            this._adaptChartSize(this._numberOfMethodsInClass);
            this._setChartOptionForSingleClass();
        } else {
            this._prepareChartData();
            this._cardHeadline = "History of all test classes";
            this._setChartOption();
        }
        this._addDataZoomSlider();
    }

    private _addDataZoomSlider() {
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

    private compareByIndexAndName(a: any, b: any): number {
        if (a.value[0] !== b.value[0]) {
            return a.value[0] - b.value[0];
        }
        if (a.value[1] > b.value[1]) return -1;
        if (a.value[1] < b.value[1]) return 1;
        return 0;
    }

    private _truncateErrorMessage(str: string): string {
        if (str.length <= this._maxErrorMessageLength) {
            return str;
        }
        return str.slice(0, this._maxErrorMessageLength - 3) + '...';
    }

    private _prepareSingleClassChartData() {
        let newData = [];

        const style = new Map<number, string>();
        style.set(ResultStatusType.PASSED, this._statusConverter.getColorForStatus(ResultStatusType.PASSED));
        style.set(ResultStatusType.REPAIRED, this._statusConverter.getColorForStatus(ResultStatusType.REPAIRED));
        style.set(ResultStatusType.PASSED_RETRY, this._statusConverter.getColorForStatus(ResultStatusType.PASSED_RETRY));
        style.set(ResultStatusType.SKIPPED, this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED));
        style.set(ResultStatusType.FAILED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED));
        style.set(ResultStatusType.FAILED_EXPECTED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED));
        style.set(ResultStatusType.FAILED_MINOR, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_MINOR));
        style.set(ResultStatusType.FAILED_RETRIED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_RETRIED));

        let availableStatuses = new Set<ResultStatusType>();
        let uniqueHistoryIndices = new Set<number>();

        this._historyStatistics.getHistoryAggregateStatistics().forEach(aggregate => {
            const historyIndex = aggregate.historyAggregate.historyIndex;
            const foundClass = Array.from(aggregate.classes.values()).find(currentClass =>
                currentClass.identifier === this._selectedClass
            );
            if (foundClass) {
                foundClass.methods.forEach(method => {
                    if (method.context.methodType === MethodType.TEST_METHOD) {
                        const status = method.context.resultStatus;
                        if (status === ResultStatusType.FAILED_RETRIED) {
                            return;
                        }
                        if (this._selectedStatus) {
                            if (this._selectedStatus != status) {
                                return;
                            }
                        }

                        availableStatuses.add(status);
                        uniqueHistoryIndices.add(historyIndex);

                        const startTime = method.context.contextValues.startTime;
                        const endTime = method.context.contextValues.endTime;

                        newData.push({
                            value: [historyIndex, method.identifier],
                            itemStyle: {
                                color: style.get(status),
                                opacity: 1
                            },
                            status: status,
                            statusName: this._statusConverter.getLabelForStatus(status),
                            errorMessage: method.getCombinedErrorMessage(),
                            startTime: startTime,
                            endTime: endTime,
                            duration: endTime - startTime
                        });
                    }
                });
            }
        });

        this._data = newData;
        if (!this._selectedStatus) {
            this._availableStatuses = Array.from(availableStatuses.values());
        }

        this._data.sort(this.compareByIndexAndName);
        const uniqueMethodNames = new Set(this._data.map(entry => entry.value[1]));
        this._numberOfMethodsInClass = uniqueMethodNames.size;

        this._numberOfRuns = uniqueHistoryIndices.size;
    }

    private _setChartOptionForSingleClass() {
        const dateFormatter = this._dateFormatter;
        const durationFormatter = this._durationFormatter;
        let maxYCategoryLength = this._maxYCategoryLength;
        const self = this;

        this._setCommonChartOptions(maxYCategoryLength);
        this._option.tooltip = {
            trigger: 'item',
            formatter: function (params) {
                let tooltip = '<div class="header" style="background-color: ' +
                    params.color + ';">' + params.data.statusName + '</div>' +
                    '<br>Run ' + params.value[0];

                if (params.data.errorMessage) {
                    tooltip += '<br><div class="tooltip-content">' + self._truncateErrorMessage(params.data.errorMessage) + '</div>';
                }

                tooltip += '<br>Start time: ' + dateFormatter.toView(params.data.startTime, 'full')
                    + '<br>End time: ' + dateFormatter.toView(params.data.endTime, 'full')
                    + '<br>Duration: ' + durationFormatter.toView(params.data.duration);

                return tooltip;
            }
        };

        // Variables to construct the custom chart elements
        const subQuadWidth = Math.sqrt((this._symbolSize * this._symbolSize) / 4);
        const largeSubQuadLength = subQuadWidth * 2;
        const subQuadHeight = subQuadWidth * 2 / 3;

        this._option.series = [
            {
                type: 'custom',
                cursor: 'default',
                renderItem: function (params, api) {
                    const x = api.coord([api.value(0), api.value(1)])[0];
                    const y = api.coord([api.value(0), api.value(1)])[1];

                    const children = [];

                    // invisible rect as background
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

                    // status-rect
                    children.push(
                        {
                            type: 'rect',
                            shape: {
                                x: x - subQuadWidth,
                                y: y - (subQuadHeight / 2),
                                width: largeSubQuadLength,
                                height: subQuadHeight
                            },
                            style: {fill: api.visual("color")}
                        }
                    );

                    return {
                        type: 'group',
                        children: children
                    };
                },
                data: this._data
            }
        ]
    }

    private _prepareChartData() {
        let newData = [];
        this._uniqueClasses = [];
        let availableStatuses = new Set<ResultStatusType>();
        let uniqueHistoryIndices = new Set<number>();

        this._historyStatistics.getHistoryAggregateStatistics().forEach(aggregate => {
            const historyIndex = aggregate.historyAggregate.historyIndex;

            const classes = Array.from(aggregate.classes.values());
            const startTime = aggregate.historyAggregate.executionContext.contextValues.startTime;
            const endTime = aggregate.historyAggregate.executionContext.contextValues.endTime;
            const duration = endTime - startTime;

            classes.forEach(testClass => {
                const className = this._classNameValueConverter.toView(testClass.identifier, ClassName.simpleName);
                const relevantStatuses = testClass.statusConverter.relevantStatuses;
                let statusesInClass = new Map<ResultStatusType, number>();
                let classStatuses: any[] = [];
                let testcases = 0;

                relevantStatuses.forEach(status => {
                    if (status === ResultStatusType.PASSED) {
                        const count = testClass.overallPassed;
                        statusesInClass.set(ResultStatusType.PASSED, count);
                        classStatuses.push(count);
                        testcases += count;
                    } else {
                        const count = testClass.getStatusCount(status);
                        statusesInClass.set(status, count);
                        classStatuses.push(count);
                        testcases += count;
                    }
                });

                // Skip classes with no testcases
                if (testcases === 0) {
                    return;
                }

                if (this._selectedStatus) {
                    const availableStatusesInClass = Array.from(statusesInClass.entries())
                        .filter(([key, value]) => value != 0)
                        .map(([key, value]) => key);
                    if (!availableStatusesInClass.includes(this._selectedStatus)) {
                        return;
                    }
                }

                uniqueHistoryIndices.add(historyIndex);

                const availableStatusesInClass = Array.from(statusesInClass.keys());
                availableStatusesInClass.forEach(status => {
                    availableStatuses.add(status);
                });

                if (!this._uniqueClasses.includes(testClass.identifier)) {
                    this._uniqueClasses.push(testClass.identifier);
                }

                newData.push({
                    value: [
                        historyIndex,
                        className,
                        classStatuses[3], // passed
                        classStatuses[0], // failed
                        classStatuses[1], // expected-failed
                        classStatuses[2]  // skipped
                    ],
                    itemStyle: {
                        color: 'rgb(221,221,221)',
                        opacity: 1
                    },
                    testcases: testcases,
                    startTime: startTime,
                    endTime: endTime,
                    duration: duration
                });
            });
        });

        if (!this._selectedStatus) {
            this._availableStatuses = Array.from(availableStatuses.values());
        }

        this._data = newData;
        this._numberOfRuns = uniqueHistoryIndices.size;
        this._uniqueClasses.sort((a, b) => this._classNameValueConverter.toView(a, 1).localeCompare(this._classNameValueConverter.toView(b, 1)));
        this._data.sort(this.compareByIndexAndName);
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
                return `<div class="class-history-chart-tooltip-header">${params.value[1]}</div>
                        <br>Run ${params.value[0]}
                        <br>Testcases: ${params.data.testcases}
                        <br><br>Run start time: ${dateFormatter.toView(params.data.startTime, 'full')}
                        <br>Run end time: ${dateFormatter.toView(params.data.endTime, 'full')}
                        <br>Run duration: ${durationFormatter.toView(params.data.duration)}`;
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
                triggerEvent: true,
                axisLabel: {
                    formatter: function (value) {
                        const regex = new RegExp(`.{1,${maxYCategoryLength}}`, 'g');
                        return `{link|${value.match(regex)?.join('\n')}}`;
                    },
                    rich: {
                        link: {
                            color: 'blue'
                        }
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

    private _navigateToMethodHistory(methodHistoryStatistics: MethodHistoryStatistics) {
        this._router.navigateToRoute('method', {
            methodId: methodHistoryStatistics.getIdOfLatestRun(),
            subPage: "method-history"
        });
    }
}
