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
import {useState} from "react";

type MultiSelectInputProps = {
    label: string,
    values: string[],
    onChange: (value: string[]) => void;
    menuItems: string[];
    sx?: SxProps<Theme>;
    renderValue?: (selected: string[]) => React.ReactNode;
}

const MultiSelectInput = ({label, values, onChange, menuItems, sx, renderValue}: MultiSelectInputProps) => {

    const [isOpen, setIsOpen] = useState(false);
    const availableMenuItems = menuItems.filter(className => !values?.includes(className));

    return (
        <Box sx={sx}>
            <FormControl fullWidth>
                <InputLabel>{label}</InputLabel>
                <Select
                    multiple
                    value={values}
                    label={label}
                    open={isOpen}
                    onOpen={() => setIsOpen(true)}
                    onClose={() => setIsOpen(false)}
                    onChange={(e) => {
                        onChange(e.target.value as unknown as string[]);
                        setIsOpen(false);
                    }}
                    renderValue={renderValue}
                    MenuProps={{
                        anchorOrigin: { vertical: 'bottom', horizontal: 'left' },
                        transformOrigin: { vertical: 'top', horizontal: 'left' },
                    }}
                >
                    {availableMenuItems.length === 0 && (
                        <MenuItem disabled>
                            <em>All items selected</em>
                        </MenuItem>
                    )}
                    {availableMenuItems.map(menuItem => (
                        <MenuItem key={menuItem} value={menuItem}>
                            {menuItem}
                        </MenuItem>
                    ))}
                </Select>
            </FormControl>
        </Box>
    );
}

export default MultiSelectInput;
