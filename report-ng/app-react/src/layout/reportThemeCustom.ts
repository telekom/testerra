import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";
import type {CSSProperties} from "react";

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
    listContainer: (height: number) => SystemStyleObject<Theme>;
    list: (height: number) => CSSProperties;
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
        p: 1,
        bgcolor: "#2b2b2b",
        color: "#c0dee0",
        fontSize: 14,
    },
    listContainer: (height: number) => ({
        maxHeight: height,
    }),
    list: (height: number) => ({
        height,
        width: "100%",
    }),
};

export type ReportThemeGeneralDetailsStyles = {
    listItem: SystemStyleObject<Theme>;
    nowrapLabel: SystemStyleObject<Theme>
    wrapText: SystemStyleObject<Theme>;
    truncateText: SystemStyleObject<Theme>;
    blockLink: SystemStyleObject<Theme>;
    compactWrappedText: SystemStyleObject<Theme>
};

export const generalDetails: ReportThemeGeneralDetailsStyles = {
    listItem: {
        gap: 1,
        alignItems: "baseline",
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
};
