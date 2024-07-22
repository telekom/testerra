/*
 * Testerra
 *
 * (C) 2023, Martin Großmann, Telekom MMS GmbH, Deutsche Telekom AG
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

import {autoinject, observable} from "aurelia-framework";
import {StatusConverter} from "services/status-converter";
import {StatisticsGenerator} from "services/statistics-generator";
import {ExecutionStatistics} from "services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import {Container} from "aurelia-dependency-injection";
import "./threads.scss"
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import * as echarts from "echarts";
import {EChartsOption} from "echarts";
import {data} from "../../services/report-model";
import {
    IntlDateFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter";
import {
    DurationFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/duration-format-value-converter";
import {ResultStatusType} from "../../services/report-model/framework_pb";
import {ClassName, ClassNameValueConverter} from "../../value-converters/class-name-value-converter";
import MethodContext = data.MethodContext;
import {MdcSelect} from "@aurelia-mdc-web/select";

interface MethodInfo {
    id: string;
    name: string;
}

@autoinject()
export class Threads extends AbstractViewModel {

    private _searchRegexp: RegExp;
    private _inputValue;
    private _availableStatuses: data.ResultStatusType[] | number[];
    private _selectedStatus: data.ResultStatusType;
    private _selectedClass: string;
    private statusSelect: MdcSelect;
    private classSelect: MdcSelect;
    private _executionStatistics: ExecutionStatistics;
    private _initialChartLoading = true;
    private _filterActive = false;          // To prevent unnecessary method calls
    private _options: EChartsOption;
    @observable()
    private _chart: echarts.ECharts;

    // Aurelia Value Converters
    private _dateFormatter: IntlDateFormatValueConverter;
    private _durationFormatter: DurationFormatValueConverter;

    // Values for presentation
    private _gapFromBorderToStart = 400;        // To prevent that the beginning of the first test is located ON the y-axis.
    private _threadHeight = 50;                 // Height of y-axis categories in pixel
    private _sliderSpacingFromChart = 90;       // Distance between chart and dataZoom-slider in pixel
    private _cardHeight;
    private _opacityOfInactiveElements = 0.38;  // Default opacity of disabled elements https://m2.material.io/design/interaction/states.html#disabled

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _statistics: StatisticsGenerator,
        private _router: Router,
        private _classNameValueConverter: ClassNameValueConverter
    ) {
        super();
    }

    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            this._executionStatistics.classStatistics.sort((a, b) => this._classNameValueConverter.toView(a.classIdentifier, 1).localeCompare(this._classNameValueConverter.toView(b.classIdentifier, 1)))
            this._availableStatuses = [];
            this._availableStatuses = executionStatistics.availableStatuses;
            this._initDateFormatter();
            this._initDurationFormatter();
            this._prepareTimeline(executionStatistics);
            this._initialChartLoading = false;
        });
    };

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;

        const self = this
        if (this.queryParams.status || params.status) {
            window.setTimeout(() => {
                self._selectedStatus = self._statusConverter.getStatusForClass(params.status);
                self._zoomInOnFilter(self._statusConverter.getStatusForClass(params.status), 7)
                self.statusSelect.value = self._statusConverter.normalizeStatus(self._statusConverter.getStatusForClass(self.queryParams.status)).toString();       // necessary to keep selection after refreshing the page
            }, 200)
        }  else {
            this._selectedStatus = null;
        }

        if (this.queryParams.class || params.class) {
            window.setTimeout(() => {
                self._selectedClass = params.class;
                self._zoomInOnFilter(params.class, 8);
                self.classSelect.value = self._executionStatistics.classStatistics.find(classStat => classStat.classIdentifier == self.queryParams.class).classIdentifier;      // necessary to keep selection after refreshing the page
            }, 200)
        }  else {
            this._selectedClass = null;
        }
    };

    private _getLookupOptions = async (filter: string, methodId: string): Promise<MethodInfo[]> => {
        if (this._initialChartLoading == true) {
            await new Promise(f => setTimeout(f, 100)); // Timeout for first loading of chart to prevent zoom-issue
        }
        return this._statistics.getExecutionStatistics().then(executionStatistics => {
            let methodContexts: MethodContext[];
            let methodInfo: MethodInfo[] = [];
            if (methodId) {
                methodContexts = [executionStatistics.executionAggregate.methodContexts[methodId]];
                this._searchRegexp = null;
                delete this.queryParams.methodName;
                if (this._selectedStatus != null) {
                    this._selectedStatus = null;
                    delete this.queryParams.status
                }
                if (this._selectedClass != null) {
                    this._selectedClass = null;
                    delete this.queryParams.class
                }
                this._resetColor();
                this._zoomInOnFilter(methodId, 6);
                this.updateUrl({methodId: methodId});
            } else if (filter?.length > 0) {
                this._searchRegexp = this._statusConverter.createRegexpFromSearchString(filter);
                delete this.queryParams.methodId;
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts).filter(methodContext => methodContext.contextValues.name.match(this._searchRegexp));
            } else {
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts);
            }

            for (const methodContext of methodContexts) {
                methodInfo.push({
                    id: methodContext.contextValues.id,
                    name: methodContext.contextValues.name + " (" + methodContext.methodRunIndex + ")"
                });
            }
            return methodInfo.sort(function (a, b) {
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

    private _chartChanged() {
        this._chart.on('click', event => this._handleClickEvent(event));
    }

    private _selectionChanged() {
        if (this._inputValue.length == 0) {
            this._searchRegexp = null;
            if (this._filterActive && this._selectedStatus == null && this._selectedClass == null) {
                this._resetZoom();
            }
        }
    }

    private _statusChanged() {
        if (this._filterActive) {
            this._resetColor();
        }

        if(this._selectedStatus > 0){
            this._zoomInOnFilter(this._selectedStatus, 7)
            this.updateUrl({status: this._statusConverter.getClassForStatus(this._selectedStatus)});
        }
        // prevent overwriting of method and status filter when _selectedStatus is set undefined by their observers
        else if (!this.queryParams.class && !this._inputValue) {
            this._resetZoom();
            this.queryParams = {};
        }
    }

    private _classChanged() {
        if (this._filterActive) {
            this._resetColor();
        }

        if(this._selectedClass){
            this._zoomInOnFilter(this._selectedClass, 8)
            this.updateUrl({class: this._selectedClass});
        }
        // prevent overwriting of method and class filter when _selectedClass is set undefined by their observers
        else if (!this.queryParams.status && !this._inputValue) {
            this._resetZoom();
            this.queryParams = {};
        }
    }

    private _zoom(zoomStart: number, zoomEnd: number) {
        this._filterActive = true;
        const spacing = (zoomEnd - zoomStart) * 0.05;
        this._chart.dispatchAction({
            type: 'dataZoom',
            id: 'threadZoom',
            startValue: zoomStart - spacing,
            endValue: zoomEnd + spacing
        });
    }

    private _zoomInOnFilter(filter: any, valueIndex: number) {
        this._resetColor();
        const opacity = this._opacityOfInactiveElements;
        let startTimes: number[] = [];
        let endTimes: number[] = [];

        this._options.series[0].data.forEach(function (value) {
            const criterion = value.value[valueIndex];
            if (criterion != filter) {
                value.itemStyle.normal.opacity = opacity;
            } else {
                startTimes.push(value.value[1]);
                endTimes.push(value.value[2]);
            }
        });
        this._chart.setOption(this._options);

        const zoomStart = Math.min.apply(Math, startTimes);
        const zoomEnd = Math.max.apply(Math, endTimes);
        this._zoom(zoomStart, zoomEnd);
    }

    private _resetZoom() {
        this._filterActive = false;
        this._resetColor();
        this.updateUrl({});

        this._chart.dispatchAction({
            type: 'dataZoom',
            id: 'threadZoom',
            start: 0,
            end: 100
        });
    }

    private _resetColor() {
        this._options.series[0].data.forEach(function (value) {
            value.itemStyle.normal.opacity = 1;
        });
        this._chart.setOption(this._options);
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

    /**
     * Build Thread timeline according https://echarts.apache.org/examples/en/editor.html?c=custom-profile
     * and https://echarts.apache.org/en/option.html#series-custom
     */
    private _prepareTimeline(executionStatistics: ExecutionStatistics) {
        const data = [];
        let startTimes: number[] = [];
        const threadCategories = new Map();

        Object.values(executionStatistics.executionAggregate.methodContexts).forEach(methodContext => {
            if (!threadCategories.has(methodContext.threadName)) {
                threadCategories.set(methodContext.threadName, []);
            }
            threadCategories.get(methodContext.threadName).push(methodContext);
            startTimes.push(methodContext.contextValues.startTime);
        });

        const chartStartTime = Math.min.apply(Math, startTimes) - this._gapFromBorderToStart;

        const style = new Map<number, string>();
        style.set(ResultStatusType.PASSED, this._statusConverter.getColorForStatus(ResultStatusType.PASSED));
        style.set(ResultStatusType.REPAIRED, this._statusConverter.getColorForStatus(ResultStatusType.REPAIRED));
        style.set(ResultStatusType.PASSED_RETRY, this._statusConverter.getColorForStatus(ResultStatusType.PASSED_RETRY));
        style.set(ResultStatusType.SKIPPED, this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED));
        style.set(ResultStatusType.FAILED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED));
        style.set(ResultStatusType.FAILED_EXPECTED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED));
        style.set(ResultStatusType.FAILED_MINOR, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_MINOR));
        style.set(ResultStatusType.FAILED_RETRIED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_RETRIED));

        threadCategories.forEach(function (methodContexts, threadName) {
            methodContexts.forEach((context: MethodContext) => {

                const itemColor = style.get(context.resultStatus);
                const duration = context.contextValues.endTime - context.contextValues.startTime;
                const classId = executionStatistics.classStatistics.find(classStat => {
                    const classContextIds = classStat.methodContexts
                        .map(con => con.classContextId)
                        .filter((value, index, self) => self.indexOf(value) === index);
                    return classContextIds.includes(context.classContextId);
                }).classIdentifier

                data.push({
                    name: context.contextValues.name,
                    value: [
                        threadName,
                        context.contextValues.startTime,
                        context.contextValues.endTime,
                        context.contextValues.name,
                        duration,
                        context.methodRunIndex,
                        context.contextValues.id,
                        context.resultStatus,
                        classId
                    ],
                    itemStyle: {
                        normal: {
                            color: itemColor
                        }
                    }
                });
            });
        });

        // Some calculations for chard presentation
        const gridHeight = threadCategories.size * this._threadHeight;
        const sliderFromTop = gridHeight + this._sliderSpacingFromChart
        const dateFormatter = this._dateFormatter;
        const durationFormatter = this._durationFormatter;
        const classNameFormatter = this._classNameValueConverter;
        this._cardHeight = sliderFromTop + 60; // 60px space for dataZoom-slider

        // Set gridLeftValue dynamically to the longest thread name
        const longestThreadName = Array.from(threadCategories.keys()).reduce(
            function (a, b) {
                return a.length > b.length ? a : b;
            }
        );
        let gridLeftValue = longestThreadName.length * 7;   // Calculate the value for grid:left
        gridLeftValue = gridLeftValue > 100 ? gridLeftValue : 100;  // Set to default of 100, if lower

        this._options = {
            tooltip: {
                formatter: function (params) {
                    return '<div class="header" style="background-color: ' +
                        params.color + ';"> ' + params.name + ' (' + params.value[5] + ')' + '</div>'
                        + '<br>Start time: ' + dateFormatter.toView(params.value[1], 'full')
                        + '<br>End time: ' + dateFormatter.toView(params.value[2], 'full')
                        + '<br>Duration: ' + durationFormatter.toView(params.value[4])
                        + '<br>Class: ' + classNameFormatter.toView(params.value[8], ClassName.simpleName);
                }
            },
            dataZoom: [
                {
                    type: 'slider',
                    filterMode: 'weakFilter',
                    showDataShadow: false,
                    top: sliderFromTop,
                    labelFormatter: ''
                },
                {
                    id: 'threadZoom',
                    type: 'inside',
                    filterMode: 'weakFilter'
                }
            ],
            grid: {
                height: gridHeight,
                top: 30,
                bottom: 100,
                left: gridLeftValue
            },
            xAxis: {
                min: chartStartTime,
                scale: true,
                axisLabel: {
                    interval: 2,
                    formatter: function (val) {
                        return dateFormatter.toView(Number(val), 'time') + '\n\n' + dateFormatter.toView(Number(val), 'date');
                    }
                }
            },
            yAxis: {
                data: Array.from(threadCategories.keys())
            },
            series: [
                {
                    type: 'custom',
                    renderItem: function (params: echarts.CustomSeriesRenderItemParams, api: echarts.CustomSeriesRenderItemAPI) {
                        const categoryIndex = api.value(0);
                        const start = api.coord([api.value(1), categoryIndex]);
                        const end = api.coord([api.value(2), categoryIndex]);
                        const height = api.size([0, 1])[1] * 0.7; // 0.7: Minimized height to get space between threads

                        const rectShape = echarts.graphic.clipRectByRect(
                            {
                                x: start[0],
                                y: start[1] - height / 2,
                                width: end[0] - start[0],
                                height: height
                            },
                            {
                                x: params.coordSys["x"],
                                y: params.coordSys["y"],
                                width: params.coordSys["width"],
                                height: params.coordSys["height"]
                            },
                        );
                        return (
                            rectShape && {
                                type: 'rect',
                                transition: ['shape'],
                                shape: rectShape,
                                style: api.style()
                            }
                        );
                    },
                    encode: {
                        x: [1, 2],
                        y: 0,
                        label: 3    // Index in value array
                    },
                    data: data
                }
            ]
        };
    }

    private _handleClickEvent(event: echarts.ECElementEvent) {
        this._router.navigateToRoute('method', {methodId: event.value[6]})
    }
}
