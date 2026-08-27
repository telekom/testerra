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

import type {FilterChip} from "../hooks/useChipListFilters"
import ReportChip from "../widgets/ReportChip";
import {Button, Stack} from "@mui/material";

type SelectedFilterChipsProps = {
    chips: FilterChip[];
    handleClearAllClick: () => void;
}

const SelectedFilterChips = ({chips, handleClearAllClick}: SelectedFilterChipsProps) => {
    return (
        <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
            {chips.map((chip) => (
                <ReportChip
                    key={chip.id}
                    label={chip.label}
                    color={chip.color}
                    handleDelete={chip.onDelete}
                    tooltipText={chip.tooltipText}
                />
            ))}

            {chips.length > 0 && (
                <Button variant="text" onClick={handleClearAllClick}>
                    CLEAR ALL
                </Button>
            )}
        </Stack>
    )
};

export default SelectedFilterChips;
