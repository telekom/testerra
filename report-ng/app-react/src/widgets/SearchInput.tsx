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

import {useState} from "react";
import TextField from "@mui/material/TextField";
import SearchIcon from "@mui/icons-material/Search";
import InputAdornment from "@mui/material/InputAdornment";

type SearchInputProps = {
    currentTexts: string[];
    onConfirm: (newTexts: string[]) => void;
    onSearchTextChange?: (text: string) => void;
};

const SearchInput = ({currentTexts, onConfirm, onSearchTextChange,}: SearchInputProps) => {
    const [searchText, setSearchText] = useState("");

    return (
        <TextField
            label="Search"
            value={searchText}
            onChange={(e) => {
                setSearchText(e.target.value);
                onSearchTextChange?.(e.target.value);
            }}
            onKeyDown={(e) => {
                if (e.key === "Enter") {
                    const newCustomText = [...currentTexts, searchText.trim()]
                        .filter((v, i, a) => a.indexOf(v) === i,); // avoid duplicates
                    onConfirm(newCustomText);
                    setSearchText("");
                }
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
    );
};

export default SearchInput;
