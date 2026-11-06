/*
 * Testerra
 *
 * (C) 2026, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import Box from "@mui/material/Box";
import {List, ListItem, Typography, useTheme} from "@mui/material";
import type {SxProps, Theme} from "@mui/material/styles";
import ReportCard from "../../widgets/ReportCard.tsx";
import {LogMessage, LogMessageType, ResultStatusType} from "../../model/report-model/framework_pb.ts";
import {StatusService} from "../../model/status-service.tsx";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";

interface PriorityMessagesCardProps {
    promptLogs: LogMessage[];
    label?: string;
    sx?: SxProps<Theme>;
}

const PriorityMessagesCard = ({promptLogs, sx}: PriorityMessagesCardProps) => {
    const theme = useTheme();

    if (promptLogs.length === 0) {
        return null;
    }

    return (
        <Box sx={[theme.custom.priorityMessages.container, ...(Array.isArray(sx) ? sx : [sx])]}>
            <ReportCard
                label="Additional notes"
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
                                    sx={[
                                        theme.custom.priorityMessages.listItem(backgroundColor),
                                        logMessage.type !== LogMessageType.LMT_INFO &&
                                        theme.custom.priorityMessages.fontColorNotOk
                                    ]}
                                >
                                    <Typography variant="caption" component="span">
                                        {classNameConverter(logMessage.loggerName ?? "", ClassName.simpleName) ?? "Logger"}:
                                    </Typography>{" "}
                                    <Typography variant="caption"
                                                component="span">{logMessage.message ?? ""}</Typography>
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
