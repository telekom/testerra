import React from 'react';
import type {EChartsOption} from 'echarts-for-react';
import ReactEChartsCore from 'echarts-for-react/lib/core';
import * as echarts from 'echarts/core';
import {BarChart, LineChart, PieChart} from 'echarts/charts';
import {GridComponent, LegendComponent, TitleComponent, TooltipComponent} from 'echarts/components';
import {CanvasRenderer} from 'echarts/renderers';

export interface EChartProps {
    option: EChartsOption;
    width?: number;
    height?: number;
    onEvents?: Record<string, (params: any, chart: any) => void>;
    notMerge?: boolean;
}

echarts.use([
    BarChart, LineChart, PieChart,
    GridComponent, TooltipComponent, LegendComponent, TitleComponent,
    CanvasRenderer,
]);

const Echart: React.FC<EChartProps> = ({option, width, height, onEvents, notMerge}) => {
    const style: React.CSSProperties = {
        ...(width !== undefined ? {width} : {}),
        ...(height !== undefined ? {height} : {}),
    };
    const opts = {
        ...(width !== undefined ? {width} : {}),
        ...(height !== undefined ? {height} : {}),
    };

    // TODO fix resize

    return (
        <div style={style}>
            <ReactEChartsCore echarts={echarts} option={option} opts={opts} autoResize={false} onEvents={onEvents} notMerge={notMerge}/>
        </div>
    )
};

export default Echart;
