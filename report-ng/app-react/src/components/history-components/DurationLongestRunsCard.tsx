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
import {List, ListItem, ListItemIcon, ListItemText, Stack, Typography} from "@mui/material";
import TimelapseIcon from "@mui/icons-material/Timelapse";
import {useTheme, type SxProps, type Theme} from "@mui/material/styles";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {ResultStatusType} from "../../model/report-model/framework_pb.ts";
import {formatDuration} from "../../utils/durationFormatter.ts";
import ReportCard from "../../widgets/ReportCard.tsx";
import type {TestRunViewport} from "./TestRunHistoryBarChart.tsx";

interface LongestRunsCardProps {
    histStatistics: HistoryStatistics;
    viewport?: TestRunViewport;
    sx?: SxProps<Theme>;
}

const DurationLongestRunsCard = ({histStatistics, viewport, sx}: LongestRunsCardProps) => {
    const theme = useTheme();
    const longestRuns = useMemo(() => histStatistics.getHistoryAggregateStatistics()
        .filter(entry => !viewport || (entry.historyIndex >= viewport.start && entry.historyIndex <= viewport.end))
        .flatMap(entry => {
            const context = entry.historyAggregate.executionContext?.contextValues;
            if (context?.startTime === undefined || context.endTime === undefined) {
                return [];
            }
            return [{
                runIndex: entry.historyIndex,
                duration: context.endTime - context.startTime,
                testCases: entry.overallPassed
                    + entry.getStatusCount(ResultStatusType.FAILED)
                    + entry.getStatusCount(ResultStatusType.FAILED_EXPECTED)
                    + entry.getStatusCount(ResultStatusType.SKIPPED),
            }];
        })
        .sort((a, b) => b.duration - a.duration)
        .slice(0, 3), [histStatistics, viewport]);

    return (
        <ReportCard
            label="Longest runs"
            sxCard={sx}
            sxContent={{p: 0}}
            content={longestRuns.length > 0 ? (
                <List dense sx={{py: 0}}>
                    {longestRuns.map(run => (
                        <ListItem key={run.runIndex}>
                            <ListItemIcon>
                                <TimelapseIcon sx={{color: theme.custom.chart.blue.main}}/>
                            </ListItemIcon>
                            <ListItemText
                                primary={<Typography>Run {run.runIndex} ({run.testCases} test cases)</Typography>}
                                secondary={formatDuration(run.duration)}
                            />
                        </ListItem>
                    ))}
                </List>
            ) : (
                <Stack sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                    <Typography>No runs available</Typography>
                </Stack>
            )}
        />
    );
};

export default DurationLongestRunsCard;
