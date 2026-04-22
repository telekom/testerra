import {useTheme} from "@mui/material";
import Echart from "../../widgets/Echart";
import React from "react";
import ReportCard from "../../widgets/ReportCard";

const DashboardClassesChartCard: React.FC = () => {
    const theme = useTheme();

    const createSeries = (name: string, color: string, data: number[]) => ({
        name: name,
        type: 'bar',
        barMaxWidth: 40,
        stack: 'total',
        label: {show: true},
        emphasis: {focus: 'series'},
        itemStyle: {color},
        data,
    });

    const option = {
        tooltip: {
            trigger: 'item',
            axisPointer: {type: 'shadow'},
            formatter: '{a}: {c}',
        },
        xAxis: {
            type: 'value',
        },
        yAxis: {
            type: 'category',
            data: ['SimpleTest2'],
            max: 1,

        },
        series: [
            createSeries('Failed', theme.custom.statusColors.failed, [1]),
            createSeries('Expected Failed', theme.custom.statusColors.expected_failed, [1]),
            createSeries('Skipped', theme.custom.statusColors.skipped, [1]),
            createSeries('Passed', theme.custom.statusColors.passed, [3]),
        ],
    };

    return (
        <ReportCard label="Test Classes" sxContent={{p: 0}}>
            <Echart option={option} height={100}/>
        </ReportCard>
    );
};
export default DashboardClassesChartCard;
