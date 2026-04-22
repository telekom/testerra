import {Box, FormControl, MenuItem} from "@mui/material";
import InputLabel from "@mui/material/InputLabel";
import Select from "@mui/material/Select";
import ReportChip from "./ReportChip";
import type {SxProps, Theme} from "@mui/material/styles";
import {StatusService} from "../model/status-service";
import type {ResultStatus} from "../model/status-service";

type SelectInputProps = {
    label: string
    selectedStatuses?: ResultStatus[],
    onChange: (value: ResultStatus[]) => void;
    menuItems: number[];
    sx?: SxProps<Theme>;
}

const StatusSelectInput = ({label, selectedStatuses, onChange, menuItems, sx}: SelectInputProps) => {

    const availableMenuItems = menuItems.filter(status => !selectedStatuses?.includes(status as ResultStatus));

    return (
        <Box sx={sx}>
            <FormControl fullWidth>
                <InputLabel>{label}</InputLabel>
                <Select
                    multiple
                    value={selectedStatuses}
                    label={label}
                    onChange={(e) =>
                        onChange(e.target.value as ResultStatus[])
                    }
                    sx={{height: "56px"}}
                    renderValue={(selected: ResultStatus[]) => {
                        if (!selected?.length) return "";
                        if (selected.length === 1) return "1 status selected";
                        return `${selected.length} statuses selected`;
                    }}
                >
                    {availableMenuItems.length === 0 && (
                        <MenuItem disabled>
                            <em>All status selected</em>
                        </MenuItem>
                    )}
                    {availableMenuItems
                        .map(status => {
                        const statusInformation = StatusService.get(String(status));
                        if (!statusInformation) return null;

                        return (
                            <MenuItem key={status} value={status}>
                                <ReportChip label={statusInformation.label}
                                            size="small"
                                            sx={{background: statusInformation.color, color: "white"}}/>
                            </MenuItem>
                        )
                    })}
                </Select>
            </FormControl>
        </Box>
    );
}

export default StatusSelectInput;
