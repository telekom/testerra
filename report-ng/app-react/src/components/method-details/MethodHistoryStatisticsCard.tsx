import type {SxProps, Theme} from "@mui/material/styles";
import type {MethodHistoryStatistics} from "../../model/MethodHistoryStatistics.ts";
import HistoryStatisticsCard from "../../widgets/HistoryStatisticsCard.tsx";

interface MethodHistoryStatisticsCardProps {
    methodHistoryStatistics: MethodHistoryStatistics;
    sx?: SxProps<Theme>;
}

const MethodHistoryStatisticsCard = ({methodHistoryStatistics, sx}: MethodHistoryStatisticsCardProps) => (
    <HistoryStatisticsCard
        totalRunCount={methodHistoryStatistics.getRunCount()}
        averageRunDuration={methodHistoryStatistics.averageDuration}
        flakiness={methodHistoryStatistics.flakiness}
        sx={sx}
    />
);

export default MethodHistoryStatisticsCard;
