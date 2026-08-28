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
import {ResultStatusType} from "../../model/report-model/framework_pb.ts";
import type {SxProps, Theme} from "@mui/material/styles";
import Box from "@mui/material/Box";

interface CommonDetailsCardProps {
    methodDetails: MethodDetails;
    sx?: SxProps<Theme>;
    previousDetail?: MethodDetails;
    nextDetail?: MethodDetails;
}

const CommonDetailsCard = ({methodDetails, sx, previousDetail, nextDetail}: CommonDetailsCardProps) => {
    const theme = useTheme();

    return (
        <ReportCard
            sxCard={sx}
            sxContent={{p: 0}}
            label={
                <Stack direction="row" spacing={2} alignItems="center">
                    <ReportChip key={methodDetails.methodContext.resultStatus}
                                label={StatusService.getLabel(methodDetails.methodContext.resultStatus ?? "")}
                                size="medium"
                                sx={theme.custom.generalDetails.statusChip(StatusService.getColor(methodDetails.methodContext.resultStatus ?? ""))}/>
                    <Typography color="black"
                                sx={(theme) => theme.custom.generalDetails.wrapText}>{methodDetails.identifier}</Typography>
                    {methodDetails.methodContext.methodType == 2 &&
                        <ReportChip label="Configuration" size="small" color="lightGrey"
                                   sx={theme.custom.generalDetails.configurationChip}/>}
                </Stack>
            }
            details={
                (methodDetails.testAnnotation?.description ||
                 methodDetails.xrayAnnotation?.ticketUrls?.length > 0 ||
                 methodDetails.failsAnnotation) ? (
                    <>
                        {methodDetails.testAnnotation?.description && (
                            <Typography variant="body2" sx={theme.custom.generalDetails.topSpacingText}>
                                {methodDetails.testAnnotation.description}
                            </Typography>
                        )}
                        {methodDetails.xrayAnnotation?.ticketUrls?.length > 0 && (
                            <Typography variant="body2" sx={theme.custom.generalDetails.topSpacingText}>
                                Related Tickets:{" "}
                                {methodDetails.xrayAnnotation.ticketUrls.map((ticketUrl: string, index: number) => (
                                    <span key={`xray-ticket-${index}`}>
                                       <Link href={ticketUrl}>{ticketUrl}</Link>
                                       {index < methodDetails.xrayAnnotation.ticketUrls.length - 1 ? " " : ""}
                                    </span>
                                ))}
                            </Typography>
                        )}
                        {methodDetails.failsAnnotation && (
                            <Stack direction="row" spacing={1} sx={theme.custom.generalDetails.failsStack}>
                                <ReportChip label="@Fails" size="small"
                                           sx={theme.custom.generalDetails.failsChip}/>
                                {methodDetails.methodContext.resultStatus !== ResultStatusType.FAILED_EXPECTED && (
                                    <>
                                       {methodDetails.failsAnnotation.description && (
                                           <Typography variant="body2">{methodDetails.failsAnnotation.description}</Typography>
                                       )}
                                        {methodDetails.failsAnnotation.ticketString && (
                                            <Typography variant="body2">
                                                Ticket: {methodDetails.failsAnnotation.ticketString}
                                            </Typography>
                                        )}
                                    </>
                                )}
                                {methodDetails.methodContext.resultStatus === ResultStatusType.FAILED_EXPECTED && methodDetails.failsAnnotation.validator && (
                                    <>
                                       <Typography variant="body2">Your conditions for Expected fails were not fulfilled:</Typography>
                                       <Typography variant="body2">{methodDetails.failsAnnotation.validator}</Typography>
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
                        <Box sx={{ pl: 2}}>
                            <List sx={{flex: 1, minWidth: 0}}>
                                <DetailKeyValueListItem
                                    label="Class"
                                    value={
                                        <Link
                                            component={RouterLink}
                                            to={{
                                                pathname: "/Tests",
                                                search: `class=${classNameConverter(methodDetails.classStatistics.classIdentifier, ClassName.simpleName)}`,
                                            }}
                                            sx={(theme) => theme.custom.generalDetails.wrapText}
                                        >
                                            <Typography variant="caption"
                                                        sx={(theme) => theme.custom.generalDetails.wrapText}>
                                                {classNameConverter(methodDetails.classStatistics.classIdentifier, ClassName.simpleName)}
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
                                            {classNameConverter(methodDetails.classStatistics.classIdentifier, ClassName.package)}
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
                                            {methodDetails?.testContext?.contextValues?.name}
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
                                            {methodDetails?.suiteContext?.contextValues?.name}
                                        </Typography>
                                    }
                                    wideLabel={false}
                                    compact={true}
                                />
                            </List>
                        </Box>
                    </Grid>

                    <Grid size={6}>
                        <Box sx={{ pl: 2}}>
                            <List sx={{flex: 1, minWidth: 0}}>
                                {methodDetails.failedStep && (
                                    <DetailKeyValueListItem
                                        label="Failed in"
                                        value={
                                            <Link
                                                component={RouterLink}
                                                to={{
                                                    pathname: `steps/${(methodDetails?.methodContext?.failedStepIndex as number) + 1}`,
                                                }}
                                                underline="hover"
                                                sx={(theme) => theme.custom.generalDetails.blockLink}
                                            >
                                                <Typography
                                                    variant="caption"
                                                    sx={(theme) => ({...theme.custom.generalDetails.compactWrappedText, ...theme.custom.generalDetails.wrapText})}
                                                >
                                                    {methodDetails.failedStep.name}
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
                                                search: `method=${methodDetails.methodContext.contextValues?.id}`
                                            }}
                                        >
                                            <Typography variant="caption"
                                                        sx={(theme) => ({...theme.custom.generalDetails.truncateText, ...theme.custom.generalDetails.compactWrappedText})}>
                                                {methodDetails.methodContext.threadName}
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
                                            #{methodDetails.methodContext.methodRunIndex}
                                        </Typography>
                                    }
                                    wideLabel={false}
                                    compact={true}
                                />
                            </List>
                        </Box>
                    </Grid>
                </Grid>
            )
            }
            footer={
                (previousDetail || nextDetail) && methodDetails.numDetails > 0 && (
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
    );
};

export default CommonDetailsCard;
