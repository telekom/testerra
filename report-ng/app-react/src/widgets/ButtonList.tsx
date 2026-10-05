/*
 * Testerra
 *
 * (C) 2026, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
    fixedItemHeight?: number | string,
    handleClick?: (item: ButtonListItem) => void
}

const ButtonList = ({list, disablePadding, fixedItemHeight, handleClick}: ButtonListProps) => {
    return (
        <List sx={!disablePadding ? {p: 0} : undefined}>
            {list.map((item, index) => (
                <ListItem key={index} disablePadding>
                    <ListItemButton
                        sx={{
                            alignItems: "flex-start",
                            ...(disablePadding ? {pt: 0, pb: 0} : undefined),
                            ...(fixedItemHeight !== undefined ? {height: fixedItemHeight} : undefined)
                        }}
                        selected={item.selected}
                        onClick={() => handleClick?.(item)}
                    >
                        <ListItemIcon sx={{alignSelf: "flex-start", mt: 0.5}}>
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
