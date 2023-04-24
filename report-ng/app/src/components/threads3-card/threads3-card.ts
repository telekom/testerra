import * as echarts from "echarts/core";
import {EChartsOption} from "echarts";

export class Threads3Card {
    private _options: EChartsOption;
    private _chart: echarts.ECharts;
    private _container: HTMLDivElement;

    constructor(
    ) {
        this._options = {
            xAxis: {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    data: [150, 230, 224, 218, 135, 147, 260],
                    type: 'line'
                }
            ]
        };
    }
}


/*import * as echarts from "echarts/core";
import {EChartsOption} from "echarts";
import {StatusConverter} from "../../services/status-converter";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {Router} from "aurelia-router";
import {ExecutionStatistics} from "../../services/statistic-models";
import {autoinject, bindable} from "aurelia-framework";
import {bindingMode} from "aurelia-binding";

@autoinject
export class Threads3Card {
    private _options: EChartsOption;
    private _chart: echarts.ECharts;
    private _classNamesMap: { [key: string]: string };
    @bindable({defaultBindingMode: bindingMode.toView})
    executionStatistics: ExecutionStatistics;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _statistics: StatisticsGenerator,
        private _router: Router
    ) {
    }

    executionStatisticsChanged() {
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            this._classNamesMap = {};
            executionStatistics.classStatistics.forEach(classStatistic => {
                this._classNamesMap[classStatistic.classContext.contextValues.id] = classStatistic.classIdentifier;
            });
            this._prepareChart(executionStatistics);
        });
    }

    private _prepareChart(executionStatistics: ExecutionStatistics): void {

        this._options = {
            xAxis: {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    data: [150, 230, 224, 218, 135, 147, 260],
                    type: 'line'
                }
            ]
        };

        // this._chart.setOption(this._options);
        // console.log(this._chart.getOption());
    }
}*/


/*import {bindable} from "aurelia-templating";
import {bindingMode} from "aurelia-binding";
import * as echarts from "echarts/core";
import {ECBasicOption} from "echarts/types/dist/shared";

/!**
 * Component for Apache ECharts
 * @author Mike Reiche <mike@reiche.world>
 *!/
export class Threads3Card {
    private _container: HTMLDivElement;

    @bindable({defaultBindingMode: bindingMode.fromView})
    _chart: echarts.ECharts | null;

    @bindable({defaultBindingMode: bindingMode.toView})
    _options: ECBasicOption;

    @bindable({defaultBindingMode: bindingMode.toView})
    class: string;

    private readonly onResize = () => {
        this._chart?.resize();
    }

    attached() {
        if (this._options) {
            this._createChart();
        }
        window.addEventListener("resize", this.onResize);
    }

    detached() {
        if (this._chart) {
            this._chart.dispose();
            this._chart = null;
        }
        window.removeEventListener("resize", this.onResize);
    }

    optionsChanged(newOptions: ECBasicOption) {
        if (this._chart) {
            this._chart.setOption(newOptions, true);
        } else {
            this._createChart();
        }
    }

    private _createChart() {
        if (!this._container) {
            return;
        }

        this._options = {
            xAxis: {
                type: 'category',
                data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
            },
            yAxis: {
                type: 'value'
            },
            series: [
                {
                    data: [150, 230, 224, 218, 135, 147, 260],
                    type: 'line'
                }
            ]
        };

        this._chart = echarts.init(this._container);
        this._chart.setOption(this._options);
    }
}*/
