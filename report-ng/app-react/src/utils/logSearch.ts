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

import type {ILogEntry} from "../model/Logs";
import {dateFormatter} from "./dateFormatter";

// helper function to flatten a log entry's stack trace into a list of readable lines (string array)
export function flattenStackTrace(log: ILogEntry): string[] {
    // return array for each stack trace element (flattened)
    return log.stackTrace?.flatMap(stackTrace =>
        [
            stackTrace.className && stackTrace.message
                ? `${stackTrace.className}: ${stackTrace.message}`
                : (stackTrace.className ?? stackTrace.message),
            ...(stackTrace.stackTraceElements ?? []),
        ].filter((line): line is string => Boolean(line)), // return only non-empty strings
    ) ?? [];
}

// checks whether a log entry matches the given search term
// returns both overall match and whether it matched in the stack trace
export function checkMatches(log: ILogEntry, searchText?: string) {
    const term = searchText?.trim();
    if (!term) {
        return {
            matched: false,
            matchedInStackTrace: false,
        };
    }

    const lowerTerm = term.toLowerCase();

    const foundInMessage = (log.message ?? "").toLowerCase().includes(lowerTerm);

    const stackTraceLines = flattenStackTrace(log);
    const foundInStackTrace = stackTraceLines.some((line) =>
        line.toLowerCase().includes(lowerTerm),
    );

    const loggerClass = log.loggerName
        ? log.loggerName.split(".").pop() ?? log.loggerName
        : "";
    const foundInLoggerName = loggerClass.toLowerCase().includes(lowerTerm);

    const foundInTimeStamp = String(dateFormatter(log.timestamp, "short"))
        .toLowerCase()
        .includes(lowerTerm);

    return {
        matched: foundInMessage || foundInLoggerName || foundInTimeStamp || foundInStackTrace,
        matchedInStackTrace: foundInStackTrace,
    };
}