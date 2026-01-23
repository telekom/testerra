import {useReportData} from "../provider/DataProvider.tsx";
import Box from "@mui/material/Box";
import {Typography} from "@mui/material";

const Tests = () => {

    const { executionMngr, isLoading, error } = useReportData();

    if (isLoading) return <div>Lade Konfiguration...</div>;
    if (error) return <div>Fehler: {error.message}</div>;
    if (!executionMngr) return null;

    return (
        <>
            <Box>
                <Typography variant="h6">Test view</Typography>
                <Typography>
                    {executionMngr.getRunConfig()?.reportName}
                </Typography>
            </Box>
        </>
    );
};
export default Tests;
