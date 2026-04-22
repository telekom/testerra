import ReportCard from "../../widgets/ReportCard";
import ButtonList from "../../widgets/ButtonList";
import type {ButtonListItem} from "../../widgets/ButtonList";
import {ResultStatusType} from "../../model/report-model/framework_pb";
import {StatusService} from "../../model/status-service";
import type {SxProps, Theme} from "@mui/material/styles";

interface DashboardTestResultsProps {
    execStatistics: any;
    onListItemClick: (newItem: string) => void;
    selectedStatus: string | null;
    sx?: SxProps<Theme>
}

const getSecondaryStatusLabel = (
    status: ResultStatusType, execStatistics: any
) => {
    switch (status) {
        case ResultStatusType.FAILED:
            return " + " + execStatistics.getStatusCount(ResultStatusType.FAILED_RETRIED) + " Retried";

        case ResultStatusType.PASSED: {
            const repairedCount = execStatistics.getStatusCount(ResultStatusType.REPAIRED);
            const recoveredCount = execStatistics.getStatusCount(ResultStatusType.PASSED_RETRY);

            const parts: string[] = [];
            if (repairedCount > 0) {
                parts.push(`⊃ ${repairedCount} ${StatusService.getLabel(ResultStatusType.REPAIRED)}`);
            }
            if (recoveredCount > 0) {
                parts.push(`⊃ ${recoveredCount} ${StatusService.getLabel(ResultStatusType.PASSED_RETRY)}`);
            }
            return parts.join(" ");
        }

        default:
            return "";
    }
};

const DashboardTestResultsCard = ({execStatistics, onListItemClick, selectedStatus, sx}: DashboardTestResultsProps) => {

    const itemList = StatusService.getRelevantStatuses()
        .filter((status) => execStatistics.getStatusCount(status) > 0)
        .map((status) => ({
            key: StatusService.getLabel(status),
            primaryText: execStatistics.getStatusCount(status) + " " + StatusService.getLabel(status),
            secondaryText: getSecondaryStatusLabel(status, execStatistics),
            icon: StatusService.getIcon(status),
            selected: selectedStatus === StatusService.getLabel(status)
        }));

    const label = "Tests: " + execStatistics.overallTestCases

    // from "12 Passed" to Passed
    const handleClick = (buttonListItem: ButtonListItem) => {
        const clickedItem = buttonListItem.primaryText.split(" ").slice(1).join(" ");
        onListItemClick(clickedItem);
    }

    return (
        <ReportCard label={label} sxContent={{p: 0, ":last-child": {padding: 0}}} sxCard={sx}>
            <ButtonList list={itemList} handleClick={handleClick}/>
        </ReportCard>
    );
};
export default DashboardTestResultsCard;
