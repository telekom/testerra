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
import type {EChartsOption} from "echarts-for-react";
import type {EChartsType, TooltipComponentFormatterCallbackParams} from "echarts";
import Echart from "../../widgets/Echart";
import {StatusService} from "../../model/status-service.tsx";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {reportTheme} from "../../layout/reportTheme.tsx";
import {formatDate} from "../../utils/dateFormatter.ts";
import {formatDuration} from "../../utils/durationFormatter.ts";
import {buildChartTooltip, buildTooltipStatusBadge} from "../../utils/chartTooltip";

export interface TestRunChartProps {
    histStatistics: HistoryStatistics;
    selectedStatus: string | null;
    additionalChartOptions?: EChartsOption;
    onViewportChange?: (viewport: TestRunViewport) => void;
}

export interface TestRunViewport {
    start: number;
    end: number;
}

interface TestRunChartData {
    value: number;
    runIndex: number;
    started: string;
    ended: string;
    duration: string;
}

const TestRunChart = ({histStatistics, selectedStatus, additionalChartOptions, onViewportChange}: TestRunChartProps) => {
    const totalRuns = histStatistics.getTotalRunCount();
    const hasHistory = totalRuns > 1;
    const statuses = useMemo(
        () => StatusService.getRelevantStatuses().filter(status => !selectedStatus || StatusService.getLabel(status) === selectedStatus),
        [selectedStatus]
    );
    const historyEntries = useMemo(() => histStatistics.getHistoryAggregateStatistics(), [histStatistics]);
    const runMetaData = useMemo(() => historyEntries.map(entry => {
        const contextValues = entry.historyAggregate.executionContext?.contextValues;
        const startTime = contextValues?.startTime;
        const endTime = contextValues?.endTime;

        return {
            started: formatDate(startTime, "long"),
            ended: formatDate(endTime, "long"),
            duration: startTime !== undefined && endTime !== undefined ? formatDuration(endTime - startTime) : "0ms"
        };
    }), [historyEntries]);

    const placeHolderSeries: NonNullable<EChartsOption["series"]> = useMemo(() => [{
        data: [1000, 1100, 1100, 1200, 1290, 1330, 1320],
        type: "line",
        areaStyle: {
            color: "rgba(20,20,20,0.05)"
        },
        lineStyle: {
            color: "rgba(255,255,255,0)",
            width: 0
        },
        silent: true,
        symbol: "none",
        emphasis: {
            disabled: true
        },
        tooltip: {
            show: false
        }
    }], []);

    const historySeries: NonNullable<EChartsOption["series"]> = useMemo(
        () => statuses.map(status => ({
            name: StatusService.getLabel(status),
            type: "line",
            stack: "Total",
            silent: true,
            lineStyle: {
                width: 0
            },
            symbol: "none",
            areaStyle: {
                color: StatusService.getColor(status),
                opacity: 1
            },
            emphasis: {
                disabled: true
            },
            data: historyEntries.map((entry, index): TestRunChartData => ({
                runIndex: entry.historyIndex,
                value: entry.getSummarizedStatusCount(StatusService.getGroup(status)),
                started: runMetaData[index].started,
                ended: runMetaData[index].ended,
                duration: runMetaData[index].duration
            }))
        })),
        [historyEntries, runMetaData, statuses]
    );

    const baseOption: EChartsOption = useMemo(() => ({
        grid: {
            top: '5%',
            left: '3%',
            right: '3%',
            bottom: '2%',
            outerBoundsMode: "same",
            outerBoundsContain: "axisLabel"
        },
        tooltip: hasHistory ? {
            trigger: "axis",
            axisPointer: {
                type: "line"
            },
            confine: true,
            borderWidth: 1,
            borderColor: reportTheme.palette.lightGrey.light,
            formatter: function (params: TooltipComponentFormatterCallbackParams) {
                if (!Array.isArray(params) || params.length === 0) {
                    return "";
                }
                const rows = params.map(item => {
                    const point = item.data as TestRunChartData | undefined;
                    const value = Number(point?.value ?? item.value ?? 0);
                    return {
                        seriesName: String(item.seriesName ?? ""),
                        value,
                        point
                    };
                });

                const values = rows
                    .map(row => row.value)
                    .filter(value => Number.isFinite(value));
                const firstPointData = rows[0].point;
                const testCases = values.reduce((sum, value) => sum + value, 0);
                const runNumber = firstPointData?.runIndex ?? 0;

                const statusListItems = rows
                    .filter(row => row.value > 0)
                    .map(row => {
                        const status = StatusService.getStatusByLabel(row.seriesName);
                        return `<li style="display:grid;grid-template-columns:14ch 5ch;column-gap:8px;align-items:center;margin:2px 0;">                            
                            <span>
                                ${buildTooltipStatusBadge(status)}
                            </span>
                            <span style="text-align:right;font-variant-numeric:tabular-nums;font-feature-settings:'tnum' 1;">
                                &nbsp;${row.value}
                            </span>
                            
                        </li>`;
                    })
                    .join("");

                const timeListItemStyle = "display:grid;grid-template-columns:8ch auto;column-gap:8px;align-items:center;margin:2px 0;";
                const timeListItems =
                    `<li style="${timeListItemStyle}"><span>Started</span><span>${firstPointData?.started ?? "0"}</span></li>
                    <li style="${timeListItemStyle}"><span>Ended</span><span>${firstPointData?.ended ?? "0"}</span></li>
                    <li style="${timeListItemStyle}"><span>Duration</span><span>${firstPointData?.duration ?? "0"}</span></li>`;

                return buildChartTooltip({
                    header: {
                        content: `Run ${runNumber} - Tests: ${testCases}`,
                        style: {
                            backgroundColor: reportTheme.palette.lightGrey.light,
                        },
                    },
                    body: {
                        content: [
                            `<ul style="list-style:none;padding:0;margin:0;">${statusListItems}</ul>`,
                            `<div style="margin-top:8px;padding-top:6px;border-top:1px solid ${reportTheme.palette.lightGrey.main};">
                                <ul style="list-style:none;padding:0;margin:0;">${timeListItems}</ul>                  
                            </div>`,
                        ],
                    },
                });
            }
        } : undefined,
        xAxis: [{
            type: "category",
            axisLine: {
                show: true
            },
            axisLabel: {
                show: hasHistory
            },
            boundaryGap: false,
            splitLine: {
                show: true
            },
            data: hasHistory ? historyEntries.map(entry => entry.historyIndex) : undefined
        }],
        yAxis: [{
            type: "value",
            axisLine: {
                show: true
            },
            axisLabel: {
                show: hasHistory
            },
            splitLine: {
                show: false
            }
        }],
        series: hasHistory ? historySeries : placeHolderSeries,
        graphic: hasHistory ? undefined : {
            type: 'text',
            left: 'center',
            top: 'center',
            silent: true,
            z: 10,
            style: {
                text: 'No history available',
                font: '20px Roboto',
                fill: reportTheme.palette.lightGrey.dark
            }
        }
    }), [hasHistory, historyEntries, historySeries, placeHolderSeries]);

    const option: EChartsOption = useMemo(
        () => additionalChartOptions ? {...baseOption, ...additionalChartOptions} : baseOption,
        [additionalChartOptions, baseOption]
    );

    // ECharts stores the resolved zoom window (category indices) in startValue/endValue of the dataZoom option
    const emitViewport = useCallback((chart: EChartsType) => {
        if (!onViewportChange || !hasHistory) {
            return;
        }
        const dataZoom = (chart.getOption().dataZoom as { startValue?: number; endValue?: number }[] | undefined)?.[0];
        onViewportChange({
            start: historyEntries[dataZoom?.startValue ?? 0].historyIndex,
            end: historyEntries[dataZoom?.endValue ?? historyEntries.length - 1].historyIndex,
        });
    }, [hasHistory, historyEntries, onViewportChange]);

    return (
        <Echart
            option={option}
            notMerge={true}
            autoResize={true}
            onChartReady={emitViewport}
            onEvents={{
                datazoom: (_: unknown, chart: EChartsType) => emitViewport(chart),
                restore: (_: unknown, chart: EChartsType) => emitViewport(chart)
            }}
        />
    );
};

export default TestRunChart;
