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

import {useMemo} from "react";
import {Link as RouterLink, useOutletContext, useSearchParams} from "react-router-dom";
import {Box, Grid, Link, List, Typography, useTheme} from "@mui/material";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import {useReportData} from "../../provider/DataProvider.tsx";
import {MetricType} from "../../model/report-model/framework_pb.ts";
import {dateFormatter} from "../../utils/dateFormatter.ts";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";
import {LogConsole} from "../LogConsole.tsx";
import type {ILogEntry} from "../../model/Logs.ts";
import ReportCard from "../../widgets/ReportCard.tsx";
import {useScrollToElementById} from "../../hooks/useScrollToElementById.ts";
import DetailKeyValueListItem from "./DetailKeyValueListItem.tsx";

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
    const theme = useTheme();
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

    useScrollToElementById(highlightedSessionId, sessionInformationArray.length);

    if (!methodDetail) {
        return <NoResultsCard title="No method selected"/>;
    }

    if (sessionInformationArray.length === 0) {
        return <NoResultsCard title="No browser session information available"/>;
    }

    return (
        <Box sx={theme.custom.sessionInfo.cardsContainer}>
            {sessionInformationArray.map(session => (
                <Box key={session.sessionId} id={session.sessionId}>
                    <ReportCard
                        label={`Session: ${session.sessionName}`}
                        content={
                        <Grid container spacing={2}>
                            <Grid size={6}>
                                <List dense disablePadding>
                                    <Typography variant="subtitle2">General information</Typography>

                                    <DetailKeyValueListItem label="ID" value={<Typography variant="caption">{session.sessionId}</Typography>} />
                                    <DetailKeyValueListItem label="Browser" value={<Typography variant="caption">{session.browserName}:{session.browserVersion}</Typography>} />
                                    <DetailKeyValueListItem label="User agent" value={<Typography variant="caption">{session.userAgent}</Typography>} />

                                    {session.serverUrl && (
                                        <DetailKeyValueListItem
                                            label="Server"
                                            value={<Link href={session.serverUrl} variant="caption">{session.serverUrl}</Link>}
                                        />
                                    )}

                                    {session.nodeUrl && (
                                        <DetailKeyValueListItem
                                            label="Node"
                                            value={<Link href={session.nodeUrl} variant="caption">{session.nodeUrl}</Link>}
                                        />
                                    )}

                                    {session.videoId && (
                                        <DetailKeyValueListItem
                                            label="Video ID"
                                            value={(
                                                <Link
                                                    component={RouterLink}
                                                    to={{pathname: "../video", search: `?id=${encodeURIComponent(session.sessionId)}`}}
                                                    variant="caption"
                                                >
                                                    {session.videoId}
                                                </Link>
                                            )}
                                        />
                                    )}

                                    <Typography variant="subtitle2" sx={theme.custom.sessionInfo.sectionTitle}>Session metrics</Typography>

                                    {session.sessionDuration > 0 && (
                                        <DetailKeyValueListItem label="Session duration" value={<Typography variant="caption">{session.sessionDuration}s</Typography>} />
                                    )}

                                    {session.sessionStartDuration > 0 && (
                                        <DetailKeyValueListItem label="Session start duration" value={<Typography variant="caption">{session.sessionStartDuration}s</Typography>} />
                                    )}

                                    {(session.sessionStartTime ?? 0) > 0 && (
                                        <DetailKeyValueListItem
                                            label="Session start time"
                                            value={<Typography variant="caption">{dateFormatter(session.sessionStartTime, "long")}</Typography>}
                                        />
                                    )}

                                    {session.baseUrl && (
                                        <>
                                            <Typography variant="subtitle2" sx={theme.custom.sessionInfo.sectionTitle}>Base URL metrics</Typography>
                                            <DetailKeyValueListItem
                                                label="Base URL"
                                                value={<Link href={session.baseUrl} variant="caption">{session.baseUrl}</Link>}
                                            />

                                            {(session.baseurlStartDuration ?? 0) > 0 && (
                                                <DetailKeyValueListItem
                                                    label="Base URL start duration"
                                                    value={<Typography variant="caption">{session.baseurlStartDuration}s</Typography>}
                                                />
                                            )}

                                            {(session.baseurlStartTime ?? 0) > 0 && (
                                                <DetailKeyValueListItem
                                                    label="Base URL start time"
                                                    value={<Typography variant="caption">{dateFormatter(session.baseurlStartTime, "long")}</Typography>}
                                                />
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
