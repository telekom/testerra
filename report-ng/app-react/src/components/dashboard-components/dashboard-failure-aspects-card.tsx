import {useTheme} from "@mui/material";
import ReportCard from "../../widgets/report-card/report-card";
import ButtonList from "../../widgets/button-list/button-list";
import CancelIcon from "@mui/icons-material/Cancel";

interface DashboardFailureAspectsProps {
    className: string;
}

const DashboardFailureAspectsCard = ({className}: DashboardFailureAspectsProps) => {
    const theme = useTheme();

    const itemList = [
        {
            primaryText: "AssertionError: Failing for reasons",
            icon: <CancelIcon sx={{color: theme.custom.statusColors.failed}}/>
        },
        {
            primaryText: "Throwable: Method eu.tsystems.mms.tic.testerra.plugins.teamcity.test.SimpleTest2.testT04_SkippedTest() on instance eu.tsystems.mms.tic.testerra.plugins.teamcity.test.SimpleTest2@6134ac4a depends on not successfully finished methods [eu.tsystems.mms.tic.testerra.plugins.teamcity.test.SimpleTest2.testT03_SimpleFailedTest() on instance eu.tsystems.mms.tic.testerra.plugins.teamcity.test.SimpleTest2@6134ac4a]",
            icon: <CancelIcon sx={{color: theme.custom.statusColors.skipped}}/>
        },
        {
            primaryText: "AssertionError: Failing for reasons...",
            icon: <CancelIcon sx={{color: theme.custom.statusColors.expected_failed}}/>
        },
    ]

    return (
        <ReportCard label="Top 3 Failure Aspects" sx={{p: 0, ":last-child": {padding: 0}}} className={className}>
            <ButtonList list={itemList} disablePadding={true}/>
        </ReportCard>
    );
};
export default DashboardFailureAspectsCard;
