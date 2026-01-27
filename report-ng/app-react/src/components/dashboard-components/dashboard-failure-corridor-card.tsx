import {Typography, useTheme} from "@mui/material";
import ReportCard from "../../widgets/report-card/report-card";
import Stack from '@mui/material/Stack';
import Chip from '@mui/material/Chip';

interface DashboardFailureCorridorProps {
    className: string;
}

const DashboardFailureCorridorCard = ({className}: DashboardFailureCorridorProps) => {
    const theme = useTheme();

    const chipList = [
        {label: "1 High", chipColor: theme.custom.statusColors.failed, textColor: "white", total: 0},
        {label: "0 Mid", chipColor: theme.custom.statusColors.passed, textColor: "white", total: 0},
        {label: "0 Low", chipColor: theme.custom.statusColors.passed, textColor: "white", total: 0}
    ]

    return (
        <ReportCard label="Failure Corridor" sx={{":last-child": {padding: 2}}} className={className}>
            <Stack direction="column" spacing={1} sx={{alignItems: "center"}}>
                {chipList.map((chip) => (
                    <Stack direction="row" key={chip.label} spacing={1} sx={{alignItems: "center"}}>
                        <Chip label={chip.label} sx={{background: chip.chipColor, color: chip.textColor}}/>
                        <Typography color="primary"> of {chip.total} </Typography>
                    </Stack>
                ))}
            </Stack>
        </ReportCard>
    );
};
export default DashboardFailureCorridorCard;
