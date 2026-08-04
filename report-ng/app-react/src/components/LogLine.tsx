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

import React, {useMemo} from "react";
import {Box, Link} from "@mui/material";
import type {ILogEntry} from "../model/Logs";
import HighlightText from "../utils/highlightText";
import {StatusService} from "../model/status-service";
import {logLevelNameConverter} from "../utils/logLevelNameConverter";
import {dateFormatter} from "../utils/dateFormatter";
import {checkMatches, flattenStackTrace} from "../utils/logSearch";

interface LogLineProps {
    log: ILogEntry;
    searchText?: string;
    isActiveMatch?: boolean;
    isExpanded?: boolean;
    onToggleExpanded?: () => void;
    isInStepsList?: boolean;
    showMetadata?: boolean;
}

function levelClass(type?: number) {
    if (type === 1) return "error";
    if (type === 2) return "warn";
    return "";
}

export const LogLine: React.FC<LogLineProps> = ({log, searchText, isActiveMatch = false, isExpanded = false, onToggleExpanded, isInStepsList = false, showMetadata = true}) => {
    const stackTraceLines = flattenStackTrace(log);
    const logger = StatusService.separateNamespace(log.loggerName ?? "");
    const lvlClass = levelClass(log.type);
    const searchTerms = searchText?.trim() ? [searchText.trim()] : [];

    const matchResult = useMemo(
        () => checkMatches(log, searchText),
        [log, searchText],
    );

    const methodId = log.methodContext?.contextValues?.id;
    const showDetails = isExpanded || (isActiveMatch && matchResult.matchedInStackTrace);

    return (
        <Box
            className={`line ${lvlClass}`}
            sx={(theme) => ({
                ...theme.custom.logLine.root,
                ...(isInStepsList && {
                    ml: 0,
                    pl: 0,
                }),
            })}
        >
            <Box
                component="span"
                sx={(theme) => theme.custom.logLine.expandToggleWrapper}
            >
                {stackTraceLines.length > 0 && isInStepsList && (
                    <Box
                        component="span"
                        sx={(theme) => theme.custom.logLine.expandToggle}
                        onClick={onToggleExpanded}
                    >
                        {showDetails ? "-" : "+"}
                    </Box>
                )}
            </Box>
            <Box sx={(theme) => theme.custom.logLine.content}>
                {showMetadata && (
                    <>
                        <HighlightText
                            text={dateFormatter(log.timestamp, "short")}
                            searchWord={searchTerms}
                        />
                        {log.methodContext && methodId && (
                            <>
                                {" "}
                                <Link
                                    href={`#/method/${methodId}`}
                                    underline="hover"
                                    sx={(theme) => theme.custom.logLine.methodLink}
                                >
                                    [{log.threadName ?? "method"}]
                                </Link>
                            </>
                        )}
                        {" "}
                        [{logLevelNameConverter(log.type)}]:{" "}
                        <span title={`${logger.package}.${logger.class}`}>
                            <HighlightText text={logger.class} searchWord={searchTerms}/>
                        </span>
                        <span> - </span>
                    </>
                )}
                <Box component="span" sx={(theme) => theme.custom.logLine.message}>
                    <HighlightText text={log.message ?? ""} searchWord={searchTerms}/>
                </Box>
                {showDetails && stackTraceLines.length > 0 && (
                    <Box sx={(theme) => theme.custom.logLine.details}>
                        {stackTraceLines.map((line, index) => (
                            <Box key={`${log.id ?? log.timestamp ?? 0}-stack-${index}`}>
                                <HighlightText text={line} searchWord={searchTerms}/>
                            </Box>
                        ))}
                    </Box>
                )}
            </Box>
        </Box>
    );
};
