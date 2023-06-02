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
import "./threads3.scss"
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
// import echarts from "echarts/types/dist/shared";
import * as echarts from "echarts";
import {EChartsOption} from "echarts";
import {data} from "../../services/report-model";
import ResultStatusType = data.ResultStatusType;
import MethodContext = data.MethodContext;
import {IntlDateFormatValueConverter} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter";
import moment from "moment";


@autoinject()
export class Threads3 extends AbstractViewModel {

    private _filter: IFilter;
    private _loading = true;

    private _options: EChartsOption;
    @observable()
    private _chart: echarts.ECharts;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _dateTimeConverter: IntlDateFormatValueConverter,
        private _router: Router
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;
        if (this.queryParams.status) {
            this._filter = {
                status: this._statusConverter.getStatusForClass(this.queryParams.status)
            }
        }
    }

    attached() {
        this._statisticsGenerator.getExecutionStatistics().then(executionStatistics => {
            // this._executionStatistics = executionStatistics;
            // TODO: Add logic to prepare timeline
            this._prepareTimeline(executionStatistics);

            this._loading = false;
        });
    };

    private _chartChanged() {
        this._chart.on('click', event => this._handleClickEvent(event));
    }

    // TODO: Zoom into method with name 'x'
    zoom() {
        this._chart.dispatchAction({
            type: 'dataZoom',
            id: 'threadZoom',
            start: 10,  // Percent from
            end: 20,     // Percent to
            // startValue: 10000,
            // endValue: 20000
        });
    }

    resetZoom() {
        // console.log("Data", this._options);
        // console.log("End", this._options.series[0].data.endValue);
        this._chart.dispatchAction({
            type: 'dataZoom',
            id: 'threadZoom',
            start: 0,
            end: 100
            // startValue:0,
            // endValue: undefined
        });
    }

    private getColor() {
        return "foo";
    }

    /**
     * Build Thread timeline according https://echarts.apache.org/examples/en/editor.html?c=custom-profile
     */
    private _prepareTimeline(executionStatistics: ExecutionStatistics) {
        const data = [];
        // const dataCount = 10;
        // const startTime = +new Date();
        let startTimes : number[] = [];

        const threadCategories = new Map();
        Object.values(executionStatistics.executionAggregate.methodContexts).forEach(methodContext => {
            if (!threadCategories.has(methodContext.threadName)) {
                threadCategories.set(methodContext.threadName, []);
            }
            threadCategories.get(methodContext.threadName).push(methodContext);
            startTimes.push(methodContext.contextValues.startTime);
        });

        const chartStartTime = Math.min.apply(Math, startTimes) - 1_000;

        const style = new Map<number, string>();
        style.set(ResultStatusType.PASSED, this._statusConverter.getColorForStatus(ResultStatusType.PASSED));
        style.set(ResultStatusType.REPAIRED, this._statusConverter.getColorForStatus(ResultStatusType.REPAIRED));
        style.set(ResultStatusType.PASSED_RETRY, this._statusConverter.getColorForStatus(ResultStatusType.PASSED_RETRY));
        style.set(ResultStatusType.SKIPPED, this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED));
        style.set(ResultStatusType.FAILED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED));
        style.set(ResultStatusType.FAILED_EXPECTED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED));
        style.set(ResultStatusType.FAILED_MINOR, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_MINOR));
        style.set(ResultStatusType.FAILED_RETRIED, this._statusConverter.getColorForStatus(ResultStatusType.FAILED_RETRIED));

        // TODO: Add method contexts to every thread
        // Generate mock data
        threadCategories.forEach(function (methodContexts, threadName) {
            // let baseTime = startTime;
            // for (var i = 0; i < dataCount; i++) {
            methodContexts.forEach((context: MethodContext) => {
                console.log(context);
                const itemColor = style.get(context.resultStatus);
                const duration = context.contextValues.endTime - context.contextValues.startTime;

                data.push({
                    name: context.contextValues.name,
                    // value: [threadName, baseTime, (baseTime += duration), duration],
                    value: [
                        threadName,
                        context.contextValues.startTime,
                        context.contextValues.endTime,
                        duration
                    ],
                    itemStyle: {
                        normal: {
                            color: itemColor
                        }
                    }
                });
                // baseTime += Math.round(Math.random() * 2000);
            });
            // const minValue = Math.min(methodContexts.map(context => context.contextValues.startTime));
            // console.log(Math.min(methodContexts.map(context => context.contextValues.startTime)));
        });

        console.log("data", data);

        this._options = {
            tooltip: {
                formatter: function (params) {
                    // console.log(params);
                    // params.value[1]: from date
                    // params.value[2]: to date
                    return params.marker + params.name + ': ' + params.value[3] + ' ms';
                }
            },
            // title: {
            //     text: 'Profile',
            //     left: 'center'
            // },
            dataZoom: [
                {
                    type: 'slider',
                    filterMode: 'weakFilter',
                    showDataShadow: false,
                    // TODO Based of grid height + 100 px
                    top: 700,
                    labelFormatter: ''
                },
                {
                    id: 'threadZoom',
                    type: 'inside',
                    filterMode: 'weakFilter'
                }
            ],
            grid: {
                // TODO: Custom height according number of threads
                height: 600
            },
            xAxis: {
                min: chartStartTime,
                scale: true,
                // axisLabel: {
                //     formatter: function (val) {
                //         return Math.max(0, val - startTime) + ' ms';
                //     }
                // }
                axisLabel: {
                    formatter: function (val) {
                        return val;
                        // return moment(Number(val));
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
                        // return this.getRenderItem(params, api); -> does not work
                        const categoryIndex = api.value(0);
                        // TODO: (Start, end) -> (start time, end time)
                        const start = api.coord([api.value(1), categoryIndex]);
                        const end = api.coord([api.value(2), categoryIndex]);
                        // TODO: 0.6: Minimized height to get space between threads
                        const height = api.size([0, 1])[1] * 0.6;

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
                            }
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
                    itemStyle: {
                        opacity: 0.8
                    },
                    encode: {
                        x: [1, 2],
                        y: 0
                    },
                    data: data
                }
            ]
        };
    }

    private _handleClickEvent(event: echarts.ECElementEvent) {
        // TODO: Route to method details
        console.log("clicked", event);
    }

    getRenderItem(params, api): any {
        const categoryIndex = api.value(0);
        const start = api.coord([api.value(1), categoryIndex]);
        const end = api.coord([api.value(2), categoryIndex]);
        const height = api.size([0, 1])[1] * 0.6;
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
            }
        );
        return (
            rectShape && {
                type: 'rect',
                transition: ['shape'],
                shape: rectShape,
                style: api.style()
            }
        );
    }

}

