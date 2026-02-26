import * as React from 'react';
import Tabs from '@mui/material/Tabs';
import Tab from '@mui/material/Tab';
import Box from '@mui/material/Box';
import {useLocation, useNavigate} from "react-router-dom";
import type { TabConfig } from "../../utils/generateTabsFromRoutes";

interface TabNavigationProps {
    tabs: TabConfig[];
}

export default function TabNavigation({ tabs }: TabNavigationProps) {
    const navigate = useNavigate();
    const location = useLocation();

    let currentTab = tabs.findIndex((tab) => location.pathname.includes(tab.route));

    const handleChange = (_: React.SyntheticEvent, newValue: number) => {
        navigate(tabs[newValue].route);
    };

    return (
        <Box
            sx={{width: '100%', maxWidth: {sm: '100%', md: '1700px'}, p: '24px 32px'}}
        >
            <Box sx={{ borderBottom: 1, borderColor: 'divider'}}>
                <Tabs value={currentTab} onChange={handleChange} variant="fullWidth">
                    {tabs.map((tab) => (
                        <Tab key={tab.label} label={tab.label} icon={tab.icon} iconPosition="start" sx={{ flex: 1 }}/>
                    ))}
                </Tabs>
            </Box>
        </Box>
    );
}