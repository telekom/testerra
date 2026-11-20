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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {useEffect, useMemo, useRef, useState} from "react";
import {useNavigate} from "react-router-dom";
import {Box, Stack, Typography} from "@mui/material";
import type {EChartsOption} from "echarts-for-react";
import type {
    CustomSeriesRenderItemAPI,
    CustomSeriesRenderItemParams,
    CustomSeriesRenderItemReturn,
    EChartsType,
    TooltipComponentFormatterCallbackParams
} from "echarts";
import ReportCard from "../../widgets/ReportCard.tsx";
import Echart from "../../widgets/Echart.tsx";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {ResultStatusType} from "../../model/report-model/framework_pb.ts";
import {StatusService} from "../../model/status-service.tsx";
import {formatDate} from "../../utils/dateFormatter.ts";
import {formatDuration} from "../../utils/durationFormatter.ts";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";
import {buildChartTooltip, buildTooltipStatusBadge} from "../../utils/chartTooltip.ts";
import {reportTheme} from "../../layout/reportTheme.tsx";
import {
    buildTestClassCompareData,
    buildTestMethodCompareData,
    type HistoryRunPoint
} from "./testClassCompareData.ts";
import type {ResultStatus} from "../../model/status-service.tsx";
import {escapeHtml} from "../../utils/escapeHtml.ts";

type TestClassChartChildren = NonNullable<Extract<
    NonNullable<CustomSeriesRenderItemReturn>,
    { type: "group" }
>["children"]>;

interface TestClassCompareChartProps {
    historyStatistics: HistoryStatistics;
    selectedClass?: string;
    selectedStatus?: ResultStatus;
    onSelectClass: (identifier: string) => void;
}

interface ChartAxisEvent {
    componentType?: string;
    targetType?: string;
    value?: string;
}

const TestClassCompareChartCard = ({historyStatistics, selectedClass, selectedStatus, onSelectClass}: TestClassCompareChartProps) => {
    const navigate = useNavigate();

    // constants
    const categoryHeight = 80;          // Height of y-axis categories in pixel
    const categoryWidth = 62;           // Width of x-axis categories in pixel
    const symbolSize = 60;             // Width and height of chart symbol in pixel
    const labelFormatLength = 45;       // Maximum number of characters for y-category names before linebreak
    const maxLabelLength = 220;         // Total maximum length of y-category names
    const gridLeft = labelFormatLength * 7;
    const cardBorderSpacing = 20;       // To ensure the grid's width stays smaller than the card

    const chartContainerRef = useRef<HTMLDivElement>(null);
    const [containerWidth, setContainerWidth] = useState(0);

    const compareData = useMemo(() => buildTestClassCompareData(historyStatistics), [historyStatistics]);
    const visibleClasses = useMemo(() => compareData.classes.filter(testClass =>
        (!selectedClass || testClass.identifier === selectedClass)
        && (!selectedStatus || compareData.classStatuses.get(testClass.identifier)?.has(selectedStatus))
    ), [compareData, selectedClass, selectedStatus]);
    const classNames = useMemo(
        () => new Map(compareData.classes.map(testClass => [testClass.identifier, testClass.name])),
        [compareData.classes]
    );
    const classIds = useMemo(() => visibleClasses.map(testClass => testClass.identifier), [visibleClasses]);
    const visiblePoints = useMemo(
        () => compareData.points.filter(point => classIds.includes(point.value[1])),
        [classIds, compareData.points]
    );
    const methodCompareData = useMemo(
        () => selectedClass ? buildTestMethodCompareData(historyStatistics, selectedClass, selectedStatus) : undefined,
        [historyStatistics, selectedClass, selectedStatus]
    );
    const rowIds = methodCompareData?.methods ?? classIds;
    const chartRuns = methodCompareData?.runs ?? compareData.runs;
    const chartPoints = methodCompareData?.points ?? visiblePoints;
    const methodLinks = useMemo(
        () => methodCompareData?.methodLinks ?? new Map<string, string>(),
        [methodCompareData]
    );
    const hasHistory = historyStatistics.getTotalRunCount() > 1;
    const cardHeight = Math.max(300, rowIds.length * categoryHeight + 120);
    const chartWidth = Math.max(containerWidth, gridLeft + categoryWidth + cardBorderSpacing);
    const visibleRunCount = Math.min(
        chartRuns.length,
        Math.max(1, Math.floor((chartWidth - gridLeft - cardBorderSpacing) / categoryWidth))
    );
    const gridWidth = visibleRunCount * categoryWidth;

    useEffect(() => {
        const container = chartContainerRef.current;
        if (!container) {
            return;
        }
        const observer = new ResizeObserver(([entry]) => {
            setContainerWidth(entry.contentRect.width);
        });
        observer.observe(container);
        return () => observer.disconnect();
    }, [hasHistory]);

    const option: EChartsOption = useMemo(() => ({
        grid: {
            top: 55,
            left: gridLeft,
            width: gridWidth,
            bottom: 20,
            height: Math.max(0, rowIds.length * categoryHeight),
            outerBoundsMode: "none",
        },
        tooltip: hasHistory ? {
            trigger: "item",
            appendToBody: true,
            confine: false,
            borderColor: reportTheme.palette.lightGrey.light,
            formatter: (params: TooltipComponentFormatterCallbackParams) => {
                if (Array.isArray(params)) {
                    return "";
                }
                const point = params.data as HistoryRunPoint | undefined;
                if (!point) {
                    return "";
                }
                const [runIndex] = point.value;

                const timeListItemStyle = "display:grid;grid-template-columns:12ch auto;column-gap:8px;margin:2px 0;";
                const timeListItems =
                    `<li style="${timeListItemStyle}"><span>Run start time</span><span>${formatDate(point.startTime, "long")}</span></li>
                    <li style="${timeListItemStyle}"><span>Run end time</span><span>${formatDate(point.endTime, "long")}</span></li>
                    <li style="${timeListItemStyle}"><span>Run duration</span><span>${formatDuration(point.duration ?? 0)}</span></li>`;

                const tooltipTitle = point.kind === "method"
                    ? escapeHtml(point.value[1])
                    : escapeHtml(classNameConverter(point.value[1], ClassName.simpleName));
                let commonListItems = "";
                let statusListItems = "";

                if (point.kind === "method") {
                    const errorMessage = point.errorMessage.length > 400
                        ? `${point.errorMessage.slice(0, 397)}...`
                        : point.errorMessage;

                    commonListItems = `<div style="margin-top:6px;">Run ${runIndex}</div>
                                <div>${buildTooltipStatusBadge(point.status)}</div>
                                ${errorMessage ? `<div style="max-width:400px;white-space:pre-wrap;overflow-wrap:anywhere;margin-top:6px;">${escapeHtml(errorMessage)}</div>` : ""}`;
                }
                if (point.kind === "class") {
                    const statusRows = [
                        [ResultStatusType.PASSED, point.value[2]],
                        [ResultStatusType.FAILED, point.value[3]],
                        [ResultStatusType.FAILED_EXPECTED, point.value[4]],
                        [ResultStatusType.SKIPPED, point.value[5]],
                    ] as const;

                    const commonListItemStyle = "display:grid;grid-template-columns:15ch 5ch;column-gap:8px;align-items:center;margin:2px 0;";
                    commonListItems =
                        `<li style="${commonListItemStyle}"><span>Run</span><span>${runIndex}</span></li>
                    <li style="${commonListItemStyle}"><span>Test methods</span><span>${point.total}</span></li>`;
                    statusListItems = statusRows
                        .filter(([, count]) => count > 0)
                        .map(([status, count]) => {
                            return `<li style="${commonListItemStyle}">
                            <span>
                                ${buildTooltipStatusBadge(status)}
                            </span>
                        <span>${count}</span>
                    </li>`;
                        })
                        .join("");
                }

                return buildChartTooltip({
                    header: {
                        content: tooltipTitle,
                        style: {backgroundColor: reportTheme.palette.lightGrey.light},
                    },
                    body: {
                        content: `<ul style="list-style:none;padding:0;margin:6px 0;">
                                    ${commonListItems}
                                    ${statusListItems}
                                </ul>
                            <div style="border-top:1px solid ${reportTheme.palette.lightGrey.main};padding-top:6px;margin-top:6px;">
                                <ul style="list-style:none;padding:0;margin:0;">${timeListItems}</ul>
                            </div>`,
                    },
                });
            },
        } : undefined,
        xAxis: {
            type: "category",
            position: "top",
            boundaryGap: true,
            data: chartRuns.map(String),
        },
        yAxis: {
            type: "category",
            inverse: true,
            data: rowIds,
            triggerEvent: true,
            axisLabel: {
                color: (identifier: string) => !selectedClass || methodLinks.has(identifier)
                    ? "blue"
                    : reportTheme.palette.text.primary,
                formatter: (identifier: string) => {
                    const className = selectedClass ? identifier : (classNames.get(identifier)
                        ?? classNameConverter(identifier, ClassName.simpleName));
                    const cleanedName = className.replace(/\n/g, " ");
                    const formattedName = cleanedName.match(new RegExp(`.{1,${labelFormatLength}}`, "g"))?.join("\n") ?? className;
                    return formattedName.length > maxLabelLength
                        ? `${formattedName.substring(0, maxLabelLength - 2)}...`
                        : formattedName;
                },
            },
            axisTick: {show: false},
            splitArea: {
                show: true,
                areaStyle: {
                    color: [reportTheme.palette.background.paper, reportTheme.palette.lightGrey.light],
                    opacity: 1,
                },
            },
        },
        dataZoom: hasHistory && chartRuns.length > 0 ? [{
            type: "slider",
            xAxisIndex: [0],
            left: gridLeft,
            width: gridWidth,
            height: 20,
            startValue: Math.max(0, chartRuns.length - visibleRunCount),
            endValue: chartRuns.length - 1,
            brushSelect: false,
            zoomLock: true,
            showDataShadow: false,
            top: "top",
        }] : undefined,
        series: hasHistory ? [{
            type: "custom",
            encode: {x: 0, y: 1},
            data: chartPoints,
            renderItem: (_params: CustomSeriesRenderItemParams, api: CustomSeriesRenderItemAPI) => {
                const x = api.coord([api.value(0), api.value(1)])[0];
                const y = api.coord([api.value(0), api.value(1)])[1];
                const half = symbolSize / 2;
                const third = symbolSize / 3;
                if (selectedClass) {
                    return {
                        type: "group",
                        emphasisDisabled: true,
                        children: [
                            {
                                type: "rect",
                                shape: {x: x - half, y: y - half, width: symbolSize, height: symbolSize},
                                style: {fill: "rgba(0,0,0,0)"},
                            },
                            {
                                type: "rect",
                                shape: {x: x - half, y: y - third / 2, width: symbolSize, height: third},
                                style: {fill: StatusService.getColor(String(api.value(2)))},
                            },
                        ],
                    };
                }
                const passed = Number(api.value(2));
                const failed = Number(api.value(3));
                const expectedFailed = Number(api.value(4));
                const skipped = Number(api.value(5));
                const children: TestClassChartChildren = [{
                    type: "rect" as const,
                    shape: {x: x - half, y: y - half, width: symbolSize, height: symbolSize},
                    style: {fill: "rgba(0,0,0,0)"},
                }];

                const addSegment = (segmentX: number, segmentY: number, width: number, height: number, color: string, count: number) => {
                    if (count <= 0) {
                        return;
                    }
                    children.push({
                        type: "rect" as const,
                        shape: {x: segmentX, y: segmentY, width, height},
                        style: {fill: color},
                    });
                    children.push({
                        type: "text" as const,
                        style: {
                            x: segmentX + width / 2,
                            y: segmentY + height / 2,
                            text: String(count),
                            fill: "#fff",
                            fontSize: 12,
                            align: "center",
                            verticalAlign: "middle",
                        },
                    });
                };

                addSegment(x - half, y - half, symbolSize, third, StatusService.getColor(ResultStatusType.PASSED), passed);
                addSegment(x - half, y - third / 2, failed > 0 && expectedFailed > 0 ? half : symbolSize, third,
                    StatusService.getColor(ResultStatusType.FAILED), failed);
                addSegment(x - (failed > 0 && expectedFailed > 0 ? 0 : half), y - third / 2,
                    failed > 0 && expectedFailed > 0 ? half : symbolSize, third,
                    StatusService.getColor(ResultStatusType.FAILED_EXPECTED), expectedFailed);
                addSegment(x - half, y + third / 2, symbolSize, third, StatusService.getColor(ResultStatusType.SKIPPED), skipped);

                return {type: "group", children, emphasisDisabled: true};
            },
        }] : [],
        graphic: hasHistory && rowIds.length === 0 ? {
            type: "text",
            left: "center",
            top: "center",
            silent: true,
            style: {
                text: selectedClass
                    ? "No test methods available for the selected filters"
                    : "No test classes available for the selected filters",
                font: "16px Roboto",
                fill: reportTheme.palette.lightGrey.dark,
            },
        } : undefined,
    }), [categoryHeight, rowIds, classNames, chartRuns, chartPoints, gridLeft, gridWidth, hasHistory, methodLinks, selectedClass, visibleRunCount]);

    const cardTitle = !hasHistory
        ? "No history available"
        : selectedClass
            ? `History of all test cases (${rowIds.length}) in class: ${classNames.get(selectedClass) ?? selectedClass}`
            : `History of all test classes (${compareData.classes.length})`;

    return (
        <ReportCard
            label={cardTitle}
            sxCard={{height: cardHeight}}
            content={hasHistory ? (
                <Box ref={chartContainerRef}
                     sx={{height: "100%", display: "flex", flexDirection: "column", overflowX: "auto", pt: 1}}>
                    <Echart
                        option={option}
                        width={chartWidth}
                        notMerge={true}
                        autoResize={true}
                        onEvents={{
                            click: (event: ChartAxisEvent) => {
                                if (event.componentType !== "yAxis" || event.targetType !== "axisLabel" || !event.value) {
                                    return;
                                }
                                if (!selectedClass) {
                                    onSelectClass(event.value);
                                    return;
                                }
                                const target = methodLinks.get(event.value);
                                if (target) {
                                    navigate(target);
                                }
                            },
                            mousemove: (event: ChartAxisEvent, chart: EChartsType) => {
                                const isLink = event.componentType === "yAxis"
                                    && event.targetType === "axisLabel"
                                    && (!selectedClass || methodLinks.has(event.value ?? ""));
                                chart.getZr().setCursorStyle(isLink ? "pointer" : "default");
                            },
                        }}
                    />
                </Box>
            ) : (
                <Stack sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                    <Typography>No history available</Typography>
                </Stack>
            )}
        />
    );
};

export default TestClassCompareChartCard;
