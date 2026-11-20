import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";

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
