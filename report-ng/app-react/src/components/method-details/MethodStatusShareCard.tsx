import {useMemo} from "react";
import type {SxProps, Theme} from "@mui/material/styles";
import type {MethodHistoryStatistics} from "../../model/MethodHistoryStatistics.ts";
import {ResultStatusType} from "../../model/report-model/framework_pb.ts";
import {StatusService} from "../../model/status-service.tsx";
import StatusShareCard from "../../widgets/StatusShareCard.tsx";

interface MethodStatusShareCardProps {
    methodHistoryStatistics: MethodHistoryStatistics;
    sx?: SxProps<Theme>;
}

const MethodStatusShareCard = ({methodHistoryStatistics, sx}: MethodStatusShareCardProps) => {
    const statusData = useMemo(() => StatusService.getRelevantStatuses()
        .map(status => ({
            status,
            value: methodHistoryStatistics.getSummarizedStatusCount(
                status === ResultStatusType.FAILED
                    ? [ResultStatusType.FAILED, ResultStatusType.FAILED_RETRIED]
                    : StatusService.getGroup(status)
            ),
        }))
        .filter(item => item.value > 0), [methodHistoryStatistics]);

    return (
        <StatusShareCard
            statusData={statusData}
            tooltipText="Displays the distribution of statuses across all executions of this method"
            sx={sx}
        />
    );
};

export default MethodStatusShareCard;
