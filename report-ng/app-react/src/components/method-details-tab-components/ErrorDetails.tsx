import {useMemo} from "react";
import {useOutletContext} from "react-router-dom";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import type {ErrorContext, LayoutCheckContext, StackTraceCause} from "../../model/report-model/framework_pb.ts";
import {StatusService} from "../../model/status-service.tsx";
import {
    Accordion,
    AccordionDetails,
    AccordionSummary,
    Alert,
    Box,
    IconButton,
    Stack,
    Typography,
} from "@mui/material";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";
import ReportCard from "../../widgets/ReportCard.tsx";
import LayoutComparison from "../LayoutComparison.tsx";

type ErrorDetail = {
    failureAspect: MethodDetails["failureAspects"][number];
    errorContext?: ErrorContext;
    layoutCheckContext?: LayoutCheckContext;
};

const ErrorDetails = () => {
    const methodDetail = useOutletContext<MethodDetails | undefined>();

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

    if (!methodDetail) {
        return <Alert severity="info">No method selected.</Alert>;
    }

    if (!methodDetail.numDetails) {
        return <Alert severity="info">This method has no error context information</Alert>;
    }

    return (
        <>
            <Stack spacing={2}>
            {errorDetails.map((errorDetail, index) => {
                const statusColor = StatusService.getColor(methodDetail.methodContext.resultStatus ?? "");
                const stackTrace = errorDetail.errorContext?.stackTrace ?? [];
                const scriptSource = errorDetail.errorContext?.scriptSource;
                const scriptMark = scriptSource?.mark;
                const methodName = methodDetail.methodContext.contextValues?.name ?? "";

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
                                    <LayoutComparison
                                        layoutCheckContext={errorDetail.layoutCheckContext}
                                        sessionContexts={methodDetail.sessionContexts}
                                    />
                                )}
                            </Box>
                        )}
                        details={(
                            <>
                                <Typography variant="subtitle2" sx={{mb: 1}}>
                                    Origin ({scriptSource?.fileName ?? "unknown"})
                                </Typography>
                                <Box sx={{
                                    border: "1px solid",
                                    borderColor: "divider",
                                    borderRadius: 1,
                                    p: 1,
                                    fontFamily: "monospace",
                                    fontSize: 13
                                }}>
                                    {(scriptSource?.lines ?? []).map((sourceLine, lineIndex) => (
                                        <Box
                                            key={`source-line-${lineIndex}`}
                                            sx={{
                                                whiteSpace: "pre-wrap",
                                                overflowWrap: "anywhere",
                                                backgroundColor: sourceLine.lineNumber === scriptMark ? "action.hover" : "transparent",
                                                px: 0.5
                                            }}
                                        >
                                            {(sourceLine.lineNumber ?? 0).toString().padStart(4, " ")}: {sourceLine.line ?? ""}
                                        </Box>
                                    ))}
                                </Box>
                            </>
                        )}
                        content={(
                            <>
                                <Stack direction="row" sx={{justifyContent: "space-between", alignItems: "center", mb: 1}}>
                                    <Typography variant="subtitle2">Stacktrace</Typography>
                                    <IconButton
                                        size="small"
                                        title="Copy to clipboard"
                                        onClick={() => {
                                            void copyStackTraceToClipboard(stackTrace);
                                        }}
                                    >
                                        <ContentCopyIcon fontSize="small"/>
                                    </IconButton>
                                </Stack>

                                <Stack spacing={1}>
                                    {stackTrace.map((cause, causeIndex) => (
                                        <Accordion key={`cause-${causeIndex}`} disableGutters>
                                            <AccordionSummary expandIcon={<ExpandMoreIcon/>}>
                                                <Typography variant="body2" sx={{overflowWrap: "anywhere"}}>
                                                    {cause.className
                                                        ? classNameConverter(cause.className, ClassName.simpleName)
                                                        : "UnknownClass"}
                                                    {cause.message ? `: ${cause.message}` : ""}
                                                </Typography>
                                            </AccordionSummary>
                                            <AccordionDetails sx={{pt: 0}}>
                                                <Box sx={{
                                                    fontFamily: "monospace",
                                                    fontSize: 13,
                                                    border: "1px solid",
                                                    borderColor: "divider",
                                                    borderRadius: 1,
                                                    p: 1
                                                }}>
                                                    {(cause.stackTraceElements ?? []).map((line, lineIndex) => (
                                                        <Box
                                                            key={`stack-line-${lineIndex}`}
                                                            sx={{
                                                                whiteSpace: "pre-wrap",
                                                                overflowWrap: "anywhere",
                                                                color: methodName && line.includes(methodName) ? "error.main" : "inherit"
                                                            }}
                                                        >
                                                            {line}
                                                        </Box>
                                                    ))}
                                                </Box>
                                            </AccordionDetails>
                                        </Accordion>
                                    ))}
                                </Stack>
                            </>
                        )}
                    />
                );
            })}
            </Stack>
        </>
    );
};

export default ErrorDetails;
