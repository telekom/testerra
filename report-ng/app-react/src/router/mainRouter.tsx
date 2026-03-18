
import {createHashRouter, type RouteObject, Navigate} from 'react-router-dom';
import MainLayout from "../layout/MainLayout.tsx";
import AboutPage from "../pages/AboutPage.tsx";
import TestsPage from "../pages/TestsPage.tsx";
import TimingsPage from "../pages/TimingsPage.tsx";
import DashboardPage from "../pages/DashboardPage.tsx";
import MethodDetailsPage from "../pages/MethodDetailsPage.tsx";
import NotFoundPage from "../pages/NotFoundPage.tsx";
import TestListPage from "../pages/TestListPage";

import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import AnalyticsRoundedIcon from '@mui/icons-material/AnalyticsRounded';
import InfoRoundedIcon from '@mui/icons-material/InfoRounded';
import OpenInNewIcon from '@mui/icons-material/OpenInNew';
import ListIcon from '@mui/icons-material/List';
import type {JSX} from "react";
import TestTimings from "../components/timings-tab-components/test-timings";
import Sessions from "../components/timings-tab-components/sessions";

// Custom attributes for menu elements
export interface RouteHandle {
    label: string;
    show: boolean;
    icon: JSX.Element;
}

export const routesConfig: RouteObject[] = [
    {
        path: "/",
        element: <MainLayout/>,
        children: [
            {
                index: true,
                element: <DashboardPage/>,
                handle: {label: "Dashboard", show: true, icon: <HomeRoundedIcon />} as RouteHandle
            },
            {
                path: "Tests",
                element: <TestsPage/>,
                handle: {label: "Tests", show: true, icon: <AnalyticsRoundedIcon />} as RouteHandle
            },
            {
                path: "testlist",
                element: <TestListPage/>,
                handle: {label: "Test List", show: true, icon: <AnalyticsRoundedIcon />} as RouteHandle
            },
            {
                path: "about",
                element: <AboutPage/>,
                handle: {label: "About", show: true, icon: <InfoRoundedIcon />} as RouteHandle
            },
            {
                path: "timings",
                element: <TimingsPage/>,
                handle: {label: "Timings", show: true, icon: <AnalyticsRoundedIcon />} as RouteHandle,
                children: [
                    // makes sure that never only /timings is rendered but always /timings/test-timings
                    {
                        index: true,
                        element: <Navigate to="test-timings" replace />
                    },
                    {
                        path: "test-timings",
                        element: <TestTimings/>,
                        handle: {label: "Tests", show: true, icon: <ListIcon />} as RouteHandle
                    },
                    {
                        path: "sessions",
                        element: <Sessions/>,
                        handle: {label: "Sessions", show: true, icon: <OpenInNewIcon />} as RouteHandle
                    },
                ]
            },
            {
                path: "method/:methodId?",
                element: <MethodDetailsPage />
            },
            {
                path: '*',
                element: <NotFoundPage />,
            },
        ],
    },
];

export const router = createHashRouter(routesConfig);
