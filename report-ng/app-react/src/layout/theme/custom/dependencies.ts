import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";

export type ReportThemeDependenciesStyles = {
    graphContainer: (height: number) => SystemStyleObject<Theme>;
};

export const dependencies: ReportThemeDependenciesStyles = {
    graphContainer: (height: number) => ({
        width: "100%",
        height: `${height}px`,
    }),
};
