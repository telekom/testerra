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

    const [searchParams, setSearchParams] = useSearchParams();

    const expectedFailedChecked = React.useMemo(
        () => {
            const param = searchParams.get("expectedFailed");
            return param === null ? true : param === "true";
        },
        [searchParams]
    );

    const type = searchParams.get("type") ?? "";

    const handleExpectedFailedChecked = (event: React.ChangeEvent<HTMLInputElement>) => {
        const checked = event.target.checked;
        const params = new URLSearchParams(searchParams);

        if (checked) {
            params.set("expectedFailed", "true");
        } else {
            params.set("expectedFailed", "false");
        }
        setSearchParams(params);
    };

    const handleTypeChanged = (value: string) => {
        const params = new URLSearchParams(searchParams);
        if (value) {
            params.set("type", value);  // "major" oder "minor"
        } else {
            params.delete("type");                // (All)
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
                            onChange={(e) => handleTypeChanged(e.target.value)}
                        >
                            <MenuItem value="">(All)</MenuItem>
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
