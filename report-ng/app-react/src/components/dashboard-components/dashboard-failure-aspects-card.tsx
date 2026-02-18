import {useTheme} from "@mui/material";
import ReportCard from "../../widgets/report-card/report-card";
import ButtonList from "../../widgets/button-list/button-list";
import CancelIcon from "@mui/icons-material/Cancel";
import Link from '@mui/material/Link';
import type {SxProps, Theme} from "@mui/material/styles";

interface DashboardFailureAspectsProps {
    sx?: SxProps<Theme>
}

const DashboardFailureAspectsCard = ({sx}: DashboardFailureAspectsProps) => {
    const theme = useTheme();

    const failureAspectsLabel = (
        <>
            Top 3 Failure Aspects (
            <Link href="#/Tests">Major</Link>
            {" | "}
            <Link href="#/Tests">Minor</Link>
            )
        </>
    );

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
        <ReportCard label={failureAspectsLabel} sxContent={{p: 0, ":last-child": {padding: 0}}}
                    tooltipText="The most critical errors that caused the highest number of failed test cases"
                    sxCard={sx}>
            <ButtonList list={itemList} disablePadding={true}/>
        </ReportCard>
    );
};
export default DashboardFailureAspectsCard;
