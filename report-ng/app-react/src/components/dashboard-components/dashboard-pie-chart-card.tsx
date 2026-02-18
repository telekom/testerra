import EChart from "../../widgets/echart/echart";
import ReportCard from "../../widgets/report-card/report-card";
import {StatusService} from "../../model/status-service";
import type {SxProps, Theme} from "@mui/material/styles";

interface DashboardPieChartProps {
    execStatistics: any;
    onChartPieceClick: (newPiece: string) => void;
    selectedStatus: string | null;
    sx?: SxProps<Theme>
}

const DashboardPieChartCard = ({execStatistics, onChartPieceClick, selectedStatus, sx}: DashboardPieChartProps) => {

    let data = []
    for (const status of StatusService.getRelevantStatuses()) {
        const statusGroup = StatusService.getGroup(status);
        const statusInformation = StatusService.get(status);
        if (!statusInformation) return null;

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
            formatter: '{b}: {c}'
        },
        label: {
            show: false
        },
        labelLine: {
            show: false
        },
        series: [
            {
                name: 'Access From',
                type: 'pie',
                radius: '50%',
                selectedMode: 'single', // how many items can be selected at once
                selectedOffset: 5,      // how far slice is away from rest of the pie
                data: data
            }
        ]
    };

    const onEvents = {
        click: (params: any) => {
            onChartPieceClick(params.data.name);
        }
    };

    return (
        <ReportCard label="Breakdown" sxCard={sx}>
            <EChart option={option} onEvents={onEvents} notMerge={true}/>
        </ReportCard>
    );
};
export default DashboardPieChartCard;
