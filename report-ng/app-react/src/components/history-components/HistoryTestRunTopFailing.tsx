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
import {List, ListItem, ListItemButton, ListItemIcon, ListItemText, Stack, Typography} from "@mui/material";
import HighlightOffIcon from "@mui/icons-material/HighlightOff";
import CelebrationIcon from "@mui/icons-material/Celebration";
import ReportCard from "../../widgets/ReportCard.tsx";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {useNavigate} from "react-router-dom";

interface HistoryTestRunTopFailingProps {
    histStatistics: HistoryStatistics;
    sx?: SxProps<Theme>;
}

interface FailingMethodItem {
    name: string;
    failingStreak: number;
    methodId: string | null;
}

const HistoryTestRunTopFailing = ({histStatistics, sx}: HistoryTestRunTopFailingProps) => {
    const navigate = useNavigate();

    const topFailingTests = useMemo((): FailingMethodItem[] => {
        const availableRuns = histStatistics.availableRuns;
        if (availableRuns.length < 2) {
            return [];
        }

        const startIndex = Math.min(...availableRuns);
        const endIndex = Math.max(...availableRuns);
        const methods = histStatistics.getClassHistory().flatMap(classItem => classItem.methods);

        return methods
            .filter(method => method.getFailingStreakInRange(startIndex, endIndex) > 0)
            .filter(method => method.isTestMethod())
            .map(method => ({
                name: method.identifier,
                failingStreak: method.getFailingStreakInRange(startIndex, endIndex),
                methodId: method.getIdOfRun(endIndex) ?? null,
            }))
            .sort((a, b) => b.failingStreak - a.failingStreak)
            .slice(0, 3);
    }, [histStatistics]);

    return (
        <ReportCard
            label="Top 3 failing tests"
            tooltipText="Test cases that aren't passed since multiple runs"
            sxContent={{p: 0}}
            sxCard={sx}
            content={topFailingTests.length > 0 ? (
                <List dense sx={{py: 0}}>
                    {topFailingTests.map((method) => (
                        <ListItem key={`${method.name}-${method.failingStreak}`} disablePadding>
                            <ListItemButton
                                disabled={!method.methodId}
                                onClick={() => {
                                    if (method.methodId) {
                                        navigate(`/method/${method.methodId}`);
                                    }
                                }}
                            >
                                <ListItemIcon>
                                    <HighlightOffIcon color="error"/>
                                </ListItemIcon>
                                <ListItemText
                                    primary={<Typography>{method.name}</Typography>}
                                    secondary={`Failing since ${method.failingStreak} ${method.failingStreak === 1 ? "run" : "runs"}`}
                                />
                            </ListItemButton>
                        </ListItem>
                    ))}
                </List>
            ) : (
                <Stack direction="row" spacing={1} sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                    <CelebrationIcon/>
                    <Typography>No failing tests</Typography>
                </Stack>
            )}
        />
    );
};

export default HistoryTestRunTopFailing;
