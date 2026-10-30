import Box from "@mui/material/Box";
import {Grid} from "@mui/material";
import Alert from "@mui/material/Alert";
import LinearProgress from "@mui/material/LinearProgress";
import Typography from "@mui/material/Typography";
import {useTheme} from "@mui/material/styles";
import {useState} from "react";
import {useReportData} from "../../provider/DataProvider.tsx";
import {formatDate} from "../../utils/dateFormatter.ts";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";
import ReportCard from "../../widgets/ReportCard.tsx";
import SelectInput from "../../widgets/SelectInput.tsx";

const HistoryRunComparisonPage = () => {
    const theme = useTheme();
    const {executionMngr, isLoading, error} = useReportData();
    const [selectedHistoryIndex, setSelectedHistoryIndex] = useState<number>();
    const historyStatistics = executionMngr?.getHistoryStatistics();
    const currentRun = historyStatistics?.getLastEntry();
    const availableRuns = historyStatistics?.getHistoryAggregateStatistics().slice(0, -1).reverse() ?? [];
    const selectedRun = availableRuns.find(run => run.historyIndex === selectedHistoryIndex) ?? availableRuns[0];

    if (isLoading) return <LinearProgress aria-label="Loading…"/>;
    if (error) return <Alert severity="error">An error occurred: {error.message}</Alert>;
    if (!historyStatistics) return null;
    if (!currentRun || !selectedRun) return <NoResultsCard title="No history available"/>;

    return (
        <Box sx={{width: "100%"}}>
            <Grid container spacing={2}>
                <Grid size={{xs: 12, lg: 3}}>
                    <SelectInput
                        label="Compare latest run with"
                        value={selectedRun.historyIndex}
                        onChange={setSelectedHistoryIndex}
                        menuItems={availableRuns.map(run => ({
                            value: run.historyIndex,
                            label: `Run ${run.historyIndex} (${formatDate(run.historyAggregate.executionContext?.contextValues?.startTime, "long")})`,
                        }))}
                    />
                </Grid>
                <Grid size={{xs: 12, lg: 6}}/>
                <Grid size={{xs: 12, lg: 6}}>
                    <ReportCard
                        label="Selected run"
                        sxCard={theme.mixins.cardHeight(5)}
                        content={<Typography>Run {selectedRun.historyIndex}</Typography>}
                    />
                </Grid>
                <Grid size={{xs: 12, lg: 6}}>
                    <ReportCard
                        label="Latest run"
                        sxCard={theme.mixins.cardHeight(5)}
                        content={<Typography>Run {currentRun.historyIndex}</Typography>}
                    />
                </Grid>
                <Grid size={12}>
                    <ReportCard
                        label="Method status changes"
                        sxCard={theme.mixins.cardHeight(5)}
                        content={<Typography color="text.secondary">Method comparison placeholder</Typography>}
                    />
                </Grid>
            </Grid>
        </Box>
    );
};

export default HistoryRunComparisonPage;
