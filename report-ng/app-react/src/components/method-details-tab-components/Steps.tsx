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
import {useOutletContext, useParams} from "react-router-dom";
import {Accordion, AccordionDetails, AccordionSummary, Box, Card, Stack, Typography, useTheme} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {
    type ClickPathEvent,
    type ErrorContext,
    type LogMessage, ResultStatusType,
    type TestStep,
    type TestStepAction,
    type TestStepActionEntry,
} from "../../model/report-model/framework_pb.ts";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import {useReportData} from "../../provider/DataProvider.tsx";
import {dateFormatter} from "../../utils/dateFormatter.ts";
import LazyImage from "../../widgets/LazyImage.tsx";
import Modal from "../../widgets/Modal.tsx";
import CodeView from "../CodeView.tsx";
import NoResultsCard from "../../widgets/NoResultsCard.tsx";
import {LogConsole} from "../LogConsole.tsx";
import type {ILogEntry} from "../../model/Logs.ts";
import {StatusService} from "../../model/status-service.tsx";
import {useScrollToElementById} from "../../hooks/useScrollToElementById.ts";

const EntryType = {
    SCREENSHOT: "SCREENSHOT",
    ERROR_CONTEXT: "ERROR_CONTEXT",
    LOG_MESSAGE: "LOG_MESSAGE",
    CLICK_PATH_EVENT: "CLICK_PATH_EVENT",
} as const;
type EntryType = typeof EntryType[keyof typeof EntryType];

interface TestStepActionGroup {
    screenshotIds: string[];
    clickPathEvents: ClickPathEvent[];
    logMessages: LogMessage[];
    errorContexts: ErrorContext[];
}

interface GroupedAction extends TestStepAction {
    groups: TestStepActionGroup[];
}

interface GroupedStep extends TestStep {
    actions: GroupedAction[];
}

const getEntryType = (entry: TestStepActionEntry) => {
    if (entry.screenshotId) {
        return EntryType.SCREENSHOT;
    }
    if (entry.errorContext) {
        return EntryType.ERROR_CONTEXT;
    }
    if (entry.logMessageId) {
        return EntryType.LOG_MESSAGE;
    }
    if (entry.clickPathEvent) {
        return EntryType.CLICK_PATH_EVENT;
    }
    return undefined;
};

const buildActionGroups = (
    entries: TestStepActionEntry[] = [],
    logsById: { [key: string]: LogMessage },
): TestStepActionGroup[] => {
    const groups: TestStepActionGroup[] = [];
    let currentGroup: TestStepActionGroup | undefined;
    let lastEntryType: EntryType | undefined;

    entries.forEach((entry) => {
        const currentEntryType = getEntryType(entry);
        if (currentEntryType === undefined) {
            return;
        }

        if (currentEntryType !== lastEntryType) {
            currentGroup = {
                screenshotIds: [],
                clickPathEvents: [],
                logMessages: [],
                errorContexts: [],
            };
            groups.push(currentGroup);
        }

        if (!currentGroup) {
            return;
        }

        switch (currentEntryType) {
            case EntryType.SCREENSHOT:
                if (entry.screenshotId) {
                    currentGroup.screenshotIds.push(entry.screenshotId);
                }
                break;
            case EntryType.ERROR_CONTEXT:
                if (entry.errorContext) {
                    currentGroup.errorContexts.push(entry.errorContext);
                }
                break;
            case EntryType.CLICK_PATH_EVENT:
                if (entry.clickPathEvent) {
                    currentGroup.clickPathEvents.push(entry.clickPathEvent);
                }
                break;
            case EntryType.LOG_MESSAGE:
                if (entry.logMessageId && logsById[entry.logMessageId]) {
                    currentGroup.logMessages.push(logsById[entry.logMessageId]);
                }
                break;
        }

        lastEntryType = currentEntryType;
    });

    return groups;
};

const getErrorContextTitle = (errorContext: ErrorContext) => {
    if (errorContext.description) {
        return errorContext.description;
    }
    const relevantCause = errorContext.stackTrace?.[0];
    if (relevantCause?.className || relevantCause?.message) {
        return `${relevantCause.className ?? "Error"}${relevantCause.message ? `: ${relevantCause.message}` : ""}`;
    }
    return "Error context";
};

const toLogEntries = (logMessages: LogMessage[]): ILogEntry[] => {
    return logMessages.map(logMessage => ({
        ...logMessage,
    }));
};

const Steps = () => {
    const methodDetail = useOutletContext<MethodDetails | undefined>();
    const {executionMngr} = useReportData();
    const {stepId} = useParams();
    const theme = useTheme();
    const [isScreenshotModalOpen, setIsScreenshotModalOpen] = useState(false);
    const [selectedScreenshotId, setSelectedScreenshotId] = useState<string | undefined>(undefined);

    const logsById = useMemo(() => executionMngr?.getLogs() ?? {}, [executionMngr]);

    const groupedSteps = useMemo<GroupedStep[]>(() => {
        const testSteps = methodDetail?.methodContext.testSteps ?? [];
        return testSteps.map((testStep) => ({
            ...testStep,
            actions: (testStep.actions ?? []).map((action) => ({
                ...action,
                groups: buildActionGroups(action.entries ?? [], logsById),
            })),
        }));
    }, [methodDetail, logsById]);

    const allScreenshotIds = useMemo(() => (
        methodDetail?.methodContext.testSteps
            ?.flatMap(step => step.actions ?? [])
            .flatMap(action => action.entries ?? [])
            .map(entry => entry.screenshotId)
            .filter((id): id is string => Boolean(id)) ?? []
    ), [methodDetail]);

    useScrollToElementById(stepId ? `step${stepId}` : undefined, groupedSteps.length);

    if (!methodDetail) {
        return <NoResultsCard title="No method selected"/>;
    }

    if (groupedSteps.length === 0) {
        return <NoResultsCard title="No steps found for this method"/>;
    }

    return (
        <>
            <Box sx={theme.custom.steps.listContainer}>
                <Stack
                    sx={theme.custom.steps.timelineContainer}
                >
                    {groupedSteps.map((testStep, stepIndex) => {
                        const stepNumber = stepIndex + 1;
                        return (
                            <Box key={`step-${stepIndex}`} sx={theme.custom.steps.stepContainer}>
                                <Typography
                                    variant="h5"
                                    id={`step${stepNumber}`}
                                    sx={theme.custom.steps.stepNumber}
                                >
                                    {stepNumber} {testStep.name}
                                </Typography>
                                {(testStep.actions ?? []).map((action, actionIndex) => (
                                    <Box key={`step-${stepIndex}-action-${actionIndex}`} sx={theme.custom.steps.actionContainer}>
                                        <Typography
                                            variant="body1"
                                            sx={theme.custom.steps.actionLabel}
                                        >
                                            {stepNumber}.{actionIndex + 1} {action.name}
                                            <br/>
                                            <Typography
                                                component="span"
                                                variant="body2"
                                                color="text.secondary"
                                                title={dateFormatter(action.timestamp, "long")}
                                            >
                                                {dateFormatter(action.timestamp, "long")}
                                            </Typography>
                                        </Typography>
                                        <Stack
                                            sx={theme.custom.steps.actionGroupContainer}
                                            spacing={3}
                                        >
                                            {(action.groups ?? []).map((group, groupIndex) => (
                                                <Box
                                                    key={`step-${stepIndex}-action-${actionIndex}-group-${groupIndex}`}
                                                    sx={theme.custom.steps.actionGroupItem}
                                                >
                                                    {group.errorContexts.map((errorContext, errorContextIndex) => (
                                                        <Accordion
                                                            key={`error-${stepIndex}-${actionIndex}-${groupIndex}-${errorContextIndex}`}
                                                            disableGutters
                                                            sx={theme.custom.steps.errorContextAccordion}
                                                        >
                                                            <AccordionSummary expandIcon={<ExpandMoreIcon/>}
                                                                              sx={theme.custom.steps.errorContextAccordionSummary(StatusService.getColor(errorContext.optional ? ResultStatusType.SKIPPED : ResultStatusType.FAILED))}>
                                                                <Typography variant="body2"
                                                                            sx={{overflowWrap: "anywhere"}}>
                                                                    {getErrorContextTitle(errorContext)}
                                                                </Typography>
                                                            </AccordionSummary>
                                                            <AccordionDetails sx={{p: 0}}>
                                                                {errorContext.scriptSource ? (
                                                                    <Box
                                                                        sx={theme.custom.steps.errorContextCodeView}
                                                                    >
                                                                        <CodeView source={errorContext.scriptSource}/>
                                                                    </Box>
                                                                ) : (
                                                                    <Typography variant="body2" color="text.secondary">
                                                                        No script source available.
                                                                    </Typography>
                                                                )}
                                                            </AccordionDetails>
                                                        </Accordion>
                                                    ))}

                                                    {group.screenshotIds.length > 0 && (
                                                        <Stack direction="row" spacing={3} flexWrap="wrap" useFlexGap>
                                                            {group.screenshotIds.map((screenshotId) => (
                                                                <Card
                                                                    key={`screenshot-${stepIndex}-${actionIndex}-${groupIndex}-${screenshotId}`}
                                                                    variant="outlined"
                                                                    sx={theme.custom.steps.screenshotCard}
                                                                >
                                                                    <LazyImage
                                                                        fileId={screenshotId}
                                                                        onClick={(file) => {
                                                                            if (file.id) {
                                                                                setSelectedScreenshotId(file.id);
                                                                                setIsScreenshotModalOpen(true);
                                                                            }
                                                                        }}
                                                                        style={theme.custom.steps.screenshotImage}
                                                                    />
                                                                </Card>
                                                            ))}
                                                        </Stack>
                                                    )}

                                                    {group.logMessages.length > 0 && (
                                                        <Box sx={theme.custom.steps.logContainer}>
                                                            <LogConsole
                                                                logs={toLogEntries(group.logMessages)}
                                                                isInStepsList={true}
                                                            />
                                                        </Box>
                                                    )}

                                                    {group.clickPathEvents.length > 0 && (
                                                        <Box>
                                                            {group.clickPathEvents.map((clickPathEvent, clickPathEventIndex) => (
                                                                <Typography
                                                                    key={`click-${stepIndex}-${actionIndex}-${groupIndex}-${clickPathEventIndex}`}
                                                                    variant="body2"
                                                                    color="text.secondary"
                                                                    sx={theme.custom.steps.clickPathText}
                                                                >
                                                                    {clickPathEvent.type ?? "Event"}: {clickPathEvent.subject ?? "-"}
                                                                </Typography>
                                                            ))}
                                                        </Box>
                                                    )}
                                                </Box>
                                            ))}
                                        </Stack>
                                    </Box>
                                ))}
                            </Box>
                        );
                    })}
                </Stack>
            </Box>

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
export default Steps;
