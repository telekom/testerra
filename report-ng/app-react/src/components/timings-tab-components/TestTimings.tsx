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


import {useCallback, useMemo} from "react";
import TextField from "@mui/material/TextField";
import Autocomplete from "@mui/material/Autocomplete";
import {Box, Grid, Switch, Typography} from "@mui/material";
import Stack from "@mui/material/Stack";
import SelectInput from "../../widgets/SelectInput";
import Echart from "../../widgets/Echart";
import ReportCard from "../../widgets/ReportCard";
import {useReportData} from "../../provider/DataProvider";
import {MethodDetails} from "../../model/MethodDetails";
import {MethodType} from "../../model/report-model/framework_pb";
import {StatusService} from "../../model/status-service";
import type {ResultStatus} from "../../model/status-service";
import type {EChartsOption} from "echarts-for-react";
import {useNavigate} from "react-router-dom";
import {useTheme} from "@mui/material/styles";
import type {CallbackDataParams} from "echarts/types/dist/shared";
import {createSearchParams} from "react-router-dom";
import {useTimingSearchParams} from "./useTimingSearchParams";
import {buildDurationBuckets, type DurationBucket} from "./durationBuckets";

const TEST_NUMBER_LIMIT = 10;

interface ITestDurationMethod {
    id: string;
    name: string;
    duration: number;
    methodType: MethodType;
    status: ResultStatus;
}

interface MethodOption {
    id: string;
    name: string;
}

const TestTimings = () => {
    const {executionMngr} = useReportData();
    const navigate = useNavigate();
    const theme = useTheme();
    // const isMobileLayout = useMediaQuery(theme.breakpoints.down("md"));
    const configurationChipColor = theme.palette.lightGrey.main;
    const {
        rangeNum,
        showConfigMethods,
        methodIdParam,
        handleRangeNumChange,
        handleShowConfigMethodsChange,
        handleSelectedMethodIdChange,
    } = useTimingSearchParams();

    const rangeOptions = [
        {value: 5, label: "5"},
        {value: 10, label: "10"},
        {value: 15, label: "15"},
        {value: 20, label: "20"},
    ];

    const filteredMethodDetails = useMemo<MethodDetails[]>(() => {
        if (!executionMngr) return [];
        const executionStats = executionMngr.getExecutionStatistics();
        const result: MethodDetails[] = [];
        for (const classStats of executionStats.classStatistics) {
            for (const methodContext of classStats.methodContexts) {
                if (showConfigMethods || methodContext.methodType === MethodType.TEST_METHOD) {
                    result.push(new MethodDetails(methodContext, classStats));
                }
            }
        }
        return result;
    }, [executionMngr, showConfigMethods]);

    const lookupOptions = useMemo<MethodOption[]>(() => {
        return filteredMethodDetails
            .map(methodContext => ({id: methodContext.methodContext.contextValues?.id ?? "", name: methodContext.identifier}))
            .filter(data => data.id && data.name.trim().length > 0)
            .sort((a, b) => a.name.localeCompare(b.name));
    }, [filteredMethodDetails]);

    // Keep selected option derived from URL param + current options (no effect/setState sync needed).
    const selectedMethod = useMemo(
        () => (methodIdParam ? (lookupOptions.find(option => option.id === methodIdParam) ?? null) : null),
        [methodIdParam, lookupOptions]
    );

    const methods = useMemo<ITestDurationMethod[]>(
        () => filteredMethodDetails.map(md => ({
            id: md.methodContext.contextValues?.id ?? "",
            name: md.identifier,
            duration: ((md.methodContext.contextValues?.endTime ?? 0) - (md.methodContext.contextValues?.startTime ?? 0)) / 1000,
            methodType: md.methodContext.methodType ?? MethodType.TEST_METHOD,
            status: md.methodContext.resultStatus as ResultStatus,
        })),
        [filteredMethodDetails]
    );
    // Convert methods to chart bins (bars + x-axis labels + y-values).
    const {bars, labels, chartData} = useMemo(
        () => buildDurationBuckets<ITestDurationMethod>(methods, rangeNum),
        [methods, rangeNum]
    );

    const seriesData = useMemo(() => {
        if (!selectedMethod || bars.length === 0) {
            return chartData.map(v => ({value: v, itemStyle: {color: theme.custom.testTimings.barColor}}));
        }
        // Highlight only the bar containing the selected method.
        const highlightIndex = bars.findIndex(bar => bar.methodList.some(m => m.id === selectedMethod.id));
        return chartData.map((v, i) => ({
            value: v,
            itemStyle: {color: i === highlightIndex ? theme.custom.testTimings.barColor : theme.custom.testTimings.barColorPale},
        }));
    }, [chartData, bars, selectedMethod, theme.custom.testTimings.barColor, theme.custom.testTimings.barColorPale]);

    const option: EChartsOption = useMemo(() => ({
        tooltip: {
            trigger: "axis",
            axisPointer: {type: "shadow"},
            appendToBody: true,     // allows tooltip to overflow chart container (otherwise it would be clipped)
            formatter: (params: CallbackDataParams | CallbackDataParams[]) => {
                if (!Array.isArray(params) || params.length === 0) return "";
                const bar: DurationBucket<ITestDurationMethod> | undefined = bars[params[0].dataIndex];
                if (!bar || bar.durationAmount === 0) return "";

                let tooltip = `${bar.durationAmount} test case(s):<br/><br/>`;
                bar.methodList.slice(0, TEST_NUMBER_LIMIT).forEach(method => {
                    const statusInfo = StatusService.get(method.status);
                    tooltip += `<div style="margin-bottom:4px">
                        <span style="background:${statusInfo.color};color:#fff;padding:1px 6px;border-radius:20px;margin-right:4px">${statusInfo.label}</span>
                        ${method.name}
                        ${method.methodType === MethodType.CONFIGURATION_METHOD
                        ? `<span style="background:${configurationChipColor};color:#fff;padding:1px 6px;border-radius:20px;margin-left:4px;font-size:0.85em">Configuration</span>`
                        : ""}
                    </div>`;
                });
                if (bar.durationAmount > TEST_NUMBER_LIMIT) {
                    tooltip += `and ${bar.durationAmount - TEST_NUMBER_LIMIT} more`;
                }
                return tooltip;
            },
        },
        xAxis: {
            type: "category",
            data: labels,
            name: "Duration",
            axisLabel: {
                interval: rangeNum >= 15 ? 1 : 0,
            },
        },
        yAxis: {
            type: "value",
            minInterval: 1, // allows only integer values
            name: "Number of test cases",
        },
        series: [{
            data: seriesData,
            type: "bar",
            itemStyle: {color: theme.custom.testTimings.barColor},
        }],
    }), [labels, seriesData, bars, rangeNum, theme.custom.testTimings.barColor, configurationChipColor]);

    const handleBarClick = useCallback((params: CallbackDataParams) => {
        const methodIds = bars[params.dataIndex]?.methodList.map(m => m.id);
        if (methodIds?.length) {
            navigate({
                pathname: "/Tests",
                search: createSearchParams({
                    methods: methodIds.join("~"),
                    ...(showConfigMethods ? {config: "true"} : {}),
                }).toString(),
            });
        }
    }, [bars, navigate, showConfigMethods]);

    return (
        <Box sx={theme.custom.testTimings.container}>
            <Grid container spacing={2} columns={12}>
                <Grid size={4}>
                    <SelectInput
                        label="Number of method ranges"
                        value={rangeNum}
                        onChange={handleRangeNumChange}
                        menuItems={rangeOptions}
                        sx={theme.custom.testTimings.rangeSelect}
                    />
                </Grid>
                <Grid size={4}>
                    <Autocomplete
                        disablePortal
                        options={lookupOptions}
                        getOptionLabel={(option) => option.name}
                        renderOption={(props, option) => (
                            <li {...props} key={option.id}>
                                {option.name}
                            </li>
                        )}
                        isOptionEqualToValue={(opt, val) => opt.id === val.id}
                        value={selectedMethod}
                        onChange={(_, newValue) => handleSelectedMethodIdChange(newValue?.id ?? null)}
                        sx={theme.custom.testTimings.methodAutocomplete}
                        renderInput={(params) => <TextField {...params} label="Method"/>}
                    />
                </Grid>
                <Grid size={4} sx={theme.custom.testTimings.configMethodsCell}>
                    <Stack direction="row" sx={theme.custom.testTimings.configMethodsStack}>
                        <Switch
                            checked={showConfigMethods}
                            onChange={(e) => handleShowConfigMethodsChange(e.target.checked)}
                        />
                        <Typography variant="body2">Show configuration methods</Typography>
                    </Stack>
                </Grid>
                <Grid size={12}>
                    <ReportCard
                        label="Test durations"
                        content={<Echart option={option} height="55dvh" onEvents={{"click": handleBarClick}} autoResize notMerge/>}
                    />
                </Grid>
            </Grid>
        </Box>
    );
};
export default TestTimings;
