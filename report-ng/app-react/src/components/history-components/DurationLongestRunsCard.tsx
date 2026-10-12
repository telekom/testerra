import {useMemo} from "react";
import {List, ListItem, ListItemIcon, ListItemText, Stack, Typography} from "@mui/material";
import TimelapseIcon from "@mui/icons-material/Timelapse";
import {useTheme, type SxProps, type Theme} from "@mui/material/styles";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import {ResultStatusType} from "../../model/report-model/framework_pb.ts";
import {formatDuration} from "../../utils/durationFormatter.ts";
import ReportCard from "../../widgets/ReportCard.tsx";
import type {TestRunViewport} from "./TestRunChart.tsx";

interface LongestRunsCardProps {
    histStatistics: HistoryStatistics;
    viewport?: TestRunViewport;
    sx?: SxProps<Theme>;
}

const DurationLongestRunsCard = ({histStatistics, viewport, sx}: LongestRunsCardProps) => {
    const theme = useTheme();
    const longestRuns = useMemo(() => histStatistics.getHistoryAggregateStatistics()
        .filter(entry => !viewport || (entry.historyIndex >= viewport.start && entry.historyIndex <= viewport.end))
        .flatMap(entry => {
            const context = entry.historyAggregate.executionContext?.contextValues;
            if (context?.startTime === undefined || context.endTime === undefined) {
                return [];
            }
            return [{
                runIndex: entry.historyIndex,
                duration: context.endTime - context.startTime,
                testCases: entry.overallPassed
                    + entry.getStatusCount(ResultStatusType.FAILED)
                    + entry.getStatusCount(ResultStatusType.FAILED_EXPECTED)
                    + entry.getStatusCount(ResultStatusType.SKIPPED),
            }];
        })
        .sort((a, b) => b.duration - a.duration)
        .slice(0, 3), [histStatistics, viewport]);

    return (
        <ReportCard
            label="Longest runs"
            sxCard={sx}
            sxContent={{p: 0}}
            content={longestRuns.length > 0 ? (
                <List dense sx={{py: 0}}>
                    {longestRuns.map(run => (
                        <ListItem key={run.runIndex}>
                            <ListItemIcon>
                                <TimelapseIcon sx={{color: theme.custom.chart.blue.main}}/>
                            </ListItemIcon>
                            <ListItemText
                                primary={<Typography>Run {run.runIndex} ({run.testCases} test cases)</Typography>}
                                secondary={formatDuration(run.duration)}
                            />
                        </ListItem>
                    ))}
                </List>
            ) : (
                <Stack sx={{height: "100%", alignItems: "center", justifyContent: "center"}}>
                    <Typography>No runs available</Typography>
                </Stack>
            )}
        />
    );
};

export default DurationLongestRunsCard;
