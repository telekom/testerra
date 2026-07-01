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

import {Box, Paper, Typography} from "@mui/material";
import React, {useEffect, useMemo} from "react";
import type {ILogEntry} from "../model/Logs";
import {LogLine} from "./LogLine";

// use react window library (https://react-window.vercel.app/) according to MUI recommendation (https://mui.com/material-ui/react-list/#virtualized-list)
import {List, type RowComponentProps, useDynamicRowHeight, useListRef,} from "react-window";

export interface LogConsoleProps {
    logs: ILogEntry[];
    searchText?: string | null;
    height?: number | string;
    activeLogIndex?: number;
}

export const LogConsole: React.FC<LogConsoleProps> = ({logs, searchText, height = "calc(100dvh - 200px)", activeLogIndex = -1,}) => {
    if (logs.length === 0) {
        return (
            <Paper sx={{p: 1, bgcolor: "#2b2b2b", color: "#c0dee0", fontSize: 14}}>
                <Typography>(No log messages matching this criteria)</Typography>
            </Paper>
        )
    }

    // calculate height
    const listHeight = useMemo(() => {
        if (typeof height === "number") return height;
        return window.innerHeight - 200;
    }, [height]);

    // dynamic row height
    const rowHeight = useDynamicRowHeight({
        defaultRowHeight: 21, // height of default log line (1 row)
    });

    // Ref for imperative API (scrollToRow)
    const listRef = useListRef(null);

    // row-component for react-window
    const Row = ({index, style,}: RowComponentProps<{}>) => {
        const log = logs[index];

        return (
            <div style={style}>
                <LogLine
                    log={log}
                    searchText={searchText ?? undefined}
                    isActiveMatch={index === activeLogIndex}
                />
            </div>
        );
    };

    // scroll to active log if activeLogIndex changes
    useEffect(() => {
        if (activeLogIndex < 0) return;
        const list = listRef.current;
        if (!list) return;

        list.scrollToRow({
            index: activeLogIndex,
            align: "start",
            behavior: "auto",
        });
    }, [activeLogIndex, listRef]);

    return (
        <Paper sx={{p: 1, bgcolor: "#2b2b2b", color: "#c0dee0", fontSize: 14}}>
            <Box sx={{maxHeight: listHeight}}>
                <List
                    listRef={listRef}
                    rowComponent={Row}
                    rowCount={logs.length}
                    rowHeight={rowHeight}
                    rowProps={{}}
                    style={{height: listHeight, width: "100%"}}
                />
            </Box>
        </Paper>
    );
};
