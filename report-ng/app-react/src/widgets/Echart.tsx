import React, {forwardRef} from 'react';
import type {EChartsOption, EChartsReactProps} from 'echarts-for-react';
import ReactEChartsCore from 'echarts-for-react/lib/core';
import * as echarts from 'echarts/core';
import {BarChart, CustomChart, LineChart, PieChart, ScatterChart} from 'echarts/charts';
import {DataZoomComponent, GraphicComponent, GridComponent, LegendComponent, TitleComponent, ToolboxComponent, TooltipComponent,} from 'echarts/components';
import {CanvasRenderer} from 'echarts/renderers';
import Box from "@mui/material/Box";

export interface EChartProps {
    option: EChartsOption;
    width?: number;
    height?: number | string;
    onEvents?: EChartsReactProps['onEvents'];
    notMerge?: boolean;
    autoResize?: boolean;
    onChartReady?: EChartsReactProps['onChartReady'];
}

echarts.use([
    BarChart, CustomChart, LineChart, PieChart, ScatterChart,
    GridComponent, TooltipComponent, LegendComponent, TitleComponent, DataZoomComponent, ToolboxComponent, GraphicComponent,
    CanvasRenderer,
]);

export type EchartRef = ReactEChartsCore;

const Echart = forwardRef<ReactEChartsCore, EChartProps>(
    ({option, width, height, onEvents, notMerge, autoResize = false, onChartReady}, ref) => {
        const style: React.CSSProperties = {
            width: width ?? '100%',
            height: height ?? '100%',
        };
        const opts = {
            ...(width !== undefined ? {width} : {}),
            ...(typeof height === 'number' ? {height} : {}),
        };

        return (
            <Box sx={{flex: 1, minHeight: 0}}>
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
            </Box>
        );
    }
);

Echart.displayName = "Echart";

export default Echart;
