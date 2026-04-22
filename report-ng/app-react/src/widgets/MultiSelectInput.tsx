import {Box, FormControl, MenuItem} from "@mui/material";
import InputLabel from "@mui/material/InputLabel";
import Select from "@mui/material/Select";
import type {SxProps, Theme} from "@mui/material/styles";

type MultiSelectInputProps = {
    label: string,
    values: string[],
    onChange: (value: string[]) => void;
    menuItems: string[];
    sx?: SxProps<Theme>;
    renderValue?: (selected: string[]) => React.ReactNode;
}

const MultiSelectInput = ({label, values, onChange, menuItems, sx, renderValue}: MultiSelectInputProps) => {

    const availableMenuItems = menuItems.filter(className => !values?.includes(className));

    return (
        <Box sx={sx}>
            <FormControl fullWidth>
                <InputLabel>{label}</InputLabel>
                <Select
                    multiple
                    value={values}
                    label={label}
                    onChange={(e) => {
                        onChange(e.target.value as unknown as string[]);
                    }}
                    renderValue={renderValue}
                    MenuProps={{
                        anchorOrigin: { vertical: 'bottom', horizontal: 'left' },
                        transformOrigin: { vertical: 'top', horizontal: 'left' },
                    }}
                >
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
