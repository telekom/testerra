/*
 * Testerra
 *
 * (C) 2026, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {Outlet} from 'react-router-dom';

import Box from '@mui/material/Box';
import CssBaseline from '@mui/material/CssBaseline';
import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';
import MainNavigation from "../components/navigation/MainNavigation.tsx";

import {reportTheme} from "./reportTheme.tsx";
import {ThemeProvider} from "@mui/material/styles";
import {Stack} from "@mui/material";
import Toolbar from "@mui/material/Toolbar";

const MainLayout = () => {

    return (

        <ThemeProvider theme={reportTheme}>
            <CssBaseline/>
            <Box sx={{
                display: 'flex',
            }}
            >
                <MainNavigation/>

                <Box
                    component="main"
                    sx={{
                        flexGrow: 1,
                        overflow: 'auto',
                    }}
                >
                    {/*Useful to have spacing so that there is not overlap if the nav bar is horizontally on top of the page*/}
                    <Toolbar sx={{display: {xs: 'block', md: 'none'}}}/>

                    <Stack
                        spacing={2}
                        sx={{
                            alignItems: 'center',
                            mx: 3,
                            pb: 5,
                            mt: {xs: 3, md: 3},
                        }}
                    >
                        <Outlet/>
                    </Stack>
                </Box>
            </Box>
        </ThemeProvider>

    );
};
export default MainLayout;
