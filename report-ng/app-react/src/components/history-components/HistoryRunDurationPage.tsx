import Box from "@mui/material/Box";
import LinearProgress from "@mui/material/LinearProgress";
import Alert from "@mui/material/Alert";
import {useTheme} from "@mui/material/styles";
import {useReportData} from "../../provider/DataProvider.tsx";
import DurationChartCard from "./DurationChartCard.tsx";
import {Grid} from "@mui/material";
import {useCallback, useState} from "react";
import DurationLongestRunsCard from "./DurationLongestRunsCard.tsx";
import DurationLongestMethodsCard from "./DurationLongestMethodsCard.tsx";
import type {TestRunViewport} from "./TestRunChart.tsx";

const HistoryRunDurationPage = () => {
    const theme = useTheme();
    const {executionMngr, isLoading, error} = useReportData();
    const historyStatistics = executionMngr?.getHistoryStatistics();
    const [viewport, setViewport] = useState<TestRunViewport>();
    const handleViewportChange = useCallback((nextViewport: TestRunViewport) => {
        setViewport(current => current?.start === nextViewport.start && current?.end === nextViewport.end
            ? current
            : nextViewport);
    }, []);

    if (isLoading) return <LinearProgress aria-label="Loading…"/>;
    if (error) return <Alert severity="error">An error occurred: {error.message}</Alert>;
    if (!historyStatistics) return null;

    return (
        <Box sx={{width: "100%"}}>
            <Grid container spacing={2}>
                <Grid size={12}>
                    <DurationChartCard
                        histStatistics={historyStatistics}
                        sx={theme.mixins.cardHeight(9)}
                        onViewportChange={handleViewportChange}
                    />
                </Grid>
                {historyStatistics.getTotalRunCount() > 1 && (
                    <>
                        <Grid size={{xs: 12, lg: 6}}>
                            <DurationLongestRunsCard
                                histStatistics={historyStatistics}
                                viewport={viewport}
                                sx={theme.mixins.cardHeight(5)}
                            />
                        </Grid>
                        <Grid size={{xs: 12, lg: 6}}>
                            <DurationLongestMethodsCard
                                histStatistics={historyStatistics}
                                viewport={viewport}
                                sx={theme.mixins.cardHeight(5)}
                            />
                        </Grid>
                    </>
                )}
            </Grid>
        </Box>
    );
};

export default HistoryRunDurationPage;
