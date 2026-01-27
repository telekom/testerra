import {useReportData} from "../provider/DataProvider.tsx";
import Box from "@mui/material/Box";
import {Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography} from "@mui/material";
import {ResultStatusType} from "../provider/report-model/framework_pb.ts";

function createRow(
    key: string,
    value: number
) {
    return {key, value};
}

const Tests = () => {

    const {executionMngr, isLoading, error} = useReportData();

    if (isLoading) return <div>Lade Konfiguration...</div>;
    if (error) return <div>Fehler: {error.message}</div>;
    if (!executionMngr) return null;

    const execStatistics = executionMngr.getExecutionStatistics();

    const rows = [
        createRow('Number of tests', execStatistics.overallTestCases),
        createRow('Overall passed', execStatistics.overallPassed),
        createRow('Overall failed', execStatistics.overallFailed),
        createRow('Passed', execStatistics.getStatusCount(ResultStatusType.PASSED)),
        createRow('Repaired', execStatistics.getStatusCount(ResultStatusType.REPAIRED)),
        createRow('Recovered', execStatistics.getStatusCount(ResultStatusType.PASSED_RETRY)),
        createRow('Failed', execStatistics.getStatusCount(ResultStatusType.FAILED)),
        createRow('Expected failed', execStatistics.getStatusCount(ResultStatusType.FAILED_EXPECTED)),
        createRow('Failed retry', execStatistics.getStatusCount(ResultStatusType.FAILED_RETRIED)),
        createRow('Skipped', execStatistics.getStatusCount(ResultStatusType.SKIPPED)),
    ];

    return (
        <>
            <Box>
                <Typography variant="h6">Test view</Typography>
                <Typography>
                    {executionMngr.getExecutionAggregate().executionContext?.runConfig?.reportName}
                </Typography>
                <TableContainer component={Paper}>
                    <Table sx={{minWidth: 200}} aria-label="simple table">
                        <TableHead>
                            <TableRow>
                                <TableCell>Statistic</TableCell>
                                <TableCell align="center">Value</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {rows.map((row) => (
                                <TableRow
                                    key={row.key}
                                    sx={{'&:last-child td, &:last-child th': {border: 0}}}
                                >
                                    <TableCell component="th" scope="row">
                                        {row.key}
                                    </TableCell>
                                    <TableCell align="right">{row.value}</TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>
        </>
    );
};
export default Tests;
