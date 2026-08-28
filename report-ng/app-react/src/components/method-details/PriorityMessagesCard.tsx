import Box from "@mui/material/Box";
import {List, ListItem, Typography, useTheme} from "@mui/material";
import ReportCard from "../../widgets/ReportCard.tsx";
import {LogMessage, LogMessageType, ResultStatusType} from "../../model/report-model/framework_pb.ts";
import {StatusService} from "../../model/status-service.tsx";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";

interface PriorityMessagesCardProps {
    promptLogs: LogMessage[];
}

const PriorityMessagesCard = ({promptLogs}: PriorityMessagesCardProps) => {
    const theme = useTheme();

    if (promptLogs.length === 0) {
        return null;
    }

    return (
        <Box sx={theme.custom.priorityMessages.container}>
            <ReportCard
                label="Priority messages"
                sxContent={theme.custom.priorityMessages.content}
                content={(
                    <List sx={theme.custom.priorityMessages.list}>
                        {promptLogs.map((logMessage, index) => {
                            const isError = logMessage.type === LogMessageType.LMT_ERROR;
                            const isWarn = logMessage.type === LogMessageType.LMT_WARN;
                            const backgroundColor = isError
                                ? StatusService.getColor(ResultStatusType.FAILED)
                                : isWarn
                                    ? StatusService.getColor(ResultStatusType.SKIPPED)
                                    : "transparent";
                            return (
                                <ListItem
                                    key={`priority-log-${index}`}
                                    sx={theme.custom.priorityMessages.listItem(backgroundColor)}
                                >
                                    <Typography variant="caption" component="span">
                                        {classNameConverter(logMessage.loggerName ?? "", ClassName.simpleName) ?? "Logger"}:
                                    </Typography>{" "}
                                    <Typography variant="caption" component="span">{logMessage.message ?? ""}</Typography>
                                </ListItem>
                            );
                        })}
                    </List>
                )}
            />
        </Box>
    );
};

export default PriorityMessagesCard;
