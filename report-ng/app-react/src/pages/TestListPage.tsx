import Box from "@mui/material/Box";
import {Grid, Stack, Switch} from "@mui/material";
import * as React from "react";
import StatusSelectInput from "../widgets/select-input/status-select-input";
import FormControlLabel from "@mui/material/FormControlLabel";
import TextField from '@mui/material/TextField';
import SearchIcon from '@mui/icons-material/Search';
import InputAdornment from '@mui/material/InputAdornment';
import {useSearchParams} from "react-router-dom";
import ReportChip from "../widgets/report-chip/report-chip";
import MultiSelectInput from "../widgets/select-input/multi-select-input";
import {StatusService} from "../model/status-service";
import type { ResultStatus } from "../model/status-service";

const TestListPage = () => {
    const statusMenuItems = StatusService.getRelevantStatuses();

    const classMenuItems: { value: string, label: string }[] = [{
        value: "SimpleTest2",
        label: "SimpleTest2"
    }, {value: "SimpleTest", label: "SimpleTest"}]

    const [configurationMethodsChecked, setConfigurationMethodsChecked] = React.useState(false);
    const [searchParams, setSearchParams] = useSearchParams();

    const statusParam = searchParams.get("status");
    const classParam = searchParams.get("class");

    const selectedStatuses: ResultStatus[] = statusParam
        ? statusParam
            .split("~")
            .map(statusKey => StatusService.getStatusByKey(statusKey))
            .filter(Boolean) as ResultStatus[]
        : [];
    const selectedClasses: string[] = classParam
        ? (classParam.split("~") as string[])
        : [];

    const handleStatusChange = (statuses: ResultStatus[]) => {
        const params = new URLSearchParams(searchParams);

        if (statuses.length > 0) {
            const keys = statuses.map(
                s => StatusService.get(s)?.key
            ).filter(Boolean);

            params.set("status", keys.join("~"));
        } else {
            params.delete("status");
        }

        setSearchParams(params);
    };

    const handleClassChange = (classes: string[]) => {
        const params = new URLSearchParams(searchParams);

        if (classes.length > 0) {
            params.set("class", classes.join("~"));
        } else {
            params.delete("class");
        }

        setSearchParams(params);
    };

    const handleConfigurationMethodsChecked = (value: any) => {
        setConfigurationMethodsChecked(value);
    };

    const handleDelete = (value: string | ResultStatus, type: "status" | "class") => {
        setSearchParams(prev => {
            const params = new URLSearchParams(prev);

            if (type === "status") {
                const updated = selectedStatuses.filter(s => s !== value);
                if (updated.length > 0) {
                    const keys = updated
                        .map(s => StatusService.get(s)?.key)
                        .filter(Boolean);
                    params.set("status", keys.join("~"));
                } else {
                    params.delete("status");
                }
            }

            if (type === "class") {
                const updated = selectedClasses.filter(c => c !== value);
                if (updated.length > 0) {
                    params.set("class", updated.join("~"));
                } else {
                    params.delete("class");
                }
            }

            return params;
        });
    };


    return (
        <Box
            sx={{width: '100%', maxWidth: {sm: '100%', md: '1700px'}}}
        >
            <Grid
                container
                spacing={3}
                columns={12}
            >
                <Grid size={2}>
                    <StatusSelectInput label="Status" selectedStatuses={selectedStatuses} onChange={handleStatusChange}
                                       menuItems={statusMenuItems}/>
                </Grid>
                <Grid size={3}>
                    <MultiSelectInput label="Class" values={selectedClasses} onChange={handleClassChange}
                                      menuItems={classMenuItems}/>
                </Grid>
                <Grid size={5}>
                    <TextField
                        label="Search"
                        slotProps={{
                            input: {
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <SearchIcon sx={{color: 'action.active'}}/>
                                    </InputAdornment>
                                ),
                            },
                        }}
                        sx={{width: "100%"}}
                    />
                </Grid>
                <Grid size={2}>
                    <FormControlLabel control={<Switch defaultChecked value={configurationMethodsChecked}
                                                       onChange={handleConfigurationMethodsChecked}/>}
                                      label="Show configuration methods"/>
                </Grid>
                <Grid size={12}>
                    <Stack direction="row" spacing={1}>
                        {selectedStatuses.map((status) => {
                            const statusInformation = StatusService.get(status);
                            if (!statusInformation) return null;

                            return (
                                <ReportChip key={status} label={statusInformation.label} type="status"
                                    handleDelete={() => handleDelete(status, "status")}
                                />
                            );
                        })}
                        {selectedClasses.map((rClass) => (
                            <ReportChip key={rClass} label={rClass} type="class"
                                        handleDelete={() => handleDelete(rClass, "class")}/>
                        ))}
                    </Stack>

                </Grid>
            </Grid>
        </Box>

    );
};
export default TestListPage;
