import {useMemo} from "react";
import {Stack, Typography} from "@mui/material";
import type {SxProps, Theme} from "@mui/material/styles";
import type {EChartsOption} from "echarts-for-react";
import type {CallbackDataParams} from "echarts/types/dist/shared";
import type {MethodHistoryStatistics} from "../../model/MethodHistoryStatistics.ts";
import {StatusService} from "../../model/status-service.tsx";
import Echart from "../../widgets/Echart.tsx";
import ReportCard from "../../widgets/ReportCard.tsx";
import {escapeHtml} from "../../utils/escapeHtml.ts";
import {buildChartTooltip} from "../../utils/chartTooltip.ts";

interface MethodFailureAspectsCardProps {
    methodHistoryStatistics: MethodHistoryStatistics;
    selectedFailureAspect?: string;
    onSelectFailureAspect: (failureAspect: string | undefined) => void;
    sx?: SxProps<Theme>;
}

const MethodFailureAspectsCard = ({
    methodHistoryStatistics,
    selectedFailureAspect,
    onSelectFailureAspect,
    sx,
}: MethodFailureAspectsCardProps) => {
    const failureAspects = useMemo(() => methodHistoryStatistics.getFailureAspectHistory()
        .sort((a, b) => b.count - a.count), [methodHistoryStatistics]);
    const statuses = useMemo(() => Array.from(new Set(
        failureAspects.flatMap(aspect => Array.from(aspect.statusCounts.keys()))
    )).sort((a, b) => a - b), [failureAspects]);

    const option = useMemo((): EChartsOption => ({
        grid: {top: 55, bottom: 20, left: 50, right: 50, containLabel: true},
        legend: {type: "scroll", top: 5},
        tooltip: {
            trigger: "item",
            appendToBody: true,
            formatter: (params: CallbackDataParams) => {
                const failureAspect = failureAspects[params.dataIndex];
                if (!failureAspect) {
                    return "";
                }
                const {message, count} = failureAspect;
                const truncatedMessage = message.length > 400 ? `${message.slice(0, 397)}...` : message;
                return buildChartTooltip({
                    body: {
                        content: `${escapeHtml(truncatedMessage)}<br><br>Occurrences: ${count}`,
                        style: {whiteSpace: "pre-wrap", maxWidth: "400px", overflowWrap: "anywhere"},
                    },
                });
            },
        },
        xAxis: {
            type: "category",
            data: failureAspects.map(({message}) => message),
            axisLabel: {
                formatter: (value: string) => value.length > 28 ? `${value.slice(0, 28)}...` : value,
            },
        },
        yAxis: {type: "value", minInterval: 1},
        series: statuses.map(status => ({
            name: StatusService.getLabel(status),
            type: "bar",
            stack: "failureAspects",
            itemStyle: {color: StatusService.getColor(status)},
            data: failureAspects.map(({message, statusCounts}) => ({
                value: statusCounts.get(status) ?? 0,
                itemStyle: {
                    opacity: selectedFailureAspect === undefined || selectedFailureAspect === message ? 1 : 0.38,
                },
            })),
        })),
    }), [failureAspects, statuses, selectedFailureAspect]);

    return (
        <ReportCard
            label="Common failure aspects"
            sxCard={sx}
            sxContent={{p: 0}}
            content={failureAspects.length > 0 ? (
                <Echart
                    option={option}
                    notMerge
                    autoResize
                    onEvents={{
                        click: (params: CallbackDataParams) => {
                            const failureAspect = failureAspects[params.dataIndex];
                            if (params.componentType === "series" && failureAspect) {
                                const {message} = failureAspect;
                                onSelectFailureAspect(selectedFailureAspect === message ? undefined : message);
                            }
                        },
                    }}
                />
            ) : (
                <Stack sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                    <Typography>No failure aspects available</Typography>
                </Stack>
            )}
        />
    );
};

export default MethodFailureAspectsCard;
