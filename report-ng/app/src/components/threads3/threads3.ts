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
import {IFilter, StatusConverter} from "services/status-converter";
import {StatisticsGenerator} from "services/statistics-generator";
import {ExecutionStatistics} from "services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import {Container} from "aurelia-dependency-injection";
import "./threads3.scss"
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import * as echarts from "echarts";
import {EChartsOption} from "echarts";
import {data} from "../../services/report-model";
import ResultStatusType = data.ResultStatusType;
import MethodContext = data.MethodContext;
import {IntlDateFormatValueConverter} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter";
// import {IntlDurationFormatValueConverter} from "t-systems-aurelia-components/src/value-converters/intl-duration-format-value-converter";
import IContextValues = data.IContextValues;
import IMethodContext = data.IMethodContext;


@autoinject()
export class Threads3 extends AbstractViewModel {

    private _filter: IFilter;
    private _loading = true;
    private _searchRegexp: RegExp;
    private _options: EChartsOption;
    private _inputValue;
    private _methodNameInput:HTMLElement;
    private _availableStatuses: data.ResultStatusType[] | number[];
    private _selectedStatus: data.ResultStatusType;
    private newMethodToFilter = false;
    @observable()
    private _chart: echarts.ECharts;

    private _dateFormatter : IntlDateFormatValueConverter;
    // private _durationFormatter: IntlDurationFormatValueConverter;

    // Some values for presentation
    private _gapFromBorderToStart = 400;      // To prevent that the beginning of the first test is located ON the y axis.
    private _threadHeight = 50;                 // height of y-axis categories in pixel
    private _sliderSpacingFromChart = 90;      // distance between chart and dataZoom-slider in pixel
    private _cardHeight;
    private _opacityOfInactiveElements = 0.38;  // Default opacity of disabled elements https://m2.material.io/design/interaction/states.html#disabled

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _statistics: StatisticsGenerator,
        private _router: Router
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;

        if (params.status) {
            this._selectedStatus = this._statusConverter.getStatusForClass(params.status);
        } else {
            this._selectedStatus = null;
        }

        if (this.queryParams.status) {
            this._filter = {
                status: this._statusConverter.getStatusForClass(this.queryParams.status)
            }
        }
    }

    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            this._availableStatuses = [];
            this._availableStatuses = executionStatistics.availableStatuses;
            // this._initDurationFormatter();
            this._initDateFormatter();
            this._prepareTimeline(executionStatistics);
            this._loading = false;
        });
    };

    private _chartChanged() {
        this._chart.on('click', event => this._handleClickEvent(event));
    }

    private selectionChanged(){
        if (this._inputValue.length == 0){
            this.resetZoom();
        }
    }

    private _getLookupOptions = async (filter: string, methodId: string): Promise<IContextValues[]>  => {
        return this._statistics.getExecutionStatistics().then(executionStatistics => {
            let methodContexts:IMethodContext[];
            if (methodId) {
                methodContexts = [executionStatistics.executionAggregate.methodContexts[methodId]];
                this._searchRegexp = null;
                delete this.queryParams.methodName;
                if (this._selectedStatus != null) {
                    this.newMethodToFilter = true;
                    this._selectedStatus = undefined;
                }
                this.resetColor();
                this.zoomInOnMethod(methodId);
                this.updateUrl({methodId: methodId});
            } else if (filter?.length > 0) {
                this._searchRegexp = this._statusConverter.createRegexpFromSearchString(filter);
                delete this.queryParams.methodId;
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts).filter(methodContext => methodContext.contextValues.name.match(this._searchRegexp));
            } else {
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts);
            }
            return methodContexts.map(methodContext => methodContext.contextValues).sort(function(a, b){
                if(a.name < b.name) { return -1; }
                if(a.name > b.name) { return 1; }
                return 0;
            });
        });
    };

    private zoomInOnMethod(methodId: string) {
        console.log("zoomInOnMethod " + methodId);
        const dataToZoomInOn = this._options.series[0].data.find(function(method) {
            return method.value[6] == methodId;
        });
        const zoomStart = dataToZoomInOn.value[1];
        const zoomEnd = dataToZoomInOn.value[2];
        const opacity = this._opacityOfInactiveElements;

        this._options.series[0].data.forEach(function (value) {
            const mid = value.value[6];
            if (mid != methodId) {
                value.itemStyle.normal.opacity = opacity;
            }
        });
        this._chart.setOption(this._options);
        this.zoom(zoomStart, zoomEnd);
    }

    private zoom(zoomStart: number, zoomEnd: number) {
        const spacing = (zoomEnd - zoomStart) * 0.05;
        this._chart.dispatchAction({
            type: 'dataZoom',
            id: 'threadZoom',
            startValue: zoomStart - spacing,
            endValue: zoomEnd + spacing
        });
    }

    private resetColor() {
        console.log("resetColor");
        this._options.series[0].data.forEach(function (value) {
            value.itemStyle.normal.opacity = 1;
        });
        this._chart.setOption(this._options);
    }

    private resetZoomButtonClicked() {
        this.clearMethodFilter();
        if (this._selectedStatus != null) {
            this._selectedStatus = undefined;
        } else {
            this.resetZoom();
        }
    }

    private clearMethodFilter() {
        this._inputValue = "";
        this._inputValue = undefined;
    }

    private resetZoom() {
        this.resetColor();
        this.updateUrl({});

        this._chart.dispatchAction({
            type: 'dataZoom',
            id: 'threadZoom',
            start: 0,
            end: 100
        });
    }

    private _statusChanged() {
        if (this.newMethodToFilter) {
            this.newMethodToFilter = false;
            return;
        }
        const opacity = this._opacityOfInactiveElements;
        const selectedStat = this._selectedStatus;
        let startTimes : number[] = [];
        let endTimes : number[] = [];

        this.clearMethodFilter();
        this.resetColor();
        if (this._selectedStatus > 0) {
            this._options.series[0].data.forEach(function (value) {
                const stat = value.value[7];
                if (stat != selectedStat) {
                    value.itemStyle.normal.opacity = opacity;
                } else {
                    startTimes.push(value.value[1]);
                    endTimes.push(value.value[2]);
                }
            });
            this._chart.setOption(this._options);

            const zoomStart = Math.min.apply(Math, startTimes);
            const zoomEnd = Math.max.apply(Math, endTimes);
            this.zoom(zoomStart, zoomEnd);
        } else {
            this.resetZoom();
        }
    }

    // private _initDurationFormatter() {
    //     const container = new Container();
    //     this._durationFormatter = container.get(IntlDurationFormatValueConverter);
    //     this._durationFormatter.setLocale('en-GB');
    //     this._durationFormatter.setOptions('duration', {})
    // }

    private _initDateFormatter() {
        const container = new Container();
        this._dateFormatter = container.get(IntlDateFormatValueConverter);
        this._dateFormatter.setLocale('en-GB');
        this._dateFormatter.setOptions('date', { year: 'numeric', month: 'short', day: 'numeric' });
        this._dateFormatter.setOptions('time_full', { hour: 'numeric', minute: 'numeric', second: 'numeric', fractionalSecondDigits: '2', hour12: false });
        this._dateFormatter.setOptions('time', { hour: 'numeric', minute: 'numeric', second: 'numeric', hour12: false });
        this._dateFormatter.setOptions('full', { year: 'numeric', month: 'short', day: 'numeric',  hour: 'numeric', minute: 'numeric', second: 'numeric', hour12: false });
    }

    /**
     * Build Thread timeline according https://echarts.apache.org/examples/en/editor.html?c=custom-profile
     * and https://echarts.apache.org/en/option.html#series-custom
     */
    private _prepareTimeline(executionStatistics: ExecutionStatistics) {
        const data = [];
        let startTimes : number[] = [];
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
                        context.resultStatus
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
        // const durationFormatter = this._durationFormatter;
        this._cardHeight = sliderFromTop + 60; // 60px space for dataZoom-slider

        // Set gridLeftValue dynamically to the longest thread name
        const longestThreadName = Array.from(threadCategories.keys()).reduce(
            function (a, b) {
                return a.length > b.length ? a : b;
            }
        );
        let gridLeftValue= longestThreadName.length * 6.5; // Calculate the value for grid:left
        gridLeftValue = gridLeftValue > 100 ? gridLeftValue : 100; // Set to default of 100, if lower

        this._options = {
            tooltip: {
                formatter: function (params) {
                    // Calculations for test duration
                    const fullMinutes = Math.floor(params.value[4]/(60000));
                    const fullSeconds = Math.floor(params.value[4]/1000);
                    const remainingMS = (params.value[4]%1000);
                    let duration;
                    if (fullMinutes == 0) {
                        if (fullSeconds == 0) {
                            duration = remainingMS + "ms";
                        } else {
                            duration = fullSeconds + "s " + remainingMS + "ms";
                        }
                    } else {
                        const remainingSeconds = Math.floor(fullSeconds%60);
                        duration = fullMinutes + "min " + remainingSeconds + "s " + remainingMS + "ms";
                    }

                    return '<div class="header" style="background-color: ' +
                        params.color + ';"> ' + params.name + '</div>'
                        + '<br>Start time: ' + dateFormatter.toView(params.value[1], 'full')
                        + '<br>End time: ' + dateFormatter.toView(params.value[2], 'full')
                        // TODO use duration value converter: -> IntlDurationFormatValueConverter broken?
                        // + '<br>Duration: ' + durationFormatter.toView(params.value[4])
                        + '<br>Duration: ' + duration
                        + '<br>Run index: ' + params.value[5];
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
