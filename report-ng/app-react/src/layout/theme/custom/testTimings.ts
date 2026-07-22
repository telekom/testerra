import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";

export type ReportThemeTestTimingsStyles = {
    barColor: string;
    barColorPale: string;
    container: SystemStyleObject<Theme>;
    rangeSelect: SystemStyleObject<Theme>;
    methodAutocomplete: SystemStyleObject<Theme>;
    configMethodsCell: SystemStyleObject<Theme>;
    configMethodsStack: SystemStyleObject<Theme>;
};

export const testTimings: ReportThemeTestTimingsStyles = {
    barColor: "#6897EA",
    barColorPale: "#c8d4f4",
    container: {
        width: "100%",
        maxWidth: {sm: "100%", md: "1700px"},
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
