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
import {
    Link, ListItemText, Stack, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography,
} from "@mui/material";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import {Link as RouterLink} from "react-router-dom";
import type {HistoryAggregateStatistics} from "../../model/HistoryAggregateStatistics.ts";
import type {ExecutionStatistics} from "../../model/ExecutionStatistics.ts";
import {ResultStatusType} from "../../model/report-model/framework_pb.ts";
import {StatusService} from "../../model/status-service";
import {compareRunMethods} from "./runComparison.ts";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";
import ReportCard from "../../widgets/ReportCard.tsx";
import ReportChip from "../../widgets/ReportChip.tsx";

interface RunCompareMethodsCardProps {
    selectedRun: HistoryAggregateStatistics;
    currentRun: HistoryAggregateStatistics;
    executionStatistics: ExecutionStatistics;
}

const StatusChip = ({status}: {status: ResultStatusType}) => (
    <ReportChip
        label={status === ResultStatusType.NO_RUN ? "No run" : StatusService.getLabel(status)}
        size="small"
        sx={{
            backgroundColor: status === ResultStatusType.NO_RUN ? "text.disabled" : StatusService.getColor(status),
            color: "white",
        }}
    />
);

const RunCompareMethodsCard = ({selectedRun, currentRun, executionStatistics}: RunCompareMethodsCardProps) => {
    const comparisons = useMemo(
        () => compareRunMethods(selectedRun, currentRun, executionStatistics),
        [selectedRun, currentRun, executionStatistics],
    );

    return (
        <ReportCard
            label="Method status changes"
            sxContent={{p: 0}}
            content={comparisons.length > 0 ? (
                <TableContainer>
                    <Table
                        aria-label="Method status changes"
                        sx={{
                            tableLayout: "auto",
                            minWidth: 800,
                            "& thead th:nth-of-type(-n+3)": {whiteSpace: "nowrap"},
                        }}
                    >
                        <colgroup>
                            <col style={{width: 120}}/>
                            <col style={{width: 30}}/>
                            <col style={{width: 120}}/>
                            <col style={{width: "1%"}}/>
                            <col/>
                        </colgroup>
                        <TableHead>
                            <TableRow>
                                <TableCell align="center">Status (Run {selectedRun.historyIndex})</TableCell>
                                <TableCell/>
                                <TableCell align="center">Status (Run {currentRun.historyIndex})</TableCell>
                                <TableCell sx={{whiteSpace: "nowrap"}}>Class</TableCell>
                                <TableCell>Method</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {comparisons.map(method => (
                                <TableRow key={method.methodPath}>
                                    <TableCell align="center">
                                        <StatusChip status={method.selectedStatus}/>
                                    </TableCell>
                                    <TableCell align="center"><ArrowForwardIcon/></TableCell>
                                    <TableCell align="center">
                                        <StatusChip status={method.currentStatus}/>
                                    </TableCell>
                                    <TableCell sx={{whiteSpace: "nowrap"}}>
                                        {classNameConverter(method.classIdentifier, ClassName.simpleName)}
                                    </TableCell>
                                    <TableCell sx={{overflowWrap: "anywhere"}}>
                                        <ListItemText
                                            primary={
                                                <Stack component="span" direction="row" spacing={2} useFlexGap
                                                       sx={{alignItems: "center", flexWrap: "wrap"}}>
                                                    {method.currentMethodId ? (
                                                        <Link component={RouterLink} to={`/method/${method.currentMethodId}`}>
                                                            {method.methodName}
                                                        </Link>
                                                    ) : `Method not found in current run: ${method.methodName}`}
                                                    {method.isConfiguration && (
                                                        <ReportChip label="Configuration" size="small"
                                                                    color="lightGrey" sx={{color: "white"}}/>
                                                    )}
                                                </Stack>
                                            }
                                            secondary={[
                                                method.currentMethodId ? undefined : method.methodPath,
                                                ...method.relatedTestMethods.map(name => `(for test method ${name})`),
                                                method.changedFailureAspect && method.selectedStatus === method.currentStatus
                                                    ? "Changed failure aspect" : undefined,
                                            ].filter(Boolean).join("\n") || undefined}
                                            slotProps={{secondary: {sx: {whiteSpace: "pre-line"}}}}
                                        />
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            ) : (
                <Typography sx={{p: 2}}>No method status changes</Typography>
            )}
        />
    );
};

export default RunCompareMethodsCard;
