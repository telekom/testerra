import React, {forwardRef} from 'react';
import type {EChartsOption} from 'echarts-for-react';
import ReactEChartsCore from 'echarts-for-react/lib/core';
import * as echarts from 'echarts/core';
import {BarChart, CustomChart, LineChart, PieChart, ScatterChart} from 'echarts/charts';
import {
    DataZoomComponent,
    GridComponent,
    LegendComponent,
    TitleComponent,
    ToolboxComponent,
    TooltipComponent,
    GraphicComponent,
} from 'echarts/components';
import {CanvasRenderer} from 'echarts/renderers';

export interface EChartProps {
    option: EChartsOption;
    width?: number;
    height?: number | string;
    onEvents?: Record<string, (params: any, chart: any) => void>;
    notMerge?: boolean;
    autoResize?: boolean;
    onChartReady?: (chart: any) => void;
}

echarts.use([
    BarChart,CustomChart, LineChart, PieChart, ScatterChart,
    GridComponent, TooltipComponent, LegendComponent, TitleComponent, DataZoomComponent, ToolboxComponent, GraphicComponent,
    CanvasRenderer,
]);

export type EchartRef = ReactEChartsCore;

const Echart = forwardRef<ReactEChartsCore, EChartProps>(
    ({option, width, height, onEvents, notMerge, autoResize = false, onChartReady}, ref) => {
        const style: React.CSSProperties = {
            width: width ?? '100%',
            ...(height !== undefined ? {height} : {}),
        };
        const opts = {
            ...(width !== undefined ? {width} : {}),
            ...(typeof height === 'number' ? {height} : {}),
        };

        return (
            <ReactEChartsCore
                ref={ref}
                echarts={echarts}
                option={option}
                opts={opts}
                autoResize={autoResize}
                onEvents={onEvents}
                notMerge={notMerge}
                onChartReady={onChartReady}
                style={style}
            />
        );
    }
);

Echart.displayName = "Echart";

export default Echart;
