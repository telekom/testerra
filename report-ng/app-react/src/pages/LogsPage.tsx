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

import {FormControl, Grid, MenuItem} from "@mui/material";
import Box from "@mui/material/Box";
import Select from "@mui/material/Select";
import InputLabel from "@mui/material/InputLabel";
import TextField from "@mui/material/TextField";
import InputAdornment from "@mui/material/InputAdornment";
import SearchIcon from "@mui/icons-material/Search";
import {useMemo, useState} from "react";
import {LogConsole} from "../components/LogConsole";
import {useReportData} from "../provider/DataProvider";
import LinearProgress from "@mui/material/LinearProgress";
import Alert from "@mui/material/Alert";
import type {ILogEntry} from "../model/Logs";
import {logLevelNameConverter} from "../utils/logLevelNameConverter";

const LogsPage = () => {
    const {executionMngr, isLoading, error} = useReportData();
    const [searchText, setSearchText] = useState("");
    const [logLevel, setLogLevel] = useState<string>("");

    const logs = useMemo<ILogEntry[]>(() => {
        if (!executionMngr) {
            return [];
        }

        // Find methodContext to every log entry to create links from log view to method details view
        const methodContextByLogId = new Map<
            string,
            NonNullable<ILogEntry["methodContext"]>
        >(); // Map<logMessageId, methodContext>
        const executionAggregate = executionMngr.getExecutionAggregate();

        Object.values(executionAggregate.methodContexts ?? {}).forEach(
            (methodContext) => {
                methodContext.testSteps
                    ?.flatMap((step) => step.actions ?? [])
                    .flatMap((action) => action?.entries ?? [])
                    .forEach((entry) => {
                        if (entry?.logMessageId) {
                            methodContextByLogId.set(entry.logMessageId, methodContext);
                        }
                    });
            },
        );

        // combine log entry and methodcontext + sort by timestamp
        return Object.values(executionMngr.getLogs())
            .map((logEntry) => ({
                ...logEntry,
                methodContext: logEntry.id
                    ? methodContextByLogId.get(logEntry.id)
                    : undefined,
            }))
            .sort((left, right) => (left.timestamp ?? 0) - (right.timestamp ?? 0));
    }, [executionMngr]);

    // collect all logLevel types from logs
    const availableLogLevels = useMemo(() => {
        const levels = new Set<NonNullable<ILogEntry["type"]>>();   // use set to avoid duplicates

        logs.forEach((logEntry) => {
            if (logEntry.type !== undefined) {
                levels.add(logEntry.type);
            }
        });

        return Array.from(levels).sort(   // turn Set into array to use sort() and map()
            (left, right) => Number(left) - Number(right),
        );
    }, [logs]);

    const searchTerm = useMemo(
        () =>
            searchText.trim(),
        [searchText],
    );

    const filteredLogs = useMemo(() => (
        logs.filter((logEntry) => !logLevel || String(logEntry.type) === logLevel)
    ), [logs, logLevel]);

    if (isLoading) return <LinearProgress aria-label="Loading…"/>;
    if (error)
        return <Alert severity="error">An error occured: {error?.message}</Alert>;
    if (!executionMngr) return null;

    return (
        <Box sx={{width: "100%", maxWidth: {sm: "100%", md: "1700px"}}}>
            <Grid container spacing={3} columns={12}>
                <Grid size={2} sx={{alignItems: "stretch"}}>
                    <FormControl fullWidth>
                        <InputLabel>Log Level</InputLabel>
                        <Select
                            value={logLevel}
                            label="Log level"
                            onChange={(e) => setLogLevel(e.target.value)}
                        >
                            <MenuItem value="">(All)</MenuItem>
                            {availableLogLevels.map((level) => (
                                <MenuItem key={level} value={String(level)}>
                                    {logLevelNameConverter(level)}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                </Grid>
                <Grid size={8}>
                    <TextField
                        label="Search"
                        value={searchText}
                        onChange={(e) => {
                            setSearchText(e.target.value);
                        }}
                        slotProps={{
                            input: {
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <SearchIcon sx={{color: "action.active"}}/>
                                    </InputAdornment>
                                ),
                            },
                        }}
                        sx={{width: "100%"}}
                    />
                </Grid>
                <Grid size={12}>
                    <LogConsole
                        logs={filteredLogs}
                        searchText={searchTerm}
                    />
                </Grid>
            </Grid>
        </Box>
    );
};
export default LogsPage;
