import {Typography, useTheme} from "@mui/material";
import ReportCard from "../../widgets/ReportCard";
import Stack from '@mui/material/Stack';
import Chip from '@mui/material/Chip';
import type {SxProps, Theme} from "@mui/material/styles";
import {FailureCorridorValue, type ExecutionContext} from "../../model/report-model/framework_pb";

interface DashboardFailureCorridorProps {
    executionContext?: ExecutionContext;
    sx?: SxProps<Theme>;
}

const DashboardFailureCorridorCard = ({executionContext, sx}: DashboardFailureCorridorProps) => {
    const theme = useTheme();

    const chipList = [
        {label: "High", value: FailureCorridorValue.FCV_HIGH},
        {label: "Mid", value: FailureCorridorValue.FCV_MID},
        {label: "Low", value: FailureCorridorValue.FCV_LOW}
    ].map(({label, value}) => {
        const count = executionContext?.failureCorridorCounts?.[value] ?? 0;
        const limit = executionContext?.failureCorridorLimits?.[value] ?? 0;

        return {
            label: `${count} ${label}`,
            chipColor: count <= limit
                ? theme.custom.statusColors.passed
                : theme.custom.statusColors.failed,
            limit
        };
    });

    return (
        <ReportCard
            label="Failure Corridor"
            sxContent={{":last-child": {padding: 2}}}
            tooltipText="The severity distribution of failed test cases in relation to the defined test goal"
            sxCard={sx}
            content={(
                <Stack direction="column" spacing={1} sx={{alignItems: "center"}}>
                    {chipList.map((chip) => (
                        <Stack direction="row" key={chip.label} spacing={1} sx={{alignItems: "center"}}>
                            <Chip label={chip.label} sx={{background: chip.chipColor, color: "white"}}/>
                            <Typography color="primary"> of {chip.limit} </Typography>
                        </Stack>
                    ))}
                </Stack>
            )}
        />
    );
};
export default DashboardFailureCorridorCard;
