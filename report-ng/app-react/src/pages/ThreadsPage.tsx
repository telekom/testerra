import Box from "@mui/material/Box";
import {
    Card,
    CircularProgress,
    Grid,
    Stack,
    TextField,
    Typography,
    Autocomplete
} from "@mui/material";
import {useNavigate} from "react-router-dom";
import {useMemo, useRef, useEffect, useCallback, type SyntheticEvent} from "react";
import type {
    EChartsOption,
    TooltipComponentFormatterCallbackParams,
    ECElementEvent,
    EChartsType,
    CustomSeriesOption
} from 'echarts';
import * as echarts from 'echarts';
import {useReportData} from '../provider/DataProvider';
import {dateFormatter} from '../utils/dateFormatter';
import {StatusService} from '../model/status-service';
import type {ResultStatus} from '../model/status-service';
import {ClassName, classNameConverter} from "../utils/classNameConverter.ts";
import type {MethodContext} from '../model/report-model/framework_pb';
import Echart, {type EchartRef} from "../widgets/Echart.tsx";
import InfoOutlineIcon from '@mui/icons-material/InfoOutline';
import {useChipListFilters} from "../hooks/useChipListFilters";
import type {FilterType} from "../hooks/useChipListFilters";
import StatusSelectInput from "../widgets/StatusSelectInput";
import MultiSelectInput from "../widgets/MultiSelectInput";
import SelectedFilterChips from "../components/SelectedFilterChips";
import {escapeHtml} from "../utils/escapeHtml";
import {reportTheme} from "../layout/reportTheme";

// Types
interface MethodInfo {
    id: string;
    name: string;
}

interface TimelineEntry {
    name: string;
    value: [string, number, number, string, number, number, string, number, string, number]; // [threadName, startTime, endTime, methodName, duration, runIndex, methodId, status, classId, opacity]
    itemStyle: {
        color: string;
        opacity: number;
    };
}

// list of filter types that are used for the thread list (+ part of FilterType)
const THREAD_FILTER_TYPES = ["status", "class", "method"] as const satisfies readonly FilterType[];

const ThreadsPage = () => {
    const navigate = useNavigate();

    // Status, class and method filters are shared with the test list and are read from / written to
    // the URL by the same hook, so filtering work identically on both pages.
    const {
        statusMenuItems,
        classMenuItems,
        filters,
        chips,
        setFilter,
        clearAll,
    } = useChipListFilters(THREAD_FILTER_TYPES);

    const {executionMngr, isLoading} = useReportData();

    const chartRef = useRef<EchartRef | null>(null);

    const methodLookup = useMemo((): MethodInfo[] => {
        if (!executionMngr) return [];
        const methodContexts = executionMngr.getExecutionAggregate().methodContexts || {};
        return Object.entries(methodContexts)
            // methods without a name would only be displayed by their id, so they are skipped
            .filter(([, methodContext]) => methodContext.contextValues?.name)
            .map(([methodId]) => ({
                id: methodId,
                name: (executionMngr?.getMethodDetails(String(methodId))?.identifier ?? methodId)
                    + " (" + executionMngr?.getMethodDetails(String(methodId))?.methodContext.methodRunIndex + ")",
            }))
            .sort((a, b) => a.name.localeCompare(b.name));
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

        const classIdentifierByContextId = new Map<string, string>();
        for (const classStatistic of executionMngr.getExecutionStatistics().classStatistics) {
            for (const methodContext of classStatistic.methodContexts) {
                const classContextId = methodContext.classContextId;
                if (classContextId !== undefined && !classIdentifierByContextId.has(classContextId)) {
                    classIdentifierByContextId.set(classContextId, classStatistic.classIdentifier);
                }
            }
        }

        // Build data for custom series
        for (const [threadName, methodContextsList] of threadCategories.entries()) {
            for (const methodContext of methodContextsList) {

                if (!methodContext.contextValues?.id || !methodContext.contextValues?.endTime || !methodContext.contextValues?.startTime || !methodContext.methodRunIndex || !methodContext.resultStatus) continue;

                const methodDetails = executionMngr.getMethodDetails(methodContext.contextValues.id);
                const itemColor = StatusService.getColor(methodContext.resultStatus!);
                const duration = methodContext.contextValues?.endTime - methodContext.contextValues?.startTime;
                const classId = methodContext.classContextId === undefined
                    ? undefined
                    : classIdentifierByContextId.get(methodContext.classContextId);

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
                        classId ?? "",
                        1
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
                    const methodName = escapeHtml(String(params.name));
                    const className = escapeHtml(classNameConverter(value[8], ClassName.simpleName));
                    const statusInfo = StatusService.get(value[7] as ResultStatus);
                    const statusBadge = '<span style="background:' + statusInfo.color + ';color:#fff;padding:1px 6px;border-radius:20px;margin-right:4px;">' + escapeHtml(statusInfo.label) + '</span>';
                    return '<div style="background-color: ' +
                        reportTheme.palette.lightGrey.light + '; padding: 5px; margin: -10px; color: ' + reportTheme.palette.lightGrey.main + ';">' + statusBadge + '<span style="font-weight: 500;">' + methodName + '</span></div>'
                        + '<br><b>Start time:</b> ' + new Date(value[1]).toLocaleString()
                        + '<br><b>End time:</b> ' + new Date(value[2]).toLocaleString()
                        + '<br><b>Duration:</b> ' + Math.floor(value[4] / 1000) + 's'
                        + '<br><b>Class:</b> ' + className
                        + '<br><b>Run Index:</b> ' + value[5];
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
                        const visualColor = api.visual('color');
                        const rawOpacity = api.value(9);

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
                            style: {
                                fill: (typeof visualColor === 'string' || (typeof visualColor === 'object' && visualColor !== null))
                                    ? visualColor
                                    : undefined,
                                opacity: typeof rawOpacity === 'number' ? rawOpacity : 1
                            }
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


    // Single source of truth for the active filters, derived from URL params.
    // All three filters are multi-value (like in the test list) and are combined with AND.
    const activeFilters = useMemo(() => {
        const statuses = filters.status ?? [];
        const classes = filters.class ?? [];
        const methodIds = filters.method ?? [];
        return {
            statuses,
            classes,
            methodIds,
            isActive: statuses.length > 0 || classes.length > 0 || methodIds.length > 0
        };
    }, [filters.status, filters.class, filters.method]);

    // A timeline entry is highlighted when it matches every active filter.
    // Non-matching entries are never removed, they only get a reduced opacity.
    const matchesActiveFilters = useCallback((value: TimelineEntry['value']) => {
        if (activeFilters.methodIds.length > 0 && !activeFilters.methodIds.includes(value[6])) return false;
        if (activeFilters.statuses.length > 0 && !activeFilters.statuses.includes(value[7] as ResultStatus)) return false;
        return !(activeFilters.classes.length > 0 && !activeFilters.classes.includes(classNameConverter(value[8], ClassName.simpleName)));
    }, [activeFilters]);

    // Derives the final chart config from the cached threads timeline and the active filters.
    // When a filter is active, each entry gets full or reduced opacity — without mutating
    // the original preparedTimeline.options (immutable spread instead of forEach-mutation).
    const chartOptions = useMemo((): EChartsOption | null => {
        if (!preparedTimeline.options) return null;
        if (!activeFilters.isActive) return preparedTimeline.options;

        const originalSeries = (preparedTimeline.options.series as CustomSeriesOption[])[0];
        const seriesData = (originalSeries.data as TimelineEntry[]).map(entry => ({
            ...entry,
            value: [
                ...entry.value.slice(0, 9),
                matchesActiveFilters(entry.value) ? 1 : OPACITY_OF_INACTIVE_ELEMENTS
            ] as TimelineEntry["value"]
        }));

        // Spread preserves all ECharts config (axes, tooltip, renderItem fn, …),
        // only series.data is replaced with the opacity-adjusted copy.
        return {
            ...preparedTimeline.options,
            series: [{...originalSeries, data: seriesData}]
        };
    }, [preparedTimeline.options, activeFilters, matchesActiveFilters]);

    // The filters never remove entries from the chart, so an over-restrictive filter combination
    // would silently highlight nothing. This flag drives an explicit hint next to the filter chips.
    const hasNoHighlightedEntries = useMemo(() => {
        if (!activeFilters.isActive) return false;
        const seriesData = (preparedTimeline.options?.series as CustomSeriesOption[])?.[0]?.data as TimelineEntry[] | undefined;
        if (!seriesData) return false;
        return !seriesData.some(entry => matchesActiveFilters(entry.value));
    }, [preparedTimeline.options, activeFilters, matchesActiveFilters]);

    // Zooms the chart to the time range of the filtered entries.
    // Opacity changes are handled separately in chartOptions (immutable, reactive),
    // so this function only handles the zoom dispatching
    const dispatchZoom = useCallback((instance: EChartsType | undefined) => {
        if (!chartOptions) return;

        if (!activeFilters.isActive) {    // reset zoom to full range if no filter is active
            instance?.dispatchAction({type: 'dataZoom', id: 'threadZoom', start: 0, end: 100});
            return;
        }

        const seriesData = (chartOptions.series as echarts.EChartsOption[])?.[0]?.data as TimelineEntry[];
        if (!seriesData) return;

        // find matching entries to calculate their start and end timespan for the zoom
        const matching = seriesData.filter(entry => matchesActiveFilters(entry.value));
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
    }, [chartOptions, activeFilters, matchesActiveFilters]);

    // Re-zooms whenever the chart config changes (filter or data update).
    useEffect(() => {
        dispatchZoom(chartRef.current?.getEchartsInstance());
    }, [dispatchZoom]);

    const handleChartClick = (params: ECElementEvent): void => {
        if (params.value) {
            const value = params.value as TimelineEntry['value'];
            if (value[6]) navigate(`/method/${value[6]}`);
        }
    };

    const handleMethodInputChange = (_e: SyntheticEvent, value: MethodInfo[]): void => {
        setFilter("method", value.map(method => method.id));
    };

    const selectedMethods = useMemo(
        () => {
            const selectedIds = new Set(filters.method ?? []);
            return methodLookup.filter(method => selectedIds.has(method.id));
        },
        [methodLookup, filters.method]
    );

    if (isLoading) {
        return <CircularProgress/>;
    }

    return (
        <Box sx={{width: '100%', p: '24px 32px'}}>
            <Stack spacing={2}>
                <Grid container spacing={2}>
                    <Grid size={2}>
                        <StatusSelectInput label="Status"
                                           selectedStatuses={filters.status ?? []}
                                           onChange={(newStatuses) => setFilter("status", newStatuses)}
                                           menuItems={statusMenuItems}/>
                    </Grid>

                    <Grid size={3}>
                        <MultiSelectInput label="Class"
                                          values={filters.class ?? []}
                                          onChange={(newClasses) => setFilter("class", newClasses)}
                                          menuItems={classMenuItems}
                                          renderValue={(selected: string[]) => {
                                              if (!selected?.length) return "";
                                              if (selected.length === 1) return "1 class selected";
                                              return `${selected.length} classes selected`;
                                          }}/>
                    </Grid>

                    <Grid size="grow">
                        <Autocomplete
                            multiple
                            disableCloseOnSelect
                            disableClearable
                            filterSelectedOptions
                            options={methodLookup}
                            noOptionsText={
                                methodLookup.length > 0 && selectedMethods.length === methodLookup.length
                                    ? <em>All methods are selected</em>
                                    : "No methods found"
                            }
                            slotProps={{
                                paper: {
                                    sx: {
                                        "& .MuiAutocomplete-noOptions": {color: "text.disabled"},
                                    },
                                },
                            }}
                            getOptionLabel={(option: MethodInfo) => option.name}
                            isOptionEqualToValue={(option, value) => option.id === value.id}
                            value={selectedMethods}
                            onChange={handleMethodInputChange}
                            // selected methods are only represented by the filter chips below,
                            // so the input itself always stays empty
                            renderValue={() => null}
                            renderInput={(params) => (
                                <TextField {...params} label="Method" placeholder="Search methods..."/>
                            )}
                        />
                    </Grid>

                    <Grid size={12} minHeight={36}>
                        <SelectedFilterChips chips={chips}
                                             handleClearAllClick={clearAll}/>
                    </Grid>
                </Grid>

                <Card sx={{width: '100%'}}>
                    <Box sx={{
                        height: `${preparedTimeline.cardHeight}px`,
                        width: '100%',
                        position: 'relative',
                        overflow: 'hidden'
                    }}>
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
                        {hasNoHighlightedEntries && (
                            <Stack
                                direction="row"
                                spacing={0.5}
                                sx={{
                                    position: "absolute",
                                    top: 12,
                                    right: 12,
                                    zIndex: 1400,
                                    px: "14px",
                                    py: "8px",
                                    borderRadius: "4px",
                                    backgroundColor: "rgba(0, 0, 0, 0.8)",
                                    color: "#fff",
                                    alignItems: "center",
                                    pointerEvents: "none",
                                }}
                            >
                                <InfoOutlineIcon sx={{fontSize: 14}}/>
                                <Typography sx={{fontSize: "13px"}}>
                                    No entries matching this criteria
                                </Typography>
                            </Stack>
                        )}
                    </Box>
                </Card>
            </Stack>
        </Box>
    );
};
export default ThreadsPage;
