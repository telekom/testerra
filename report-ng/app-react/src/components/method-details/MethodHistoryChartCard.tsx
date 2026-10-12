import {useMemo} from "react";
import {Stack, Typography, useTheme} from "@mui/material";
import type {SxProps, Theme} from "@mui/material/styles";
import type {EChartsOption} from "echarts-for-react";
import type {TooltipComponentFormatterCallbackParams} from "echarts";
import type {MethodHistoryStatistics} from "../../model/MethodHistoryStatistics.ts";
import {ResultStatusType} from "../../model/report-model/framework_pb.ts";
import {StatusService} from "../../model/status-service.tsx";
import Echart from "../../widgets/Echart.tsx";
import ReportCard from "../../widgets/ReportCard.tsx";
import {formatDate} from "../../utils/dateFormatter.ts";
import {formatDuration} from "../../utils/durationFormatter.ts";
import {buildChartTooltip, buildTooltipStatusBadge} from "../../utils/chartTooltip.ts";
import {escapeHtml} from "../../utils/escapeHtml.ts";
import {reportTheme} from "../../layout/reportTheme.tsx";

interface MethodHistoryChartCardProps {
    methodHistoryStatistics: MethodHistoryStatistics;
    sx?: SxProps<Theme>;
}

const MethodHistoryChartCard = ({methodHistoryStatistics, sx}: MethodHistoryChartCardProps) => {
    const theme = useTheme();
    const missingRunColor = theme.palette.text.disabled;
    console.log(methodHistoryStatistics);
    const chartData = useMemo(() => {
        const runs = methodHistoryStatistics.runs;
        if (runs.length === 0) {
            return [];
        }
        const runsByIndex = new Map(runs.map(run => [run.historyIndex, run]));
        const start = Math.min(...runsByIndex.keys());
        const end = Math.max(...runsByIndex.keys());

        return Array.from({length: end - start + 1}, (_, index) => {
            const historyIndex = start + index;
            const run = runsByIndex.get(historyIndex);
            const context = run?.context.contextValues;
            const status = run?.context.resultStatus ?? ResultStatusType.NO_RUN;
            const noRun = !run || status === ResultStatusType.NO_RUN;

            return {
                value: [index, 0],
                historyIndex,
                statusName: noRun ? "No Run" : StatusService.getLabel(status),
                noRun,
                errorMessage: run?.combinedErrorMessage ?? "",
                started: formatDate(context?.startTime, "long"),
                ended: formatDate(context?.endTime, "long"),
                duration: context?.startTime !== undefined && context.endTime !== undefined
                    ? formatDuration(context.endTime - context.startTime)
                    : "Unavailable",
                itemStyle: {color: noRun ? missingRunColor : StatusService.getColor(status)},
            };
        });
    }, [methodHistoryStatistics, missingRunColor]);

    const option: EChartsOption = useMemo(() => ({
        grid: {
            top: "20%",
            bottom: "40%",
            left: "2%",
            right: "2%"
        },
        tooltip: {
            trigger: "item",
            appendToBody: true,
            confine: false,
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

                const errorMessage = run.errorMessage.length > 400
                    ? `${run.errorMessage.slice(0, 397)}...`
                    : run.errorMessage;
                const errorMessageContent = errorMessage
                    ? `<div style="margin-bottom:
                            8px;padding-bottom:
                            6px;border-bottom:1px solid ${reportTheme.palette.lightGrey.main};
                            white-space:pre-wrap;
                            max-width: 400px;">${escapeHtml(errorMessage)}</div>`
                    : "";
                const status = StatusService.getStatusByLabel(run.statusName)

                return buildChartTooltip({
                    header: {
                        content: run.noRun ? "No Run" : `Run ${run.historyIndex} - ${buildTooltipStatusBadge(status)}`,
                        style: {
                            backgroundColor: reportTheme.palette.lightGrey.light,
                        },
                    },
                    body: run.noRun ? undefined : {
                        content: [
                            errorMessageContent,
                            listItems,
                        ],
                    },
                });
            },
        },
        xAxis: {type: "category", data: chartData.map(run => run.historyIndex)},
        yAxis: {type: "value", min: -1, max: 1, show: false},
        series: [
            {
                type: "scatter",
                symbolSize: chartData.length > 50 ? 14 : 20,
                data: chartData,
                cursor: "default",
                z: 2,
            },
            {
                type: "line",
                symbol: "none",
                data: chartData.map(() => 0),
                lineStyle: {color: theme.palette.divider, width: 1},
                tooltip: {show: false},
                silent: true,
                z: 1,
            },
        ],
    }), [chartData, theme.palette.common.white, theme.palette.divider]);

    return (
        <ReportCard
            label="Previous runs"
            sxCard={sx}
            sxContent={{p: 0}}
            content={chartData.length > 0 ? (
                <Echart option={option} notMerge autoResize/>
            ) : (
                <Stack sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                    <Typography>No method history available</Typography>
                </Stack>
            )}
        />
    );
};

export default MethodHistoryChartCard;
