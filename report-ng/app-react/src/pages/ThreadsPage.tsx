import Box from "@mui/material/Box";
import {Card, CircularProgress, FormControl, InputLabel, MenuItem, Select, Stack, TextField, Autocomplete} from "@mui/material";
import {useSearchParams, useNavigate} from "react-router-dom";
import {useEffect, useState, useRef} from "react";
import EChartsReact from 'echarts-for-react';
import type { EChartsOption } from 'echarts';
import * as echarts from 'echarts';
import {useReportData} from '../provider/DataProvider';
import {dateFormatter} from '../utils/dateFormatter';
import type {
    MethodInfo,
    ChartDataPoint,
    StatusType,
    ClassStat
} from './ThreadsPage.types';
import {STATUS_COLORS, STATUS_NAMES} from './ThreadsPage.types';
import {ClassName, classNameConverter} from "../utils/classNameConverter.ts";

interface ExecutionStatistics {
    classStatistics: ClassStat[];
    [key: string]: any;
}

interface ExecutionAggregate {
    methodContexts?: Record<string, any>;
    classContexts?: Record<string, any>;
    [key: string]: any;
}

interface ExecutionManager {
    getExecutionStatistics(): ExecutionStatistics;
    getExecutionAggregate(): ExecutionAggregate;
    getMethodDetails(methodId: string): { identifier: string } | undefined;
}

const ThreadsPage = () => {
    const [searchParams, setSearchParams] = useSearchParams();
    const navigate = useNavigate();
    
    const methodIdParam = searchParams.get("methodId");
    const statusParam = searchParams.get("status");
    const classParam = searchParams.get("class");

    const {executionMngr, isLoading: dataLoading} = useReportData();

    // State with proper types
    const [loading, setLoading] = useState<boolean>(true);
    const [selectedStatus, setSelectedStatus] = useState<number | null>(null);
    const [selectedClass, setSelectedClass] = useState<string | null>(null);
    const [chartOptions, setChartOptions] = useState<EChartsOption | null>(null);
    const [cardHeight, setCardHeight] = useState<number>(600);
    const [methodLookup, setMethodLookup] = useState<MethodInfo[]>([]);

    const chartRef = useRef<EChartsReact | null>(null);

    // Constants
    const GAP_FROM_BORDER_TO_START = 400;
    const THREAD_HEIGHT = 50;
    const SLIDER_SPACING_FROM_CHART = 90;
    const OPACITY_OF_INACTIVE_ELEMENTS = 0.38;
    const AVAILABLE_STATUSES: StatusType[] = [1, 2, 3, 4];

    const getColorForStatus = (status: StatusType): string => {
        return STATUS_COLORS[status] || '#BDBDBD';
    };

    const prepareTimeline = async (manager: ExecutionManager): Promise<void> => {
        if (!manager) return;
        
        try {
            const data: ChartDataPoint[] = [];
            const startTimes: number[] = [];
            const threadCategories = new Map<string, any[]>();
            const stats = manager.getExecutionStatistics();
            const agg = manager.getExecutionAggregate();

            const methodContexts = agg.methodContexts || {};
            console.log('Total method contexts:', Object.keys(methodContexts).length);

            // Collect all threads and methods
            Object.entries(methodContexts).forEach(([, methodContext]: [string, any]) => {
                const threadName = methodContext.threadName || 'Unknown';
                const startTime = methodContext.contextValues?.startTime;
                
                if (!threadCategories.has(threadName)) {
                    threadCategories.set(threadName, []);
                }
                threadCategories.get(threadName)!.push(methodContext);
                
                if (typeof startTime === 'number' && startTime > 0) {
                    startTimes.push(startTime);
                }
            });

            console.log('Thread categories:', threadCategories.size, 'Total start times:', startTimes.length);

            if (startTimes.length === 0) {
                console.error('No valid start times found');
                setChartOptions(null);
                return;
            }

            const chartStartTime = Math.min(...startTimes) - GAP_FROM_BORDER_TO_START;

            // Build data for custom series
            for (const [threadName, methodContextsList] of threadCategories.entries()) {
                for (const context of methodContextsList) {
                    const startTime = context.contextValues?.startTime || 0;
                    const endTime = context.contextValues?.endTime || 0;
                    const duration = endTime - startTime;
                    
                    let classId = '';
                    const classStats = stats.classStatistics;
                    if (classStats) {
                        for (const classStat of classStats) {
                            const classContextIds = (classStat.methodContexts || [])
                                .map((con: any) => con.classContextId)
                                .filter((value: string, index: number, self: string[]) => self.indexOf(value) === index);
                            if (classContextIds.includes(context.classContextId)) {
                                classId = classStat.classIdentifier;
                                break;
                            }
                        }
                    }

                    const itemColor = getColorForStatus(context.resultStatus as StatusType);
                    const methodDetails = manager.getMethodDetails(context.id);
                    
                    data.push({
                        name: methodDetails?.identifier || context.contextValues?.name || 'Unknown',
                        value: [
                            threadName,
                            startTime,
                            endTime,
                            context.contextValues?.name || '',
                            duration,
                            context.methodRunIndex,
                            context.id,
                            context.resultStatus,
                            classId
                        ],
                        itemStyle: {
                            color: itemColor,
                            opacity: 1
                        }
                    });
                }
            }

            console.log('Data items:', data.length, data.slice(0, 3));

            const threadNames = Array.from(threadCategories.keys());
            const gridHeight = Math.max(threadNames.length * THREAD_HEIGHT, 200);
            const sliderFromTop = gridHeight + SLIDER_SPACING_FROM_CHART;
            setCardHeight(sliderFromTop + 60);

            const longestThreadName = threadNames.reduce(
                (a, b) => a.length > b.length ? a : b,
                ''
            );
            let gridLeftValue = longestThreadName.length * 7;
            gridLeftValue = gridLeftValue > 100 ? gridLeftValue : 100;

            const options: EChartsOption = {
                tooltip: {
                    formatter: function (params: any) {
                        if (!params || !params.value) return '';
                        return '<div style="background-color: ' +
                            params.color + '; padding: 5px; color: white; margin: -10px;">' + params.name + ' (' + params.value[5] + ')</div>'
                            + '<br>Start time: ' + new Date(params.value[1]).toLocaleString()
                            + '<br>End time: ' + new Date(params.value[2]).toLocaleString()
                            + '<br>Duration: ' + Math.floor(params.value[4] / 1000) + 's'
                            + '<br>Class: ' + classNameConverter(params.value[8], ClassName.simpleName);
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
                    data: threadNames,
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
                        renderItem: function (params: any, api: any) {
                            const categoryIndex = api.value(0);
                            const start = api.coord([api.value(1), categoryIndex]);
                            const end = api.coord([api.value(2), categoryIndex]);
                            const height = api.size([0, 1])[1] * 0.7;

                            // Create rect shape
                            const rectShape = echarts.graphic.clipRectByRect(
                                {
                                    x: start[0],
                                    y: start[1] - height / 2,
                                    width: end[0] - start[0],
                                    height: height
                                },
                                {
                                    x: params.coordSys.x,
                                    y: params.coordSys.y,
                                    width: params.coordSys.width,
                                    height: params.coordSys.height
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

            setChartOptions(options);
            console.log('Chart options set successfully');
        } catch (error) {
            console.error('Error preparing timeline:', error);
        }
    };

    // Initialize data on mount
    useEffect(() => {
        if (!dataLoading && executionMngr) {
            const init = async (): Promise<void> => {
                await prepareTimeline(executionMngr);
                
                // Populate method lookup
                const methods: MethodInfo[] = [];
                const methodContexts = Object.values(
                    executionMngr.getExecutionAggregate().methodContexts || {}
                );
                for (const methodContext of methodContexts) {
                    methods.push({
                        id: (methodContext as any).contextValues.id,
                        name: (methodContext as any).contextValues.name + " (" + (methodContext as any).methodRunIndex + ")"
                    });
                }
                setMethodLookup(methods.sort((a, b) => a.name.localeCompare(b.name)));
                setLoading(false);
            };
            init();
        }
    }, [executionMngr, dataLoading]);

    const zoomInOnFilter = (filter: string | number, valueIndex: number): void => {
        if (!chartOptions) return;
        
        const startTimes: number[] = [];
        const endTimes: number[] = [];
        const newOptions = JSON.parse(JSON.stringify(chartOptions)) as EChartsOption;

        const seriesData = (newOptions.series as any[])?.[0]?.data as ChartDataPoint[];
        if (!seriesData) return;

        seriesData.forEach((value: ChartDataPoint) => {
            if (value.value[valueIndex] !== filter) {
                value.itemStyle.opacity = OPACITY_OF_INACTIVE_ELEMENTS;
            } else {
                startTimes.push(value.value[1]);
                endTimes.push(value.value[2]);
            }
        });

        setChartOptions(newOptions);

        if (startTimes.length > 0 && endTimes.length > 0) {
            const zoomStart = Math.min(...startTimes);
            const zoomEnd = Math.max(...endTimes);
            zoom(zoomStart, zoomEnd);
        }
    };

    const zoom = (zoomStart: number, zoomEnd: number): void => {
        const spacing = (zoomEnd - zoomStart) * 0.05;
        if (chartRef.current) {
            const echartsInstance = chartRef.current.getEchartsInstance();
            echartsInstance?.dispatchAction({
                type: 'dataZoom',
                id: 'threadZoom',
                startValue: zoomStart - spacing,
                endValue: zoomEnd + spacing
            });
        }
    };

    const resetZoom = (): void => {
        resetColor();
        setSearchParams({});

        if (chartRef.current) {
            const echartsInstance = chartRef.current.getEchartsInstance();
            echartsInstance?.dispatchAction({
                type: 'dataZoom',
                id: 'threadZoom',
                start: 0,
                end: 100
            });
        }
    };

    const resetColor = (): void => {
        if (!chartOptions) return;
        const newOptions = JSON.parse(JSON.stringify(chartOptions)) as EChartsOption;
        const seriesData = (newOptions.series as any[])?.[0]?.data as ChartDataPoint[];
        if (seriesData) {
            seriesData.forEach((value: ChartDataPoint) => {
                value.itemStyle.opacity = 1;
            });
            setChartOptions(newOptions);
        }
    };

    const handleChartClick = (params: any): void => {
        if (params.value && params.value[6]) {
            navigate(`/method?methodId=${params.value[6]}`);
        }
    };

    const handleStatusChange = (e: any): void => {
        const status = e.target.value as string | number;
        setSelectedStatus(status === '' ? null : (typeof status === 'string' ? parseInt(status) : status));
        resetColor();

        if (status && typeof status === 'number' && status > 0) {
            zoomInOnFilter(status, 7);
            setSearchParams({status: status.toString()});
        } else if (!classParam) {
            resetZoom();
        }
    };

    const handleClassChange = (e: any): void => {
        const className: string = e.target.value;
        setSelectedClass(className === '' ? null : className);
        resetColor();

        if (className) {
            zoomInOnFilter(className, 8);
            setSearchParams({class: className});
        } else if (!statusParam) {
            resetZoom();
        }
    };

    const handleMethodInputChange = (_e: any, value: MethodInfo | null): void => {
        if (value) {
            handleMethodSelect(value.id);
        }
    };

    const handleMethodSelect = (selectedMethodId: string): void => {
        setSelectedStatus(null);
        setSelectedClass(null);

        if (chartOptions) {
            zoomInOnFilter(selectedMethodId, 6);
            setSearchParams({methodId: selectedMethodId});
        }
    };

    const isStatusDisabled = !!(selectedClass || classParam || methodIdParam);
    const isClassDisabled = !!(selectedStatus || statusParam || methodIdParam);
    const isMethodDisabled = !!(selectedStatus || selectedClass || statusParam || classParam);

    if (dataLoading || loading) {
        return <CircularProgress />;
    }

    const classStatistics = executionMngr?.getExecutionStatistics()?.classStatistics || [];

    return (
        <Box sx={{width: '100%', p: '24px 32px'}}>
            <Stack spacing={2}>
                <Stack direction="row" spacing={2}>
                    <FormControl sx={{minWidth: 200}}>
                        <InputLabel>Status</InputLabel>
                        <Select<number>
                            value={selectedStatus || ''}
                            onChange={handleStatusChange}
                            label="Status"
                            disabled={isStatusDisabled}
                        >
                            <MenuItem value="">
                                (All)
                            </MenuItem>
                            {AVAILABLE_STATUSES.map((status: StatusType) => (
                                <MenuItem key={status} value={status}>
                                    {STATUS_NAMES[status]}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <FormControl sx={{minWidth: 250}}>
                        <InputLabel>Class</InputLabel>
                        <Select<string>
                            value={selectedClass || ''}
                            onChange={handleClassChange}
                            label="Class"
                            disabled={isClassDisabled}
                        >
                            <MenuItem value="">
                                (All)
                            </MenuItem>
                            {classStatistics.map(classStat => (
                                <MenuItem key={classStat.classIdentifier} value={classStat.classIdentifier}>
                                    {classStat.classIdentifier.split('.').pop()}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>

                    <Autocomplete
                        options={methodLookup}
                        getOptionLabel={(option: MethodInfo) => option.name}
                        onChange={handleMethodInputChange}
                        disabled={isMethodDisabled}
                        sx={{flex: 1}}
                        renderInput={(params) => (
                            <TextField {...params} label="Method" placeholder="Search methods..." />
                        )}
                    />
                </Stack>

                <Card sx={{width: '100%'}}>
                    <Box sx={{height: `${cardHeight}px`, width: '100%', position: 'relative', overflow: 'hidden'}}>
                        {chartOptions ? (
                            <EChartsReact
                                ref={chartRef}
                                option={chartOptions}
                                style={{width: '100%', height: '100%'}}
                                opts={{renderer: 'canvas'}}
                                onEvents={{click: handleChartClick}}
                            />
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
