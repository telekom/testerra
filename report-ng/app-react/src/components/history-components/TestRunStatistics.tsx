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
import {List, ListItem, ListItemIcon, ListItemText, Typography} from "@mui/material";
import BarChartIcon from "@mui/icons-material/BarChart";
import AvTimerIcon from "@mui/icons-material/AvTimer";
import PublishedWithChangesIcon from "@mui/icons-material/PublishedWithChanges";
import DoneAllIcon from "@mui/icons-material/DoneAll";
// import FlakyIcon from "@mui/icons-material/Flaky";
import ReportCard from "../../widgets/ReportCard.tsx";
// import InfoTooltip from "../../widgets/InfoTooltip.tsx";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {formatDuration} from "../../utils/durationFormatter.ts";

interface TestRunStatisticsProps {
    histStatistics: HistoryStatistics;
    sx?: SxProps<Theme>;
}

const TestRunStatistics = ({histStatistics, sx}: TestRunStatisticsProps) => {
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

    // const flakiness = useMemo(() => {
    //     const flakinessValues = histStatistics.getClassHistory()
    //         .flatMap(classHistory => classHistory.methods)
    //         .filter(methodHistory => methodHistory.isTestMethod())
    //         .map(methodHistory => methodHistory.flakiness)
    //         .filter(value => Number.isFinite(value));
    //
    //     if (flakinessValues.length === 0) {
    //         return undefined;
    //     }
    //
    //     const avg = flakinessValues.reduce((sum, value) => sum + value, 0) / flakinessValues.length;
    //     return avg.toFixed(1);
    // }, [histStatistics]);

    return (
        <ReportCard
            label="History statistics"
            sxContent={{p: 0}}
            sxCard={sx}
            content={(
                <List dense sx={{py: 0}}>
                    <ListItem>
                        <ListItemIcon><BarChartIcon/></ListItemIcon>
                        <ListItemText
                            primary={<Typography>{totalRunCount}</Typography>}
                            secondary="Total runs"
                        />
                    </ListItem>
                    <ListItem>
                        <ListItemIcon><AvTimerIcon/></ListItemIcon>
                        <ListItemText
                            primary={<Typography>{formatDuration(averageRunDuration)}</Typography>}
                            secondary="Average run duration"
                        />
                    </ListItem>
                    {recentChanges !== undefined && (
                        <ListItem>
                            <ListItemIcon>
                                {recentChanges ? <PublishedWithChangesIcon/> : <DoneAllIcon/>}
                            </ListItemIcon>
                            <ListItemText
                                primary={<Typography>{recentChanges ? "Latest runs are not equal" : "No changes to previous run"}</Typography>}
                                secondary="Run comparison"
                            />
                        </ListItem>
                    )}
                    {/*{flakiness !== undefined && (*/}
                    {/*    <ListItem*/}
                    {/*        secondaryAction={<InfoTooltip text="Flakiness indicates how often the status changed. Higher values suggest instability. Older runs contribute less to the final calculation."/>}*/}
                    {/*    >*/}
                    {/*        <ListItemIcon><FlakyIcon/></ListItemIcon>*/}
                    {/*        <ListItemText*/}
                    {/*            primary={<Typography>{flakiness}%</Typography>}*/}
                    {/*            secondary="Flakiness"*/}
                    {/*        />*/}
                    {/*    </ListItem>*/}
                    {/*)}*/}
                </List>
            )}
        />
    );
};

export default TestRunStatistics;
