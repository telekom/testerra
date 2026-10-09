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

import {useMemo} from "react";
import type {SxProps, Theme} from "@mui/material/styles";
import {Stack, Typography} from "@mui/material";
import type {CallbackDataParams} from "echarts/types/dist/shared";
import type {EChartsOption} from "echarts-for-react";
import ReportCard from "../../widgets/ReportCard.tsx";
import Echart from "../../widgets/Echart.tsx";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {StatusService, type ResultStatus} from "../../model/status-service.tsx";
import type {TestRunViewport} from "./TestRunChart.tsx";
import {buildChartTooltip, buildTooltipStatusBadge} from "../../utils/chartTooltip.ts";

interface TestRunStatusShareCardProps {
    histStatistics: HistoryStatistics;
    sx?: SxProps<Theme>;
    viewport?: TestRunViewport;
}

interface StatusShareData {
    status: ResultStatus;
    value: number;
    name: string;
    itemStyle: { color: string };
}

const TestRunStatusShareCard = ({histStatistics, sx, viewport}: TestRunStatusShareCardProps) => {
    const statusData = useMemo((): StatusShareData[] => {
        const availableRuns = histStatistics.availableRuns;
        if (availableRuns.length < 2) {
            return [];
        }

        const startIndex = viewport?.start ?? Math.min(...availableRuns);
        const endIndex = viewport?.end ?? Math.max(...availableRuns);
        const statusCount = new Map<ResultStatus, number>();

        histStatistics.getHistoryAggregateStatistics().forEach(aggregate => {
            if (aggregate.historyIndex < startIndex || aggregate.historyIndex > endIndex) {
                return;
            }

            StatusService.getRelevantStatuses().forEach(status => {
                const current = statusCount.get(status) ?? 0;
                statusCount.set(
                    status,
                    current + aggregate.getSummarizedStatusCount(StatusService.getGroup(status))
                );
            });
        });

        return StatusService.getRelevantStatuses()
            .map(status => {
                const value = statusCount.get(status) ?? 0;
                return {
                    status,
                    value,
                    name: StatusService.getLabel(status),
                    itemStyle: {color: StatusService.getColor(status)}
                };
            })
            .filter(item => item.value > 0);
    }, [histStatistics, viewport]);

    const chartOption = useMemo((): EChartsOption => ({
        grid: {
            top: 0,
            bottom: 0,
            left: 0,
            right: 0,
        },
        tooltip: {
            formatter: (params: CallbackDataParams) => {
                const status = StatusService.getStatusByLabel(params.name);
                const percentage = Number(params.percent ?? 0).toFixed(1);
                return buildChartTooltip({
                    body: {
                        content: `${buildTooltipStatusBadge(status)} ${params.value} (${percentage}%)`,
                    },
                });
            }
        },
        legend: {
            show: false
        },
        series: [
            {
                name: "Status Share",
                type: "pie",
                radius: ["40%", "130%"],
                center: ["50%", "85%"],
                startAngle: 180,
                endAngle: 360,
                data: statusData,
                cursor: "default",
                label: {
                    show: true,
                    silent: true,
                    position: "inner",
                    color: "#ffffff",
                    formatter: (params: CallbackDataParams) => `${Number(params.percent ?? 0).toFixed(1)}%`
                },
                labelLine: {
                    length: 10,
                    length2: 10,
                    lineStyle: {
                        color: "#000000",
                        width: 1
                    }
                }
            }
        ],
        media: [
            {
                query: {maxWidth: 280},
                option: {
                    series: [
                        {
                            radius: ["30%", "100%"],
                        }
                    ]
                }
            },
            {
                query: {minWidth: 281},
                option: {
                    series: [
                        {
                            radius: ["40%", "130%"],
                        }
                    ]
                }
            }
        ]
    }), [statusData]);

    return (
        <ReportCard
            label="Status share"
            tooltipText="Displays the distribution of statuses for all runs currently visible in the overview chart"
            sxCard={sx}
            sxContent={{p: 0, ":last-child": {padding: 0}}}
            content={statusData.length > 0 ? (
                <Echart option={chartOption} autoResize={true}/>
            ) : (
                <Stack direction="row" spacing={1} sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                    <Typography>No status data</Typography>
                </Stack>
            )}
        />
    );
};

export default TestRunStatusShareCard;
