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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import {useMemo} from "react";
import {Alert, Box, Grid} from "@mui/material";
import {useSearchParams} from "react-router-dom";
import LoadingBar from "../../widgets/LoadingBar.tsx";
import SelectInput from "../../widgets/SelectInput.tsx";
import {useReportData} from "../../provider/DataProvider.tsx";
import {StatusService} from "../../model/status-service.tsx";
import TestClassCompareChartCard from "./TestClassCompareChartCard.tsx";
import {buildTestClassCompareData, buildTestMethodCompareData} from "./testClassCompareData.ts";

const HistoryTestClassesPage = () => {
    const {executionMngr, isLoading, error} = useReportData();
    const [searchParams, setSearchParams] = useSearchParams();
    const historyStatistics = executionMngr?.getHistoryStatistics();
    const classCompareData = useMemo(
        () => historyStatistics ? buildTestClassCompareData(historyStatistics) : undefined,
        [historyStatistics]
    );
    const selectedClassParam = searchParams.get("class") ?? "";
    const methodCompareData = useMemo(
        () => historyStatistics && selectedClassParam
            ? buildTestMethodCompareData(historyStatistics, selectedClassParam)
            : undefined,
        [historyStatistics, selectedClassParam]
    );

    if (isLoading) return <LoadingBar/>;
    if (error) return <Alert severity="error">An error occurred: {error.message}</Alert>;
    if (!historyStatistics || !classCompareData) return null;

    const classMenuItems = [
        {value: "", label: "(All)"},
        ...classCompareData.classes.map(testClass => ({
            value: testClass.identifier,
            label: testClass.name,
        })),
    ];
    const selectedClass = classMenuItems.some(item => item.value === selectedClassParam)
        ? selectedClassParam
        : "";
    const availableStatuses = selectedClass && methodCompareData
        ? methodCompareData.statuses
        : classCompareData.statuses;
    const statusMenuItems: {value: string; label: string}[] = [
        {value: "", label: "(All)"},
        ...availableStatuses.map(status => ({
            value: StatusService.get(status).key,
            label: StatusService.getLabel(status),
        })),
    ];
    const selectedStatusKey = searchParams.get("status") ?? "";
    const selectedStatus = availableStatuses.find(status => StatusService.get(status).key === selectedStatusKey);

    const updateFilters = (nextClass: string, nextStatusKey: string) => {
        const nextSearchParams = new URLSearchParams(searchParams);
        if (nextClass) {
            nextSearchParams.set("class", nextClass);
        } else {
            nextSearchParams.delete("class");
        }
        if (nextStatusKey) {
            nextSearchParams.set("status", nextStatusKey);
        } else {
            nextSearchParams.delete("status");
        }
        setSearchParams(nextSearchParams, {replace: true});
    };

    const selectClass = (identifier: string) => updateFilters(identifier, "");

    const hasHistory = historyStatistics.getTotalRunCount() > 1;
    const classFilterValue = selectedClass;
    const statusFilterValue = selectedStatus ? StatusService.get(selectedStatus).key : "";

    return (
        <Box sx={{width: "100%"}}>
            <Grid container spacing={2}>
                {hasHistory && (
                    <>
                        <Grid size={{xs: 12, sm: 6, lg: 3}}>
                            <SelectInput
                                label="Class"
                                value={classFilterValue}
                                onChange={selectClass}
                                menuItems={classMenuItems}
                            />
                        </Grid>
                        <Grid size={{xs: 12, sm: 6, lg: 2}}>
                            <SelectInput
                                label="Status"
                                value={statusFilterValue}
                                onChange={value => updateFilters(selectedClass, value)}
                                menuItems={statusMenuItems}
                            />
                        </Grid>
                    </>
                )}
                <Grid size={12}>
                    <TestClassCompareChartCard
                        historyStatistics={historyStatistics}
                        selectedClass={selectedClass || undefined}
                        selectedStatus={selectedStatus}
                        onSelectClass={selectClass}
                    />
                </Grid>
            </Grid>
        </Box>
    );
};

export default HistoryTestClassesPage;
