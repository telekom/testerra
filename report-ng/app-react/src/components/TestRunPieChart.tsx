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

import type {EChartsOption} from "echarts-for-react";
import type {CallbackDataParams} from "echarts/types/dist/shared";
import type {Statistics} from "../model/Statistics.ts";
import {StatusService} from "../model/status-service";
import {buildChartTooltip, buildTooltipStatusBadge} from "../utils/chartTooltip";
import Echart from "../widgets/Echart";

export interface TestRunPieChartProps {
    execStatistics: Statistics;
    onChartPieceClick?: (newPiece: string) => void;
    selectedStatus?: string | null;
    // Fixed chart height for contexts where automatic resizing is not reliable, such as printing.
    chartHeight?: number;
}

const TestRunPieChart = ({execStatistics, onChartPieceClick, selectedStatus, chartHeight}: TestRunPieChartProps) => {
    const data = StatusService.getRelevantStatuses().map(status => {
        const statusInformation = StatusService.get(status);
        return {
            value: execStatistics.getSummarizedStatusCount(StatusService.getGroup(status)),
            name: statusInformation.label,
            itemStyle: {color: statusInformation.color},
            selected: selectedStatus === statusInformation.label,
        };
    });

    const option: EChartsOption = {
        tooltip: {
            formatter: (params: CallbackDataParams) => {
                const status = StatusService.getStatusByLabel(params.name);
                return buildChartTooltip({
                    body: {
                        content: `${buildTooltipStatusBadge(status)} ${params.value}`,
                    },
                });
            },
        },
        series: [
            {
                name: "Tests",
                type: "pie",
                radius: ["42%", "89%"],
                center: ["50%", "50%"],
                selectedMode: onChartPieceClick ? "single" : false,
                selectedOffset: 5,
                cursor: onChartPieceClick ? "pointer" : "default",
                data,
                label: {
                    formatter: (params: CallbackDataParams) => params.percent === 0 ? "" : `${params.percent}%`,
                    position: "inside",
                },
            },
        ],
    };

    return (
        <Echart
            option={option}
            onEvents={onChartPieceClick ? {
                click: (params: CallbackDataParams) => onChartPieceClick(params.name),
            } : undefined}
            notMerge={true}
            autoResize={true}
            height={chartHeight}
        />
    );
};

export default TestRunPieChart;
