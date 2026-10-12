import {useMemo} from "react";
import {useOutletContext} from "react-router-dom";
import {Box, Grid, useTheme} from "@mui/material";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import {useReportData} from "../../provider/DataProvider.tsx";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";
import MethodHistoryChartCard from "./MethodHistoryChartCard.tsx";
import MethodStatusShareCard from "./MethodStatusShareCard.tsx";

const MethodHistoryTab = () => {
    const methodDetail = useOutletContext<MethodDetails | undefined>();
    const {executionMngr} = useReportData();
    const theme = useTheme();
    const methodHistory = useMemo(() => methodDetail
        ? executionMngr?.getHistoryStatistics().getMethodHistory(
            methodDetail.classStatistics.classIdentifier,
            methodDetail.identifier
        )
        : undefined, [executionMngr, methodDetail]);

    if (!methodHistory || methodHistory.getRunCount() === 0) {
        return <NoResultsCard title="No method history available"/>;
    }

    return (
        <Box sx={{width: "100%"}}>
            <Grid container spacing={2}>
                <Grid size={12}>
                    <MethodHistoryChartCard
                        methodHistoryStatistics={methodHistory}
                        sx={theme.mixins.cardHeight(3)}
                    />
                </Grid>
                <Grid size={{xs: 12, sm: 6, lg: 3}}>
                    <MethodStatusShareCard
                        methodHistoryStatistics={methodHistory}
                        sx={theme.mixins.cardHeight(5)}
                    />
                </Grid>
            </Grid>
        </Box>
    );
};

export default MethodHistoryTab;
