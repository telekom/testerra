import {useCallback, useEffect, useMemo} from "react";
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
import {useNavigate, useSearchParams} from "react-router-dom";
import {useTheme} from "@mui/material/styles";
import type {CallbackDataParams} from "echarts/types/dist/shared";
import {createSearchParams} from "react-router-dom";

const TEST_NUMBER_LIMIT = 10;

interface ITestDurationMethod {
    id: string;
    name: string;
    duration: number;
    methodType: MethodType;
    status: ResultStatus;
}

interface IDurationBar {
    label: string;
    durationAmount: number;
    methodList: ITestDurationMethod[];
}

interface MethodOption {
    id: string;
    name: string;
}

const TestTimings = () => {
    const {executionMngr} = useReportData();
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();
    const theme = useTheme();
    const configurationChipColor = theme.palette.lightGrey.main;

    const rangeOptions = [
        {value: 5, label: "5"},
        {value: 10, label: "10"},
        {value: 15, label: "15"},
        {value: 20, label: "20"},
    ];

    const rangeNumParam = searchParams.get("rangeNum");
    const parsedRangeNum = Number(rangeNumParam);
    const rangeNum = parsedRangeNum > 0 ? parsedRangeNum : 10;
    const showConfigMethods = searchParams.get("config") === "true";
    const methodIdParam = searchParams.get("methodId");

    useEffect(() => {
        if (rangeNumParam !== null) {
            return;
        }

        // set rangeNum to 10 if no range is set
        const params = new URLSearchParams(searchParams);
        params.set("rangeNum", "10");
        setSearchParams(params, {replace: true});
    }, [rangeNumParam, searchParams, setSearchParams]);

    const handleRangeNumChange = useCallback((value: number) => {
        const params = new URLSearchParams(searchParams);
        params.set("rangeNum", String(value));
        setSearchParams(params);
    }, [searchParams, setSearchParams]);

    const handleShowConfigMethodsChange = useCallback((checked: boolean) => {
        const params = new URLSearchParams(searchParams);
        if (checked) {
            params.set("config", "true");
        } else {
            params.delete("config");
        }
        setSearchParams(params);
    }, [searchParams, setSearchParams]);

    const handleSelectedMethodChange = useCallback((newValue: MethodOption | null) => {
        const params = new URLSearchParams(searchParams);
        if (newValue?.id) {
            params.set("methodId", newValue.id);
        } else {
            params.delete("methodId");
        }
        setSearchParams(params);
    }, [searchParams, setSearchParams]);

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

    const {bars, labels, chartData} = useMemo(() => {
        const methods: ITestDurationMethod[] = filteredMethodDetails.map(md => ({
            id: md.methodContext.contextValues?.id ?? "",
            name: md.identifier,
            duration: ((md.methodContext.contextValues?.endTime ?? 0) - (md.methodContext.contextValues?.startTime ?? 0)) / 1000,
            methodType: md.methodContext.methodType ?? MethodType.TEST_METHOD,
            status: md.methodContext.resultStatus as ResultStatus,
        }));

        if (methods.length === 0) {
            return {bars: [] as IDurationBar[], labels: [] as string[], chartData: [] as number[]};
        }

        // calculate maximum range
        const sorted = [...methods.map(m => m.duration)].sort((a, b) => a - b);
        let maxDuration = sorted[sorted.length - 1];

        // Round up max duration to a clean multiple of rangeNum so all buckets have equal size.
        const remainder = maxDuration % rangeNum;
        maxDuration = remainder === 0 ? maxDuration : maxDuration + (rangeNum - remainder);
        const sectionRange = maxDuration / rangeNum;

        // calculate ranges
        const sectionLabels: string[] = [];
        const sectionValues: number[] = [];
        for (let i = 0; i < rangeNum; i++) {
            const start = i * sectionRange;
            const end = (i + 1) * sectionRange - 1;
            sectionLabels.push(`${start}-${end}s`);
            sectionValues.push(end);
        }

        const barsResult: IDurationBar[] = sectionValues.map((sectionValue, i) => {
            const methodList = methods.filter(m =>
                m.duration <= sectionValue &&
                (sectionValues[i - 1] === undefined || m.duration > sectionValues[i - 1])
            );
            return {label: sectionLabels[i], durationAmount: methodList.length, methodList};
        });

        return {bars: barsResult, labels: sectionLabels, chartData: barsResult.map(b => b.durationAmount)};
    }, [filteredMethodDetails, rangeNum]);

    const seriesData = useMemo(() => {
        if (!selectedMethod || bars.length === 0) {
            return chartData.map(v => ({value: v, itemStyle: {color: theme.custom.testTimings.barColor}}));
        }
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
                const bar = bars[params[0].dataIndex];
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
                        onChange={(_, newValue) => handleSelectedMethodChange(newValue)}
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
                        content={<Echart option={option} height={500} onEvents={{"click": handleBarClick}} notMerge/>}
                    />
                </Grid>
            </Grid>
        </Box>
    );
};
export default TestTimings;
