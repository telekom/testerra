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

import {useMemo} from "react";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import {useReportData} from "../provider/DataProvider";
import ReportChip from "../widgets/ReportChip";
import {StatusService} from "../model/status-service";
import ReadMoreIcon from '@mui/icons-material/ReadMore';
import CancelIcon from '@mui/icons-material/Cancel';
import {Stack, Typography} from "@mui/material";
import Link from '@mui/material/Link';
import type {ChipColor, FiltersState} from "../hooks/useTestListFilters";
import {MethodDetails} from "../model/MethodDetails";
import {ClassName, classNameConverter} from "../utils/classNameConverter";
import HighlightText from "../utils/highlightText";
import NoResultsCard from "./NoResultsCard";
import {formatDuration} from "../utils/durationFormatter"
import {useTestListSort} from "../hooks/useTestListSort";
import TableSort from "../widgets/TableSort";



interface TestListProps {
    filters: FiltersState;
    searchText: string;
    showConfigurationMethods: boolean;
}

const TestList = ({filters, searchText, showConfigurationMethods,}: TestListProps) => {
    const {executionMngr, isLoading, error} = useReportData();

    const {
        orderDirection,
        orderBy,
        handleRequestSort,
        buildComparator
    } = useTestListSort();

    // strings used for highlighting: live text while typing
    const activeSearchTerms = useMemo(
        () =>(searchText.trim() ? [searchText.trim()] : []),
        [searchText]
    );

    // useMemo to make sure methodDetails is only built new if the data basis changes
    const methodDetails = useMemo(() => {
        if (!executionMngr) return [];
        return executionMngr.getExecutionStatistics()
            .classStatistics.flatMap(classStatistic => classStatistic.methodContexts.map((methodContext) =>
                    methodContext.contextValues?.id
                        ? executionMngr.getMethodDetails(methodContext.contextValues.id)
                        : undefined
                )).filter((detail): detail is MethodDetails => detail !== undefined);  // removes undefined values
    }, [executionMngr]);

    // useMemo to only render new if methodDetails, filter or showConfigurationMethods change
    const filteredMethodDetails = useMemo(() => {
        let filtered = methodDetails;

        // configuration methods filter
        filtered = filtered.filter(detail => {
            const methodType = detail.methodContext.methodType;
            return showConfigurationMethods || methodType === 1;
        });

        // custom filter: failure aspects
        if (filters.failureAspect && executionMngr) {
            const relevantFailureAspect =
                executionMngr.getExecutionStatistics().uniqueFailureAspects[parseInt(filters.failureAspect[0])];

            if (relevantFailureAspect) {
                filtered = filtered.filter(detail =>
                    detail.failureAspects.some(
                        failureAspect => failureAspect.identifier === relevantFailureAspect.identifier
                    )
                );
            }
        }

        // status filter
        if (filters.status && filters.status.length > 0) {
            filtered = filtered.filter(detail => {
                if (detail?.methodContext.resultStatus === undefined) return false;
                return filters.status?.includes(detail.methodContext.resultStatus);
            });
        }

        // class filter
        if (filters.class && filters.class.length > 0) {
            filtered = filtered.filter(detail => {
                return filters.class?.includes(classNameConverter(detail.classStatistics.classIdentifier, ClassName.simpleName))
            });
        }

        // free text
        if (filters.customText && filters.customText.length > 0) {
            filtered = filtered.filter(detail => {
                return filters.customText!.every(searchTerm => {
                    const regex = StatusService.createRegexpFromSearchString(searchTerm);
                    return detail.identifier.match(regex)
                        || detail.failureAspects.some(failureAspect => failureAspect.identifier.match(regex))
                        || detail.failsAnnotation?.description?.match(regex)
                        || detail.failsAnnotation?.ticketString?.match(regex)
                        || detail.promptLogs.some(logMessage => logMessage.message?.match(regex))
                        || detail.classStatistics.classIdentifier.match(regex);
                });
            });
        }

        return filtered;
    }, [methodDetails, filters, showConfigurationMethods, executionMngr]);

    const sortedMethodDetails = useMemo(() =>
        [...filteredMethodDetails].sort(buildComparator(orderDirection, orderBy)),
        [filteredMethodDetails, orderDirection, orderBy],
    );
    const statusCount = useMemo(() =>
            new Set(filteredMethodDetails.map((m) => m.methodContext.resultStatus)).size,
        [filteredMethodDetails],
    );
    const classCount = useMemo(() =>
            new Set(filteredMethodDetails.map((m) => m.classStatistics.classIdentifier),).size,
        [filteredMethodDetails],
    );

    if (isLoading) return <div>Lade Konfiguration...</div>;
    if (error) return <div>Fehler: {error.message}</div>;
    if (!executionMngr) return null;

    if(filteredMethodDetails.length < 1){
        return <NoResultsCard/>
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
                        <TableCell style={{width: "10%"}}>Status ({statusCount})</TableCell>
                        <TableCell align={"center"} style={{width: "10%"}} sortDirection={orderBy === "runIndex" ? orderDirection : false}>
                            <TableSort orderBy={orderBy} orderDirection={orderDirection} onRequestSort={handleRequestSort} headerProperty="runIndex" label="Run Index"/>
                        </TableCell>
                        <TableCell style={{width: "20%"}} sortDirection={orderBy === "class" ? orderDirection : false}>
                            <TableSort orderBy={orderBy} orderDirection={orderDirection} onRequestSort={handleRequestSort} headerProperty="class" label={`Class (${classCount})`}/>
                        </TableCell>
                        <TableCell style={{width: "10%"}} sortDirection={orderBy === "startTime" ? orderDirection : false}>
                            <TableSort orderBy={orderBy} orderDirection={orderDirection} onRequestSort={handleRequestSort} headerProperty="startTime" label="Start Time"/>
                        </TableCell>
                        <TableCell sortDirection={orderBy === "method" ? orderDirection : false}>
                            <TableSort orderBy={orderBy} orderDirection={orderDirection} onRequestSort={handleRequestSort} headerProperty="method" label={`Method (${filteredMethodDetails.length})`}/>
                        </TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {sortedMethodDetails && sortedMethodDetails.length > 0 && sortedMethodDetails.map(filteredMethodDetail => (
                        <TableRow key={filteredMethodDetail?.methodContext.methodRunIndex}
                                  sx={{'&:last-child td, &:last-child th': {border: 0}}}>
                            <TableCell component="th" scope="row">
                                <ReportChip key={filteredMethodDetail?.methodContext.resultStatus}
                                            label={StatusService.getLabel(filteredMethodDetail?.methodContext.resultStatus!)}
                                            size="small"
                                            sx={{
                                                background: StatusService.getColor(filteredMethodDetail?.methodContext.resultStatus!),
                                                color: "white"
                                            }}/>
                            </TableCell>
                            <TableCell align={"center"}>
                                <Typography> {filteredMethodDetail?.methodContext.methodRunIndex} </Typography>
                            </TableCell>
                            <TableCell sx={{overflowWrap: "anywhere"}}>
                                <Link href="#/Tests">
                                    <Typography>
                                        <HighlightText
                                            text={StatusService.separateNamespace(filteredMethodDetail?.classStatistics.classIdentifier ?? "").class}
                                            searchWord={activeSearchTerms}
                                        />
                                    </Typography>
                                </Link>
                            </TableCell>
                            <TableCell>
                                <Stack direction="column">
                                    <Typography> {new Date(filteredMethodDetail?.methodContext.contextValues?.startTime ?? 0).toLocaleTimeString()} </Typography>
                                    <Typography color="lightGrey" variant="body2">(
                                        {formatDuration((filteredMethodDetail?.methodContext.contextValues?.endTime ?? 0) - (filteredMethodDetail?.methodContext.contextValues?.startTime ?? 0))}
                                        )
                                    </Typography>
                                </Stack>
                            </TableCell>
                            <TableCell sx={{overflowWrap: "anywhere"}}>
                                <Stack direction="column">
                                    <Stack direction="row" sx={{gap: 1, alignItems: "center"}}>
                                        <ReadMoreIcon/>
                                        <Link href="#/Tests">
                                            <Typography>
                                                <HighlightText text={filteredMethodDetail?.identifier}
                                                               searchWord={activeSearchTerms}
                                                />
                                            </Typography>
                                        </Link>
                                        {filteredMethodDetail.methodContext.methodType == 2 && <ReportChip label="Configuration"
                                                    size="small"
                                                    color={"lightGrey" as ChipColor}
                                                    sx={{color: "white"}}
                                        />}
                                    </Stack>
                                    {filteredMethodDetail?.failureAspects.map((failureAspect) => (
                                        <Typography variant="body2" sx={{mt: 1}}>
                                            {failureAspect.relevantCause?.className &&
                                            <HighlightText
                                                text={StatusService.separateNamespace(failureAspect.relevantCause?.className).class}
                                                searchWord={activeSearchTerms}
                                            />}:
                                            <HighlightText
                                                text={failureAspect.message}
                                                searchWord={activeSearchTerms}
                                            />
                                        </Typography>
                                    ))}
                                    {filteredMethodDetail?.failsAnnotation?.description &&
                                        <Stack direction="row" sx={{gap: 1, alignItems: "center", mt: 1}}>
                                            <CancelIcon
                                                sx={{color: StatusService.getColor(filteredMethodDetail?.methodContext.resultStatus!)}}/>
                                            <Typography
                                                variant="caption">
                                                <HighlightText
                                                    text={filteredMethodDetail.failsAnnotation.description}
                                                    searchWord={activeSearchTerms}
                                                />
                                            </Typography>
                                        </Stack>
                                    }
                                </Stack>

                            </TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
};
export default TestList;
