import {useTheme} from "@mui/material";
import EChart from "../../widgets/echart/echart";
import ReportCard from "../../widgets/report-card/report-card";

interface DashboardPieChartProps {
    className: string;
}

const DashboardPieChartCard = ({className}: DashboardPieChartProps) => {
    const theme = useTheme();

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
                data: [
                    {value: 3, name: 'Passed', itemStyle: {color: theme.custom.statusColors.passed}},
                    {value: 1, name: 'Failed', itemStyle: {color: theme.custom.statusColors.failed}},
                    {value: 1, name: 'Expected Failed', itemStyle: {color: theme.custom.statusColors.expected_failed}},
                    {value: 1, name: 'Skipped', itemStyle: {color: theme.custom.statusColors.skipped}},
                ]
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
