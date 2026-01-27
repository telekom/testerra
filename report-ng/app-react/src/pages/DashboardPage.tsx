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
import "../components/dashboard-components/dashboard.scss"

const DashboardPage = () => {
    return (
        <Box
            sx={{width: '100%', maxWidth: {sm: '100%', md: '1700px'}}}
        >
            <Grid
                container
                spacing={2}
                columns={12}
            >
                <Grid size={3}>
                    <Stack direction="column" spacing={2} >
                        <DashboardPieChartCard className="tall-card"/>
                        <DashboardDurationCard className="short-card"/>
                    </Stack>
                </Grid>
                <Grid size={3}>
                    <Stack direction="column" spacing={2}>
                        <DashboardTestResultsCard className="tall-card"/>
                        <DashboardFailureCorridorCard className="short-card"/>
                    </Stack>
                </Grid>
                <Grid size={6}>
                    <Stack direction="column" spacing={2}>
                        <DashboardHistoryChartCard className="tall-card"/>
                        <DashboardFailureAspectsCard className="short-card"/>
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
