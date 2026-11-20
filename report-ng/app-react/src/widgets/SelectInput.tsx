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

import {Box, FormControl, MenuItem} from "@mui/material";
import InputLabel from "@mui/material/InputLabel";
import Select from "@mui/material/Select";
import type {SxProps, Theme} from "@mui/material/styles";

type SelectInputProps<T> = {
    label: string,
    value: T,
    onChange: (value: T) => void,
    menuItems: { value: T; label: string }[]
    sx?: SxProps<Theme>;
}

const SelectInput = <T extends string | number>({label, value, onChange, menuItems, sx}: SelectInputProps<T>) => {

    return (
        <Box sx={sx}>
            <FormControl fullWidth>
                <InputLabel id="demo-simple-select-label">{label}</InputLabel>
                <Select
                    labelId="demo-simple-select-label"
                    id="demo-simple-select"
                    value={value}
                    label={label}
                    onChange={e => onChange?.(e.target.value as T)}
                >
                    {menuItems.map(menuItem => (
                        <MenuItem key={menuItem.value} value={menuItem.value}>
                            {menuItem.label}
                        </MenuItem>
                    ))}
                </Select>
            </FormControl>
        </Box>
    );
}

export default SelectInput;
