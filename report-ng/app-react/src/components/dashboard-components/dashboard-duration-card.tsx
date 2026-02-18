import {Grid, Typography} from "@mui/material";
import ReportCard from "../../widgets/report-card/report-card";
import Stack from "@mui/material/Stack";
import Divider from "@mui/material/Divider";
import TimerIcon from '@mui/icons-material/Timer';
import type {SxProps, Theme} from "@mui/material/styles";

interface DashboardDurationProps {
    sx?: SxProps<Theme>;
}

const DashboardDurationCard = ({sx}: DashboardDurationProps) => {
    return (
        <ReportCard label="Top 3 Failure Aspects" sxContent={{p: 0, ":last-child": {padding: 0}}} sxCard={sx}>
            <Stack direction="column" spacing={2}
                   divider={<Divider orientation="horizontal" sx={{mt: "0 !important" as any}}/>}>
                <Stack direction="row" spacing={1} sx={{alignItems: "center", justifyContent: "center", p: 2}}>
                    <TimerIcon/>
                    <Typography variant="h5">4mins 49s 45ms</Typography>
                </Stack>
                <Grid container sx={{px: 2, py: 1, mt: "0 !important" as any}}>
                    <Grid size={3}>
                        <Typography variant="caption" color="primary">Started</Typography>
                    </Grid>
                    <Grid size={9}>
                        <Typography variant="caption">Mon, 6 Jan 2025, 10:37:40</Typography>
                    </Grid>
                    <Grid size={3}>
                        <Typography variant="caption" color="primary">Ended</Typography>
                    </Grid>
                    <Grid size={9}>
                        <Typography variant="caption">Mon, 6 Jan 2025, 10:42:29</Typography>
                    </Grid>

                </Grid>
            </Stack>
        </ReportCard>
    );
};
export default DashboardDurationCard;
