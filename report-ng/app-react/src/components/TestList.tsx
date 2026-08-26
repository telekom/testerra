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
import Paper from '@mui/material/Paper';
import Box from '@mui/material/Box';
import {useReportData} from "../provider/DataProvider";
import ReportChip from "../widgets/ReportChip";
import {StatusService} from "../model/status-service";
import ReadMoreIcon from '@mui/icons-material/ReadMore';
import CancelIcon from '@mui/icons-material/Cancel';
import {Stack, Typography} from "@mui/material";
import Link from '@mui/material/Link';
import type {FiltersState} from "../hooks/useChipListFilters";
import {MethodDetails} from "../model/MethodDetails";
import {ClassName, classNameConverter} from "../utils/classNameConverter";
import HighlightText from "../utils/highlightText";
import NoResultsCard from "../widgets/NoResultsCard.tsx";
import {formatDuration} from "../utils/durationFormatter"
import {useTestListSort} from "../hooks/useTestListSort";
import TableSort from "../widgets/TableSort";
import LinearProgress from '@mui/material/LinearProgress';
import Alert from '@mui/material/Alert';
import {List, type RowComponentProps, useDynamicRowHeight} from "react-window";

// Precomputed row data: expensive derivations are calculated once in useMemo,
// not on every render. This avoids calling StatusService.get/separateNamespace,
// new Date().toLocaleTimeString(), and formatDuration() per row per render cycle.
interface MethodRow {
    detail: MethodDetails;
    id: string;
    displayClassName: string;
    testStatusLabel: string;
    testStatusColor: string;
    failsAnnotationColor: string;
    startTimeString: string;
    durationString: string;
    processedFailureAspects: Array<{
        displayClassName?: string;
        message: string;
    }>;
}

interface TestListRowProps {
    rows: MethodRow[];
    activeSearchTerms: string[];
}

// CELL_WIDTHS defines the shared column flex sizes used by both the sticky header
// and the virtual row cells. Keeping them in one place ensures alignment.
const CELL_WIDTHS = {
    status: "10%",
    runIndex: "10%",
    class: "20%",
    startTime: "10%",
    method: 1, // flex: 1 takes the remaining space
} as const;

// Shared sx for a cell box: flex-shrink:0 keeps the box from collapsing
const cellSx = (width: string | number) =>
    typeof width === "number"
        ? {flex: width, p: 2, minWidth: 0, overflow: "hidden"}
        : {flex: `0 0 ${width}`, p: 2, minWidth: 0, overflow: "hidden"};

// Row component is defined outside TestList so its reference stays stable across
// renders, which prevents react-window from needlessly remounting every row.
function TestListRow({
    index,
    style,
    rows,
    activeSearchTerms,
}: RowComponentProps<TestListRowProps>) {
    const row = rows[index];
    const {detail, id, displayClassName, testStatusLabel, testStatusColor, failsAnnotationColor, startTimeString, durationString, processedFailureAspects} = row;

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
            {/* Status */}
            <Box sx={{...cellSx(CELL_WIDTHS.status), display: "flex", alignItems: "center"}}>
                <ReportChip
                    label={testStatusLabel}
                    size="small"
                    sx={{background: testStatusColor, color: "white"}}
                />
            </Box>

            {/* Run Index */}
            <Box sx={{...cellSx(CELL_WIDTHS.runIndex), display: "flex", alignItems: "center", justifyContent: "center"}}>
                <Typography>{detail.methodContext.methodRunIndex}</Typography>
            </Box>

            {/* Class */}
            <Box sx={{...cellSx(CELL_WIDTHS.class), display: "flex", alignItems: "center", overflowWrap: "anywhere"}}>
                <Link href={`#/Tests?class=${encodeURIComponent(displayClassName)}`}>
                    <Typography>
                        <HighlightText text={displayClassName} searchWord={activeSearchTerms}/>
                    </Typography>
                </Link>
            </Box>

            {/* Start Time + Duration */}
            <Box sx={{...cellSx(CELL_WIDTHS.startTime), display: "flex", flexDirection: "column", justifyContent: "center"}}>
                <Typography>{startTimeString}</Typography>
                <Typography color="lightGrey" variant="body2">({durationString})</Typography>
            </Box>

            {/* Method */}
            <Box sx={{...cellSx(CELL_WIDTHS.method), overflowWrap: "anywhere"}}>
                <Stack direction="column">
                    <Stack direction="row" sx={{gap: 1, alignItems: "center"}}>
                        <ReadMoreIcon/>
                        <Link href={`#/method/${id}`} underline="hover">
                            <Typography>
                                <HighlightText text={detail.identifier} searchWord={activeSearchTerms}/>
                            </Typography>
                        </Link>
                        {detail.methodContext.methodType == 2 && (
                            <ReportChip label="Configuration" size="small" color={"lightGrey" as ChipColor} sx={{color: "white"}}/>
                        )}
                    </Stack>
                    {processedFailureAspects.map((fa, i) => (
                        <Typography key={i} variant="body2" sx={{mt: 1}}>
                            {fa.displayClassName && (
                                <HighlightText text={fa.displayClassName} searchWord={activeSearchTerms}/>
                            )}:
                            <HighlightText text={fa.message} searchWord={activeSearchTerms}/>
                        </Typography>
                    ))}
                    {detail.failsAnnotation?.description && (
                        <Stack direction="row" sx={{gap: 1, alignItems: "center", mt: 1}}>
                            <CancelIcon sx={{color: failsAnnotationColor}}/>
                            <Typography variant="caption">
                                <HighlightText text={detail.failsAnnotation.description} searchWord={activeSearchTerms}/>
                            </Typography>
                        </Stack>
                    )}
                </Stack>
            </Box>
        </Box>
    );
}

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
        () => (searchText.trim() ? [searchText.trim()] : []),
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
                )).filter((detail): detail is MethodDetails => detail !== undefined);
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

        // custom filter: test timings bar click
        if (filters.methods && filters.methods.length > 0) {
            const timingMethodIds = filters.methods;
            filtered = filtered.filter(detail => {
                const methodId = detail.methodContext.contextValues?.id;
                return methodId ? timingMethodIds?.includes(methodId) : false;
            });
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

    // Keep comparator memoized separately so React Compiler can preserve memoization.
    const comparator = useMemo(
        () => buildComparator(orderDirection, orderBy),
        [buildComparator, orderDirection, orderBy],
    );
    const sortedMethodDetails = useMemo(
        () => [...filteredMethodDetails].sort(comparator),
        [filteredMethodDetails, comparator],
    );

    // Precompute all per-row derived values once after sorting. This avoids
    // calling StatusService.get/separateNamespace, new Date(), toLocaleTimeString()
    // and formatDuration() inside the render loop for every visible row.
    const rows: MethodRow[] = useMemo(() =>
        sortedMethodDetails.map(detail => {
            const startTime = detail.methodContext.contextValues?.startTime ?? 0;
            const endTime = detail.methodContext.contextValues?.endTime ?? 0;
            const testStatus = StatusService.get(detail.methodContext.resultStatus!);
            return {
                detail,
                id: detail.methodContext.contextValues?.id ?? "",
                displayClassName: StatusService.separateNamespace(detail.classStatistics.classIdentifier ?? "").class,
                testStatusLabel: testStatus.label,
                testStatusColor: testStatus.color,
                failsAnnotationColor: testStatus.color,
                startTimeString: new Date(startTime).toLocaleTimeString(),
                durationString: formatDuration(endTime - startTime),
                processedFailureAspects: detail.failureAspects.map(fa => ({
                    displayClassName: fa.relevantCause?.className
                        ? StatusService.separateNamespace(fa.relevantCause.className).class
                        : undefined,
                    message: fa.message,
                })),
            };
        }),
        [sortedMethodDetails]
    );

    const statusCount = useMemo(() =>
            new Set(filteredMethodDetails.map((m) => m.methodContext.resultStatus)).size,
        [filteredMethodDetails],
    );
    const classCount = useMemo(() =>
            new Set(filteredMethodDetails.map((m) => m.classStatistics.classIdentifier)).size,
        [filteredMethodDetails],
    );

    // Dynamic row height: rows have variable height depending on the number of
    // failure aspects and annotations they contain.
    const rowHeight = useDynamicRowHeight({defaultRowHeight: 72});

    // Stable rowProps object passed to the List. Re-created only when rows or
    // search terms change, not on every parent render.
    const rowProps = useMemo(
        () => ({rows, activeSearchTerms}),
        [rows, activeSearchTerms]
    );

    if (isLoading) return <LinearProgress aria-label="Loading…"/>;
    if (error) return <Alert severity="error">An error occured: {error?.message}</Alert>
    if (!executionMngr) return null;

    if (filteredMethodDetails.length < 1) {
        return <NoResultsCard title="No methods matching this criteria" subtitle="Please note, that your filter criteria may only match configuration methods."/>
    }

    return (
        <Paper sx={{overflow: "hidden"}}>
            {/* Sticky header row — uses the same CELL_WIDTHS flex values as TestListRow */}
            <Box
                sx={{
                    display: "flex",
                    position: "sticky",
                    top: 0,
                    bgcolor: "background.paper",
                    borderBottom: "2px solid",
                    borderColor: "divider",
                    zIndex: 1,
                    fontWeight: 500
                }}
            >
                <Box sx={{...cellSx(CELL_WIDTHS.status), alignContent: "center"}}>
                    Status ({statusCount})
                </Box>
                <Box sx={{...cellSx(CELL_WIDTHS.runIndex), alignContent: "center"}}>
                    <TableSort orderBy={orderBy} orderDirection={orderDirection} onRequestSort={handleRequestSort} headerProperty="runIndex" label="Run Index"/>
                </Box>
                <Box sx={{...cellSx(CELL_WIDTHS.class), alignContent: "center"}}>
                    <TableSort orderBy={orderBy} orderDirection={orderDirection} onRequestSort={handleRequestSort} headerProperty="class" label={`Class (${classCount})`}/>
                </Box>
                <Box sx={{...cellSx(CELL_WIDTHS.startTime), alignContent: "center"}}>
                    <TableSort orderBy={orderBy} orderDirection={orderDirection} onRequestSort={handleRequestSort} headerProperty="startTime" label="Start Time"/>
                </Box>
                <Box sx={{...cellSx(CELL_WIDTHS.method), alignContent: "center"}}>
                    <TableSort orderBy={orderBy} orderDirection={orderDirection} onRequestSort={handleRequestSort} headerProperty="method" label={`Method (${filteredMethodDetails.length})`}/>
                </Box>
            </Box>

            {/* Virtualized body — only visible rows are rendered in the DOM */}
            <List
                rowComponent={TestListRow}
                rowCount={rows.length}
                rowHeight={rowHeight}
                rowProps={rowProps}
                style={{maxHeight: "calc(100dvh - 265px)", width: "100%"}}
            />
        </Paper>
    );
};
export default TestList;
