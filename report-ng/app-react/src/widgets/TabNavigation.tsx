import * as React from 'react';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Box from '@mui/material/Box';
import {useLocation, useNavigate} from "react-router-dom";
import type { TabConfig } from "../utils/generateTabsFromRoutes";

interface TabNavigationProps {
    tabs: TabConfig[];
}

export default function TabNavigation({ tabs }: TabNavigationProps) {
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
            sx={{width: '100%', p: '24px 0px'}}
        >
            <Box sx={{ borderBottom: 1, borderColor: 'divider'}}>
                <Tabs value={currentTab} onChange={handleChange} variant="fullWidth">
                    {tabs.map((tab) => (
                        <Tab
                            key={tab.label}
                            label={tab.count !== undefined ? `${tab.label} (${tab.count})` : tab.label}
                            icon={tab.icon}
                            iconPosition="start"
                            sx={{ flex: 1 }}
                        />
                    ))}
                </Tabs>
            </Box>
        </Box>
    );
}
