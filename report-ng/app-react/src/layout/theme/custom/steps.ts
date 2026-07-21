import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";
import type {CSSProperties} from "react";

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
        },
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
        top: "24px",
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
        },
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
        height: 90,
    },
    screenshotImage: {
        width: "100%",
        height: "100%",
        objectFit: "cover",
        cursor: "pointer",
        display: "block",
    },
};
