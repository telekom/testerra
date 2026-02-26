import {Box, FormControl, MenuItem} from "@mui/material";
import InputLabel from "@mui/material/InputLabel";
import Select from "@mui/material/Select";
import ReportChip from "../report-chip/report-chip";
import type {SxProps, Theme} from "@mui/material/styles";
import {StatusService} from "../../model/status-service";
import type {ResultStatus} from "../../model/status-service";

type SelectInputProps = {
    label: string
    selectedStatuses?: ResultStatus[] | [],
    onChange: (value: ResultStatus[]) => void;
    menuItems: ResultStatus[];
    sx?: SxProps<Theme>;
}

const StatusSelectInput = ({label, selectedStatuses, onChange, menuItems, sx}: SelectInputProps) => {

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
                >
                    {menuItems.map(status => {
                        const statusInformation = StatusService.get(status);
                        if (!statusInformation) return null;

                        return (
                            <MenuItem key={status} value={status}>
                                <ReportChip label={statusInformation.label}
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
