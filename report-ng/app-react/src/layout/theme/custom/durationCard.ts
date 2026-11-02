import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";

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
