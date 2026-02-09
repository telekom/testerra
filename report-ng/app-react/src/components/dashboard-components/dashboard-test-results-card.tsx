import ReportCard from "../../widgets/report-card/report-card";
import ButtonList from "../../widgets/button-list/button-list";
import {ResultStatusType} from "../../model/report-model/framework_pb";
import {StatusConverter} from "../../model/status-converter";

interface DashboardTestResultsProps {
    className: string;
    execStatistics: any;
    onListItemClick: (newItem: string) => void;
    selectedStatus: string | null;
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
                parts.push(`⊃ ${repairedCount} ${StatusConverter.getLabelForStatus(ResultStatusType.REPAIRED)}`);
            }
            if (recoveredCount > 0) {
                parts.push(`⊃ ${recoveredCount} ${StatusConverter.getLabelForStatus(ResultStatusType.PASSED_RETRY)}`);
            }
            return parts.join(" ");
        }

        default:
            return "";
    }
};

const DashboardTestResultsCard = ({className, execStatistics, onListItemClick, selectedStatus}: DashboardTestResultsProps) => {

    const itemList = StatusConverter.relevantStatuses
        .filter((status) => execStatistics.getStatusCount(status) > 0)
        .map((status) => ({
            key: StatusConverter.getLabelForStatus(status),
            primaryText: execStatistics.getStatusCount(status) + " " + StatusConverter.getLabelForStatus(status),
            secondaryText: getSecondaryStatusLabel(status, execStatistics),
            icon: StatusConverter.getIconForStatus(status),
            selected: selectedStatus === StatusConverter.getLabelForStatus(status)
        }));

    const label = "Tests: " + execStatistics.overallTestCases

    // from "12 Passed" to Passed
    const handleClick = (buttonListLabel: string) => {
        const clickedItem = buttonListLabel.split(" ").slice(1).join(" ");
        onListItemClick(clickedItem);
    }

    return (
        <ReportCard label={label} sx={{p: 0, ":last-child": {padding: 0}}} className={className}>
            <ButtonList list={itemList} handleClick={handleClick}/>
        </ReportCard>
    );
};
export default DashboardTestResultsCard;
