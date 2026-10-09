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

import * as React from 'react';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Box from '@mui/material/Box';
import {useLocation, useNavigate} from "react-router-dom";
import type { TabConfig } from "../utils/generateTabsFromRoutes";

interface TabNavigationProps {
    tabs: TabConfig[];
    withTopPadding?: boolean;
}

export default function TabNavigation({ tabs, withTopPadding = true }: TabNavigationProps) {
    const navigate = useNavigate();
    const location = useLocation();

    const getTabBaseRoute = (route: string) => route.split("/:")[0];
    const currentTabIndex = tabs.findIndex((tab) =>
        location.pathname.includes(`/${getTabBaseRoute(tab.route)}`)
    );
    const currentTab = currentTabIndex === -1 ? false : currentTabIndex;

    const handleChange = (_: React.SyntheticEvent, newValue: number) => {
        navigate(getTabBaseRoute(tabs[newValue].route));
    };

    return (
        <Box
            sx={{width: '100%', pb: '24px', pt: withTopPadding ? '24px' : 0}}
        >
            <Box sx={{ borderBottom: 1, borderColor: 'divider'}}>
                <Tabs
                    value={currentTab}
                    onChange={handleChange}
                    variant="fullWidth"
                    sx={{ minHeight: 48 }}
                >
                    {tabs.map((tab) => (
                        <Tab
                            key={tab.label}
                            label={tab.count !== undefined ? `${tab.label} (${tab.count})` : tab.label}
                            icon={tab.icon}
                            iconPosition="start"
                            sx={{ flex: 1, minHeight: 48, pt: 0, pb: 0 }}
                        />
                    ))}
                </Tabs>
            </Box>
        </Box>
    );
}
