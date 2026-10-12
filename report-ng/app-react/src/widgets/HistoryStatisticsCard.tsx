import type {SxProps, Theme} from "@mui/material/styles";
import {List, ListItem, ListItemIcon, ListItemText, Typography} from "@mui/material";
import BarChartIcon from "@mui/icons-material/BarChart";
import AvTimerIcon from "@mui/icons-material/AvTimer";
import PublishedWithChangesIcon from "@mui/icons-material/PublishedWithChanges";
import DoneAllIcon from "@mui/icons-material/DoneAll";
import FlakyIcon from "@mui/icons-material/Flaky";
import ReportCard from "./ReportCard.tsx";
import InfoTooltip from "./InfoTooltip.tsx";
import {formatDuration} from "../utils/durationFormatter.ts";

interface HistoryStatisticsCardProps {
    totalRunCount: number;
    averageRunDuration: number;
    recentChanges?: boolean;
    flakiness?: number;
    sx?: SxProps<Theme>;
}

const HistoryStatisticsCard = ({
    totalRunCount,
    averageRunDuration,
    recentChanges,
    flakiness,
    sx,
}: HistoryStatisticsCardProps) => (
    <ReportCard
        label="History statistics"
        sxContent={{p: 0}}
        sxCard={sx}
        content={(
            <List dense sx={{py: 0}}>
                <ListItem>
                    <ListItemIcon><BarChartIcon/></ListItemIcon>
                    <ListItemText
                        primary={<Typography>{totalRunCount}</Typography>}
                        secondary="Total runs"
                    />
                </ListItem>
                <ListItem>
                    <ListItemIcon><AvTimerIcon/></ListItemIcon>
                    <ListItemText
                        primary={<Typography>{formatDuration(averageRunDuration)}</Typography>}
                        secondary="Average run duration"
                    />
                </ListItem>
                {recentChanges !== undefined && (
                    <ListItem>
                        <ListItemIcon>
                            {recentChanges ? <PublishedWithChangesIcon/> : <DoneAllIcon/>}
                        </ListItemIcon>
                        <ListItemText
                            primary={<Typography>{recentChanges ? "Latest runs are not equal" : "No changes to previous run"}</Typography>}
                            secondary="Run comparison"
                        />
                    </ListItem>
                )}
                {flakiness !== undefined && (
                    <ListItem
                        secondaryAction={<InfoTooltip text="Flakiness indicates how often the status changed. Higher values suggest instability. Older runs contribute less to the final calculation."/>}
                    >
                        <ListItemIcon><FlakyIcon/></ListItemIcon>
                        <ListItemText
                            primary={<Typography>{flakiness.toFixed(1)}%</Typography>}
                            secondary="Flakiness"
                        />
                    </ListItem>
                )}
            </List>
        )}
    />
);

export default HistoryStatisticsCard;
