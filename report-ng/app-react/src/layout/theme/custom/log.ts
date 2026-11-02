import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";
import type {CSSProperties} from "react";

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
