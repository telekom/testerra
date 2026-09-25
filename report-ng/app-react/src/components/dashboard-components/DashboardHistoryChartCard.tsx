import Echart from "../../widgets/Echart";
import ReportCard from "../../widgets/ReportCard";
import type {SxProps, Theme} from "@mui/material/styles";
import {StatusService} from "../../model/status-service.tsx";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";

interface DashboardHistoryChartProps {
    histStatistics: HistoryStatistics
    selectedStatus: string | null;
    sx?: SxProps<Theme>
}

const DashboardHistoryChartCard = ({histStatistics, selectedStatus, sx}: DashboardHistoryChartProps) => {

    const statuses = StatusService.getRelevantStatuses()
        .filter(status => !selectedStatus || StatusService.getLabel(status) === selectedStatus);
    const totalRuns = histStatistics.getTotalRunCount();


    const option = totalRuns > 1 ? undefined : placeHolderOptions;



    const placeHolderOptions = {
        grid: {
            top: '3%',
            left: '3%',
            right: '3%',
            bottom: '3%',
            outerBoundsMode: 'same',
            outerBoundsContain: 'axisLabel'
        },
        xAxis: [
            {
                type: 'category',
                axisLabel: {
                    show: false
                },
                boundaryGap: false,
                splitLine: {
                    show: true
                }
            }
        ],
        yAxis: [
            {
                type: 'value',
                axisLine: {
                    show: true
                },
                axisLabel: {
                    show: false
                },
                splitLine: {
                    show: false
                }
            }
        ],
        series: [{
            data: [1000, 1100, 1100, 1200, 1290, 1330, 1320],
            type: 'line',
            areaStyle: {
                color: 'rgba(20,20,20,0.05)'
            },
            lineStyle: {
                color: 'rgba(255,255,255,0)',
                width: 0
            },
            silent: true,
            symbol: 'none',
            emphasis: {
                focus: 'none'
            },
            tooltip: {
                show: false
            }
        }],
        graphic: {
            type: 'text',
            left: 'center',
            top: 'center',
            silent: true,
            z: 10,
            style: {
                text: 'No history available',
                font: '28px Roboto',
                fill: '#55555'
            }
        }
    };

    return (
        <ReportCard
            label="History"
            sxContent={{p: 0}}
            sxCard={sx}
            content={<Echart option={option} autoResize={true}/>}
        />
    );
};
export default DashboardHistoryChartCard;
