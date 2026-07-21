/*
 * Testerra
 *
 * (C) 2026, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {Grid, Typography} from "@mui/material";
import ReportCard from "../widgets/ReportCard";
import Stack from "@mui/material/Stack";
import Divider from "@mui/material/Divider";
import TimerIcon from '@mui/icons-material/Timer';
import type {SxProps, Theme} from "@mui/material/styles";
import {useReportData} from "../provider/DataProvider";
import React from "react";
import {dateFormatter} from "../utils/dateFormatter";
import {formatDuration} from "../utils/durationFormatter";

interface DashboardDurationProps {
    sx?: SxProps<Theme>;
    start?: number;
    end?: number;
}

const DashboardDurationCard = ({sx, start, end}: DashboardDurationProps) => {

    const {executionMngr} = useReportData();
    const [now] = React.useState(() => Date.now());

    const executionInfo = React.useMemo(() => {
        // For individual test method duration: If start/end are provided as props
        if (start) {
            const hasEnded = !!end;
            const ended = end ?? now;
            return {
                started: start,
                ended,
                hasEnded,
                duration: ended - start,
            };
        }

        if (!executionMngr) {
            return {
                started: undefined,
                ended: undefined,
                hasEnded: false,
                duration: 0,
            };
        }

        // For overall report duration: Fetch from execution context
        const executionAggregate = executionMngr.getExecutionAggregate();
        const started = executionAggregate.executionContext?.contextValues?.startTime;
        const rawEnded = executionAggregate.executionContext?.contextValues?.endTime;

        const hasEnded = !!rawEnded;
        const ended = rawEnded ?? now;
        const duration = started ? ended - started : 0;

        return {
            started,
            ended,
            hasEnded,
            duration,
        };
    }, [end, executionMngr, start, now]);


    return (
        <ReportCard label="Duration" sxContent={{p: 0, ":last-child": {padding: 0}}} sxCard={sx}
                    content={
                        <Stack direction="column" spacing={2}
                               divider={<Divider orientation="horizontal" sx={{mt: "0 !important"}}/>}>
                            <Stack direction="row" spacing={1}
                                   sx={{alignItems: "center", justifyContent: "center", p: 2}}>
                                <TimerIcon/>
                                <Typography variant="h5">{formatDuration(executionInfo.duration)}</Typography>
                            </Stack>
                            <Grid container sx={{px: 2, py: 1, mt: "0 !important"}}>
                                <Grid size={3}>
                                    <Typography variant="caption" color="primary">Started</Typography>
                                </Grid>
                                <Grid size={9}>
                                    <Typography
                                        variant="caption">{dateFormatter(executionInfo.started, "long")}</Typography>
                                </Grid>
                                <Grid size={3}>
                                    <Typography variant="caption" color="primary">Ended</Typography>
                                </Grid>
                                <Grid size={9}>
                                    <Typography
                                        variant="caption">{dateFormatter(executionInfo.ended, "long")}</Typography>
                                </Grid>

                            </Grid>
                        </Stack>
                    }
        />
    );
};
export default DashboardDurationCard;
