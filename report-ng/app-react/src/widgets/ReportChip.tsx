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

import Chip from "@mui/material/Chip";
import type {SxProps, Theme} from '@mui/material/styles';
import type {ChipColor} from "../hooks/useTestListFilters";
import Tooltip from '@mui/material/Tooltip';

type ReportChipProps = {
    label: string,
    sx?: SxProps<Theme>,
    handleDelete?: () => void;
    color?: ChipColor,
    size?: "medium" | "small",
    tooltipText?: string
}

const ReportChip = ({label, sx, handleDelete, color, size, tooltipText}: ReportChipProps) => {
    return (
        <Tooltip title={tooltipText}>
            <Chip label={label} sx={sx} onDelete={handleDelete} color={color} size={size}/>
        </Tooltip>
    );
}

export default ReportChip;
