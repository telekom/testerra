import Box from "@mui/material/Box";
import LinearProgress from "@mui/material/LinearProgress";
import Alert from "@mui/material/Alert";
import {useTheme} from "@mui/material/styles";
import {useReportData} from "../../provider/DataProvider.tsx";
import DurationChartCard from "./DurationChartCard.tsx";

const HistoryRunDurationPage = () => {
    const theme = useTheme();
    const {executionMngr, isLoading, error} = useReportData();
    const historyStatistics = executionMngr?.getHistoryStatistics();

    if (isLoading) return <LinearProgress aria-label="Loading…"/>;
    if (error) return <Alert severity="error">An error occurred: {error.message}</Alert>;
    if (!historyStatistics) return null;

    return (
        <Box sx={{width: "100%"}}>
            <DurationChartCard
                histStatistics={historyStatistics}
                sx={theme.mixins.cardHeight(10)}
            />
        </Box>
    );
};

export default HistoryRunDurationPage;
