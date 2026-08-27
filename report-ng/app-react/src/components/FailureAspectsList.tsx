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

import Paper from "@mui/material/Paper";
import {useReportData} from "../provider/DataProvider";
import {useCallback, useMemo} from "react";
import ReportChip from "../widgets/ReportChip";
import {StatusService} from "../model/status-service";
import {Box, Stack, Typography} from "@mui/material";
import HighlightText from "../utils/highlightText";
import {ClassName, classNameConverter} from "../utils/classNameConverter";
import {ResultStatusType} from "../model/report-model/framework_pb";
import Link from "@mui/material/Link";
import {Link as RouterLink, useNavigate} from "react-router-dom";

import {FailureAspectStatistics} from "../model/FailureAspectStatistics";
import NoResultsCard from "../widgets/NoResultsCard.tsx";
import {List, type RowComponentProps, useDynamicRowHeight} from "react-window";

interface FailureAspectRow {
    failureAspect: FailureAspectStatistics;
    displayText?: string;
    statuses: Array<{
        key: string;
        label: string;
        color: string;
    }>;
}

interface FailureAspectRowProps {
    rows: FailureAspectRow[];
    activeSearchTerms: string[];
    onStatusClick: (failureAspect: FailureAspectStatistics, status: string) => void;
}

const CELL_WIDTHS = {
    rank: "5%",
    failureAspect: "70%",
    type: "10%",
    status: "15%",
} as const;

const cellSx = (width: string) => ({
    flex: `0 0 ${width}`,
    p: 2,
    minWidth: 0,
    overflow: "hidden",
});

function FailureAspectRow({
    index,
    style,
    rows,
    activeSearchTerms,
    onStatusClick,
}: RowComponentProps<FailureAspectRowProps>) {
    const {failureAspect, displayText, statuses} = rows[index];

    return (
        <Box
            style={style}
            sx={{
                display: "flex",
                alignItems: "stretch",
                borderBottom: "1px solid",
                borderColor: "divider",
                boxSizing: "border-box",
                "&:hover": {bgcolor: "action.hover"},
            }}
        >
            <Box sx={{...cellSx(CELL_WIDTHS.rank), display: "flex", alignItems: "center", justifyContent: "center"}}>
                <Typography>{failureAspect.index + 1}</Typography>
            </Box>
            <Box sx={{...cellSx(CELL_WIDTHS.failureAspect), display: "flex", alignItems: "center", overflowWrap: "anywhere"}}>
                <Link
                    component={RouterLink}
                    to={{
                        pathname: "/Tests",
                        search: `failureAspect=${failureAspect.index}`,
                    }}
                >
                    <Typography>
                        {displayText && <HighlightText text={displayText} searchWord={activeSearchTerms}/>}
                    </Typography>
                </Link>
            </Box>
            <Box sx={{...cellSx(CELL_WIDTHS.type), display: "flex", alignItems: "center", justifyContent: "center"}}>
                <Typography>{failureAspect.isMinor ? "Minor" : "Major"}</Typography>
            </Box>
            <Box sx={cellSx(CELL_WIDTHS.status)}>
                <Stack direction="column" spacing={1} alignItems="flex-start">
                    {statuses.map(status => (
                        <ReportChip
                            label={status.label}
                            size="small"
                            handleClick={() => onStatusClick(failureAspect, status.key)}
                            sx={{
                                background: status.color,
                                color: "white",
                                textDecoration: "underline",
                                "&:hover": {background: status.color},
                            }}
                            key={status.key}
                        />
                    ))}
                </Stack>
            </Box>
        </Box>
    );
}

interface FailureAspectListProps {
    searchText: string;
    expectedFailedChecked: boolean;
    type: string;
}

const FailureAspectsList = ({searchText, expectedFailedChecked, type}: FailureAspectListProps) => {

    const navigate = useNavigate();
    const {executionMngr} = useReportData();

    const filteredFailureAspects = useMemo(() => {
        if (!executionMngr) return [];
        const execStatistics = executionMngr?.getExecutionStatistics()
        return execStatistics.uniqueFailureAspects
            .filter(failureAspect => {
                if (expectedFailedChecked) {
                    return true;
                } else {
                    return failureAspect.availableStatuses.filter(status => status != ResultStatusType.FAILED_EXPECTED).length > 0
                }
            })
            .filter(failureAspectStatistics => {
                return (!type || (
                    (type == "major" && !failureAspectStatistics.isMinor)
                    || (type == "minor" && failureAspectStatistics.isMinor)
                ));
            })
            .filter(failureAspectStatistics => {
                return (!searchText || failureAspectStatistics.identifier.toLowerCase().includes(searchText.trim().toLowerCase()));
            })
    }, [executionMngr, expectedFailedChecked, searchText, type]);

    // strings used for highlighting: live text while typing
    const activeSearchTerms = useMemo(
        () => (searchText.trim() ? [searchText.trim()] : []),
        [searchText]
    );

    const clickStatusChip = useCallback((failureAspect: FailureAspectStatistics, status: string) => {
        const params = new URLSearchParams(location.search);
        params.set("failureAspect", String(failureAspect.index));
        params.set("status", status);

        navigate({
            pathname: "/Tests",
            search: params.toString(),
        });
    }, [navigate]);

    const rows: FailureAspectRow[] = useMemo(
        () => filteredFailureAspects.map(failureAspect => ({
            failureAspect,
            displayText: failureAspect.relevantCause?.className
                ? classNameConverter(failureAspect.relevantCause.className, ClassName.simpleName) + ": " + failureAspect.message
                : undefined,
            statuses: failureAspect.availableStatuses.map(status => {
                const statusInformation = StatusService.get(String(status));
                return {
                    key: statusInformation.key,
                    label: `${failureAspect.getStatusCount(status)} ${statusInformation.label}`,
                    color: statusInformation.color,
                };
            }),
        })),
        [filteredFailureAspects],
    );

    const rowHeight = useDynamicRowHeight({defaultRowHeight: 72});
    const rowProps = useMemo(
        () => ({rows, activeSearchTerms, onStatusClick: clickStatusChip}),
        [rows, activeSearchTerms, clickStatusChip],
    );

    if (filteredFailureAspects.length < 1) {
        return <NoResultsCard title="No failure aspects matching this criteria"/>
    }

    return (
        <Paper sx={{overflow: "hidden"}}>
            <Box
                role="row"
                sx={{
                    display: "flex",
                    bgcolor: "background.paper",
                    borderBottom: "2px solid",
                    borderColor: "divider",
                    fontWeight: 500,
                }}
            >
                <Box role="columnheader" sx={{...cellSx(CELL_WIDTHS.rank), textAlign: "center"}}>Rank</Box>
                <Box role="columnheader" sx={cellSx(CELL_WIDTHS.failureAspect)}>
                    Failure Aspect ({filteredFailureAspects.length})
                </Box>
                <Box role="columnheader" sx={{...cellSx(CELL_WIDTHS.type), textAlign: "center"}}>Type</Box>
                <Box role="columnheader" sx={cellSx(CELL_WIDTHS.status)}>Status</Box>
            </Box>
            <List
                aria-label="Failure aspects"
                rowComponent={FailureAspectRow}
                rowCount={rows.length}
                rowHeight={rowHeight}
                rowProps={rowProps}
                style={{maxHeight: "calc(100dvh - 202px)", width: "100%"}}
            />
        </Paper>
    );
};
export default FailureAspectsList;
