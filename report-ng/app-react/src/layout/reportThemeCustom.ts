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

import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";
import type {CSSProperties} from "react";
import {darken} from "@mui/material";

export const statusColors = {
    passed: '#417336',
    skipped: '#f7af3e',
    failed: '#e63946',
    crashed: '#5d6f81',
    running: '#0089b6',
    failed_minor: '#f7af3e',
    expected_failed: '#4f031b'
};

export type Status = keyof typeof statusColors;

export type ReportThemeLogLineStyles = {
    root: SystemStyleObject<Theme>;
    expandToggleWrapper: SystemStyleObject<Theme>;
    expandToggle: SystemStyleObject<Theme>;
    content: SystemStyleObject<Theme>;
    methodLink: SystemStyleObject<Theme>;
    message: SystemStyleObject<Theme>;
    details: SystemStyleObject<Theme>;
};

export type ReportThemeLogConsoleStyles = {
    paper: SystemStyleObject<Theme>;
    listContainer: (height: number | "auto") => SystemStyleObject<Theme>;
    list: (height: number | "auto") => CSSProperties;
};

export const logLine: ReportThemeLogLineStyles = {
    root: {
        display: "grid",
        gridTemplateColumns: "20px minmax(0, 1fr)",
        columnGap: 1,
        alignItems: "start",
        fontFamily: "monospace",
        overflowWrap: "anywhere",
        width: "100%",
        "&.error": {backgroundColor: "#5c0c12"},
        "&.warn": {backgroundColor: "#784b05"},
        a: {color: "#6e95ed", textDecorationColor: "#6e95ed"},
    },
    expandToggleWrapper: {
        display: "inline-flex",
        justifyContent: "center",
        width: 20,
    },
    expandToggle: {
        cursor: "pointer",
        userSelect: "none",
    },
    content: {
        minWidth: 0,
    },
    methodLink: {
        color: "inherit",
    },
    message: {
        whiteSpace: "pre-wrap",
    },
    details: {
        mt: 0.5,
        whiteSpace: "pre-wrap",
    },
};

export const logConsole: ReportThemeLogConsoleStyles = {
    paper: {
        bgcolor: "#2b2b2b",
        color: "#c0dee0",
        fontSize: 14,
        borderRadius: 1,
        overflow: "hidden",
    },
    listContainer: (height: number | "auto") => ({
        maxHeight: height,
    }),
    list: (height: number | "auto") => ({
        height,
        width: "100%",
    }),
};

export type ReportThemeGeneralDetailsStyles = {
    listItem: SystemStyleObject<Theme>;
    failedInListItem: SystemStyleObject<Theme>;
    nowrapLabel: SystemStyleObject<Theme>
    wrapText: SystemStyleObject<Theme>;
    truncateText: SystemStyleObject<Theme>;
    blockLink: SystemStyleObject<Theme>;
    compactWrappedText: SystemStyleObject<Theme>;
    statusChip: (statusColor: string) => SystemStyleObject<Theme>;
    configurationChip: SystemStyleObject<Theme>;
    failsChip: SystemStyleObject<Theme>;
    topSpacingText: SystemStyleObject<Theme>;
    failsStack: SystemStyleObject<Theme>;
    footerContainer: SystemStyleObject<Theme>;
    nextMethodStack: SystemStyleObject<Theme>;
    lastScreenshotImage: CSSProperties;
    lastScreenshotCardContent: SystemStyleObject<Theme>;
};

export const generalDetails: ReportThemeGeneralDetailsStyles = {
    listItem: {
        gap: 1,
        alignItems: "baseline",
    },
    failedInListItem: {
        gap: "8px",
        alignItems: "center",
    },
    nowrapLabel: {
        whiteSpace: "nowrap",
    },
    wrapText: {
        whiteSpace: "normal",
        overflowWrap: "anywhere",
    },
    truncateText: {
        overflow: "hidden",
        textOverflow: "ellipsis",
    },
    blockLink: {
        minWidth: 0,
        display: "block",
        maxWidth: "100%",
    },
    compactWrappedText: {
        display: "block",
        lineHeight: 1.15,
        m: 0,
    },
    statusChip: (statusColor: string) => ({
        background: statusColor,
        color: "white",
        fontSize: "16px",
        fontWeight: "400",
    }),
    configurationChip: {
        color: "white",
        fontWeight: "400",
    },
    failsChip: {
        background: statusColors.expected_failed,
        color: "white",
    },
    topSpacingText: {
        mt: 1,
    },
    failsStack: {
        alignItems: "center",
        my: 1,
    },
    footerContainer: {
        mt: 2,
    },
    nextMethodStack: {
        alignItems: "flex-start",
        maxWidth: "100%",
    },
    lastScreenshotImage: {
        display: "block",
        width: "100%",
        height: "auto",
        cursor: "pointer",
    },
    lastScreenshotCardContent: {
        p: 0,
        "&:last-child": {
            p: 0,
        },
    },
};

export type ReportThemeCodeViewStyles = {
    codeView: SystemStyleObject<Theme>;
    error: SystemStyleObject<Theme>;
    warn: SystemStyleObject<Theme>;
    number: SystemStyleObject<Theme>;
    line: SystemStyleObject<Theme>;
    pre: SystemStyleObject<Theme>;
};

export const codeView: ReportThemeCodeViewStyles = {
    codeView: {
        //padding: 0.5em;
        fontSize: "14px",
        backgroundColor: "#2b2b2b",
        color: "#c0dee0",
        lineHeight: "1.5em",
        fontFamily: "monospace",
        //word-break: break-all;
        width: "100%",
        minWidth: 0,
        whiteSpace: "normal",
    },
    error: {
        background: darken(statusColors.failed, 0.36)
    },
    warn: {
        background: darken(statusColors.skipped, 0.36)
    },
    number: {
        width: "2rem",
        color: "#808080",
    },
    line: {
        padding: "2px 4px",
        whiteSpace: "pre-wrap",
        overflowWrap: "break-word",
        wordBreak: "break-word",
    },
    pre: {
        whiteSpace: "pre-wrap",
    }
}

export type ReportThemeStepsStyles = {
    timelineContainer: SystemStyleObject<Theme>;
    listContainer: SystemStyleObject<Theme>;
    stepContainer: SystemStyleObject<Theme>;
    actionContainer: SystemStyleObject<Theme>;
    actionGroupItem: SystemStyleObject<Theme>;
    logContainer: SystemStyleObject<Theme>;
    clickPathText: SystemStyleObject<Theme>;
    stepNumber: SystemStyleObject<Theme>;
    actionLabel: SystemStyleObject<Theme>;
    actionGroupContainer: SystemStyleObject<Theme>;
    errorContextAccordion: SystemStyleObject<Theme>;
    errorContextAccordionSummary: (statusColor: string) => SystemStyleObject<Theme>;
    errorContextCodeView: SystemStyleObject<Theme>;
    screenshotCard: SystemStyleObject<Theme>;
    screenshotImage: CSSProperties;
};

export const steps: ReportThemeStepsStyles = {
    timelineContainer: {
        position: "relative",
        "&:after": {
            content: '""',
            width: "2px",
            position: "absolute",
            top: "0.5rem",
            bottom: 0,
            left: "calc(20% + 21px)",
            zIndex: 1,
            background: "#C5C5C5",
        }
    },
    listContainer: {
        mx: 3,
    },
    stepContainer: {
        position: "relative",
        pb: 0,
        mb: 6,
    },
    actionContainer: {
        mt: 4,
    },
    actionGroupItem: {
        pl: 6,
    },
    logContainer: {
        width: "100%",
    },
    clickPathText: {
        overflowWrap: "anywhere",
    },
    stepNumber: {
        textAlign: "right",
        width: "20%",
        overflowWrap: "break-word",
    },
    actionLabel: {
        textAlign: "right",
        width: "20%",
        whiteSpace: "wrap",
        overflowWrap: "anywhere",
        position: "sticky",
        top: "24px"
    },
    actionGroupContainer: {
        position: "relative",
        left: "20%",
        width: "60%",
        mt: "-2em",
        "&:before": {
            content: '""',
            width: "14px",
            height: "14px",
            background: "#C5C5C5",
            border: "2px solid #FFFFFF",
            borderRadius: "50%",
            position: "absolute",
            left: "15px",
            top: "3px",
            zIndex: 2,
        }
    },
    errorContextAccordion: {
        borderRadius: 1,
        overflow: "hidden",
    },
    errorContextAccordionSummary: (statusColor: string) => ({
        background: statusColor,
        color: "white",
        borderTopLeftRadius: "inherit",
        borderTopRightRadius: "inherit",
        "& .MuiAccordionSummary-expandIconWrapper": {
            color: "inherit",
        },
    }),
    errorContextCodeView: {
        maxWidth: "100%",
        overflowX: "visible",
        "& > .MuiBox-root": {
            width: "100% !important",
            minWidth: 0,
        },
        "& .hljs, & .hljs *": {
            whiteSpace: "pre-wrap",
            overflowWrap: "break-word",
            wordBreak: "break-word",
        },
    },
    screenshotCard: {
        width: 160,
        height: 90
    },
    screenshotImage: {
        width: "100%",
        height: "100%",
        objectFit: "cover",
        cursor: "pointer",
        display: "block",
    }
}

export type ReportThemeMethodDetailsPageStyles = {
    container: SystemStyleObject<Theme>;
    outletContainer: SystemStyleObject<Theme>;
};

export const methodDetailsPage: ReportThemeMethodDetailsPageStyles = {
    container: {
        width: "100%",
        p: "24px 32px",
    },
    outletContainer: {
        p: "24px 0px",
    },
};

export type ReportThemeDurationCardStyles = {
    content: SystemStyleObject<Theme>;
    divider: SystemStyleObject<Theme>;
    header: SystemStyleObject<Theme>;
    metaGrid: SystemStyleObject<Theme>;
};

export const durationCard: ReportThemeDurationCardStyles = {
    content: {
        p: 0,
        ":last-child": {padding: 0},
    },
    divider: {
        mt: "0 !important",
    },
    header: {
        alignItems: "center",
        justifyContent: "center",
        p: 2,
    },
    metaGrid: {
        px: 2,
        py: 1,
        mt: "0 !important",
    },
};

export type ReportThemePriorityMessagesStyles = {
    container: SystemStyleObject<Theme>;
    content: SystemStyleObject<Theme>;
    list: SystemStyleObject<Theme>;
    listItem: (backgroundColor: string) => SystemStyleObject<Theme>;
};

export const priorityMessages: ReportThemePriorityMessagesStyles = {
    container: {
        mt: 3,
    },
    content: {
        p: 0,
        ":last-child": {p: 0},
        color: "white",
    },
    list: {
        p: 0,
    },
    listItem: (backgroundColor: string) => ({
        display: "block",
        px: 1.5,
        py: 0.5,
        lineHeight: 1,
        backgroundColor,
    }),
};

export type ReportThemeSessionInfoStyles = {
    cardsContainer: SystemStyleObject<Theme>;
    row: SystemStyleObject<Theme>;
    rowCompact: SystemStyleObject<Theme>;
    labelWide: SystemStyleObject<Theme>;
    labelNarrow: SystemStyleObject<Theme>;
    sectionTitle: SystemStyleObject<Theme>;
};

export const sessionInfo: ReportThemeSessionInfoStyles = {
    cardsContainer: {
        display: "flex",
        flexDirection: "column",
        gap: 2,
    },
    row: {
        gap: 1,
        pl: 2,
    },
    rowCompact: {
        gap: 1,
    },
    labelWide: {
        minWidth: 140,
    },
    labelNarrow: {
        minWidth: 80,
    },
    sectionTitle: {
        mt: 2,
    },
};

export type ReportThemeDependenciesStyles = {
    graphContainer: (height: number) => SystemStyleObject<Theme>;
};

export const dependencies: ReportThemeDependenciesStyles = {
    graphContainer: (height: number) => ({
        width: "100%",
        height: `${height}px`,
    }),
};

export type ReportThemeErrorDetailsStyles = {
    cardHeader: (statusColor: string) => SystemStyleObject<Theme>;
    title: SystemStyleObject<Theme>;
    detailsTitle: SystemStyleObject<Theme>;
    stackHeader: SystemStyleObject<Theme>;
    stackCauseTitle: SystemStyleObject<Theme>;
    accordionDetails: SystemStyleObject<Theme>;
};

export const errorDetails: ReportThemeErrorDetailsStyles = {
    cardHeader: (statusColor: string) => ({
        backgroundColor: statusColor,
        color: "white",
        "& .MuiTypography-root": {color: "inherit"},
    }),
    title: {
        overflowWrap: "anywhere",
    },
    detailsTitle: {
        mb: 1,
    },
    stackHeader: {
        justifyContent: "space-between",
        alignItems: "center",
        mb: 1,
    },
    stackCauseTitle: {
        overflowWrap: "anywhere",
    },
    accordionDetails: {
        p: 0,
    },
};
