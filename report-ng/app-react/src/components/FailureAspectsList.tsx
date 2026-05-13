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
import Table from "@mui/material/Table";
import TableHead from "@mui/material/TableHead";
import TableRow from "@mui/material/TableRow";
import TableCell from "@mui/material/TableCell";
import TableBody from "@mui/material/TableBody";
import TableContainer from "@mui/material/TableContainer";
import {useReportData} from "../provider/DataProvider";
import {useMemo} from "react";
import ReportChip from "../widgets/ReportChip";
import {StatusService} from "../model/status-service";
import {Stack, Typography} from "@mui/material";
import HighlightText from "../utils/highlightText";
import {ClassName, classNameConverter} from "../utils/classNameConverter";
import {ResultStatusType} from "../model/report-model/framework_pb";
import Link from "@mui/material/Link";
import {Link as RouterLink, useNavigate} from "react-router-dom";

import {FailureAspectStatistics} from "../model/FailureAspectStatistics";
import NoResultsCard from "./NoResultsCard";

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

    const clickStatusChip = (failureAspect: FailureAspectStatistics, status: string) => {
        const params = new URLSearchParams(location.search);
        params.set("failureAspect", String(failureAspect.index));
        params.set("status", status);

        navigate({
            pathname: "/Tests",
            search: params.toString(),
        });
    }

    if (filteredFailureAspects.length < 1) {
        return <NoResultsCard title="No failure aspects matching this criteria"/>
    }

    return (
        <TableContainer component={Paper}>
            <Table sx={{
                tableLayout: "fixed",
                width: "100%"
            }}
                   aria-label="simple table">
                <TableHead>
                    <TableRow>
                        <TableCell style={{width: "5%"}} align="center">Rank</TableCell>
                        <TableCell style={{width: "70%"}}>Failure Aspect ({filteredFailureAspects.length})</TableCell>
                        <TableCell style={{width: "10%"}} align="center">Type</TableCell>
                        <TableCell style={{width: "15%"}}>Status</TableCell>

                    </TableRow>
                </TableHead>
                <TableBody>
                    {filteredFailureAspects.map(failureAspect => (
                        <TableRow sx={{'&:last-child td, &:last-child th': {border: 0}}} key={failureAspect.identifier}>
                            <TableCell component="th" scope="row" align="center">
                                {failureAspect.index + 1}
                            </TableCell>
                            <TableCell component="th" scope="row" sx={{lineBreak: "anywhere"}}>
                                <Link
                                    component={RouterLink}
                                    to={{
                                        pathname: "/Tests",
                                        search: `failureAspect=${failureAspect.index}`,
                                    }}
                                >
                                    <Typography>
                                        {failureAspect.relevantCause?.className && <HighlightText
                                            text={classNameConverter(failureAspect.relevantCause.className, ClassName.simpleName) + ": " + failureAspect.message}
                                            searchWord={activeSearchTerms}
                                        />}
                                    </Typography>
                                </Link>
                            </TableCell>
                            <TableCell component="th" scope="row" align="center">
                                {failureAspect.isMinor ? "Minor" : "Major"}
                            </TableCell>
                            <TableCell component="th" scope="row">
                                <Stack direction="column" spacing={1} alignItems="flex-start">
                                    {failureAspect.availableStatuses.map(status => {
                                        const statusInformation = StatusService.get(String(status));
                                        if (!statusInformation) return null;
                                        const label = String(failureAspect.getStatusCount(status)) + " " + statusInformation.label
                                        return (
                                            <ReportChip label={label}
                                                        size="small"
                                                        handleClick={() => clickStatusChip(failureAspect, statusInformation?.key)}
                                                        sx={{
                                                            background: statusInformation.color,
                                                            color: "white",
                                                            textDecoration: "underline",
                                                            '&:hover': {background: statusInformation.color}
                                                        }}
                                                        key={label}
                                            />
                                        )
                                    })}
                                </Stack>
                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
};
export default FailureAspectsList;
