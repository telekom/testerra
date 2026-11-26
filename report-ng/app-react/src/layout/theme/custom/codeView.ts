import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";
import {darken} from "@mui/material";
import {statusColors} from "./status";

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
        fontSize: "14px",
        backgroundColor: "#2b2b2b",
        color: "#c0dee0",
        lineHeight: "1.5em",
        fontFamily: "monospace",
        width: "100%",
        minWidth: 0,
        whiteSpace: "normal",
    },
    error: {
        background: darken(statusColors.failed, 0.36),
    },
    warn: {
        background: darken(statusColors.skipped, 0.36),
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
    },
};
