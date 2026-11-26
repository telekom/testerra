import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";

export type ReportThemeDurationCardStyles = {
    divider: SystemStyleObject<Theme>;
    header: SystemStyleObject<Theme>;
    metaGrid: SystemStyleObject<Theme>;
};

export const durationCard: ReportThemeDurationCardStyles = {
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
