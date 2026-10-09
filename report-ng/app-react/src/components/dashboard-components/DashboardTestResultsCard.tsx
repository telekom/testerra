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

import ReportCard from "../../widgets/ReportCard";
import {ResultStatusType} from "../../model/report-model/framework_pb";
import {StatusService} from "../../model/status-service";
import type {SxProps, Theme} from "@mui/material/styles";
import type {ExecutionStatistics} from "../../model/ExecutionStatistics";
import {Button, List, ListItem, ListItemButton, ListItemIcon, ListItemText, Stack, Typography} from "@mui/material";
import NorthEastIcon from "@mui/icons-material/NorthEast";
import SouthEastIcon from "@mui/icons-material/SouthEast";
import EastIcon from "@mui/icons-material/East";
import CompareArrowsIcon from "@mui/icons-material/CompareArrows";
import {Link as RouterLink} from "react-router-dom";
import {useMemo} from "react";
import {useReportData} from "../../provider/DataProvider";

interface DashboardTestResultsProps {
    execStatistics: ExecutionStatistics;
    onListItemClick: (newItem: string) => void;
    selectedStatus: string | null;
    sx?: SxProps<Theme>
    withCard?: boolean;
}

interface DashboardResultItem {
    key: string;
    primaryText: string;
    secondaryText?: string;
    icon: React.ReactNode;
    selected: boolean;
    trend: number;
}

const getSecondaryStatusLabel = (
    status: ResultStatusType, execStatistics: ExecutionStatistics
) => {
    switch (status) {
        case ResultStatusType.FAILED:
            return " + " + execStatistics.getStatusCount(ResultStatusType.FAILED_RETRIED) + " Retried";

        case ResultStatusType.PASSED: {
            const repairedCount = execStatistics.getStatusCount(ResultStatusType.REPAIRED);
            const recoveredCount = execStatistics.getStatusCount(ResultStatusType.PASSED_RETRY);

            const parts: string[] = [];
            if (repairedCount > 0) {
                parts.push(`⊃ ${repairedCount} ${StatusService.getLabel(ResultStatusType.REPAIRED)}`);
            }
            if (recoveredCount > 0) {
                parts.push(`⊃ ${recoveredCount} ${StatusService.getLabel(ResultStatusType.PASSED_RETRY)}`);
            }
            return parts.join("\n");
        }

        default:
            return "";
    }
};

const getTrendIcon = (trend: number) => {
    if (trend > 0) {
        return <NorthEastIcon fontSize="small"/>;
    }
    if (trend < 0) {
        return <SouthEastIcon fontSize="small"/>;
    }
    return <EastIcon fontSize="small"/>;
};

const DashboardTestResultsCard = ({execStatistics, onListItemClick, selectedStatus, sx, withCard = true}: DashboardTestResultsProps) => {
    const {executionMngr} = useReportData();
    const historyStatistics = executionMngr?.getHistoryStatistics();
    const historyAvailable = withCard && (historyStatistics?.getTotalRunCount() ?? 0) >= 2;
    const previousRun = historyAvailable
        ? historyStatistics?.getHistoryAggregateStatistics()[historyStatistics.getHistoryAggregateStatistics().length - 2]
        : undefined;

    const itemList = StatusService.getRelevantStatuses()
        .map((status) => {
            const statusInformation = StatusService.get(status);
            const trend = (() => {
                if (!historyAvailable || !previousRun) {
                    return 0;
                }

                switch (status) {
                    case ResultStatusType.FAILED:
                        return execStatistics.overallFailed - (previousRun.overallFailed ?? 0);
                    case ResultStatusType.FAILED_EXPECTED:
                        return execStatistics.getStatusCount(ResultStatusType.FAILED_EXPECTED) - (previousRun.getStatusCount(ResultStatusType.FAILED_EXPECTED) ?? 0);
                    case ResultStatusType.SKIPPED:
                        return execStatistics.overallSkipped - (previousRun.overallSkipped ?? 0);
                    case ResultStatusType.PASSED:
                        return execStatistics.overallPassed - (previousRun.overallPassed ?? 0);
                    default:
                        return 0;
                }
            })();

            return {
                key: statusInformation.label,
                primaryText: execStatistics.getStatusCount(status) + " " + statusInformation.label,
                secondaryText: getSecondaryStatusLabel(status, execStatistics),
                icon: StatusService.getIcon(status),
                selected: selectedStatus === statusInformation.label,
                trend
            };
        }) satisfies DashboardResultItem[];

    const label = "Tests: " + (execStatistics.overallFailed + execStatistics.getStatusCount(ResultStatusType.FAILED_EXPECTED) + execStatistics.overallSkipped + execStatistics.overallPassed);

    const content = useMemo(() => (
        <Stack sx={{height: "100%"}}>
            <List sx={{p: 0}}>
                {itemList.map((item) => (
                    <ListItem key={item.key} disablePadding>
                        <ListItemButton
                            sx={{alignItems: "center"}}
                            selected={item.selected}
                            onClick={() => onListItemClick(item.key)}
                        >
                            <ListItemIcon sx={{alignSelf: "center"}}>
                                {item.icon}
                            </ListItemIcon>
                            <ListItemText disableTypography>
                                <Stack direction="row" spacing={3} sx={{alignItems: "center"}}>
                                    <Typography noWrap sx={{flexShrink: 0}}>{item.primaryText}</Typography>
                                    {item.secondaryText && (
                                        <Typography
                                            variant="caption"
                                            color="text.secondary"
                                            sx={{textAlign: "left", whiteSpace: "pre-line", lineHeight: 1.2,}}
                                        >
                                            {item.secondaryText}
                                        </Typography>
                                    )}
                                </Stack>
                            </ListItemText>
                            {historyAvailable && (
                                <Stack
                                    direction="row"
                                    spacing={0.5}
                                    alignItems="center"
                                    color="text.secondary"
                                >
                                    <Typography variant="body2">
                                        {item.trend !== 0 ? (item.trend > 0 ? `+${item.trend}` : item.trend) : "± 0"}
                                    </Typography>
                                    {getTrendIcon(item.trend)}
                                </Stack>
                            )}
                        </ListItemButton>
                    </ListItem>
                ))}
            </List>

            {historyAvailable && (
                <Stack direction="row" justifyContent="flex-end" sx={{px: 3, mt: 1, pr: 1}}>
                    <Button
                        title="Compare with previous run"
                        component={RouterLink}
                        to="/history/run-comparison"
                        variant="text"
                        size="small"
                        color="inherit"
                        startIcon={<CompareArrowsIcon />}
                        sx={{textTransform: "none", minWidth: "auto", px: 0.5}}
                    >
                        Run comparison
                    </Button>
                </Stack>
            )}
        </Stack>
    ), [historyAvailable, itemList, onListItemClick]);

    if (!withCard) {
        return content;
    }

    return (
        <ReportCard
            label={label}
            content={content}
            sxContent={{p: 0, ":last-child": {padding: 0}}}
            sxCard={sx}
        />
    );
};
export default DashboardTestResultsCard;
