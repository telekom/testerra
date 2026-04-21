import FailureAspectsList from "../components/FailureAspectsList";
import {FormControl, Grid, MenuItem, Switch, Typography} from "@mui/material";
import Stack from "@mui/material/Stack";
import Box from "@mui/material/Box";
import * as React from "react";
import {useState} from "react";
import Select from "@mui/material/Select";
import {useSearchParams} from "react-router-dom";
import InputLabel from "@mui/material/InputLabel";
import InputAdornment from "@mui/material/InputAdornment";
import SearchIcon from "@mui/icons-material/Search";
import TextField from "@mui/material/TextField";


const FailureAspectsPage = () => {

    // live search text for highlighting while typing (not yet confirmed)
    const [searchText, setSearchText] = useState("");

    const [type, setType] = useState("");

    const [searchParams, setSearchParams] = useSearchParams();

    const expectedFailedChecked = React.useMemo(
        () => searchParams.get("expectedFailed") === "true",
        [searchParams]
    );

    const handleExpectedFailedChecked = (event: React.ChangeEvent<HTMLInputElement>) => {
        const checked = event.target.checked;
        const params = new URLSearchParams(searchParams);

        if (checked) {
            params.set("expectedFailed", "true");
        } else {
            params.delete("expectedFailed");
        }
        setSearchParams(params);
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
                <Grid size={2} sx={{alignItems: "stretch"}}>
                    <FormControl fullWidth>
                        <InputLabel>Type</InputLabel>
                        <Select
                            value={type}
                            label="Type"
                            onChange={(e) => setType(e.target.value)}
                        >
                            <MenuItem>(All)</MenuItem>
                            <MenuItem value="major">Major</MenuItem>
                            <MenuItem value="minor">Minor</MenuItem>
                        </Select>
                    </FormControl>
                </Grid>
                <Grid size={8}>
                    <TextField
                        label="Search"
                        value={searchText}
                        onChange={(e) => {
                            setSearchText(e.target.value);
                        }}
                        slotProps={{
                            input: {
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <SearchIcon sx={{color: "action.active"}}/>
                                    </InputAdornment>
                                ),
                            },
                        }}
                        sx={{width: "100%"}}
                    />
                </Grid>
                <Grid size={2} sx={{alignContent: "center"}}>
                    <Stack direction="row" sx={{alignItems: "center"}}>
                        <Switch checked={expectedFailedChecked}
                                onChange={handleExpectedFailedChecked}/>
                        <Typography variant="body2">Show expected failed</Typography>
                    </Stack>
                </Grid>
                <Grid size={12}>
                    <FailureAspectsList searchText={searchText} expectedFailedChecked={expectedFailedChecked}
                                        type={type}/>
                </Grid>
            </Grid>
        </Box>
    );
};
export default FailureAspectsPage;
