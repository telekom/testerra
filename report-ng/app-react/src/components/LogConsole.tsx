/*
 * Testerra
 *
 * (C) 2026, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {Box, List, ListItem, Paper, Typography} from "@mui/material";
import type {ILogEntry} from "../model/Logs";
import {LogLine} from "./LogLine";

export interface LogConsoleProps {
    logs: ILogEntry[];
    searchText?: string | null;
    height?: number | string;
    autoScroll?: boolean;
}

export const LogConsole: React.FC<LogConsoleProps> = ({logs, searchText, height = "calc(100dvh - 200px)",}) => {
    if (logs.length === 0) {
        return (
            <Paper sx={{p: 1, bgcolor: "#2b2b2b", color: "#c0dee0", fontSize: 14}}>
                <Typography>(No log messages matching this criteria)</Typography>
            </Paper>
        )
    }

    return (
        <Paper sx={{p: 1, bgcolor: "#2b2b2b", color: "#c0dee0", fontSize: 14}}>
            <Box sx={{maxHeight: height, overflowY: "auto"}}>
                <List disablePadding>
                    {logs.map((log) => (
                        <ListItem
                            key={log.id ?? `${log.timestamp ?? 0}-${log.loggerName ?? ""}-${log.message ?? ""}`}
                            disableGutters
                            sx={{alignItems: "flex-start", py: 0}}
                        >
                            <LogLine log={log} searchText={searchText ?? undefined}/>
                        </ListItem>
                    ))}
                </List>
            </Box>
        </Paper>
    );
};
