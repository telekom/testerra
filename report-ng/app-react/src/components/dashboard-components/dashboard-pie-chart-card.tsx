import EChart from "../../widgets/echart/echart";
import ReportCard from "../../widgets/report-card/report-card";
import {StatusConverter} from "../../model/status-converter";

interface DashboardPieChartProps {
    className: string;
    execStatistics: any;
}

const DashboardPieChartCard = ({className, execStatistics}: DashboardPieChartProps) => {

    let data = []
    for (const status of StatusConverter.relevantStatuses) {
        const statusGroup = StatusConverter.groupStatus(status);
        const dataItem = {
            value: execStatistics.getSummarizedStatusCount(statusGroup),
            name: StatusConverter.getLabelForStatus(status),
            itemStyle: {
                color: StatusConverter.getColorForStatus(status)
            }
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
                data: data
            }
        ]
    };

    return (
        <ReportCard label="Breakdown" className={className}>
            <EChart option={option}/>
        </ReportCard>
    );
};
export default DashboardPieChartCard;
