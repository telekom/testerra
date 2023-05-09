import {bindable} from "aurelia-templating";
import {bindingMode} from "aurelia-binding";
import * as echarts from "echarts";
import {ECBasicOption} from "echarts/types/dist/shared";

/**
 * Component for Apache ECharts
 * @author Mike Reiche <mike@reiche.world>
 */
export class Echart {
    private _container: HTMLDivElement;

    @bindable({ defaultBindingMode: bindingMode.fromView })
    chart: echarts.ECharts|null;

    @bindable({ defaultBindingMode: bindingMode.toView })
    options: ECBasicOption;

    @bindable({ defaultBindingMode: bindingMode.toView })
    class:string;

    private readonly onResize= () => {
        this.chart?.resize();
    }

    attached() {
        if (this.options) {
            this._createChart();
        }
        window.addEventListener("resize", this.onResize);
    }

    detached() {
        if (this.chart) {
            this.chart.dispose();
            this.chart = null;
        }
        window.removeEventListener("resize", this.onResize);
    }

    optionsChanged(newOptions:ECBasicOption) {
        if (this.chart) {
            this.chart.setOption(newOptions, true);
        } else {
            this._createChart();
        }
    }

    private _createChart() {
        if (!this._container) {
            return;
        }

        this.chart = echarts.init(this._container);
        this.chart.setOption(this.options);
    }
}
