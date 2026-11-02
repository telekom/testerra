/*
 * Testerra
 *
 * (C) 2026, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {useCallback, useMemo} from "react";
import {Stack, Typography} from "@mui/material";
import type {SxProps, Theme} from "@mui/material/styles";
import {useTheme} from "@mui/material/styles";
import type {EChartsOption} from "echarts-for-react";
import type {EChartsType, TooltipComponentFormatterCallbackParams} from "echarts";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {ResultStatusType} from "../../model/report-model/framework_pb.ts";
import ReportCard from "../../widgets/ReportCard.tsx";
import Echart from "../../widgets/Echart.tsx";
import {formatDuration} from "../../utils/durationFormatter.ts";
import {formatDate} from "../../utils/dateFormatter.ts";
import {buildChartTooltip} from "../../utils/chartTooltip.ts";
import type {TestRunViewport} from "./TestRunHistoryBarChart.tsx";
import {reportTheme} from "../../layout/reportTheme.tsx";

interface DurationChartCardProps {
    histStatistics: HistoryStatistics;
    sx?: SxProps<Theme>;
    onViewportChange?: (viewport: TestRunViewport) => void;
}

const RESET_ZOOM_ICON = "M 4,1 V 5 H 0 M 3.9865238,4.9219293 C 1.602752,3.5367838 0,0.95556327 0,-2 c 0,-4.418278 3.581722,-8 8,-8 4.418278,0 8,3.581722 8,8 0,4.418278 -3.581722,8 -8,8";

const DurationChartCard = ({histStatistics, sx, onViewportChange}: DurationChartCardProps) => {
    const theme = useTheme();
    const durationColor = theme.custom.chart.blue.main;
    const chartData = useMemo(() => histStatistics.getHistoryAggregateStatistics().map(entry => {
        const context = entry.historyAggregate.executionContext?.contextValues;
        const startTime = context?.startTime;
        const endTime = context?.endTime;

        return {
            runIndex: entry.historyIndex,
            duration: startTime !== undefined && endTime !== undefined ? endTime - startTime : null,
            testCases: entry.overallPassed
                + entry.getStatusCount(ResultStatusType.FAILED)
                + entry.getStatusCount(ResultStatusType.FAILED_EXPECTED)
                + entry.getStatusCount(ResultStatusType.SKIPPED),
            started: formatDate(startTime, "long"),
            ended: formatDate(endTime, "long"),
        };
    }), [histStatistics]);
    const hasHistory = chartData.length > 1;

    const option: EChartsOption = useMemo(() => ({
        tooltip: {
            trigger: "axis",
            axisPointer: {type: "line"},
            confine: true,
            borderWidth: 1,
            borderColor: durationColor,
            formatter: (params: TooltipComponentFormatterCallbackParams) => {
                const point = Array.isArray(params) ? params[0] : params;
                const run = point ? chartData[point.dataIndex] : undefined;
                if (!run) {
                    return "";
                }
                const listItemStyle = "display:grid;grid-template-columns:8ch auto;column-gap:8px;align-items:center;margin:2px 0;";
                const listItems = `<li style="${listItemStyle}"><span>Duration</span><span>${formatDuration(run.duration ?? 0)}</span></li>
                    <li style="${listItemStyle}"><span>Started</span><span>${run.started}</span></li>
                    <li style="${listItemStyle}"><span>Ended</span><span>${run.ended}</span></li>
                `;

                return buildChartTooltip({
                    header: {
                        content: `Run ${run.runIndex} - Tests: ${run.testCases}`,
                        style: {
                            backgroundColor: reportTheme.palette.lightGrey.light,
                        },
                    },
                    body: {
                        content: listItems
                    },
                });
            },
        },
        grid: {
            top: "80px",
            left: "3%",
            right: "3%",
            bottom: "55px",
            outerBoundsMode: "same",
            outerBoundsContain: "axisLabel",
        },
        xAxis: {
            type: "category",
            data: chartData.map(run => run.runIndex),
        },
        yAxis: [
            {
                type: "value",
                name: "Duration",
                axisLabel: {formatter: (value: number) => formatDuration(value)},
            },
            {
                type: "value",
                name: "Testcases",
                minInterval: 1,
                splitLine: {show: false},
            },
        ],
        dataZoom: [
            {type: "inside", xAxisIndex: [0], start: 0, end: 100, minValueSpan: 1},
            {type: "slider", xAxisIndex: [0], start: 0, end: 100, minValueSpan: 1},
        ],
        legend: {
            data: ["Duration", "Testcases"],
            top: 10,
            bottom: "auto",
            itemGap: 30,
            selectedMode: false,
        },
        toolbox: {
            itemSize: 20,
            feature: {
                restore: {show: true, title: "Reset Zoom", icon: `path://${RESET_ZOOM_ICON}`},
            },
        },
        series: [
            {
                name: "Duration",
                type: "line",
                data: chartData.map(run => run.duration),
                z: 2,
                cursor: "default",
                lineStyle: {color: durationColor},
                itemStyle: {color: durationColor},
                symbol: "circle",
                symbolSize: 5,
            },
            {
                name: "Testcases",
                type: "bar",
                yAxisIndex: 1,
                data: chartData.map(run => run.testCases),
                z: 1,
                cursor: "default",
                itemStyle: {color: durationColor, opacity: 0.1},
            },
        ],
    }), [chartData, durationColor]);

    const emitViewport = useCallback((chart: EChartsType) => {
        if (!onViewportChange || !hasHistory) {
            return;
        }
        // ECharts resolves the zoom window to category indices, not history indices.
        const zoom = (chart.getOption().dataZoom as {startValue?: number; endValue?: number}[] | undefined)?.[0];
        const start = chartData[zoom?.startValue ?? 0];
        const end = chartData[zoom?.endValue ?? chartData.length - 1];
        if (start && end) {
            onViewportChange({start: start.runIndex, end: end.runIndex});
        }
    }, [chartData, hasHistory, onViewportChange]);

    return (
        <ReportCard
            label="History run duration"
            sxCard={sx}
            sxContent={{p: 0}}
            content={hasHistory ? (
                <Echart
                    option={option}
                    notMerge={true}
                    autoResize={true}
                    onChartReady={emitViewport}
                    onEvents={{
                        datazoom: (_: unknown, chart: EChartsType) => emitViewport(chart),
                        restore: (_: unknown, chart: EChartsType) => emitViewport(chart),
                    }}
                />
            ) : (
                <Stack sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                    <Typography>No history available</Typography>
                </Stack>
            )}
        />
    );
};

export default DurationChartCard;
