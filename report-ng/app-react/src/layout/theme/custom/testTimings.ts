import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";

export type ReportThemeTestTimingsStyles = {
    container: SystemStyleObject<Theme>;
    rangeSelect: SystemStyleObject<Theme>;
    methodAutocomplete: SystemStyleObject<Theme>;
    configMethodsCell: SystemStyleObject<Theme>;
    configMethodsStack: SystemStyleObject<Theme>;
};

export const testTimings: ReportThemeTestTimingsStyles = {
    container: {
        width: "100%",
    },
    rangeSelect: {
        width: "50%",
    },
    methodAutocomplete: {
        width: "100%",
    },
    configMethodsCell: {
        display: "flex",
        justifyContent: "flex-end",
        alignItems: "center",
    },
    configMethodsStack: {
        alignItems: "center",
    },
};
