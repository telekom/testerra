import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import {Grid, Stack} from "@mui/material";
import ReportCard from "../../widgets/ReportCard.tsx";
import {useSearchParams} from "react-router-dom";
import {useTheme} from "@mui/material/styles";
import {useReportData} from "../../provider/DataProvider.tsx";
import LinearProgress from "@mui/material/LinearProgress";
import Alert from "@mui/material/Alert";
import HistoryTestRunStatistics from "./HistoryTestRunStatistics.tsx";
import HistoryTestRunTopFailing from "./HistoryTestRunTopFailing.tsx";
import {useCallback, useMemo, useState} from "react";
import HistoryTestRunChartCard from "./HistoryTestRunsChartCard.tsx";
import type {HistoryRunViewport} from "./HistoryTestRunChart.tsx";

const HistoryTestRunPage = () => {

    const theme = useTheme()
    const {executionMngr, isLoading, error} = useReportData();

    const [searchParams] = useSearchParams();
    const selectedStatus = searchParams.get("status");
    const historyStatistics = executionMngr?.getHistoryStatistics();
    const defaultViewport = useMemo<HistoryRunViewport | undefined>(() => {
        const runs = historyStatistics?.availableRuns ?? [];
        if (runs.length === 0) {
            return undefined;
        }
        return {
            start: Math.min(...runs),
            end: Math.max(...runs),
        };
    }, [historyStatistics]);
    const [historyViewport, setHistoryViewport] = useState<HistoryRunViewport | undefined>(defaultViewport);
    const handleViewportChange = useCallback((nextViewport: HistoryRunViewport) => {
        setHistoryViewport(currentViewport => {
            if (
                currentViewport?.start === nextViewport.start
                && currentViewport?.end === nextViewport.end
            ) {
                return currentViewport;
            }
            return nextViewport;
        });
    }, []);

    // const statusMenuItems: number[] = []
    // const selectedStatuses: ResultStatus[] = []

    if (isLoading) return <LinearProgress aria-label="Loading…"/>;
    if (error) return <Alert severity="error">An error occured: {error?.message}</Alert>
    if (!executionMngr || !historyStatistics) return null;

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
                    <HistoryTestRunChartCard
                        histStatistics={historyStatistics}
                        selectedStatus={selectedStatus}
                        sx={theme.mixins.cardHeight(10.35)}
                        onViewportChange={handleViewportChange}
                    />
                </Grid>
                <Grid size={{xs: 12, sm: 12, lg: 3}}>
                    <Stack direction="column" spacing={2}>
                        <HistoryTestRunStatistics
                            histStatistics={historyStatistics}
                            sx={theme.mixins.cardHeight(5)}
                        />
                        <ReportCard
                            label="Status share"
                            sxContent={{p: 0}}
                            sxCard={theme.mixins.cardHeight(5)}
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
                    <HistoryTestRunTopFailing
                        histStatistics={historyStatistics}
                        sx={theme.mixins.cardHeight(5)}
                        viewport={historyViewport}
                    />
                </Grid>

            </Grid>
        </Box>

    );
};

export default HistoryTestRunPage;
