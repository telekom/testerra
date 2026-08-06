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

import ReportCard from "../../widgets/ReportCard.tsx";
import ReportChip from "../../widgets/ReportChip.tsx";
import {StatusService} from "../../model/status-service.tsx";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import Stack from "@mui/material/Stack";
import {Grid, Typography, useTheme} from "@mui/material";
import List from "@mui/material/List";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";
import DetailKeyValueListItem from "./DetailKeyValueListItem.tsx";
import Link from "@mui/material/Link";
import {Link as RouterLink} from "react-router-dom";
import DurationCard from "../DurationCard.tsx";
import type {ChipColor} from "../../hooks/useChipListFilters.tsx";
import {ResultStatusType} from "../../model/report-model/framework_pb.ts";
import LazyImage from "../../widgets/LazyImage.tsx";
import {useState} from "react";
import Modal from "../../widgets/Modal.tsx";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";

interface GeneralDetailsProps {
    methodDetail?: MethodDetails;
    previousDetail?: MethodDetails;
    nextDetail?: MethodDetails;
}

const GeneralDetails = ({methodDetail, previousDetail, nextDetail}: GeneralDetailsProps) => {
        const theme = useTheme();
        const [isScreenshotModalOpen, setIsScreenshotModalOpen] = useState(false);
        const [selectedScreenshotId, setSelectedScreenshotId] = useState<string | undefined>(undefined);

        if (!methodDetail) {
            return <NoResultsCard title="This method has no error context information"/>;
        }

        const allScreenshotIds = methodDetail.methodContext.testSteps
            ?.flatMap(step => step.actions ?? [])
            .flatMap(action => action.entries ?? [])
            .map(entry => entry.screenshotId)
            .filter((id): id is string => Boolean(id)) ?? [];
        const lastScreenshotId = allScreenshotIds.length > 0 ? allScreenshotIds[allScreenshotIds.length - 1] : undefined;

        return (
            <>
                <Grid container={true} spacing={3}>
                    <Grid size={lastScreenshotId ? 7 : 9}>
                        <ReportCard
                            label={
                                <Stack direction="row" spacing={2} alignItems="center">
                                    <ReportChip key={methodDetail?.methodContext.resultStatus}
                                                label={StatusService.getLabel(methodDetail?.methodContext.resultStatus ?? "")}
                                                size="medium"
                                                sx={theme.custom.generalDetails.statusChip(StatusService.getColor(methodDetail?.methodContext.resultStatus ?? ""))}/>
                                    <Typography color="black"
                                                sx={(theme) => theme.custom.generalDetails.wrapText}>{methodDetail.identifier}</Typography>
                                    {methodDetail.methodContext.methodType == 2 &&
                                        <ReportChip label="Configuration" size="small" color={"lightGrey" as ChipColor}
                                                   sx={theme.custom.generalDetails.configurationChip}/>}
                                </Stack>
                            }
                            details={
                                (methodDetail.testAnnotation?.description || 
                                 methodDetail.xrayAnnotation?.ticketUrls?.length > 0 || 
                                 methodDetail.failsAnnotation) ? (
                                    <>
                                        {methodDetail.testAnnotation?.description && (
                                            <Typography variant="body2" sx={theme.custom.generalDetails.topSpacingText}>
                                                {methodDetail.testAnnotation.description}
                                            </Typography>
                                        )}
                                        {methodDetail.xrayAnnotation?.ticketUrls?.length > 0 && (
                                            <Typography variant="body2" sx={theme.custom.generalDetails.topSpacingText}>
                                                Related Tickets:{" "}
                                                {methodDetail.xrayAnnotation.ticketUrls.map((ticketUrl: string, index: number) => (
                                                    <span key={`xray-ticket-${index}`}>
                                                       <Link href={ticketUrl}>{ticketUrl}</Link>
                                                       {index < methodDetail.xrayAnnotation.ticketUrls.length - 1 ? " " : ""}
                                                    </span>
                                                ))}
                                            </Typography>
                                        )}
                                        {methodDetail.failsAnnotation && (
                                            <Stack direction="row" spacing={1} sx={theme.custom.generalDetails.failsStack}>
                                                <ReportChip label="@Fails" size="small"
                                                           sx={theme.custom.generalDetails.failsChip}/>
                                                {methodDetail.methodContext.resultStatus !== ResultStatusType.FAILED_EXPECTED && (
                                                    <>
                                                       {methodDetail.failsAnnotation.description && (
                                                           <Typography variant="body2">{methodDetail.failsAnnotation.description}</Typography>
                                                       )}
                                                       {methodDetail.failsAnnotation.ticketString && (
                                                           <Typography variant="body2">
                                                               Ticket: {methodDetail.failsAnnotation.ticketString}
                                                           </Typography>
                                                       )}
                                                    </>
                                                )}
                                                {methodDetail.methodContext.resultStatus === ResultStatusType.FAILED_EXPECTED && methodDetail.failsAnnotation.validator && (
                                                    <>
                                                       <Typography variant="body2">Your conditions for Expected fails were not fulfilled:</Typography>
                                                       <Typography variant="body2">{methodDetail.failsAnnotation.validator}</Typography>
                                                    </>
                                                )}
                                            </Stack>
                                        )}
                                    </>
                               ) : undefined
                            }
                            content={(
                                <Grid container>
                                    <Grid size={6}>
                                        <List sx={{flex: 1, minWidth: 0}}>
                                            <DetailKeyValueListItem
                                                label="Class"
                                                value={
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
                                                }
                                                wideLabel={false}
                                                compact={true}
                                            />
                                            <DetailKeyValueListItem
                                                label="Package"
                                                value={
                                                    <Typography variant="caption"
                                                                sx={(theme) => theme.custom.generalDetails.truncateText}>
                                                        {classNameConverter(methodDetail.classStatistics.classIdentifier, ClassName.package)}
                                                    </Typography>
                                                }
                                                wideLabel={false}
                                                compact={true}
                                            />
                                            <DetailKeyValueListItem
                                                label="Test Context"
                                                value={
                                                    <Typography variant="caption"
                                                                sx={(theme) => theme.custom.generalDetails.truncateText}>
                                                        {methodDetail?.testContext?.contextValues?.name}
                                                    </Typography>
                                                }
                                                wideLabel={false}
                                                compact={true}
                                            />
                                            <DetailKeyValueListItem
                                                label="Suite"
                                                value={
                                                    <Typography variant="caption"
                                                                sx={(theme) => theme.custom.generalDetails.wrapText}>
                                                        {methodDetail?.suiteContext?.contextValues?.name}
                                                    </Typography>
                                                }
                                                wideLabel={false}
                                                compact={true}
                                            />
                                        </List>
                                    </Grid>

                                    <Grid size={6}>
                                        <List sx={{flex: 1, minWidth: 0}}>
                                            {methodDetail.failedStep && (
                                                <DetailKeyValueListItem
                                                    label="Failed in"
                                                    value={
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
                                                    }
                                                    wideLabel={false}
                                                    compact={true}
                                                />
                                            )}
                                            <DetailKeyValueListItem
                                                label="Thread"
                                                value={
                                                    <Link
                                                        component={RouterLink}
                                                        to={{
                                                            pathname: `/threads`,
                                                            search: `method=${methodDetail.methodContext.contextValues?.id}`
                                                        }}
                                                    >
                                                        <Typography variant="caption"
                                                                    sx={(theme) => ({...theme.custom.generalDetails.truncateText, ...theme.custom.generalDetails.compactWrappedText})}>
                                                            {methodDetail.methodContext.threadName}
                                                        </Typography>
                                                    </Link>
                                                }
                                                wideLabel={false}
                                                compact={true}
                                            />
                                            <DetailKeyValueListItem
                                                label="Run index"
                                                value={
                                                    <Typography variant="caption"
                                                                sx={(theme) => theme.custom.generalDetails.wrapText}>
                                                        #{methodDetail.methodContext.methodRunIndex}
                                                    </Typography>
                                                }
                                                wideLabel={false}
                                                compact={true}
                                            />
                                        </List>
                                    </Grid>
                                </Grid>
                            )
                            }
                            footer={
                                (previousDetail || nextDetail) && methodDetail.numDetails > 0 && (
                                    <Grid container spacing={2} sx={theme.custom.generalDetails.footerContainer}>
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
                                                <Stack direction="column" sx={theme.custom.generalDetails.nextMethodStack}>
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

                    {
                        lastScreenshotId && <Grid size={2}>
                            <ReportCard label={"Last Screenshot"}
                                        content={
                                            <LazyImage
                                                fileId={lastScreenshotId}
                                                onClick={(file) => {
                                                    if (file.id) {
                                                        setSelectedScreenshotId(file.id);
                                                        setIsScreenshotModalOpen(true);
                                                    }
                                                }}
                                                style={theme.custom.generalDetails.lastScreenshotImage}
                                            />

                                        }
                                        sxContent={theme.custom.generalDetails.lastScreenshotCardContent}/>
                        </Grid>
                    }

                    <Grid size={3}>
                        <DurationCard
                            start={methodDetail.methodContext.contextValues?.startTime}
                            end={methodDetail.methodContext.contextValues?.endTime}
                        />
                    </Grid>
                </Grid>

                <Modal
                    open={isScreenshotModalOpen}
                    screenshotIds={allScreenshotIds}
                    initialScreenshotId={selectedScreenshotId}
                    sessionContexts={methodDetail.sessionContexts}
                    onClose={() => setIsScreenshotModalOpen(false)}
                />
            </>
        );
};
export default GeneralDetails;
