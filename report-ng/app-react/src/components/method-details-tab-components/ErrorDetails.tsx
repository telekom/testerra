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

import {useMemo, useState} from "react";
import {useOutletContext} from "react-router-dom";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import type {ErrorContext, LayoutCheckContext, StackTraceCause} from "../../model/report-model/framework_pb.ts";
import {StatusService} from "../../model/status-service.tsx";
import {
    Accordion,
    AccordionDetails,
    AccordionSummary,
    Box,
    IconButton,
    Snackbar,
    Stack,
    Typography,
} from "@mui/material";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import CloseIcon from "@mui/icons-material/Close";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";
import ReportCard from "../../widgets/ReportCard.tsx";
import LayoutComparison from "../LayoutComparison.tsx";
import CodeView from "../CodeView.tsx";

type ErrorDetail = {
    failureAspect: MethodDetails["failureAspects"][number];
    errorContext?: ErrorContext;
    layoutCheckContext?: LayoutCheckContext;
};

const ErrorDetails = () => {
    const methodDetail = useOutletContext<MethodDetails | undefined>();
    const [stackTraceCopiedSnackbarOpen, setStackTraceCopiedSnackbarOpen] = useState(false);

    const errorDetails = useMemo<ErrorDetail[]>(() => {
        if (!methodDetail) {
            return [];
        }

        const errorContexts = methodDetail.errorContexts;
        const layoutContexts = methodDetail.methodContext.layoutCheckContext ?? [];

        return methodDetail.failureAspects.map((failureAspect, index) => {
            const errorContext = errorContexts[index];
            const layoutCheckContext = layoutContexts.find(ctx => ctx.errorContextId === errorContext?.id);
            return {failureAspect, errorContext, layoutCheckContext};
        });
    }, [methodDetail]);

    const composeStackTraceText = (stackTrace: StackTraceCause[] = []) => {
        return stackTrace
            .map(cause => {
                const header = `${cause.className ?? ""}: ${cause.message ?? ""}`.trim();
                const lines = cause.stackTraceElements ?? [];
                return lines.length > 0 ? `${header}\n    ${lines.join("\n    ")}` : header;
            })
            .join("\n");
    };

    const copyStackTraceToClipboard = async (stackTrace: StackTraceCause[] = []) => {
        await navigator.clipboard.writeText(composeStackTraceText(stackTrace));
    };

    const handleCopyStackTrace = async (stackTrace: StackTraceCause[] = []) => {
        await copyStackTraceToClipboard(stackTrace);
        setStackTraceCopiedSnackbarOpen(true);
    };

    if (!methodDetail) {
        return;
    }

    return (
        <>
            <Stack spacing={6}>
                {errorDetails.map((errorDetail, index) => {
                    const statusColor = StatusService.getColor(methodDetail.methodContext.resultStatus ?? "");
                    const stackTrace = errorDetail.errorContext?.stackTrace ?? [];
                    const scriptSource = errorDetail.errorContext?.scriptSource;

                    return (
                        <ReportCard
                            key={`${errorDetail.failureAspect.identifier}-${index}`}
                            sxCard={{boxShadow: 4}}
                            sxHeader={{
                                backgroundColor: statusColor,
                                color: "white",
                                "& .MuiTypography-root": {color: "inherit"},
                            }}
                            label={(
                                <Box>
                                    <Typography variant="h5" sx={{overflowWrap: "anywhere"}}>
                                        {errorDetail.failureAspect.relevantCause?.className
                                            ? `${classNameConverter(errorDetail.failureAspect.relevantCause.className, ClassName.simpleName)}: `
                                            : ""}
                                        {errorDetail.failureAspect.message}
                                    </Typography>
                                    {errorDetail.layoutCheckContext && (
                                        <LayoutComparison layoutCheckContext={errorDetail.layoutCheckContext}/>)}
                                </Box>
                            )}
                            details={(
                                <>
                                    <Typography variant="subtitle2" sx={{mb: 1}} color="textSecondary">
                                        Origin ({scriptSource?.fileName ?? "unknown"})
                                    </Typography>
                                    <CodeView source={scriptSource}/>
                                </>
                            )}
                            content={(
                                <>
                                    <Stack direction="row"
                                           sx={{justifyContent: "space-between", alignItems: "center", mb: 1}}>
                                        <Typography variant="subtitle2" color="textSecondary">Stacktrace</Typography>
                                        <IconButton
                                            size="small"
                                            title="Copy to clipboard"
                                            onClick={() => {
                                                void handleCopyStackTrace(stackTrace);
                                            }}
                                        >
                                            <ContentCopyIcon fontSize="small"/>
                                        </IconButton>
                                    </Stack>
                                    <Stack spacing={1}>
                                        {stackTrace.map((cause, causeIndex) => {
                                            const stackTraceElements = cause.stackTraceElements ?? [];
                                            const stackTraceSource = {
                                                fileName: cause.className ?? "StackTrace",
                                                lines: stackTraceElements.map((line, lineIndex) => ({
                                                    line,
                                                    lineNumber: lineIndex + 1,
                                                })),
                                            };

                                            return (
                                                <Accordion key={`cause-${causeIndex}`} disableGutters>
                                                    <AccordionSummary expandIcon={<ExpandMoreIcon/>}>
                                                        <Typography variant="body2" sx={{overflowWrap: "anywhere"}}>
                                                            {cause.className
                                                                ? classNameConverter(cause.className, ClassName.simpleName)
                                                                : "UnknownClass"}
                                                            {cause.message ? `: ${cause.message}` : ""}
                                                        </Typography>
                                                    </AccordionSummary>
                                                    <AccordionDetails sx={{p: 0}}>
                                                        {stackTraceElements.length > 0 ? (
                                                            <CodeView source={stackTraceSource} showNumbers={false} markingName={methodDetail.methodContext.contextValues?.name}/>
                                                        ) : (
                                                            <Typography variant="body2" color="textSecondary">
                                                                No stack trace elements available.
                                                            </Typography>
                                                        )}
                                                    </AccordionDetails>
                                                </Accordion>
                                            );
                                        })}
                                    </Stack>
                                </>
                            )}
                        />
                    );
                })}
            </Stack>
            <Snackbar
                open={stackTraceCopiedSnackbarOpen}
                autoHideDuration={3000}
                message="Stacktrace copied to clipboard"
                anchorOrigin={{vertical: "bottom", horizontal: "center"}}
                action={(
                    <IconButton
                        size="small"
                        aria-label="close"
                        color="inherit"
                        onClick={() => setStackTraceCopiedSnackbarOpen(false)}
                    >
                        <CloseIcon fontSize="small"/>
                    </IconButton>
                )}
            />
        </>
    );
};

export default ErrorDetails;
