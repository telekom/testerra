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
import HistoryStatisticsCard from "../../widgets/HistoryStatisticsCard.tsx";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";

interface TestRunStatisticsProps {
    histStatistics: HistoryStatistics;
    sx?: SxProps<Theme>;
}

const TestRunStatisticsCard = ({histStatistics, sx}: TestRunStatisticsProps) => {
    const totalRunCount = useMemo(
        () => histStatistics.history.entries?.length ?? histStatistics.getTotalRunCount(),
        [histStatistics]
    );

    const averageRunDuration = useMemo(() => {
        const durations = (histStatistics.history.entries ?? [])
            .map(entry => {
                const contextValues = entry.executionContext?.contextValues;
                const startTime = contextValues?.startTime;
                const endTime = contextValues?.endTime;
                if (typeof startTime !== "number" || typeof endTime !== "number") {
                    return undefined;
                }
                return endTime - startTime;
            })
            .filter((duration): duration is number => duration !== undefined);

        if (durations.length === 0) {
            return 0;
        }
        return Math.round(durations.reduce((sum, duration) => sum + duration, 0) / durations.length);
    }, [histStatistics]);

    const recentChanges = useMemo(() => {
        const aggregates = histStatistics.getHistoryAggregateStatistics();
        if (aggregates.length < 2) {
            return undefined;
        }
        return histStatistics.lastEntryDifferentFrom(aggregates[aggregates.length - 2]);
    }, [histStatistics]);

    return (
        <HistoryStatisticsCard
            totalRunCount={totalRunCount}
            averageRunDuration={averageRunDuration}
            recentChanges={recentChanges}
            sx={sx}
        />
    );
};

export default TestRunStatisticsCard;
