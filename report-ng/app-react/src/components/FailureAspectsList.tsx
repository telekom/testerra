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
import Stack from "@mui/material/Stack";
import HighlightText from "../utils/highlightText";
import {ClassName, classNameConverter} from "../utils/classNameConverter";
import {ResultStatusType} from "../model/report-model/framework_pb";

interface FailureAspectListProps {
    searchText: string;
    expectedFailedChecked: boolean;
    type: string;
}

const FailureAspectsList = ({searchText, expectedFailedChecked, type}: FailureAspectListProps) => {

    const {executionMngr} = useReportData();
    if (!executionMngr) return;
    const filteredFailureAspects = useMemo(() => {
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
                return (!searchText || failureAspectStatistics.identifier.match(searchText));
            })
    }, [executionMngr, expectedFailedChecked, searchText, type]);

    // strings used for highlighting: live text while typing
    const activeSearchTerms = useMemo(
        () => (searchText.trim() ? [searchText.trim()] : []),
        [searchText]
    );

    // @ts-ignore
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
                        <TableCell style={{width: "70%"}}>Failure Aspect</TableCell>
                        <TableCell style={{width: "10%"}} align="center">Type</TableCell>
                        <TableCell style={{width: "15%"}}>Status</TableCell>

                    </TableRow>
                </TableHead>
                <TableBody>
                    {filteredFailureAspects.map(failureAspect => (
                        <TableRow sx={{'&:last-child td, &:last-child th': {border: 0}}}>
                            <TableCell component="th" scope="row" align="center">
                                {failureAspect.index + 1}
                            </TableCell>
                            <TableCell component="th" scope="row" sx={{lineBreak: "anywhere"}}>
                                {failureAspect.relevantCause?.className && <HighlightText
                                    text={classNameConverter(failureAspect.relevantCause.className, ClassName.simpleName) + ": " + failureAspect.message}
                                    searchWord={activeSearchTerms}
                                />}
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
                                                        sx={{background: statusInformation.color, color: "white"}}/>
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
