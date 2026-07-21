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

import {useEffect, useMemo} from "react";
import {Link as RouterLink, useOutletContext, useSearchParams} from "react-router-dom";
import {Box, Grid, Link, List, ListItem, Typography} from "@mui/material";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import {useReportData} from "../../provider/DataProvider.tsx";
import {MetricType} from "../../model/report-model/framework_pb.ts";
import {dateFormatter} from "../../utils/dateFormatter.ts";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";
import {LogConsole} from "../LogConsole.tsx";
import type {ILogEntry} from "../../model/Logs.ts";
import ReportCard from "../../widgets/ReportCard.tsx";

interface ISessionInformation {
    sessionName: string;
    sessionId: string;
    browserName?: string;
    browserVersion?: string;
    userAgent?: string;
    serverUrl?: string;
    nodeUrl?: string;
    baseUrl?: string;
    sessionDuration: number;
    sessionStartDuration: number;
    baseurlStartDuration?: number;
    sessionStartTime?: number;
    baseurlStartTime?: number;
    capabilities?: string;
    videoId?: string;
}

const BrowserInfo = () => {
    const methodDetail = useOutletContext<MethodDetails | undefined>();
    const {executionMngr} = useReportData();
    const [searchParams] = useSearchParams();
    const highlightedSessionId = searchParams.get("id");

    const sessionInformationArray = useMemo<ISessionInformation[]>(() => {
        if (!methodDetail || !executionMngr) return [];

        const executionAggregate = executionMngr.getExecutionAggregate();
        const sessionContextIds = methodDetail.sessionContexts.map(ctx => ctx.contextValues?.id ?? "");

        const sessionMetrics = executionAggregate.testMetrics?.sessionMetrics ?? [];
        const filteredMetrics = sessionMetrics.filter(metric =>
            sessionContextIds.includes(metric.sessionContextId ?? "")
        );

        const result: ISessionInformation[] = [];

        filteredMetrics.forEach(metric => {
            const sessionData = metric.metricsValues?.find(v => v.metricType === MetricType.SESSION_LOAD);
            const baseurlData = metric.metricsValues
                ?.filter(v => v.metricType === MetricType.BASEURL_LOAD)
                .filter(v => (v.endTimestamp ?? 0) > 0)
                .find(() => true);

            if (!((sessionData?.endTimestamp ?? 0) > 0)) {
                return;
            }

            const sessionContext = executionAggregate.sessionContexts?.[metric.sessionContextId ?? ""];
            if (!sessionContext) return;

            result.push({
                sessionName: sessionContext.contextValues?.name ?? "",
                sessionId: sessionContext.contextValues?.id ?? "",
                browserName: sessionContext.browserName,
                browserVersion: sessionContext.browserVersion,
                userAgent: sessionContext.userAgent,
                serverUrl: sessionContext.serverUrl,
                nodeUrl: sessionContext.nodeUrl,
                baseUrl: sessionContext.baseUrl,
                sessionDuration: ((sessionContext.contextValues?.endTime ?? 0) - (sessionContext.contextValues?.startTime ?? 0)) / 1000,
                sessionStartDuration: ((sessionData?.endTimestamp ?? 0) - (sessionData?.startTimestamp ?? 0)) / 1000,
                baseurlStartDuration: baseurlData
                    ? ((baseurlData.endTimestamp ?? 0) - (baseurlData.startTimestamp ?? 0)) / 1000
                    : undefined,
                sessionStartTime: sessionData?.startTimestamp,
                baseurlStartTime: baseurlData?.startTimestamp,
                capabilities: sessionContext.capabilities,
                videoId: sessionContext.videoId,
            });
        });

        return result.sort((a, b) => a.sessionId.localeCompare(b.sessionId));
    }, [methodDetail, executionMngr]);

    useEffect(() => {
        if (!highlightedSessionId) {
            return;
        }
        const timeoutId = window.setTimeout(() => {
            document.getElementById(highlightedSessionId)?.scrollIntoView();
        }, 0);
        return () => window.clearTimeout(timeoutId);
    }, [highlightedSessionId]);

    if (!methodDetail) {
        return <NoResultsCard title="No method selected"/>;
    }

    if (sessionInformationArray.length === 0) {
        return <NoResultsCard title="No browser session information available"/>;
    }

    return (
        <Box sx={{display: "flex", flexDirection: "column", gap: 2}}>
            {sessionInformationArray.map(session => (
                <Box key={session.sessionId} id={session.sessionId}>
                    <ReportCard
                        label={`Session: ${session.sessionName}`}
                        content={
                        <Grid container spacing={2}>
                            <Grid size={6}>
                                <List dense disablePadding>
                                    <Typography variant="subtitle2">General information</Typography>

                                    <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                        <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>ID</Typography>
                                        <Typography variant="caption">{session.sessionId}</Typography>
                                    </ListItem>

                                    <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                        <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>Browser</Typography>
                                        <Typography variant="caption">{session.browserName}:{session.browserVersion}</Typography>
                                    </ListItem>

                                    <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                        <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>User agent</Typography>
                                        <Typography variant="caption">{session.userAgent}</Typography>
                                    </ListItem>

                                    {session.serverUrl && (
                                        <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                            <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>Server</Typography>
                                            <Link href={session.serverUrl} variant="caption">{session.serverUrl}</Link>
                                        </ListItem>
                                    )}

                                    {session.nodeUrl && (
                                        <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                            <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>Node</Typography>
                                            <Link href={session.nodeUrl} variant="caption">{session.nodeUrl}</Link>
                                        </ListItem>
                                    )}

                                    {session.videoId && (
                                        <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                            <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>Video ID</Typography>
                                            <Link
                                                component={RouterLink}
                                                to={{pathname: "../video", search: `?id=${encodeURIComponent(session.sessionId)}`}}
                                                variant="caption"
                                            >
                                                {session.videoId}
                                            </Link>
                                        </ListItem>
                                    )}

                                    <Typography variant="subtitle2" sx={{mt: 2}}>Session metrics</Typography>

                                    {session.sessionDuration > 0 && (
                                        <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                            <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>Session duration</Typography>
                                            <Typography variant="caption">{session.sessionDuration}s</Typography>
                                        </ListItem>
                                    )}

                                    {session.sessionStartDuration > 0 && (
                                        <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                            <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>Session start duration</Typography>
                                            <Typography variant="caption">{session.sessionStartDuration}s</Typography>
                                        </ListItem>
                                    )}

                                    {(session.sessionStartTime ?? 0) > 0 && (
                                        <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                            <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>Session start time</Typography>
                                            <Typography variant="caption">{dateFormatter(session.sessionStartTime, "long")}</Typography>
                                        </ListItem>
                                    )}

                                    {session.baseUrl && (
                                        <>
                                            <Typography variant="subtitle2" sx={{mt: 2}}>Base URL metrics</Typography>

                                            <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                                <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>Base URL</Typography>
                                                <Link href={session.baseUrl} variant="caption">{session.baseUrl}</Link>
                                            </ListItem>

                                            {(session.baseurlStartDuration ?? 0) > 0 && (
                                                <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                                    <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>Base URL start duration</Typography>
                                                    <Typography variant="caption">{session.baseurlStartDuration}s</Typography>
                                                </ListItem>
                                            )}

                                            {(session.baseurlStartTime ?? 0) > 0 && (
                                                <ListItem disablePadding sx={{gap: 1, pl: 2}}>
                                                    <Typography variant="caption" color="text.secondary" sx={{minWidth: 140}}>Base URL start time</Typography>
                                                    <Typography variant="caption">{dateFormatter(session.baseurlStartTime, "long")}</Typography>
                                                </ListItem>
                                            )}
                                        </>
                                    )}
                                </List>
                            </Grid>

                            {session.capabilities && (
                                <Grid size={6}>
                                    <Typography variant="subtitle2">Capabilities</Typography>
                                    <LogConsole
                                        logs={[{
                                            message: (() => {
                                                try {
                                                    return JSON.stringify(JSON.parse(session.capabilities), null, 2);
                                                } catch {
                                                    return session.capabilities;
                                                }
                                            })(),
                                        } satisfies ILogEntry]}
                                        height={300}
                                        showMetadata={false}
                                    />
                                </Grid>
                            )}
                        </Grid>
                    }
                    />
                </Box>
            ))}
        </Box>
    );
};

export default BrowserInfo;
