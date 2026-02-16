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
