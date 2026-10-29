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
import StatusShareCard from "../../widgets/StatusShareCard.tsx";
import type {StatusShareData} from "../../widgets/StatusShareCard.tsx";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {StatusService, type ResultStatus} from "../../model/status-service.tsx";
import type {TestRunViewport} from "./TestRunChart.tsx";

interface TestRunStatusShareCardProps {
    histStatistics: HistoryStatistics;
    sx?: SxProps<Theme>;
    viewport?: TestRunViewport;
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
                };
            })
            .filter(item => item.value > 0);
    }, [histStatistics, viewport]);

    return (
        <StatusShareCard
            statusData={statusData}
            tooltipText="Displays the distribution of statuses for all runs currently visible in the overview chart"
            sx={sx}
        />
    );
};

export default TestRunStatusShareCard;
