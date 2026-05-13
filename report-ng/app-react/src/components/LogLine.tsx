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

import React, { useState } from "react";
import { Box, Link } from "@mui/material";
import type { ILogEntry } from "../model/Logs";
import HighlightText from "../utils/highlightText";
import { StatusService } from "../model/status-service";
import { logLevelNameConverter } from "../utils/logLevelNameConverter";
import { dateFormatter } from "../utils/dateFormatter";

interface LogLineProps {
    log: ILogEntry;
    searchText?: string;
}

const flattenStackTrace = (log: ILogEntry): string[] =>
  log.stackTrace?.flatMap((stackTraceCause) =>
    [
      stackTraceCause.className && stackTraceCause.message
        ? `${stackTraceCause.className}: ${stackTraceCause.message}`
        : (stackTraceCause.className ?? stackTraceCause.message),
      ...(stackTraceCause.stackTraceElements ?? []),
    ].filter((line): line is string => Boolean(line)),
  ) ?? [];

function levelClass(type?: number) {
  if (type === 1) return "error";
  if (type === 2) return "warn";
  return "";
}

export const LogLine: React.FC<LogLineProps> = ({ log, searchText }) => {
  const stackTraceLines = flattenStackTrace(log);
  const [showDetails, setShowDetails] = useState(false);
  const logger = StatusService.separateNamespace(log.loggerName ?? "");
  const lvlClass = levelClass(log.type);
    const searchTerms = searchText?.trim() ? [searchText.trim()] : [];

  const methodId = log.methodContext?.contextValues?.id;

  return (
    <Box
      className={`line ${lvlClass}`}
      sx={{
        display: "grid",
        gridTemplateColumns: "20px minmax(0, 1fr)",
        columnGap: 1,
        alignItems: "start",
        fontFamily: "monospace",
        overflowWrap: "anywhere",
        width: "100%",
        "&.error": { background: "#5c0c12" },
        "&.warn": { background: "#784b05" },
        a: { color: "#6e95ed", textDecorationColor: "#6e95ed" },
      }}
    >
      <Box
        component="span"
        sx={{
          display: "inline-flex",
          justifyContent: "center",
          width: 20,
        }}
      >
        {stackTraceLines.length > 0 && (
          <Box
            component="span"
            sx={{
              cursor: "pointer",
              userSelect: "none",
            }}
            onClick={() => setShowDetails((value) => !value)}
          >
            {showDetails ? "-" : "+"}
          </Box>
        )}
      </Box>
      <Box sx={{ minWidth: 0 }}>
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
              sx={{ color: "inherit" }}
            >
                [{log.threadName ?? "method"}]
            </Link>
          </>
        )}
          {" "}
        [{logLevelNameConverter(log.type)}]:{" "}
        <span title={`${logger.package}.${logger.class}`}>
          <HighlightText text={logger.class} searchWord={searchTerms} />
        </span>
        <span> - </span>
        <Box component="span" sx={{ whiteSpace: "pre-wrap" }}>
          <HighlightText text={log.message ?? ""} searchWord={searchTerms} />
        </Box>
        {showDetails && stackTraceLines.length > 0 && (
          <Box
            sx={{
              mt: 0.5,
              whiteSpace: "pre-wrap",
            }}
          >
            {stackTraceLines.map((line, index) => (
              <Box key={`${log.id ?? log.timestamp ?? 0}-stack-${index}`}>
                <HighlightText text={line} searchWord={searchTerms} />
              </Box>
            ))}
          </Box>
        )}
      </Box>
    </Box>
  );
};
