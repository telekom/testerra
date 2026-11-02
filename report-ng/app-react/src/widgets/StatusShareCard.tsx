import {useMemo} from "react";
import {Stack, Typography} from "@mui/material";
import type {SxProps, Theme} from "@mui/material/styles";
import type {CallbackDataParams} from "echarts/types/dist/shared";
import type {EChartsOption} from "echarts-for-react";
import ReportCard from "./ReportCard.tsx";
import Echart from "./Echart.tsx";
import {StatusService, type ResultStatus} from "../model/status-service.tsx";
import {buildChartTooltip, buildTooltipStatusBadge} from "../utils/chartTooltip.ts";

export interface StatusShareData {
    status: ResultStatus;
    value: number;
}

interface StatusShareCardProps {
    statusData: StatusShareData[];
    tooltipText: string;
    sx?: SxProps<Theme>;
}

const StatusShareCard = ({statusData, tooltipText, sx}: StatusShareCardProps) => {
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
                data: statusData.map(({status, value}) => ({
                    value,
                    name: StatusService.getLabel(status),
                    itemStyle: {color: StatusService.getColor(status)},
                })),
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
            tooltipText={tooltipText}
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

export default StatusShareCard;
