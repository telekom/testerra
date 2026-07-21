import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";
import type {CSSProperties} from "react";
import {statusColors} from "./status";

export type ReportThemeGeneralDetailsStyles = {
    listItem: SystemStyleObject<Theme>;
    failedInListItem: SystemStyleObject<Theme>;
    nowrapLabel: SystemStyleObject<Theme>;
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
