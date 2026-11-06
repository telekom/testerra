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

import Box from "@mui/material/Box";
import {Grid} from "@mui/material";
import Alert from "@mui/material/Alert";
import LoadingBar from "../../widgets/LoadingBar.tsx";
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

    if (isLoading) return <LoadingBar/>;
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
