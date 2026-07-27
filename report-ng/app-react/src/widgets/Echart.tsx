import React from 'react';
import type {EChartsOption} from 'echarts-for-react';
import ReactEChartsCore from 'echarts-for-react/lib/core';
import * as echarts from 'echarts/core';
import {BarChart, LineChart, PieChart, ScatterChart} from 'echarts/charts';
import {
    DataZoomComponent,
    GridComponent,
    LegendComponent,
    TitleComponent,
    ToolboxComponent,
    TooltipComponent,
} from 'echarts/components';
import {CanvasRenderer} from 'echarts/renderers';

export interface EChartProps {
    option: EChartsOption;
    width?: number;
    height?: number | string;
    onEvents?: Record<string, (params: any, chart: any) => void>;
    notMerge?: boolean;
    autoResize?: boolean;
}

echarts.use([
    BarChart, LineChart, PieChart, ScatterChart,
    GridComponent, TooltipComponent, LegendComponent, TitleComponent, DataZoomComponent, ToolboxComponent,
    CanvasRenderer,
]);

const Echart: React.FC<EChartProps> = ({option, width, height, onEvents, notMerge, autoResize = false}) => {
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
            echarts={echarts}
            option={option}
            opts={opts}
            autoResize={autoResize}
            onEvents={onEvents}
            notMerge={notMerge}
            style={style}
        />
    )
};

export default Echart;
