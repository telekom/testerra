import {useEffect} from "react";
import {useOutletContext, useSearchParams, Link as RouterLink} from "react-router-dom";
import {Box, Grid, List, ListItem, Typography} from "@mui/material";
import {Link} from "@mui/material";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";
import LazyVideo from "../../widgets/LazyVideo.tsx";
import ReportCard from "../../widgets/ReportCard.tsx";

const Video = () => {
    const methodDetail = useOutletContext<MethodDetails | undefined>();
    const [searchParams] = useSearchParams();
    const highlightedSessionId = searchParams.get("id");

    useEffect(() => {
        if (!highlightedSessionId) return;
        const timeoutId = window.setTimeout(() => {
            document.getElementById(highlightedSessionId)?.scrollIntoView();
        }, 0);
        return () => window.clearTimeout(timeoutId);
    }, [highlightedSessionId]);

    if (!methodDetail) {
        return <NoResultsCard title="No method selected"/>;
    }

    const sessionsWithVideo = methodDetail.sessionContexts.filter(s => s.videoId);

    if (sessionsWithVideo.length === 0) {
        return <NoResultsCard title="No video available for this method"/>;
    }

    return (
        <Box sx={{display: "flex", flexDirection: "column", gap: 2}}>
            {sessionsWithVideo.map(session => (
                <Box key={session.contextValues?.id} id={session.contextValues?.id}>
                    <ReportCard
                        label={`Session: ${session.contextValues?.name}`}
                        content={
                        <Grid container spacing={2}>
                            <Grid size={2}>
                                <List dense disablePadding>
                                    <ListItem disablePadding sx={{gap: 1}}>
                                        <Typography variant="caption" color="text.secondary" sx={{minWidth: 80}}>Session ID</Typography>
                                        <Link
                                            component={RouterLink}
                                            to={{pathname: "../browser-info", search: `?id=${encodeURIComponent(session.contextValues?.id ?? "")}`}}
                                            variant="caption"
                                        >
                                            {session.contextValues?.id}
                                        </Link>
                                    </ListItem>
                                    <ListItem disablePadding sx={{gap: 1}}>
                                        <Typography variant="caption" color="text.secondary" sx={{minWidth: 80}}>Browser</Typography>
                                        <Typography variant="caption">{session.browserName}:{session.browserVersion}</Typography>
                                    </ListItem>
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
