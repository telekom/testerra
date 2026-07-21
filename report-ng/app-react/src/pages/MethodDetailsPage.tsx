/*
 * Testerra
 *
 * (C) 2026, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { useParams } from "react-router";
import TabNavigation from "../widgets/TabNavigation.tsx";
import Box from "@mui/material/Box";
import {Outlet, useLocation, useNavigate} from "react-router-dom";
import {routesConfig} from "../router/mainRouter.tsx";
import {generateTabsFromRoutes} from "../utils/generateTabsFromRoutes.ts";
import GeneralDetails from "../components/method-details-tab-components/GeneralDetails.tsx";
import PriorityMessagesCard from "../components/method-details-tab-components/PriorityMessagesCard.tsx";
import {useReportData} from "../provider/DataProvider.tsx";
import LinearProgress from "@mui/material/LinearProgress";
import Alert from "@mui/material/Alert";
import {MethodDetails} from "../model/MethodDetails.ts";
import {useEffect, useMemo} from "react";
import {MethodType} from "../model/report-model/framework_pb.ts";
import NoResultsCard from "../widgets/NoResultsCard.tsx";

const MethodDetailsPage = () => {
    const params = useParams();
    const navigate = useNavigate();
    const location = useLocation();
    const methodDetailsRoute = routesConfig[0].children?.find((route) => route.path === "method/:methodId?");
    const tabs = generateTabsFromRoutes(methodDetailsRoute?.children);
    const getTabBaseRoute = (route: string) => route.split("/:")[0];

    const {executionMngr, isLoading, error} = useReportData();
    // useMemo to make sure methodDetails is only built new if the data basis changes
    const methodDetail = useMemo<MethodDetails | undefined>(() => {
        if (!executionMngr || !params.methodId) return undefined;
        return executionMngr.getMethodDetails(params.methodId);
    }, [executionMngr, params.methodId]);

    const tabsWithCounts = useMemo(() => {
        if (!methodDetail) return tabs;
        return tabs.map(tab => {
            if (tab.route === "details") {
                const count = methodDetail.errorContexts.length;
                return {...tab, count, visible: methodDetail.numDetails > 0};
            }
            if (tab.route.startsWith("steps")) return {...tab, count: methodDetail.methodContext.testSteps?.length ?? 0, visible: true};
            if (tab.route === "browser-info") {
                const count = methodDetail.sessionContexts.length;
                return {...tab, count, visible: count > 0};
            }
            if (tab.route === "dependencies") {
                const mc = methodDetail.methodContext;
                const count = (mc.relatedMethodContextIds?.length ?? 0) + (mc.dependsOnMethodContextIds?.length ?? 0);
                return {...tab, count, visible: count > 0};
            }
            if (tab.route === "video") {
                const count = methodDetail.sessionContexts.filter(s => s.videoId).length;
                return {...tab, count, visible: count > 0};
            }
            return tab;
        }).filter(tab => tab.visible !== false);
    }, [tabs, methodDetail]);

    useEffect(() => {
        if (tabsWithCounts.length === 0) {
            return;
        }
        const isCurrentRouteVisible = tabsWithCounts.some(tab => location.pathname.includes(`/${getTabBaseRoute(tab.route)}`));
        if (!isCurrentRouteVisible) {
            navigate(getTabBaseRoute(tabsWithCounts[0].route), {replace: true});
        }
    }, [location.pathname, navigate, tabsWithCounts]);

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

    if (!methodDetail) return <NoResultsCard title="No method selected" />;

    return (
    <Box sx={{width: '100%', p: '24px 32px'}}>
        <GeneralDetails methodDetail={methodDetail} previousDetail={previousDetail} nextDetail={nextDetail}/>
        <PriorityMessagesCard promptLogs={methodDetail.promptLogs} />
        <TabNavigation tabs={tabsWithCounts}/>

        <Box sx={{p: '24px 0px'}}>
            {/* Placeholder to render child component from router */}
            <Outlet context={methodDetail}/>
        </Box>
    </Box>
    );
};
export default MethodDetailsPage;
