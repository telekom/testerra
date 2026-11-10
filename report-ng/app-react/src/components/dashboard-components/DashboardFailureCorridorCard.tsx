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
import {Typography, useTheme} from "@mui/material";
import ReportCard from "../../widgets/ReportCard";
import Stack from '@mui/material/Stack';
import Chip from '@mui/material/Chip';
import type {SxProps, Theme} from "@mui/material/styles";
import {FailureCorridorValue, type ExecutionContext} from "../../model/report-model/framework_pb";

interface DashboardFailureCorridorProps {
    executionContext?: ExecutionContext;
    sx?: SxProps<Theme>;
}

const DashboardFailureCorridorCard = ({executionContext, sx}: DashboardFailureCorridorProps) => {
    const theme = useTheme();

    const chipList = [
        {name: "High", value: FailureCorridorValue.FCV_HIGH},
        {name: "Mid", value: FailureCorridorValue.FCV_MID},
        {name: "Low", value: FailureCorridorValue.FCV_LOW}
    ].map(({name, value}) => {
        const count = executionContext?.failureCorridorCounts?.[value] ?? 0;
        const limit = executionContext?.failureCorridorLimits?.[value] ?? 0;

        return {
            name,
            count,
            chipColor: count <= limit
                ? theme.custom.statusColors.passed
                : theme.custom.statusColors.failed,
            limit
        };
    });

    return (
        <ReportCard
            testId={"failure-corridor-card"}
            label="Failure Corridor"
            sxContent={{p: 2}}
            tooltipText="The severity distribution of failed test cases in relation to the defined test goal"
            sxCard={sx}
            content={(
                <Stack direction="column" spacing={1} sx={{alignItems: "center"}}>
                    {chipList.map((chip) => {
                        const label = `${chip.count} ${chip.name}`;
                        return (
                            <Stack
                                direction="row"
                                data-testid={chip.name}
                                key={chip.name}
                                spacing={1}
                                sx={{alignItems: "center"}}>
                                <Chip
                                    label={label}
                                    sx={{background: chip.chipColor, color: "white"}}/>
                                <Typography color="primary"> of {chip.limit} </Typography>
                            </Stack>
                        );
                    })}
                </Stack>
            )}
        />
    );
};
export default DashboardFailureCorridorCard;
