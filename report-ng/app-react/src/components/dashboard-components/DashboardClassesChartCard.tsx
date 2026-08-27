import Echart from "../../widgets/Echart";
import React, {useCallback} from "react";
import ReportCard from "../../widgets/ReportCard";
import type {ExecutionStatistics} from "../../model/ExecutionStatistics";
import {StatusService, type ResultStatus} from "../../model/status-service";
import {ClassName, classNameConverter} from "../../utils/classNameConverter";
import {ResultStatusType} from "../../model/report-model/framework_pb";
import {escapeHtml} from "../../utils/escapeHtml";
import type {CallbackDataParams} from "echarts/types/dist/shared";
import {createSearchParams, useNavigate} from "react-router-dom";

interface DashboardClassesChartProps {
    execStatistics: ExecutionStatistics;
    selectedStatus: string | null;
}

const DashboardClassesChartCard: React.FC<DashboardClassesChartProps> = ({execStatistics, selectedStatus}) => {
    const navigate = useNavigate();
    const statuses = StatusService.getRelevantStatuses()
        .filter(status => !selectedStatus || StatusService.getLabel(status) === selectedStatus);

    const failureStatuses = [ResultStatusType.FAILED, ResultStatusType.FAILED_EXPECTED];
    const classes = execStatistics.classStatistics
        .map(classStatistics => {
            const failureCount = classStatistics.getSummarizedStatusCount(failureStatuses);
            const skippedCount = classStatistics.getStatusCount(ResultStatusType.SKIPPED);

            // priority: 0 - failed/expected failed, 1 - no errors but skipped, 2 passed tests only
            const priority = failureCount > 0 ? 0 : skippedCount > 0 ? 1 : 2;

            return {
                classStatistics,
                // see if current filter has results
                visible: statuses.some(status =>
                    classStatistics.getSummarizedStatusCount(StatusService.getGroup(status)) > 0
                ),
                priority,
                // status count is only set for failure & skipped classes
                statusCount: priority === 0 ? failureCount : priority === 1 ? skippedCount : 0,
                simpleName: classNameConverter(classStatistics.classIdentifier, ClassName.simpleName),
            };
        })
        .filter(classData => classData.visible)
        .sort((a, b) => {
            // priority status sorting
            if (a.priority !== b.priority) {
                return a.priority - b.priority;
            }

            // Within failure and skipped groups, sort classes by descending status count
            const statusCountDifference = b.statusCount - a.statusCount;
            if (statusCountDifference !== 0) {
                return statusCountDifference;
            }

            // alphabetic sorting
            const simpleNameDifference = a.simpleName.localeCompare(b.simpleName);
            return simpleNameDifference
                || a.classStatistics.classIdentifier.localeCompare(b.classStatistics.classIdentifier);
        })
        .map(classData => classData.classStatistics);

    const createSeries = (status: ResultStatus) => ({
        name: StatusService.getLabel(status),
        type: 'bar',
        barMaxWidth: 36, // bar height
        stack: 'total',
        label: {show: true},
        emphasis: {disabled: true},
        itemStyle: {color: StatusService.getColor(status)},
        data: classes.map(classStatistics => {
            const count = classStatistics.getSummarizedStatusCount(StatusService.getGroup(status));
            return count > 0 ? count : null;    // avoids empty bars and unnecessary labels
        }),
    });

    const dataAvailable = classes.length > 0;
    const height = classes.length * 48 + 60;    // space per bar + offset for first bar
    const option = {
        tooltip: {
            trigger: 'item',
            axisPointer: {type: 'shadow'},
            appendToBody: true,
            formatter: (params: CallbackDataParams) => {
                const color = typeof params.color === "string" ? params.color : "transparent";
                return `<div style="background-color: ${color}; padding: 5px; color: white; margin: -10px -10px 10px -10px;">
                    ${escapeHtml(params.name)}
                </div>
                ${escapeHtml(params.seriesName ?? "")}: ${params.value}`;
            },
        },
        xAxis: {
            type: 'value',
        },
        yAxis: {
            type: 'category',
            inverse: true,      // failed on top
            data: classes.map(classStatistics =>
                classNameConverter(classStatistics.classIdentifier, ClassName.simpleName)
            ),
            axisLabel: {
                show: dataAvailable,
                width: 400,
                overflow: 'truncate',
            },
            axisLine: {show: dataAvailable},
            axisTick: {show: dataAvailable},
        },
        grid: {
            containLabel: true,
            show: false,
            top: 16,
            right: 24,
            bottom: 24,
            left: 16,
        },
        graphic: dataAvailable ? undefined : {
            type: 'text',
            left: 'center',
            top: 'middle',
            style: {
                text: 'There is no data for this filter.',
                fill: '#777',
            },
        },
        series: statuses.map(createSeries),
    };

    const handleBarClick = useCallback((params: CallbackDataParams) => {
        if (params.seriesIndex === undefined) return;

        const classStatistics = classes[params.dataIndex];
        const status = statuses[params.seriesIndex];
        if (!classStatistics || status === undefined) return;

        const statusKey = StatusService.get(status).key;
        const className = classNameConverter(classStatistics.classIdentifier, ClassName.simpleName);

        navigate({
            pathname: "/Tests",
            search: createSearchParams({
                class: className,
                status: statusKey,
            }).toString(),
        });
    }, [classes, navigate, statuses]);

    return (
        <ReportCard
            label="Test Classes"
            sxContent={{p: 0}}
            content={<Echart option={option} height={height} onEvents={{click: handleBarClick}} autoResize={true}/>}
        />
    );
};
export default DashboardClassesChartCard;
