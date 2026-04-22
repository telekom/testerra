import ListItem from "@mui/material/ListItem";
import ListItemButton from "@mui/material/ListItemButton";
import ListItemIcon from "@mui/material/ListItemIcon";
import ListItemText from "@mui/material/ListItemText";
import List from "@mui/material/List";
import {Typography} from "@mui/material";

export type ButtonListItem = {
    primaryText: string,
    secondaryText?: string,
    icon: any,
    selected?: boolean;
    value?: any;
}

type ButtonListProps = {
    list: ButtonListItem[],
    disablePadding?: boolean,
    handleClick?: (item: ButtonListItem) => void
}

const ButtonList = ({list, disablePadding, handleClick}: ButtonListProps) => {
    return (
        <List sx={!disablePadding ? {p: 0} : undefined}>
            {list.map((item) => (
                <ListItem disablePadding>
                    <ListItemButton sx={disablePadding ? {pt: 0, pb: 0} : undefined} selected={item.selected} onClick={() => handleClick?.(item)}>
                        <ListItemIcon>
                            {item.icon}
                        </ListItemIcon>

                        {/* This ListItemText uses disabledTypography (to explicitly forbid using the built in typography) to add a separate typography element as its child component
                            The advantage of this is, that we can use the provided noWrap property which automatically truncates the text.*/}
                        <ListItemText disableTypography={true}>
                            <Typography noWrap>{item.primaryText}</Typography>
                            <Typography color="primary" sx={{fontSize: 14}} noWrap>{item.secondaryText}</Typography>
                        </ListItemText>

                    </ListItemButton>
                </ListItem>
            ))}
        </List>
    )
}
export default ButtonList;