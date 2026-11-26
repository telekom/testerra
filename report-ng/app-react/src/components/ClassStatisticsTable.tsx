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

import {useMemo} from 'react';
import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from '@mui/material';
import ReportChip from '../widgets/ReportChip.tsx';
import {StatusService} from '../model/status-service.tsx';
import {ClassName, classNameConverter} from '../utils/classNameConverter.ts';
import {ClassStatistics} from '../model/ClassStatistics.ts';

interface ClassStatisticsTableProps {
    classStatistics: ClassStatistics[];
}

const ClassStatisticsTable = ({classStatistics}: ClassStatisticsTableProps) => {
    // Only render columns for statuses that actually occurred, so an execution without e.g.
    // skipped tests does not show an entirely empty column.
    // Availability is checked per status group (not per exact status) because the cells below
    // count grouped as well: PASSED_RETRY and REPAIRED are counted towards PASSED. An exact
    // check would drop the passed column for an execution that only has recovered/repaired
    // tests, hiding results that the passed column would otherwise report.
    const relevantStatuses = useMemo(() => {
        const availableStatuses = new Set(
            classStatistics.flatMap(classStats => classStats.availableStatuses)
        );
        return StatusService.getRelevantStatuses().filter(status =>
            StatusService.getGroup(status).some(groupedStatus => availableStatuses.has(groupedStatus))
        );
    }, [classStatistics]);

    return (
        <TableContainer component={Paper}>
            <Table sx={{
                tableLayout: "fixed",
                width: "100%",
                '@media print': {
                    '& thead': {
                        display: 'table-row-group !important',
                    },
                },
            }}
                   aria-label="test classes table">
                <TableHead>
                    <TableRow>
                        <TableCell style={{width: "52%"}}>
                            Test Class ({classStatistics.length})
                        </TableCell>
                        {relevantStatuses.map(status => {
                            const statusInfo = StatusService.get(status);
                            return (
                                // The status columns share the remaining 48% evenly, so the layout
                                // stays balanced no matter how many statuses are shown
                                <TableCell key={status} style={{width: `${48 / relevantStatuses.length}%`}}
                                           align="center">
                                    <ReportChip
                                        label={statusInfo.label}
                                        size="small"
                                        sx={{
                                            background: statusInfo.color,
                                            color: "white",
                                        }}
                                    />
                                </TableCell>
                            );
                        })}
                    </TableRow>
                </TableHead>
                <TableBody>
                    {classStatistics.map((classStats) => (
                        <TableRow sx={{'&:last-child td, &:last-child th': {border: 0}}}
                                  key={classStats.classIdentifier}>
                            <TableCell component="th" scope="row" sx={{lineBreak: "anywhere"}}>
                                <Typography>
                                    {classNameConverter(classStats.classIdentifier, ClassName.simpleName)}
                                </Typography>
                            </TableCell>
                            {relevantStatuses.map(status => {
                                // Count the whole status group, so retried and repaired tests are
                                // reported as passed instead of being dropped
                                const count = classStats.getSummarizedStatusCount(StatusService.getGroup(status));
                                const statusInfo = StatusService.get(status);
                                return (
                                    <TableCell key={status} component="th" scope="row" align="center">
                                        {count > 0 ? (
                                            <ReportChip
                                                label={String(count)}
                                                size="small"
                                                sx={{
                                                    background: statusInfo.color,
                                                    color: "white",
                                                }}
                                            />
                                        ) : (
                                            <Typography color="text.disabled">-</Typography>
                                        )}
                                    </TableCell>
                                );
                            })}
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    );
};

export default ClassStatisticsTable;
