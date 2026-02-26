import React from 'react';
import type {EChartsOption} from 'echarts-for-react';
import ReactECharts from 'echarts-for-react';

export interface EChartProps {
    option: EChartsOption;
    width?: number;
    height?: number;
    onEvents?: Record<string, (params: any, chart: any) => void>;
    notMerge?: boolean;
}

const EChart: React.FC<EChartProps> = ({option, width, height, onEvents, notMerge}) => {
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
            <ReactECharts option={option} opts={opts} autoResize={false} onEvents={onEvents} notMerge={notMerge}/>
        </div>
    )
};

export default EChart;