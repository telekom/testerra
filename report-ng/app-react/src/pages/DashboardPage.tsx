import {Grid} from "@mui/material";
import Box from "@mui/material/Box";
import DashboardPieChartCard from "../components/dashboard-components/dashboard-pie-chart-card";
import DashboardTestResultsCard from "../components/dashboard-components/dashboard-test-results-card";
import DashboardFailureAspectsCard from "../components/dashboard-components/dashboard-failure-aspects-card";
import DashboardFailureCorridorCard from "../components/dashboard-components/dashboard-failure-corridor-card";
import Stack from '@mui/material/Stack';
import DashboardDurationCard from "../components/dashboard-components/dashboard-duration-card";
import DashboardClassesChartCard from "../components/dashboard-components/dashboard-classes-chart-card";
import DashboardHistoryChartCard from "../components/dashboard-components/dashboard-history-chart-card";
import {useReportData} from "../provider/DataProvider";
import {useSearchParams} from "react-router-dom";
import { useTheme } from '@mui/material/styles';

const DashboardPage = () => {

    const theme = useTheme()
    const {executionMngr, isLoading, error} = useReportData();
    if (isLoading) return <div>Lade Konfiguration...</div>;
    if (error) return <div>Fehler: {error.message}</div>;
    if (!executionMngr) return null;

    const execStatistics = executionMngr.getExecutionStatistics();

    const [searchParams, setSearchParams] = useSearchParams();
    const selectedStatus = searchParams.get("status");

    const handleStatusChange = (statusName: string) => {
        const params = new URLSearchParams(searchParams);
        params.set("status", statusName);
        setSearchParams(params);
    };

    return (
        <Box
            sx={{width: '100%', maxWidth: {sm: '100%', md: '1700px'}}}
        >
            <Grid
                container
                spacing={2}
                columns={12}
            >
                <Grid size={{xs: 12, sm: 6, lg: 3}}>
                    <Stack direction="column" spacing={2} >
                        <DashboardPieChartCard sx={theme.mixins.gridHeight(theme.customSizes.cardTall)} execStatistics={execStatistics} onChartPieceClick={handleStatusChange} selectedStatus={selectedStatus}/>
                        <DashboardDurationCard sx={theme.mixins.gridHeight(theme.customSizes.cardShort)}/>
                    </Stack>
                </Grid>
                <Grid size={{xs: 12, sm: 6, lg: 3}}>
                    <Stack direction="column" spacing={2}>
                        <DashboardTestResultsCard sx={theme.mixins.gridHeight(theme.customSizes.cardTall)} execStatistics={execStatistics} onListItemClick={handleStatusChange} selectedStatus={selectedStatus}/>
                        <DashboardFailureCorridorCard sx={theme.mixins.gridHeight(theme.customSizes.cardShort)}/>
                    </Stack>
                </Grid>
                <Grid size={{sm: 12, lg: 6}}>
                    <Stack direction="column" spacing={2}>
                        <DashboardHistoryChartCard sx={theme.mixins.gridHeight(theme.customSizes.cardTall)}/>
                        <DashboardFailureAspectsCard sx={theme.mixins.gridHeight(theme.customSizes.cardShort)}/>
                    </Stack>
                </Grid>
                <Grid size={12}>
                    <DashboardClassesChartCard/>
                </Grid>
            </Grid>
        </Box>
    );
};
export default DashboardPage;
