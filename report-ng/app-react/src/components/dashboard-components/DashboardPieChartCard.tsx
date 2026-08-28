import Echart from "../../widgets/Echart";
import ReportCard from "../../widgets/ReportCard";
import {StatusService} from "../../model/status-service";
import type {SxProps, Theme} from "@mui/material/styles";
import type {CallbackDataParams} from "echarts/types/dist/shared";
import Box from "@mui/material/Box";

interface DashboardPieChartProps {
    execStatistics: any;
    onChartPieceClick: (newPiece: string) => void;
    selectedStatus: string | null;
    sx?: SxProps<Theme>
    withCard?: boolean;
    // Fixed chart height, e.g. for the print preview where automatic resizing is not reliable
    chartHeight?: number;
}

const DashboardPieChartCard = ({execStatistics, onChartPieceClick, selectedStatus, sx, withCard = true, chartHeight}: DashboardPieChartProps) => {

    const data = []
    for (const status of StatusService.getRelevantStatuses()) {
        const statusGroup = StatusService.getGroup(status);
        const statusInformation = StatusService.get(status);

        const dataItem = {
            value: execStatistics.getSummarizedStatusCount(statusGroup),
            name: statusInformation.label,
            itemStyle: {
                color: statusInformation.color
            },
            selected: selectedStatus === statusInformation.label  // highlighting if selected
        }
        data.push(dataItem)
    }

    const option = {
        tooltip: {
            formatter: '<b>{b}:</b> {c}'
        },
        series: [
            {
                name: 'Tests',
                type: 'pie',
                radius: ['42%', '89%'],
                center: ['50%', '50%'],
                selectedMode: 'single', // how many items can be selected at once
                selectedOffset: 5,      // how far slice is away from rest of the pie
                data: data,
                label: {
                    formatter: (params: CallbackDataParams) => params.percent === 0 ? '' : `${params.percent}%`,
                    position: 'inside'
                }
            }
        ]
    };

    const onEvents = {
        click: (params: any) => {
            onChartPieceClick(params.data.name);
        }
    };

    const content =
        <Box sx={{ flex: 1, minHeight: 0 }}>
            <Echart
                option={option}
                onEvents={onEvents}
                notMerge={true}
                autoResize={!chartHeight}
                height={chartHeight ?? '100%'}
            /></Box>;

    if (!withCard) {
        return content;
    }

    return (
        <ReportCard
            label="Breakdown"
            sxCard={sx}
            sxContent={{p: 0, ":last-child": {padding: 0}}}
            content={content}
        />
    );
};
export default DashboardPieChartCard;
