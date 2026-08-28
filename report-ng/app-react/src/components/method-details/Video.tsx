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

import {useTheme} from "@mui/material";
import {useOutletContext, useSearchParams, Link as RouterLink} from "react-router-dom";
import {Box, Grid, List, Typography} from "@mui/material";
import {Link} from "@mui/material";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";
import LazyVideo from "../../widgets/LazyVideo.tsx";
import ReportCard from "../../widgets/ReportCard.tsx";
import {useScrollToElementById} from "../../hooks/useScrollToElementById.ts";
import DetailKeyValueListItem from "./DetailKeyValueListItem.tsx";

const Video = () => {
    const methodDetail = useOutletContext<MethodDetails | undefined>();
    const theme = useTheme();
    const [searchParams] = useSearchParams();
    const highlightedSessionId = searchParams.get("id");
    const sessionsWithVideo = methodDetail?.sessionContexts.filter(s => s.videoId) ?? [];
    useScrollToElementById(highlightedSessionId, sessionsWithVideo.length);

    if (!methodDetail) {
        return <NoResultsCard title="No method selected"/>;
    }

    if (sessionsWithVideo.length === 0) {
        return <NoResultsCard title="No video available for this method"/>;
    }

    return (
        <Box sx={theme.custom.sessionInfo.cardsContainer}>
            {sessionsWithVideo.map(session => (
                <Box key={session.contextValues?.id} id={session.contextValues?.id}>
                    <ReportCard
                        label={`Session: ${session.contextValues?.name}`}
                        content={
                        <Grid container spacing={2}>
                            <Grid size={2}>
                                <List dense disablePadding>
                                    <DetailKeyValueListItem
                                        label="Session ID"
                                        compact
                                        wideLabel={false}
                                        value={(
                                            <Link
                                                component={RouterLink}
                                                to={{pathname: "../browser-info", search: `?id=${encodeURIComponent(session.contextValues?.id ?? "")}`}}
                                                variant="caption"
                                            >
                                                {session.contextValues?.id}
                                            </Link>
                                        )}
                                    />
                                    <DetailKeyValueListItem
                                        label="Browser"
                                        compact
                                        wideLabel={false}
                                        value={<Typography variant="caption">{session.browserName}:{session.browserVersion}</Typography>}
                                    />
                                </List>
                            </Grid>
                            <Grid size={10}>
                                <LazyVideo fileId={session.videoId!}/>
                            </Grid>
                        </Grid>
                    }
                />
                </Box>
            ))}
        </Box>
    );
};

export default Video;
