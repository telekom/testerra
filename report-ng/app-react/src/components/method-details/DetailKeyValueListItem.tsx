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
