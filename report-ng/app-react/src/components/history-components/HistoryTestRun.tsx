import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import {Grid, Stack} from "@mui/material";


import HistoryTestRuns from "./HistoryTestRuns.tsx";
import ReportCard from "../../widgets/ReportCard.tsx";
import {useSearchParams} from "react-router-dom";
import {useTheme} from "@mui/material/styles";
import {useReportData} from "../../provider/DataProvider.tsx";
import LinearProgress from "@mui/material/LinearProgress";
import Alert from "@mui/material/Alert";
import HistoryTestRunCharts from "./HistoryTestRunsCharts.tsx";



const HistoryTestRun = () => {

    const theme = useTheme()
    const {executionMngr, isLoading, error} = useReportData();

    const [searchParams, setSearchParams] = useSearchParams();
    const selectedStatus = searchParams.get("status");

    // const statusMenuItems: number[] = []
    // const selectedStatuses: ResultStatus[] = []

    if (isLoading) return <LinearProgress aria-label="Loading…"/>;
    if (error) return <Alert severity="error">An error occured: {error?.message}</Alert>
    if (!executionMngr) return null;

    // const execStatistics: ExecutionStatistics = executionMngr.getExecutionStatistics();

    return (
        <Box
            sx={{width: '100%', maxWidth: {sm: '100%', md: '1700px'}}}
        >
            <Grid
                container
                spacing={2}
                columns={12}
            >
                {/*<Grid size={2}>*/}
                {/*    <StatusSelectInput label="Status" selectedStatuses={selectedStatuses}*/}
                {/*                       // onChange={(newStatuses) => setFilter("status", newStatuses)}*/}
                {/*                       onChange={(newStatuses) => {}}*/}
                {/*                       menuItems={statusMenuItems}/>*/}
                {/*</Grid>*/}
                {/*<Grid size={10}></Grid>*/}
                <Grid size={{xs: 12, sm: 12, lg: 9}}>
                    <HistoryTestRunCharts
                        histStatistics={executionMngr.getHistoryStatistics()}
                        selectedStatus={selectedStatus}
                        sx={theme.mixins.cardHeight(8.35)}
                    />
                </Grid>
                <Grid size={{xs: 12, sm: 12, lg: 3}}>
                    <Stack direction="column" spacing={2}>
                        <ReportCard
                            label="History statistics"
                            sxContent={{p: 0}}
                            sxCard={theme.mixins.cardHeight(4)}
                            content={<Typography variant="body1">Test history statistics</Typography>}
                        />
                        <ReportCard
                            label="Status share"
                            sxContent={{p: 0}}
                            sxCard={theme.mixins.cardHeight(4)}
                            content={<Typography variant="body1">Status share chart</Typography>}
                        />
                    </Stack>
                </Grid>
                <Grid size={{xs: 12, sm: 12, lg: 6}}>
                    <ReportCard
                        label="Top 3 flaky tests"
                        sxContent={{p: 0}}
                        sxCard={theme.mixins.cardHeight(4)}
                        content={<Typography variant="body1">Flaky test list</Typography>}
                    />
                </Grid>
                <Grid size={{xs: 12, sm: 12, lg: 6}}>
                    <ReportCard
                        label="Top 3 failing tests"
                        sxContent={{p: 0}}
                        sxCard={theme.mixins.cardHeight(4)}
                        content={<Typography variant="body1">Failing test list</Typography>}
                    />
                </Grid>

            </Grid>
        </Box>

    );
};

export default HistoryTestRun;
