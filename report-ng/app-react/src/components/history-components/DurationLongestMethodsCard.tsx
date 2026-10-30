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
import {List, ListItem, ListItemButton, ListItemIcon, ListItemText, Stack, Typography} from "@mui/material";
import AvTimerIcon from "@mui/icons-material/AvTimer";
import {useTheme, type SxProps, type Theme} from "@mui/material/styles";
import {Link} from "react-router-dom";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {formatDuration} from "../../utils/durationFormatter.ts";
import ReportCard from "../../widgets/ReportCard.tsx";
import type {TestRunViewport} from "./TestRunHistoryBarChart.tsx";

interface LongestMethodsCardProps {
    histStatistics: HistoryStatistics;
    viewport?: TestRunViewport;
    sx?: SxProps<Theme>;
}

const DurationLongestMethodsCard = ({histStatistics, viewport, sx}: LongestMethodsCardProps) => {
    const theme = useTheme();
    const longestMethods = useMemo(() => {
        const availableRuns = histStatistics.availableRuns;
        if (availableRuns.length === 0) {
            return [];
        }
        const start = viewport?.start ?? Math.min(...availableRuns);
        const end = viewport?.end ?? Math.max(...availableRuns);
        const latestRun = histStatistics.getLastEntry()?.historyIndex;

        return histStatistics.getClassHistory()
            .flatMap(classItem => classItem.methods)
            .filter(method => method.runs.some(run => {
                const context = run.context.contextValues;
                return run.historyIndex >= start && run.historyIndex <= end
                    && context?.startTime !== undefined && context.endTime !== undefined;
            }))
            .map(method => ({
                name: method.identifier,
                averageDuration: method.getAverageDurationInRange(start, end),
                methodId: latestRun === undefined ? null : method.getIdOfRun(latestRun),
            }))
            .sort((a, b) => b.averageDuration - a.averageDuration)
            .slice(0, 3);
    }, [histStatistics, viewport]);

    return (
        <ReportCard
            label="Longest methods"
            tooltipText="The methods with the longest average execution durations in the selected range"
            sxCard={sx}
            sxContent={{p: 0}}
            content={longestMethods.length > 0 ? (
                <List dense sx={{py: 0}}>
                    {longestMethods.map((method, index) => (
                        <ListItem key={`${method.name}-${index}`} disablePadding>
                            <ListItemButton
                                component={Link}
                                to={method.methodId ? `/method/${method.methodId}` : ""}
                                disabled={!method.methodId}
                            >
                                <ListItemIcon>
                                    <AvTimerIcon sx={{color: theme.custom.chart.blue.main}}/>
                                </ListItemIcon>
                                <ListItemText
                                    primary={<Typography>{method.name}</Typography>}
                                    secondary={formatDuration(method.averageDuration)}
                                />
                            </ListItemButton>
                        </ListItem>
                    ))}
                </List>
            ) : (
                <Stack sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                    <Typography>No methods available</Typography>
                </Stack>
            )}
        />
    );
};

export default DurationLongestMethodsCard;
