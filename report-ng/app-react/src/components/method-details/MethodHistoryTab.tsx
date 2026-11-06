/*
 * Testerra
 *
 * (C) 2026, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {useMemo, useState} from "react";
import {useOutletContext} from "react-router-dom";
import {Box, Grid, useTheme} from "@mui/material";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import {useReportData} from "../../provider/DataProvider.tsx";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";
import MethodHistoryChartCard from "./MethodHistoryChartCard.tsx";
import MethodStatusShareCard from "./MethodStatusShareCard.tsx";
import MethodHistoryStatisticsCard from "./MethodHistoryStatisticsCard.tsx";
import MethodFailureAspectsCard from "./MethodFailureAspectsCard.tsx";
import type {MethodHistoryStatistics} from "../../model/MethodHistoryStatistics.ts";

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
    const [failureAspectSelection, setFailureAspectSelection] = useState<{
        methodHistory: MethodHistoryStatistics;
        failureAspect: string | undefined;
    }>();
    const selectedFailureAspect = failureAspectSelection?.methodHistory === methodHistory
        ? failureAspectSelection?.failureAspect : undefined;

    if (!methodHistory || methodHistory.getRunCount() === 0) {
        return <NoResultsCard title="No method history available"/>;
    }

    return (
        <Box sx={{width: "100%"}}>
            <Grid container spacing={2}>
                <Grid size={12}>
                    <MethodHistoryChartCard
                        methodHistoryStatistics={methodHistory}
                        selectedFailureAspect={selectedFailureAspect}
                        sx={theme.mixins.cardHeight(3)}
                    />
                </Grid>
                <Grid size={{xs: 12, sm: 6, lg: 3}}>
                    <MethodStatusShareCard
                        methodHistoryStatistics={methodHistory}
                        sx={theme.mixins.cardHeight(5)}
                    />
                </Grid>
                <Grid size={{xs: 12, sm: 6, lg: 3}}>
                    <MethodHistoryStatisticsCard
                        methodHistoryStatistics={methodHistory}
                        sx={theme.mixins.cardHeight(5)}
                    />
                </Grid>
                <Grid size={{xs: 12, lg: 6}}>
                    <MethodFailureAspectsCard
                        methodHistoryStatistics={methodHistory}
                        selectedFailureAspect={selectedFailureAspect}
                        onSelectFailureAspect={failureAspect => setFailureAspectSelection({methodHistory, failureAspect})}
                        sx={theme.mixins.cardHeight(5)}
                    />
                </Grid>
            </Grid>
        </Box>
    );
};

export default MethodHistoryTab;
