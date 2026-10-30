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

import {Box, List, ListItem, ListItemIcon, ListItemText} from "@mui/material";
import FlagIcon from "@mui/icons-material/Flag";
import CalendarTodayIcon from "@mui/icons-material/CalendarToday";
import TimerIcon from "@mui/icons-material/Timer";
import BarChartIcon from "@mui/icons-material/BarChart";
import type {SxProps, Theme} from "@mui/material/styles";
import type {HistoryAggregateStatistics} from "../../model/HistoryAggregateStatistics.ts";
import {StatusService} from "../../model/status-service";
import {formatDate} from "../../utils/dateFormatter.ts";
import {formatDuration} from "../../utils/durationFormatter.ts";
import ReportCard from "../../widgets/ReportCard.tsx";
import TestRunPieChart from "../TestRunPieChart.tsx";

interface RunCompareStatisticsCardProps {
    label: string;
    runStatistics: HistoryAggregateStatistics;
    sx?: SxProps<Theme>;
}

const RunCompareStatisticsCard = ({label, runStatistics, sx}: RunCompareStatisticsCardProps) => {
    const contextValues = runStatistics.historyAggregate.executionContext?.contextValues;
    const startTime = contextValues?.startTime;
    const endTime = contextValues?.endTime;
    const executedTestCases = StatusService.getRelevantStatuses().reduce<number>(
        (total, status) => total + runStatistics.getSummarizedStatusCount(StatusService.getGroup(status)),
        0,
    );

    return (
        <ReportCard
            label={label}
            sxCard={sx}
            content={
                <Box sx={{display: "flex", flexDirection: {xs: "column", sm: "row"}, flex: 1, gap: 2}}>
                    <Box sx={{display: "flex", flex: 1, minWidth: 0, minHeight: 240}}>
                        <TestRunPieChart execStatistics={runStatistics}/>
                    </Box>
                    <List dense sx={{flex: 1, py: 0, minWidth: 0}}>
                        <ListItem>
                            <ListItemIcon><FlagIcon/></ListItemIcon>
                            <ListItemText primary={runStatistics.historyIndex} secondary="Execution index"/>
                        </ListItem>
                        <ListItem>
                            <ListItemIcon><CalendarTodayIcon/></ListItemIcon>
                            <ListItemText
                                primary={startTime !== undefined ? formatDate(startTime, "long") : "Not available"}
                                secondary="Started"
                            />
                        </ListItem>
                        <ListItem>
                            <ListItemIcon><TimerIcon/></ListItemIcon>
                            <ListItemText
                                primary={startTime !== undefined && endTime !== undefined
                                    ? formatDuration(endTime - startTime)
                                    : "Not available"}
                                secondary="Run duration"
                            />
                        </ListItem>
                        <ListItem>
                            <ListItemIcon><BarChartIcon/></ListItemIcon>
                            <ListItemText primary={executedTestCases} secondary="Executed test cases"/>
                        </ListItem>
                    </List>
                </Box>
            }
        />
    );
};

export default RunCompareStatisticsCard;
