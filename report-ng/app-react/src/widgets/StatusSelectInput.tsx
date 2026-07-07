import {Box, FormControl, MenuItem} from "@mui/material";
import InputLabel from "@mui/material/InputLabel";
import Select from "@mui/material/Select";
import ReportChip from "./ReportChip";
import type {SxProps, Theme} from "@mui/material/styles";
import {StatusService} from "../model/status-service";
import type {ResultStatus} from "../model/status-service";
import {useState} from "react";

type SelectInputProps = {
    label: string
    selectedStatuses?: ResultStatus[],
    onChange: (value: ResultStatus[]) => void;
    menuItems: number[];
    sx?: SxProps<Theme>;
}

type StatusMenuItem = {
    status: ResultStatus;
    statusInformation: NonNullable<ReturnType<typeof StatusService.get>>;
}

const StatusSelectInput = ({label, selectedStatuses, onChange, menuItems, sx}: SelectInputProps) => {

    const [isOpen, setIsOpen] = useState(false);
    const availableMenuItems = menuItems
        .filter(status => !selectedStatuses?.includes(status as ResultStatus))
        .map(status => ({
            status: status as ResultStatus,
            statusInformation: StatusService.get(String(status)),
        }))
        .filter((item): item is StatusMenuItem => item.statusInformation !== null)
        .toSorted((a, b) => a.statusInformation.label.localeCompare(b.statusInformation.label));

    return (
        <Box sx={sx}>
            <FormControl fullWidth>
                <InputLabel>{label}</InputLabel>
                <Select
                    multiple
                    value={selectedStatuses}
                    label={label}
                    open={isOpen}
                    onOpen={() => setIsOpen(true)}
                    onClose={() => setIsOpen(false)}
                    onChange={(e) => {
                        onChange(e.target.value as ResultStatus[]);
                        setIsOpen(false);
                    }}
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
                    {availableMenuItems.map(({status, statusInformation}) => (
                        <MenuItem key={status} value={status}>
                            <ReportChip label={statusInformation.label}
                                        size="small"
                                        sx={{background: statusInformation.color, color: "white"}}/>
                        </MenuItem>
                    ))}
                </Select>
            </FormControl>
        </Box>
    );
}

export default StatusSelectInput;
