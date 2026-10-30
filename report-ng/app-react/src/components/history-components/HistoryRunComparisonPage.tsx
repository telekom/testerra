import Box from "@mui/material/Box";
import {Grid} from "@mui/material";
import Alert from "@mui/material/Alert";
import LinearProgress from "@mui/material/LinearProgress";
import {useState} from "react";
import {useReportData} from "../../provider/DataProvider.tsx";
import {formatDate} from "../../utils/dateFormatter.ts";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";
import SelectInput from "../../widgets/SelectInput.tsx";
import RunCompareStatisticsCard from "./RunCompareStatisticsCard.tsx";
import RunCompareMethodsCard from "./RunCompareMethodsCard.tsx";

const HistoryRunComparisonPage = () => {
    const {executionMngr, isLoading, error} = useReportData();
    const [selectedHistoryIndex, setSelectedHistoryIndex] = useState<number>();
    const historyStatistics = executionMngr?.getHistoryStatistics();
    const currentRun = historyStatistics?.getLastEntry();
    const availableRuns = historyStatistics?.getHistoryAggregateStatistics().slice(0, -1).reverse() ?? [];
    const selectedRun = availableRuns.find(run => run.historyIndex === selectedHistoryIndex) ?? availableRuns[0];

    if (isLoading) return <LinearProgress aria-label="Loading…"/>;
    if (error) return <Alert severity="error">An error occurred: {error.message}</Alert>;
    if (!executionMngr || !historyStatistics) return null;
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
                <Grid size={{xs: 12, lg: 9}}/>
                <Grid size={{xs: 12, lg: 6}}>
                    <RunCompareStatisticsCard
                        label="Selected run"
                        runStatistics={selectedRun}
                    />
                </Grid>
                <Grid size={{xs: 12, lg: 6}}>
                    <RunCompareStatisticsCard
                        label="Latest run"
                        runStatistics={currentRun}
                    />
                </Grid>
                <Grid size={12}>
                    <RunCompareMethodsCard
                        selectedRun={selectedRun}
                        currentRun={currentRun}
                        executionStatistics={executionMngr.getExecutionStatistics()}
                    />
                </Grid>
            </Grid>
        </Box>
    );
};

export default HistoryRunComparisonPage;
