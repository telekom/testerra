import ReportCard from "../../widgets/ReportCard.tsx";
import ReportChip from "../../widgets/ReportChip.tsx";
import {StatusService} from "../../model/status-service.tsx";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import Stack from "@mui/material/Stack";
import {Grid, Typography} from "@mui/material";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";
import Link from "@mui/material/Link";
import {Link as RouterLink} from "react-router-dom";
import DashboardDurationCard from "../dashboard-components/DashboardDurationCard.tsx";
import type {ChipColor} from "../../hooks/useTestListFilters.tsx";
import {ResultStatusType} from "../../model/report-model/framework_pb.ts";


interface GeneralDetailsProps {
    methodDetail?: MethodDetails;
    previousDetail?: MethodDetails;
    nextDetail?: MethodDetails;
}

const GeneralDetails = ({methodDetail, previousDetail, nextDetail}: GeneralDetailsProps) => {
    if (!methodDetail) {
        return <ReportCard label="Method details" content="No method selected."/>;
    }

    const allScreenshotIds = methodDetail.methodContext.testSteps
        ?.flatMap(step => step.actions ?? [])
        .flatMap(action => action.entries ?? [])
        .map(entry => entry.screenshotId)
        .filter((id): id is string => Boolean(id)) ?? [];
    const lastScreenshotId = allScreenshotIds.length > 0 ? allScreenshotIds[allScreenshotIds.length - 1] : undefined;

    return (
        <Grid container={true} spacing={3}>
            <Grid size={lastScreenshotId ? 7 : 9}>
                <ReportCard
                    label={
                        <Stack direction="row" spacing={2} alignItems="center">
                            <ReportChip key={methodDetail?.methodContext.resultStatus}
                                        label={StatusService.getLabel(methodDetail?.methodContext.resultStatus ?? "")}
                                        size="medium"
                                        sx={{
                                            background: StatusService.getColor(methodDetail?.methodContext.resultStatus ?? ""),
                                            color: "white",
                                            fontSize: "16px",
                                            fontWeight: "400"
                                        }}/>
                            <Typography color="black"
                                        sx={(theme) => theme.custom.generalDetails.wrapText}>{methodDetail.identifier}</Typography>
                            {methodDetail.methodContext.methodType == 2 &&
                                <ReportChip label="Configuration" size="small" color={"lightGrey" as ChipColor}
                                            sx={{color: "white", fontWeight: "400"}}/>}
                            {/*TODO methodDetail.testAnnotation.description (html)*/}
                            {/*TODO if methodDetail.xrayAnnotatio : Related Tickets for ticketURL of _methodDetails.xrayAnnotation.ticketUrls (Links)*/}
                        </Stack>}
                    details={
                        methodDetail.failsAnnotation && (
                            <Stack direction="row" spacing={1} sx={{alignItems: "center", my: 1}}>
                                <ReportChip label="@Fails" size="small"
                                            sx={{
                                                background: StatusService.getColor(ResultStatusType.FAILED_EXPECTED),
                                                color: "white"
                                            }}/>
                                <Typography variant="body2">{methodDetail.failsAnnotation.description}</Typography>
                                {/*TODO Ticket String*/}
                                {/*TODO Failsannotation Validator*/}
                            </Stack>
                        )}
                    content={(
                        <>
                            <Stack direction="row" spacing={10} sx={{width: "100%"}}>
                                <List sx={{flex: 1, minWidth: 0}}>
                                    <ListItem sx={(theme) => theme.custom.generalDetails.listItem} disablePadding>
                                        <Typography variant="caption" color="textSecondary">
                                            Class
                                        </Typography>
                                        <Link
                                            component={RouterLink}
                                            to={{
                                                pathname: "/Tests",
                                                search: `class=${classNameConverter(methodDetail.classStatistics.classIdentifier, ClassName.simpleName)}`,
                                            }}
                                            sx={(theme) => theme.custom.generalDetails.wrapText}
                                        >
                                            <Typography variant="caption"
                                                        sx={(theme) => theme.custom.generalDetails.wrapText}>
                                                {classNameConverter(methodDetail.classStatistics.classIdentifier, ClassName.simpleName)}
                                            </Typography>
                                        </Link>
                                    </ListItem>
                                    <ListItem sx={(theme) => theme.custom.generalDetails.listItem} disablePadding>
                                        <Typography variant="caption" color="textSecondary">
                                            Package
                                        </Typography>
                                        <Typography variant="caption"
                                                    sx={(theme) => theme.custom.generalDetails.truncateText}>
                                            {classNameConverter(methodDetail.classStatistics.classIdentifier, ClassName.package)}
                                        </Typography>
                                    </ListItem>
                                    <ListItem sx={(theme) => theme.custom.generalDetails.listItem} disablePadding>
                                        <Typography variant="caption" color="textSecondary"
                                                    sx={(theme) => theme.custom.generalDetails.nowrapLabel}>
                                            Test Context
                                        </Typography>
                                        <Typography variant="caption"
                                                    sx={(theme) => theme.custom.generalDetails.truncateText}>
                                            {methodDetail?.testContext?.contextValues?.name}
                                        </Typography>
                                    </ListItem>
                                    <ListItem sx={(theme) => theme.custom.generalDetails.listItem} disablePadding>
                                        <Typography variant="caption" color="textSecondary">
                                            Suite
                                        </Typography>
                                        <Typography variant="caption"
                                                    sx={(theme) => theme.custom.generalDetails.wrapText}>
                                            {methodDetail?.suiteContext?.contextValues?.name}
                                        </Typography>
                                    </ListItem>
                                </List>

                                <List sx={{flex: 1, minWidth: 0}}>
                                    {methodDetail.failedStep && (
                                        <ListItem sx={{gap: "8px", alignItems: "center"}}
                                                  disablePadding>
                                            <Typography variant="caption" color="textSecondary"
                                                        sx={(theme) => theme.custom.generalDetails.nowrapLabel}>
                                                Failed in
                                            </Typography>
                                            <Link
                                                component={RouterLink}
                                                to={{
                                                    pathname: `steps/${(methodDetail?.methodContext?.failedStepIndex as number) + 1}`,
                                                }}
                                                underline="hover"
                                                sx={(theme) => theme.custom.generalDetails.blockLink}
                                            >
                                                <Typography
                                                    variant="caption"
                                                    sx={(theme) => ({...theme.custom.generalDetails.compactWrappedText, ...theme.custom.generalDetails.wrapText})}
                                                >
                                                    {methodDetail.failedStep.name}
                                                </Typography>
                                            </Link>

                                        </ListItem>
                                    )}
                                    <ListItem sx={(theme) => theme.custom.generalDetails.listItem} disablePadding>
                                        <Typography variant="caption" color="textSecondary">
                                            Thread
                                        </Typography>
                                        <Link
                                            component={RouterLink}
                                            to={{
                                                pathname: `/threads`,
                                                search: `methodId=${methodDetail.methodContext.contextValues?.id}`
                                            }}
                                        >
                                            <Typography variant="caption"
                                                        sx={(theme) => ({...theme.custom.generalDetails.truncateText, ...theme.custom.generalDetails.compactWrappedText})}>
                                                {methodDetail.methodContext.threadName}
                                            </Typography>
                                        </Link>
                                    </ListItem>
                                    <ListItem sx={(theme) => theme.custom.generalDetails.listItem} disablePadding>
                                        <Typography variant="caption" color="textSecondary"
                                                    sx={(theme) => theme.custom.generalDetails.nowrapLabel}>
                                            Run index
                                        </Typography>
                                        <Typography variant="caption"
                                                    sx={(theme) => theme.custom.generalDetails.wrapText}>
                                            #{methodDetail.methodContext.methodRunIndex}
                                        </Typography>
                                    </ListItem>
                                </List>
                            </Stack>
                        </>
                    )}
                    footer={
                        (previousDetail || nextDetail) && (
                            <Grid container spacing={2} sx={{mt: 2}}>
                                <Grid size={6}>
                                    {previousDetail && (
                                        <Stack direction="column">
                                            <Typography variant="caption" color="textSecondary"
                                                        sx={(theme) => theme.custom.generalDetails.nowrapLabel}>
                                                Previous failed method
                                            </Typography>
                                            <Link
                                                component={RouterLink}
                                                to={`/method/${previousDetail.methodContext.contextValues?.id}`}
                                                underline="hover"
                                                variant="caption"
                                                noWrap
                                                sx={(theme) => ({
                                                    ...theme.custom.generalDetails.blockLink,
                                                    ...theme.custom.generalDetails.truncateText
                                                })}
                                            >
                                                {previousDetail.identifier}
                                            </Link>
                                        </Stack>
                                    )}
                                </Grid>
                                <Grid size={6} justifyContent={"flex-end"}>
                                    {nextDetail && (
                                        <Stack direction="column" sx={{alignItems: "flex-start", maxWidth: "100%"}}>
                                            <Typography variant="caption" color="textSecondary"
                                                        sx={(theme) => theme.custom.generalDetails.nowrapLabel}>
                                                Next failed method
                                            </Typography>
                                            <Link
                                                component={RouterLink}
                                                to={`/method/${nextDetail.methodContext.contextValues?.id}`}
                                                underline="hover"
                                                variant="caption"
                                                noWrap
                                                sx={(theme) => ({
                                                    ...theme.custom.generalDetails.blockLink,
                                                    ...theme.custom.generalDetails.truncateText
                                                })}
                                            >
                                                {nextDetail.identifier}
                                            </Link>
                                        </Stack>
                                    )}
                                </Grid>
                            </Grid>
                        )
                    }
                />
            </Grid>

            {lastScreenshotId && <Grid>
                <ReportCard label={"Last Screenshot"} content={
                    <ListItem sx={{gap: "8px", alignItems: "end"}} disablePadding>
                        <Typography variant="caption" color="textSecondary">Last screenshot</Typography>
                        <Typography variant="caption">
                            {lastScreenshotId} ({allScreenshotIds.length})
                        </Typography>
                    </ListItem>

                }/>
            </Grid>}

            <Grid>
                <DashboardDurationCard/>
            </Grid>
        </Grid>

    )
        ;
};
export default GeneralDetails;
