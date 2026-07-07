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
import {MethodDetails} from "../model/MethodDetails.ts";
import {useMemo} from "react";
import {MethodType} from "../model/report-model/framework_pb.ts";

const MethodDetailsPage = () => {
    const params = useParams();
    const methodDetailsRoute = routesConfig[0].children?.find((route) => route.path === "method/:methodId?");
    const tabs = generateTabsFromRoutes(methodDetailsRoute?.children);

    const {executionMngr, isLoading, error} = useReportData();
    // useMemo to make sure methodDetails is only built new if the data basis changes
    const methodDetail = useMemo<MethodDetails | undefined>(() => {
        if (!executionMngr || !params.methodId) return undefined;
        return executionMngr.getMethodDetails(params.methodId);
    }, [executionMngr, params.methodId]);

    const {previousDetail, nextDetail} = useMemo<{
        previousDetail?: MethodDetails;
        nextDetail?: MethodDetails;
    }>(() => {
        if (!executionMngr || !methodDetail) {
            return {previousDetail: undefined, nextDetail: undefined};
        }

        const myRunIndex = methodDetail.methodContext.methodRunIndex ?? Number.NEGATIVE_INFINITY;
        const details = executionMngr.getExecutionStatistics().classStatistics
            .flatMap(classStatistic => classStatistic.methodContexts)
            .filter(methodContext => methodContext.methodType === MethodType.TEST_METHOD)
            .sort((a, b) => (a.methodRunIndex ?? 0) - (b.methodRunIndex ?? 0))
            .map(methodContext => new MethodDetails(methodContext, methodDetail.classStatistics))
            .filter(detail => detail.numDetails !== 0);

        const previousDetail = [...details]
            .reverse()
            .find(detail => (detail.methodContext.methodRunIndex ?? Number.NEGATIVE_INFINITY) < myRunIndex);
        const nextDetail = details.find(
            detail => (detail.methodContext.methodRunIndex ?? Number.NEGATIVE_INFINITY) > myRunIndex
        );

        return {previousDetail, nextDetail};
    }, [executionMngr, methodDetail]);

    if (isLoading) return <LinearProgress aria-label="Loading…" />;
    if (error) return <Alert severity="error">An error occured: {error?.message}</Alert>
    if (!executionMngr) return null;

    return (
    <Box sx={{width: '100%', p: '24px 32px'}}>
        <GeneralDetails methodDetail={methodDetail} previousDetail={previousDetail} nextDetail={nextDetail}/>
        <TabNavigation tabs={tabs}/>

        <Box sx={{p: '24px 32px'}}>
            {/* Placeholder to render child component from router */}
            <Outlet context={methodDetail}/>
        </Box>
    </Box>
    );
};
export default MethodDetailsPage;
