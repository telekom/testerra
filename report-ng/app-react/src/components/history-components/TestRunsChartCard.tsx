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

import ReportCard from "../../widgets/ReportCard";
import type {SxProps, Theme} from "@mui/material/styles";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import TestRunChart, {type TestRunViewport} from "./TestRunChart.tsx";
import {useMemo} from "react";
import type {EChartsOption} from "echarts-for-react";

interface HistoryChartProps {
    histStatistics: HistoryStatistics
    selectedStatus: string | null;
    sx?: SxProps<Theme>;
    onViewportChange?: (viewport: TestRunViewport) => void;
}

const TestRunChartCard = ({histStatistics, selectedStatus, sx, onViewportChange}: HistoryChartProps) => {
    const additionalChartOptions: EChartsOption = useMemo(() => {
        const resetZoomIconSvgPath = "M 4,1 V 5 H 0 M 3.9865238,4.9219293 C 1.602752,3.5367838 0,0.95556327 0,-2 c 0,-4.418278 3.581722,-8 8,-8 4.418278,0 8,3.581722 8,8 0,4.418278 -3.581722,8 -8,8";

        return {
            dataZoom: [
                {
                    type: "inside",
                    start: 0,
                    end: 100,
                    minValueSpan: 1
                },
                {
                    start: 0,
                    end: 100,
                    minValueSpan: 1
                }
            ],
            toolbox: {
                itemSize: 20,
                feature: {
                    restore: {
                        show: true,
                        title: "Reset Zoom",
                        icon: `path://${resetZoomIconSvgPath}`
                    }
                }
            },
            // Adapt grid for data-zoom slider & reset zoom button
            grid: {
                top: "50px",
                left: "1%",
                right: "3%",
                bottom: "55px",
                containLabel: true
            }
        };
    }, []);

    return (
        <ReportCard
            label="History test runs"
            sxContent={{p: 0}}
            sxCard={sx}
            content={
                <TestRunChart
                    histStatistics={histStatistics}
                    selectedStatus={selectedStatus}
                    additionalChartOptions={additionalChartOptions}
                    onViewportChange={onViewportChange}
                />
            }
        />
    );
};
export default TestRunChartCard;
