import ReportCard from "../../widgets/report-card/report-card";
import ButtonList from "../../widgets/button-list/button-list";
import CancelIcon from "@mui/icons-material/Cancel";
import {useTheme} from "@mui/material";

interface DashboardTestResultsProps {
    className: string;
}

const DashboardTestResultsCard = ({className}: DashboardTestResultsProps) => {
    const theme = useTheme();

    const itemList = [
        { primaryText: "1", secondaryText: "Failed", icon: <CancelIcon sx={{color: theme.custom.statusColors.failed}}/>},
        { primaryText: "1", secondaryText: "Expected Failed", icon: <CancelIcon sx={{color: theme.custom.statusColors.expected_failed}}/>},
    ]

    return (
        <ReportCard label="Tests: 7" sx={{p: 0, ":last-child": {padding: 0}}} className={className}>
            <ButtonList list={itemList}/>
        </ReportCard>
    );
};
export default DashboardTestResultsCard;
