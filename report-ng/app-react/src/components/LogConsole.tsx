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

import {Box, Paper} from "@mui/material";
import React, {useCallback, useEffect, useMemo, useState} from "react";
import {useTheme} from "@mui/material/styles";
import type {ILogEntry} from "../model/Logs";
import {LogLine} from "./LogLine";

// use react window library (https://react-window.vercel.app/) according to MUI recommendation (https://mui.com/material-ui/react-list/#virtualized-list)
import {List, type RowComponentProps, useDynamicRowHeight, useListRef,} from "react-window";
import NoResultsCard from "../widgets/NoResultsCard.tsx";

interface LogConsoleRowProps {
    logs: ILogEntry[];
    searchText?: string | null;
    activeLogIndex: number;
    expandedLogIds: Record<string, true>;
    onToggleExpanded: (logKey: string) => void;
    isInStepsList: boolean;
}

// render function for row-component for react-window (virtualization)
function LogConsoleRow({
    index,
    style,
    logs,
    searchText,
    activeLogIndex,
    expandedLogIds,
    onToggleExpanded,
    isInStepsList,
}: RowComponentProps<LogConsoleRowProps>) {
    const log = logs[index];
    const logKey = log.id ?? `${log.timestamp ?? 0}-${log.loggerName ?? ""}-${log.threadName ?? ""}-${log.message ?? ""}`;

    return (
        <div style={style}>
            <LogLine
                log={log}
                searchText={searchText ?? undefined}
                isActiveMatch={index === activeLogIndex}
                isExpanded={expandedLogIds[logKey] ?? false}
                onToggleExpanded={() => onToggleExpanded(logKey)}
                isInStepsList={isInStepsList}
            />
        </div>
    );
}

export interface LogConsoleProps {
    logs: ILogEntry[];
    searchText?: string | null;
    height?: number | string;
    activeLogIndex?: number;
    isInStepsList?: boolean;
}

export const LogConsole: React.FC<LogConsoleProps> = ({logs, searchText, height = "calc(100dvh - 200px)", activeLogIndex = -1, isInStepsList = false}) => {
    const theme = useTheme();

    // Hooks must stay before the early return so renders remain stable.
    const listHeight = useMemo(() => {
        if (typeof height === "number") return height;
        return isInStepsList ? "auto" : window.innerHeight - 200;
    }, [height, isInStepsList]);

    // dynamic row height
    const rowHeight = useDynamicRowHeight({
        defaultRowHeight: 21, // height of default log line (1 row)
    });

    // Ref for imperative API (scrollToRow)
    const listRef = useListRef(null);

    /* separate store for expanded logs:
    if this would not exist, the information about expansion is lost when this log entry leaves the dom due to virtualization
    since this can cause confusion for the user (entrys collapse when not visible), these states are stored separately
    only expanded rows are stored; collapsed rows are simply absent from the map */
    const [expandedLogIds, setExpandedLogIds] = useState<Record<string, true>>({})
    const toggleExpanded = useCallback((logKey: string) => {
        setExpandedLogIds((previous) => {
            if (previous[logKey]) {
                const next = {...previous};
                delete next[logKey];
                return next;
            }

            return {
                ...previous,
                [logKey]: true,
            };
        });
    }, []);

    const rowProps = useMemo(
        () => ({
            logs,
            searchText,
            activeLogIndex,
            expandedLogIds,
            onToggleExpanded: toggleExpanded,
            isInStepsList
        }),
        [logs, searchText, activeLogIndex, expandedLogIds, toggleExpanded, isInStepsList],
    );

    // scroll to active log if activeLogIndex changes
    useEffect(() => {
        if (isInStepsList) {
            return;
        }
        if (logs.length === 0) {
            return;
        }

        if (activeLogIndex < 0) return;
        const list = listRef.current;
        if (!list) return;

        list.scrollToRow({
            index: activeLogIndex,
            align: "start",
            behavior: "auto",
        });
    }, [activeLogIndex, listRef, logs.length, isInStepsList]);

    if (logs.length === 0) {
        return <NoResultsCard title="No log messages matching this criteria"/>
    }

    return (
        <Paper sx={(theme) => theme.custom.logConsole.paper}>
            <Box sx={(theme) => theme.custom.logConsole.listContainer(listHeight)}>
                <List
                    listRef={listRef}
                    rowComponent={LogConsoleRow}
                    rowCount={logs.length}
                    rowHeight={rowHeight}
                    rowProps={rowProps}
                    style={theme.custom.logConsole.list(listHeight)}
                />
            </Box>
        </Paper>
    );
};
