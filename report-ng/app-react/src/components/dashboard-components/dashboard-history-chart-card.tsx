import EChart from "../../widgets/echart/echart";
import ReportCard from "../../widgets/report-card/report-card";

interface DashboardHistoryChartProps {
    className: string;
}

const DashboardHistoryChartCard = ({className}: DashboardHistoryChartProps) => {

    const option = {
        grid: {
            top: '3%',
            left: '3%',
            right: '3%',
            bottom: '3%',
            containLabel: true
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
        <ReportCard label="Test Classes" sx={{p: 0}} className={className}>
            <EChart option={option}/>
        </ReportCard>
    );
};
export default DashboardHistoryChartCard;
