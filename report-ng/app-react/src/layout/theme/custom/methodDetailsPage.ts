import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";

export type ReportThemeMethodDetailsPageStyles = {
    container: SystemStyleObject<Theme>;
    outletContainer: SystemStyleObject<Theme>;
};

export const methodDetailsPage: ReportThemeMethodDetailsPageStyles = {
    container: {
        width: "100%",
        // p: "24px 32px",
    },
    outletContainer: {
        p: "24px 0px",
    },
};
