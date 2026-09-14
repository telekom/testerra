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
import AppBar from "@mui/material/AppBar";
import Toolbar from "@mui/material/Toolbar";
import Typography from "@mui/material/Typography";
import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';
import MainNavigation from "../components/navigation/MainNavigation.tsx";

import {reportTheme} from "./reportTheme.tsx";
import {ThemeProvider} from "@mui/material/styles";
import {Stack} from "@mui/material";
import {useReportData} from "../provider/DataProvider.tsx";

const MainLayout = () => {
    const {executionMngr} = useReportData();
    const reportName = executionMngr?.getExecutionStatistics().getExecutionAggregate.executionContext?.runConfig?.reportName ?? "";

    return (

        <ThemeProvider theme={reportTheme}>
            <CssBaseline/>
            <Box sx={{
                display: 'flex',
                height: '100vh',
            }}
            >
                <MainNavigation/>

                <Box
                    component="main"
                    sx={{
                        flexGrow: 1,
                        overflow: 'auto',
                        position: "relative",
                    }}
                >
                    <AppBar
                        position="sticky"
                        sx={{
                            // left: {xs: 0, md: "240px"},
                            width: "100%",
                            backgroundColor: "primary.main",
                            color: "primary.contrastText",
                            right: 0
                        }}
                    >
                        <Toolbar>
                            <Typography
                                variant="h6"
                                sx={{
                                    overflowX: "auto",
                                    overflowY: "hidden",
                                    whiteSpace: "nowrap",
                                }}
                            >
                                {reportName}
                            </Typography>
                        </Toolbar>
                    </AppBar>
                    <Stack
                        spacing={2}
                        sx={{
                            alignItems: 'center',
                            mx: 3,
                            pb: 5,
                            mt: 3,
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
