import Box from "@mui/material/Box";
import {List, ListItem, Typography} from "@mui/material";
import ReportCard from "../../widgets/ReportCard.tsx";
import {LogMessage, LogMessageType, ResultStatusType} from "../../model/report-model/framework_pb.ts";
import {StatusService} from "../../model/status-service.tsx";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";

interface PriorityMessagesCardProps {
    promptLogs: LogMessage[];
}

const PriorityMessagesCard = ({promptLogs}: PriorityMessagesCardProps) => {
    if (promptLogs.length === 0) {
        return null;
    }

    return (
        <Box sx={{mt: 3}}>
            <ReportCard
                label="Priority messages"
                sxContent={{p: 0, ":last-child": {p: 0}, color: "white"}}
                content={(
                    <List sx={{p: 0}}>
                        {promptLogs.map((logMessage, index) => {
                            const isError = logMessage.type === LogMessageType.LMT_ERROR;
                            const isWarn = logMessage.type === LogMessageType.LMT_WARN;
                            return (
                                <ListItem
                                    key={`priority-log-${index}`}
                                    sx={{
                                        display: "block", px: 1.5, py: 0.5, lineHeight: 1,
                                        backgroundColor: isError ? StatusService.getColor(ResultStatusType.FAILED) :
                                            isWarn ? StatusService.getColor(ResultStatusType.SKIPPED) : "transparent",
                                    }}>
                                    <Typography variant="caption" component="span">
                                        {classNameConverter(logMessage.loggerName ?? "", ClassName.simpleName) ?? "Logger"}:
                                    </Typography>{" "}
                                    <Typography
                                        variant="caption"
                                        component="span"
                                        dangerouslySetInnerHTML={{__html: logMessage.message ?? ""}}
                                    />
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
