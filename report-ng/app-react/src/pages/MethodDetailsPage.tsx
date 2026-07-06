import { useParams } from "react-router";
import TabNavigation from "../widgets/TabNavigation.tsx";
import Box from "@mui/material/Box";
import {Outlet} from "react-router-dom";
import {routesConfig} from "../router/mainRouter.tsx";
import {generateTabsFromRoutes} from "../utils/generateTabsFromRoutes.ts";
import GeneralDetails from "../components/method-details-tab-components/GeneralDetails.tsx";
import {useReportData} from "../provider/DataProvider.tsx";
import LinearProgress from "@mui/material/LinearProgress";
import Alert from "@mui/material/Alert";
import type {MethodDetails} from "../model/MethodDetails.ts";
import {useMemo} from "react";

const MethodDetailsPage = () => {
    const params = useParams();
    const methodDetailsRoute = routesConfig[0].children?.find((route) => route.path === "method/:methodId?");
    const tabs = generateTabsFromRoutes(methodDetailsRoute?.children);

    const {executionMngr, isLoading, error} = useReportData();

    // useMemo to make sure methodDetails is only built new if the data basis changes
    const methodDetail = useMemo<MethodDetails | undefined>(() => {
        if (!executionMngr || !params.methodId) return undefined;
        return executionMngr.getExecutionStatistics()
            .classStatistics.flatMap(classStatistic => classStatistic.methodContexts.map((methodContext) =>
                methodContext.contextValues?.id
                    ? executionMngr.getMethodDetails(methodContext.contextValues.id)
                    : undefined
            )).filter((detail): detail is MethodDetails => detail !== undefined)
            .find(detail => detail.methodContext.contextValues?.id === params.methodId);
    }, [executionMngr, params.methodId]);

    if (isLoading) return <LinearProgress aria-label="Loading…" />;
    if (error) return <Alert severity="error">An error occured: {error?.message}</Alert>
    if (!executionMngr) return null;

    return (
    <Box sx={{width: '100%', p: '24px 32px'}}>
        <GeneralDetails methodDetail={methodDetail}/>
        <TabNavigation tabs={tabs}/>

        <Box sx={{p: '24px 32px'}}>
            {/* Placeholder to render child component from router */}
            <Outlet context={methodDetail}/>
        </Box>
    </Box>
    );
};
export default MethodDetailsPage;
