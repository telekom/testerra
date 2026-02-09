import EChart from "../../widgets/echart/echart";
import ReportCard from "../../widgets/report-card/report-card";
import {StatusConverter} from "../../model/status-converter";

interface DashboardPieChartProps {
    className: string;
    execStatistics: any;
    onChartPieceClick: (newPiece: string) => void;
    selectedStatus: string | null;
}

const DashboardPieChartCard = ({className, execStatistics, onChartPieceClick, selectedStatus}: DashboardPieChartProps) => {

    let data = []
    for (const status of StatusConverter.relevantStatuses) {
        const statusGroup = StatusConverter.groupStatus(status);
        const dataItem = {
            value: execStatistics.getSummarizedStatusCount(statusGroup),
            name: StatusConverter.getLabelForStatus(status),
            itemStyle: {
                color: StatusConverter.getColorForStatus(status)
            },
            selected: selectedStatus === StatusConverter.getLabelForStatus(status)  // highlighting if selected
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
        <ReportCard label="Breakdown" className={className}>
            <EChart option={option} onEvents={onEvents} notMerge={true}/>
        </ReportCard>
    );
};
export default DashboardPieChartCard;
