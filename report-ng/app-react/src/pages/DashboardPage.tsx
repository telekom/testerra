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

import {Grid} from "@mui/material";
import Box from "@mui/material/Box";
import DashboardPieChartCard from "../components/dashboard-components/DashboardPieChartCard";
import DashboardTestResultsCard from "../components/dashboard-components/DashboardTestResultsCard";
import DashboardFailureAspectsCard from "../components/dashboard-components/DashboardFailureAspectsCard";
import DashboardFailureCorridorCard from "../components/dashboard-components/DashboardFailureCorridorCard";
import Stack from '@mui/material/Stack';
import DurationCard from "../components/DurationCard.tsx";
import DashboardClassesChartCard from "../components/dashboard-components/DashboardClassesChartCard";
import DashboardHistoryChartCard from "../components/dashboard-components/DashboardHistoryChartCard";
import {useReportData} from "../provider/DataProvider";
import {useSearchParams} from "react-router-dom";
import { useTheme } from '@mui/material/styles';
import {ExecutionStatistics} from "../model/ExecutionStatistics";
import LinearProgress from "@mui/material/LinearProgress";
import Alert from "@mui/material/Alert";

const DashboardPage = () => {

    const theme = useTheme()
    const {executionMngr, isLoading, error} = useReportData();
    const [searchParams, setSearchParams] = useSearchParams();
    const selectedStatus = searchParams.get("status");

    if (isLoading) return <LinearProgress aria-label="Loading…" />;
    if (error) return <Alert severity="error">An error occured: {error?.message}</Alert>
    if (!executionMngr) return null;

    const execStatistics: ExecutionStatistics = executionMngr.getExecutionStatistics();

    const handleStatusChange = (statusName: string) => {
        const params = new URLSearchParams(searchParams);
        const currentStatus = params.get("status");
        if (statusName == currentStatus) {
            // Deactivate status filter by selecting again the same status
            params.delete("status");
        } else {
            params.set("status", statusName);
        }
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
                        <DashboardPieChartCard sx={theme.mixins.cardHeight(2)} execStatistics={execStatistics} onChartPieceClick={handleStatusChange} selectedStatus={selectedStatus}/>
                        <DurationCard sx={theme.mixins.cardHeight(1)}/>
                    </Stack>
                </Grid>
                <Grid size={{xs: 12, sm: 6, lg: 3}}>
                    <Stack direction="column" spacing={2}>
                        <DashboardTestResultsCard sx={theme.mixins.cardHeight(2)} execStatistics={execStatistics} onListItemClick={handleStatusChange} selectedStatus={selectedStatus}/>
                        <DashboardFailureCorridorCard sx={theme.mixins.cardHeight(1)}/>
                    </Stack>
                </Grid>
                <Grid size={{sm: 12, lg: 6}}>
                    <Stack direction="column" spacing={2}>
                        <DashboardHistoryChartCard sx={theme.mixins.cardHeight(2)}/>
                        <DashboardFailureAspectsCard sx={theme.mixins.cardHeight(1)} execStatistics={execStatistics}/>
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
