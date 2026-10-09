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
import FlakyIcon from "@mui/icons-material/Flaky";
import CelebrationIcon from "@mui/icons-material/Celebration";
import ReportCard from "../../widgets/ReportCard.tsx";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {useNavigate} from "react-router-dom";
import type {TestRunViewport} from "./TestRunChart.tsx";

interface TestRunTopFlakyProps {
    histStatistics: HistoryStatistics;
    sx?: SxProps<Theme>;
    viewport?: TestRunViewport;
}

interface FlakyMethodItem {
    name: string;
    flakiness: string;
    passingStreak: number;
    methodId: string | null;
}

const TestRunTopFlakyCard = ({histStatistics, sx, viewport}: TestRunTopFlakyProps) => {
    const navigate = useNavigate();

    const topFlakyTests = useMemo((): FlakyMethodItem[] => {
        const availableRuns = histStatistics.availableRuns;
        if (availableRuns.length < 2) {
            return [];
        }

        const startIndex = viewport?.start ?? Math.min(...availableRuns);
        const endIndex = viewport?.end ?? Math.max(...availableRuns);
        const methods = histStatistics.getClassHistory().flatMap(classItem => classItem.methods);

        return methods
            .filter(method => method.isTestMethod())
            .map(method => ({
                name: method.identifier,
                flakiness: method.getFlakinessInRange(startIndex, endIndex),
                passingStreak: method.getPassingStreakInRange(startIndex, endIndex),
                methodId: method.getIdOfRun(endIndex) ?? null,
            }))
            .filter(method => method.flakiness > 0.1)
            .sort((a, b) => b.flakiness - a.flakiness)
            .slice(0, 3)
            .map(method => ({
                ...method,
                flakiness: method.flakiness.toFixed(1)
            }));
    }, [histStatistics, viewport]);

    return (
        <ReportCard
            label="Top 3 flaky tests"
            tooltipText="Test cases with a high frequency of status changes in the currently visible viewport of the overview chart"
            sxContent={{p: 0}}
            sxCard={sx}
            content={topFlakyTests.length > 0 ? (
                <List dense sx={{py: 0}}>
                    {topFlakyTests.map((method) => (
                        <ListItem key={`${method.name}-${method.flakiness}`} disablePadding>
                            <ListItemButton
                                disabled={!method.methodId}
                                onClick={() => {
                                    if (method.methodId) {
                                        navigate(`/method/${method.methodId}`);
                                    }
                                }}
                            >
                                <ListItemIcon>
                                    <FlakyIcon color="error"/>
                                </ListItemIcon>
                                <ListItemText
                                    primary={<Typography>{method.name}</Typography>}
                                    secondary={`Flakiness: ${method.flakiness}% (${method.passingStreak === 0 ? "Currently failing" : `Passed since ${method.passingStreak} ${method.passingStreak === 1 ? "run" : "runs"}`})`}
                                />
                            </ListItemButton>
                        </ListItem>
                    ))}
                </List>
            ) : (
                <Stack direction="row" spacing={1} sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                    <CelebrationIcon/>
                    <Typography>No flaky tests</Typography>
                </Stack>
            )}
        />
    );
};

export default TestRunTopFlakyCard;
