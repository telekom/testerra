import type {Theme} from "@mui/material/styles";
import type {SystemStyleObject} from "@mui/system";

export type ReportThemePriorityMessagesStyles = {
    container: SystemStyleObject<Theme>;
    content: SystemStyleObject<Theme>;
    list: SystemStyleObject<Theme>;
    listItem: (backgroundColor: string) => SystemStyleObject<Theme>;
};

export const priorityMessages: ReportThemePriorityMessagesStyles = {
    container: {
        mt: 3,
    },
    content: {
        p: 0,
        ":last-child": {p: 0},
        color: "white",
    },
    list: {
        p: 0,
    },
    listItem: (backgroundColor: string) => ({
        display: "block",
        px: 1.5,
        py: 0.5,
        lineHeight: 1,
        backgroundColor,
    }),
};
