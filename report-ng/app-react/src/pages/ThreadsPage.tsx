import Box from "@mui/material/Box";
import {
    Card,
    CircularProgress,
    FormControl,
    Grid,
    InputLabel,
    MenuItem,
    Select,
    Stack,
    TextField,
    Autocomplete
} from "@mui/material";
import {useSearchParams, useNavigate} from "react-router-dom";
import {useMemo, useRef, useEffect, type SyntheticEvent} from "react";
import type {EChartsOption, TooltipComponentFormatterCallbackParams, ECElementEvent, EChartsType, CustomSeriesOption} from 'echarts';
import * as echarts from 'echarts';
import type {SelectChangeEvent} from "@mui/material/Select";
import {useReportData} from '../provider/DataProvider';
import {dateFormatter} from '../utils/dateFormatter';
import {StatusService} from '../model/status-service';
import {ClassName, classNameConverter} from "../utils/classNameConverter.ts";
import type {MethodContext} from '../model/report-model/framework_pb';
import Echart, {type EchartRef} from "../widgets/Echart.tsx";

// Types
interface MethodInfo {
    id: string;
    name: string;
}

interface TimelineEntry {
    name: string;
    value: [string, number, number, string, number, number, string, number, string]; // [threadName, startTime, endTime, methodName, duration, runIndex, methodId, status, classId]
    itemStyle: {
        color: string;
        opacity: number;
    };
}

const ThreadsPage = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();

    const methodIdParam = searchParams.get("methodId");
    const statusParam = searchParams.get("status");
    const classParam = searchParams.get("class");

    const selectedStatus = statusParam ? parseInt(statusParam) : null;
    const selectedClass = classParam ?? null;

    const {executionMngr, isLoading} = useReportData();

    const chartRef = useRef<EchartRef | null>(null);

    const availableStatuses = useMemo(
        () => executionMngr?.getExecutionStatistics().availableStatuses || [],
        [executionMngr]
    );

    const methodLookup = useMemo((): MethodInfo[] => {
        if (!executionMngr) return [];
        const methodInfo: MethodInfo[] = [];
        const methodContexts = Object.values(
            executionMngr.getExecutionAggregate().methodContexts || {}
        );
        for (const methodContext of methodContexts) {
            if (!methodContext.contextValues) continue;
            methodInfo.push({
                id: methodContext.contextValues.id ?? "",
                name: methodContext.contextValues.name + " (" + methodContext.methodRunIndex + ")"
            });
        }
        return methodInfo.sort((a, b) => a.name.localeCompare(b.name));
    }, [executionMngr]);

    // Values for presentation
    const GAP_FROM_BORDER_TO_START = 400;           // To prevent that the beginning of the first test is located ON the y-axis.
    const THREAD_HEIGHT = 50;                       // Height of y-axis categories in pixel
    const SLIDER_SPACING_FROM_CHART = 90;           // Distance between chart and dataZoom-slider in pixel
    const OPACITY_OF_INACTIVE_ELEMENTS = 0.38;      // Default opacity of disabled elements https://m2.material.io/design/interaction/states.html#disabled

    /**
     * Build Thread timeline according https://echarts.apache.org/examples/en/editor.html?c=custom-profile
     * and https://echarts.apache.org/en/option.html#series-custom
     */
    const preparedTimeline = useMemo((): { options: EChartsOption | null; cardHeight: number } => {
        if (!executionMngr) return {options: null, cardHeight: 600};

        const data: TimelineEntry[] = [];
        const startTimes: number[] = [];
        const threadCategories = new Map<string, MethodContext[]>();

        const methodContexts = executionMngr.getExecutionAggregate().methodContexts || {};

        // Collect all threads and methods
        Object.entries(methodContexts).forEach(([, methodContext]: [string, MethodContext]) => {
            const threadName = methodContext.threadName || 'Unknown';
            if (!threadCategories.has(threadName)) {
                threadCategories.set(threadName, []);
            }
            threadCategories.get(threadName)!.push(methodContext);
            if (methodContext.contextValues?.startTime) {
                startTimes.push(methodContext.contextValues.startTime);
            }
        });

        if (startTimes.length === 0) return {options: null, cardHeight: 600};

        const chartStartTime = Math.min(...startTimes) - GAP_FROM_BORDER_TO_START;

        // Build data for custom series
        for (const [threadName, methodContextsList] of threadCategories.entries()) {
            for (const methodContext of methodContextsList) {

                if (!methodContext.contextValues?.id || !methodContext.contextValues?.endTime || !methodContext.contextValues?.startTime || !methodContext.methodRunIndex || !methodContext.resultStatus) continue;

                const methodDetails = executionMngr.getMethodDetails(methodContext.contextValues.id);
                const itemColor = StatusService.getColor(methodContext.resultStatus!);
                const duration = methodContext.contextValues?.endTime - methodContext.contextValues?.startTime;
                const classId = executionMngr.getExecutionStatistics().classStatistics.find(classStat => {
                    const classContextIds = classStat.methodContexts
                        .map(context => context.classContextId)
                        .filter((value, index, self) => self.indexOf(value) === index);
                    return classContextIds.includes(methodContext.classContextId)
                })?.classIdentifier

                data.push({
                    name: methodDetails?.identifier || methodContext.contextValues?.name || 'Unknown',
                    value: [
                        threadName,
                        methodContext.contextValues?.startTime,
                        methodContext.contextValues?.endTime,
                        methodContext.contextValues?.name || '',
                        duration,
                        methodContext.methodRunIndex,
                        methodContext.contextValues.id,
                        methodContext.resultStatus,
                        classId ?? ""
                    ],
                    itemStyle: {
                        color: itemColor,
                        opacity: 1
                    }
                });
            }
        }

        // Some calculations for chart presentation
        const gridHeight = Math.max(threadCategories.size * THREAD_HEIGHT, 200);
        const sliderFromTop = gridHeight + SLIDER_SPACING_FROM_CHART;
        const computedCardHeight = sliderFromTop + 60;

        // Set gridLeftValue dynamically to the longest thread name
        const longestThreadName = Array.from(threadCategories.keys()).reduce(
            function (a, b) {
                return a.length > b.length ? a : b;
            }
        );
        let gridLeftValue = longestThreadName.length * 7;   // Calculate the value for grid:left
        gridLeftValue = gridLeftValue > 100 ? gridLeftValue : 100;  // Set to default of 100, if lower

        const options: EChartsOption = {
            tooltip: {
                formatter: function (params: TooltipComponentFormatterCallbackParams) {
                    if (!params || Array.isArray(params) || !params.value) return '';
                    const value = params.value as TimelineEntry['value'];
                    return '<div style="background-color: ' +
                        params.color + '; padding: 5px; color: white; margin: -10px;">' + params.name + ' (' + value[5] + ')</div>'
                        + '<br>Start time: ' + new Date(value[1]).toLocaleString()
                        + '<br>End time: ' + new Date(value[2]).toLocaleString()
                        + '<br>Duration: ' + Math.floor(value[4] / 1000) + 's'
                        + '<br>Class: ' + classNameConverter(value[8], ClassName.simpleName);
                }
            },
            dataZoom: [
                {
                    type: 'slider',
                    filterMode: 'weakFilter',
                    showDataShadow: false,
                    top: sliderFromTop,
                    labelFormatter: ''
                },
                {
                    id: 'threadZoom',
                    type: 'inside',
                    filterMode: 'weakFilter'
                }
            ],
            grid: {
                height: gridHeight,
                top: 30,
                bottom: 100,
                left: gridLeftValue
            },
            xAxis: {
                type: 'value',
                min: chartStartTime,
                scale: true,
                axisLabel: {
                    interval: 2,
                    formatter: (val: number) =>
                        `${dateFormatter(val, "time")}\n\n${dateFormatter(val, "date")}`
                }
            },
            yAxis: {
                type: 'category',
                data: Array.from(threadCategories.keys()),
                splitArea: {
                    show: true,
                    areaStyle: {
                        color: ['rgb(255,255,255)', 'rgb(239,239,239)'],
                        opacity: 1
                    }
                }
            },
            series: [
                {
                    type: 'custom',
                    renderItem: function (params, api) {
                        const categoryIndex = api.value(0);
                        const start = api.coord([api.value(1), categoryIndex]);
                        const end = api.coord([api.value(2), categoryIndex]);

                        const size = api.size?.([0, 1]);
                        const height = Array.isArray(size) ? size[1] * 0.7 : 0;

                        const coordSys = params.coordSys as unknown as {
                            x: number;
                            y: number;
                            width: number;
                            height: number;
                        };
                        const rectShape = echarts.graphic.clipRectByRect(
                            {
                                x: start[0],
                                y: start[1] - height / 2,
                                width: end[0] - start[0],
                                height: height
                            },
                            {
                                x: coordSys.x,
                                y: coordSys.y,
                                width: coordSys.width,
                                height: coordSys.height
                            }
                        );

                        return rectShape ? {
                            type: 'rect',
                            transition: ['shape'],
                            shape: rectShape,
                            style: api.style()
                        } : null;
                    },
                    encode: {
                        x: [1, 2],
                        y: 0,
                        label: 3    // Index in value array
                    },
                    data: data
                }
            ]
        };

        return {options, cardHeight: computedCardHeight};
    }, [executionMngr]);


    // Single source of truth for the active filter, derived from URL params.
    // Only one filter can be active at a time (status > class > method priority).
    const activeFilter = useMemo(() =>
        statusParam ? {value: parseInt(statusParam) as string | number, index: 7}
        : classParam ? {value: classParam as string | number, index: 8}
        : methodIdParam ? {value: methodIdParam as string | number, index: 6}
        : null,
        [statusParam, classParam, methodIdParam]
    );

    // Derives the final chart config from the cached threads timeline and the active filter.
    // When a filter is active, each entry gets full or reduced opacity — without mutating
    // the original preparedTimeline.options (immutable spread instead of forEach-mutation).
    const chartOptions = useMemo((): EChartsOption | null => {
        if (!preparedTimeline.options) return null;
        if (!activeFilter) return preparedTimeline.options;

        const originalSeries = (preparedTimeline.options.series as CustomSeriesOption[])[0];
        const seriesData = (originalSeries.data as TimelineEntry[]).map(entry => ({
            ...entry,
            itemStyle: {
                ...entry.itemStyle,
                opacity: entry.value[activeFilter.index] === activeFilter.value
                    ? 1
                    : OPACITY_OF_INACTIVE_ELEMENTS
            }
        }));

        // Spread preserves all ECharts config (axes, tooltip, renderItem fn, …),
        // only series.data is replaced with the opacity-adjusted copy.
        return {
            ...preparedTimeline.options,
            series: [{...originalSeries, data: seriesData}]
        };
    }, [preparedTimeline.options, activeFilter]);

    // Zooms the chart to the time range of the filtered entries.
    // Opacity changes are handled separately in chartOptions (immutable, reactive),
    // so this function only handles the zoom dispatching
    const dispatchZoom = (instance: EChartsType | undefined) => {
        if (!chartOptions) return;

        if (!activeFilter) {    // reset zoom to full range if no filter is active
            instance?.dispatchAction({type: 'dataZoom', id: 'threadZoom', start: 0, end: 100});
            return;
        }

        const seriesData = (chartOptions.series as echarts.EChartsOption[])?.[0]?.data as TimelineEntry[];
        if (!seriesData) return;

        // find matching entries to calculare their start and end timespan for the zoom
        const matching = seriesData.filter(e => e.value[activeFilter.index] === activeFilter.value);
        if (matching.length === 0) return;

        const zoomStart = Math.min(...matching.map(e => e.value[1]));
        const zoomEnd = Math.max(...matching.map(e => e.value[2]));
        const spacing = (zoomEnd - zoomStart) * 0.05;

        instance?.dispatchAction({
            type: 'dataZoom',
            id: 'threadZoom',
            startValue: zoomStart - spacing,
            endValue: zoomEnd + spacing
        });
    };

    // Re-zooms whenever the chart config changes (filter or data update).
    useEffect(() => {
        dispatchZoom(chartRef.current?.getEchartsInstance());
    }, [chartOptions, activeFilter]);

    const handleChartClick = (params: ECElementEvent): void => {
        if (params.value) {
            const value = params.value as TimelineEntry['value'];
            if (value[6]) navigate(`/method/${value[6]}`);
        }
    };

    const handleStatusChange = (e: SelectChangeEvent<number>): void => {
        const status = e.target.value;
        const params = new URLSearchParams(searchParams);
        if (status && typeof status === 'number' && status > 0) {
            params.set('status', status.toString());
        } else {
            params.delete('status');
        }
        setSearchParams(params);
    };

    const handleClassChange = (e: SelectChangeEvent<string>): void => {
        const className: string = e.target.value;
        const params = new URLSearchParams(searchParams);
        if (className) {
            params.set('class', className);
        } else {
            params.delete('class');
        }
        setSearchParams(params);
    };

    const handleMethodInputChange = (_e: SyntheticEvent, value: MethodInfo | null): void => {
        const params = new URLSearchParams(searchParams);
        if (value) {
            params.set('methodId', value.id);
        } else {
            params.delete('methodId');
        }
        setSearchParams(params);
    };

    const isStatusDisabled = !!(classParam || methodIdParam);
    const isClassDisabled = !!(statusParam || methodIdParam);
    const isMethodDisabled = !!(statusParam || classParam);

    if (isLoading) {
        return <CircularProgress/>;
    }

    return (
        <Box sx={{width: '100%', p: '24px 32px'}}>
            <Stack spacing={2}>
                <Grid container spacing={2}>
                    <Grid size={2}>
                        <FormControl fullWidth sx={isStatusDisabled ? {cursor: 'not-allowed', '& *': {pointerEvents: 'none'}} : undefined}>
                            <InputLabel sx={isStatusDisabled ? {color: 'text.disabled'} : undefined}>Status</InputLabel>
                            <Select<number>
                                value={selectedStatus || ''}
                                onChange={handleStatusChange}
                                label="Status"
                                disabled={isStatusDisabled}
                            >
                                <MenuItem value="">
                                    (All)
                                </MenuItem>
                                {availableStatuses.map((status: number) => (
                                    <MenuItem key={status} value={status}>
                                        {StatusService.getLabel(status as unknown as string)}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>

                    <Grid size={3}>
                        <FormControl fullWidth sx={isClassDisabled ? {cursor: 'not-allowed', '& *': {pointerEvents: 'none'}} : undefined}>
                            <InputLabel sx={isClassDisabled ? {color: 'text.disabled'} : undefined}>Class</InputLabel>
                            <Select<string>
                                value={selectedClass || ''}
                                onChange={handleClassChange}
                                label="Class"
                                disabled={isClassDisabled}
                                MenuProps={{PaperProps: {sx: {maxHeight: '50vh'}}}}
                            >
                                <MenuItem value="">
                                    (All)
                                </MenuItem>
                                {[...(executionMngr?.getExecutionStatistics()?.classStatistics ?? [])]
                                    .sort((a, b) => a.classIdentifier.localeCompare(b.classIdentifier))
                                    .map(classStat => (
                                    <MenuItem key={classStat.classIdentifier} value={classStat.classIdentifier}>
                                        {classStat.classIdentifier.split('.').pop()}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>

                    <Grid size="grow">
                        <Autocomplete
                            options={methodLookup}
                            getOptionLabel={(option: MethodInfo) => option.name}
                            value={methodLookup.find(m => m.id === methodIdParam) ?? null}
                            onChange={handleMethodInputChange}
                            disabled={isMethodDisabled}
                            sx={isMethodDisabled ? {cursor: 'not-allowed', '& *': {pointerEvents: 'none'}} : undefined}
                            renderInput={(params) => (
                                <TextField {...params} label="Method" placeholder="Search methods..."/>
                            )}
                        />
                    </Grid>
                </Grid>

                <Card sx={{width: '100%'}}>
                    <Box sx={{height: `${preparedTimeline.cardHeight}px`, width: '100%', position: 'relative', overflow: 'hidden'}}>
                        {chartOptions ? (
                            <Echart ref={chartRef} option={chartOptions} onEvents={{click: handleChartClick}}
                                    notMerge={true}
                                    autoResize={true} height={preparedTimeline.cardHeight}
                                    onChartReady={dispatchZoom}/>
                        ) : (
                            <Box sx={{p: 2, textAlign: 'center', color: 'text.secondary'}}>
                                Loading chart data...
                            </Box>
                        )}
                    </Box>
                </Card>
            </Stack>
        </Box>
    );
};
export default ThreadsPage;
