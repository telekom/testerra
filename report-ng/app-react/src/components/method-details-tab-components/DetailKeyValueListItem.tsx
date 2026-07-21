import {ListItem, Typography, useTheme} from "@mui/material";
import type {ReactNode} from "react";

interface DetailKeyValueListItemProps {
    label: string;
    value: ReactNode;
    compact?: boolean;
    wideLabel?: boolean;
}

const DetailKeyValueListItem = ({label, value, compact = false, wideLabel = true}: DetailKeyValueListItemProps) => {
    const theme = useTheme();

    return (
        <ListItem
            disablePadding
            sx={compact ? theme.custom.sessionInfo.rowCompact : theme.custom.sessionInfo.row}
        >
            <Typography
                variant="caption"
                color="text.secondary"
                sx={wideLabel ? theme.custom.sessionInfo.labelWide : theme.custom.sessionInfo.labelNarrow}
            >
                {label}
            </Typography>
            {value}
        </ListItem>
    );
};

export default DetailKeyValueListItem;
