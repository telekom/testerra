import Typography from "@mui/material/Typography";
import LoadingBar from "../../widgets/LoadingBar.tsx";
import Alert from "@mui/material/Alert";
import {useReportData} from "../../provider/DataProvider.tsx";

const HistoryTestClassesPage = () => {

    const {executionMngr, isLoading, error} = useReportData();

    if (isLoading) return <LoadingBar/>;
    if (error) return <Alert severity="error">An error occurred: {error.message}</Alert>;
    if (!executionMngr) return null;

    return <Typography variant="body1">Test classes placeholder</Typography>;
};

export default HistoryTestClassesPage;
