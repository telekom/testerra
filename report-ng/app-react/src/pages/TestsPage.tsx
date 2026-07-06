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

import Box from "@mui/material/Box";
import {Grid, Switch, Typography} from "@mui/material";
import StatusSelectInput from "../widgets/StatusSelectInput";
import MultiSelectInput from "../widgets/MultiSelectInput";
import {useTestListFilters} from "../hooks/useTestListFilters";
import SelectedFiltersChips from "../components/SelectedFilterChips";
import TestList from "../components/TestList";
import SearchInput from "../widgets/SearchInput";
import {useState} from "react";
import Stack from "@mui/material/Stack";

const TestsPage = () => {
    const {
        statusMenuItems,
        classMenuItems,
        filters,
        setFilter,
        configurationMethodsChecked,
        handleConfigurationMethodsChecked,
        handleDelete,
        clearAll,
    } = useTestListFilters();

    const selectedStatuses = filters.status ?? [];
    const selectedClasses = filters.class ?? [];

    // live search text for highlighting while typing (not yet confirmed)
    const [searchText, setSearchText] = useState("");

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
                    <StatusSelectInput label="Status" selectedStatuses={selectedStatuses}
                                       onChange={(newStatuses) => setFilter("status", newStatuses)}
                                       menuItems={statusMenuItems}/>
                </Grid>
                <Grid size={3}>
                    <MultiSelectInput label="Class"
                                      values={selectedClasses}
                                      onChange={(newClasses) => setFilter("class", newClasses)}
                                      menuItems={classMenuItems}
                                      renderValue={(selected: string[]) => {
                                          if (!selected?.length) return "";
                                          if (selected.length === 1) return "1 class selected";
                                          return `${selected.length} classes selected`;
                                      }}/>
                </Grid>
                <Grid size={5}>
                    <SearchInput
                        currentTexts={filters.customText ?? []}
                        onConfirm={(newTexts) => {
                            setFilter("customText", newTexts);
                            setSearchText("");
                        }}
                        onSearchTextChange={setSearchText}
                    />
                </Grid>
                <Grid size={2} sx={{alignContent: "center"}}>
                    <Stack direction="row" sx={{alignItems: "center"}}>
                        <Switch checked={configurationMethodsChecked}
                                onChange={handleConfigurationMethodsChecked}/>
                        <Typography variant="body2">Show configuration methods</Typography>
                    </Stack>
                </Grid>
                <Grid size={12} minHeight={36}>
                    <SelectedFiltersChips selectedFilters={filters}
                                          handleDelete={handleDelete}
                                          handleClearAllClick={clearAll}/>

                </Grid>
                <Grid size={12} >
                    <TestList filters={filters} searchText={searchText} showConfigurationMethods={configurationMethodsChecked}/>
                </Grid>
            </Grid>
        </Box>

    );
};
export default TestsPage;
