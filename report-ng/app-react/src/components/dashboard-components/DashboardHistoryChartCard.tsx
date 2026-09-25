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

import Echart from "../../widgets/Echart";
import ReportCard from "../../widgets/ReportCard";
import type {SxProps, Theme} from "@mui/material/styles";
import {StatusService} from "../../model/status-service.tsx";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import type {EChartsOption} from "echarts-for-react";
import {useMemo} from "react";
import type {TooltipComponentFormatterCallbackParams} from "echarts";
import {reportTheme} from "../../layout/reportTheme.tsx";
import {dateFormatter} from "../../utils/dateFormatter.ts";
import {formatDuration} from "../../utils/durationFormatter.ts";

interface DashboardHistoryChartProps {
    histStatistics: HistoryStatistics
    selectedStatus: string | null;
    sx?: SxProps<Theme>
}

interface HistoryChartData {
    value: number;
    runIndex: number;
    started: string;
    ended: string;
    duration: string;
}

const DashboardHistoryChartCard = ({histStatistics, selectedStatus, sx}: DashboardHistoryChartProps) => {
    const totalRuns = histStatistics.getTotalRunCount();
    const hasHistory = totalRuns > 1;
    const relevantStatuses = StatusService.getRelevantStatuses();
    const statuses = relevantStatuses.filter(status =>
        !selectedStatus || StatusService.getLabel(status) === selectedStatus
    );
    const historyEntries = histStatistics.getHistoryAggregateStatistics();
    const runMetaData = useMemo(() => historyEntries.map(entry => {
        const contextValues = entry.historyAggregate.executionContext?.contextValues;
        const startTime = contextValues?.startTime;
        const endTime = contextValues?.endTime;

        return {
            started: dateFormatter(startTime, "long"),
            ended: dateFormatter(endTime, "long"),
            duration: startTime !== undefined && endTime !== undefined ? formatDuration(endTime - startTime) : "0ms"
        };
    }), [historyEntries]);

    const placeHolderSeries: NonNullable<EChartsOption["series"]> = [{
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
    }];

    const historySeries: NonNullable<EChartsOption["series"]> = statuses.map(status => ({
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
        data: historyEntries.map((entry, index): HistoryChartData => ({
            runIndex: entry.historyIndex,
            value: entry.getSummarizedStatusCount(StatusService.getGroup(status)),
            started: runMetaData[index].started,
            ended: runMetaData[index].ended,
            duration: runMetaData[index].duration
        }))
    }));

    const option: EChartsOption = useMemo(() => ({
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
                    const point = item.data as HistoryChartData | undefined;
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
                        const statusColor = StatusService.getColor(status);
                        return `<li style="display:flex;margin:2px 0;">                            
                            <span style="display:flex;align-items:center;">
                                <span style="display:inline-block;width:12px;height:12px;border-radius:50%;background:${statusColor};margin-right:6px;"></span>    
                                <strong>${row.seriesName}:</strong>
                                &nbsp;${row.value}
                            </span>
                            
                        </li>`;
                    })
                    .join("");

                let tooltip = `<div style="background-color: ${reportTheme.palette.lightGrey.light}; padding: 5px; margin: -10px -10px 10px -10px;"><strong>Run ${runNumber}</strong></div>`;
                tooltip += `<div style="margin-bottom:6px;"><strong>Testcases:</strong> ${testCases}</div>`;
                tooltip += `<ul style="list-style:none;padding:0;margin:0;">${statusListItems}</ul>`;
                tooltip += `<div style="margin-top:8px;padding-top:6px;border-top:1px solid ${reportTheme.palette.lightGrey.main};">
                    <div><strong>Started:</strong> ${firstPointData?.started ?? "0"}</div>
                    <div><strong>Ended:</strong> ${firstPointData?.ended ?? "0"}</div>
                    <div><strong>Duration:</strong> ${firstPointData?.duration ?? "0ms"}</div>
                </div>`;
                return tooltip;
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

    return (
        <ReportCard
            label="History"
            sxContent={{p: 0}}
            sxCard={sx}
            content={<Echart option={option} notMerge={true} autoResize={true}/>}
        />
    );
};
export default DashboardHistoryChartCard;
