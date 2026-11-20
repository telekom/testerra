import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";

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
