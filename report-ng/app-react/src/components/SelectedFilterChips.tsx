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

import type {ResultStatus} from "../model/status-service";
import type {FilterType, FiltersState} from "../hooks/useTestListFilters"
import ReportChip from "../widgets/ReportChip";
import {Button, Stack} from "@mui/material";
import {FILTERS} from "../hooks/useTestListFilters";

type SelectedFiltersChipsProps = {
    selectedFilters: FiltersState,
    handleDelete: (filter: FilterType, filterToRemove?: string | ResultStatus) => void;
    handleClearAllClick: () => void;
}

const SelectedFiltersChips = ({selectedFilters, handleDelete, handleClearAllClick}: SelectedFiltersChipsProps) => {

    // loops through each filter type and combines them in one array, e.g. [3, 4, SimpleTest2]
    const chips = (Object.keys(FILTERS) as FilterType[]).flatMap((filterType) => {
        const values = selectedFilters[filterType];
        if (!values || values.length === 0) return [];

        // loops through values for each type and fixes label for status (3 -> Passed)
        return values.map((value) => {
            const filterDef = FILTERS[filterType];

            // returns chip for each value
            return (
                <ReportChip
                    key={String(value)}
                    label={filterDef.getLabel(value)}
                    color={FILTERS[filterType].color}
                    handleDelete={() => handleDelete(filterType, value)}
                    tooltipText={filterDef.tooltipText}
                />
            );
        });
    });

    return (
        <Stack direction="row" spacing={1}>
            {chips}

            {chips.length > 0 && (
                <Button variant="text" onClick={handleClearAllClick}>
                    CLEAR ALL
                </Button>
            )}
        </Stack>
    )
};

export default SelectedFiltersChips;
