
import {createHashRouter, type RouteObject, Navigate} from 'react-router-dom';
import MainLayout from "../layout/MainLayout.tsx";
import AboutPage from "../pages/AboutPage.tsx";
import TimingsPage from "../pages/TimingsPage.tsx";
import DashboardPage from "../pages/DashboardPage.tsx";
import MethodDetailsPage from "../pages/MethodDetailsPage.tsx";
import NotFoundPage from "../pages/NotFoundPage.tsx";
import TestsPage from "../pages/TestsPage";

import HomeRoundedIcon from '@mui/icons-material/HomeRounded';
import AnalyticsRoundedIcon from '@mui/icons-material/AnalyticsRounded';
import InfoRoundedIcon from '@mui/icons-material/InfoRounded';
import OpenInNewIcon from '@mui/icons-material/OpenInNew';
import ListIcon from '@mui/icons-material/List';
import SmsFailedIcon from '@mui/icons-material/SmsFailed';
import CenterFocusStrongIcon from '@mui/icons-material/CenterFocusStrong';
import DevicesIcon from '@mui/icons-material/Devices';
import AccountTreeIcon from '@mui/icons-material/AccountTree';
import PlayCircleIcon from '@mui/icons-material/PlayCircle';
import ClearAllIcon from '@mui/icons-material/ClearAll';
import CodeIcon from '@mui/icons-material/Code';
import type {JSX} from "react";
import TestTimings from "../components/timings-tab-components/TestTimings";
import Sessions from "../components/timings-tab-components/Sessions";
import FailureAspectsPage from "../pages/FailureAspectsPage";
import LogsPage from "../pages/LogsPage";
import ErrorDetails from "../components/method-details-tab-components/ErrorDetails.tsx";
import Steps from "../components/method-details-tab-components/Steps.tsx";
import BrowserInfo from "../components/method-details-tab-components/BrowserInfo.tsx";
import Dependencies from "../components/method-details-tab-components/Dependencies.tsx";
import Video from "../components/method-details-tab-components/Video.tsx";
import ThreadsPage from "../pages/ThreadsPage.tsx";

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
                path: "failureAspects",
                element: <FailureAspectsPage/>,
                handle: {label: "Failure Aspects", show: true, icon: <SmsFailedIcon />} as RouteHandle
            },
            {
                path: "logs",
                element: <LogsPage/>,
                handle: {label: "Logs", show: true, icon: <CodeIcon />} as RouteHandle
            },
            {
                path: "about",
                element: <AboutPage/>,
                handle: {label: "About", show: true, icon: <InfoRoundedIcon />} as RouteHandle
            },
            {
                path: "threads/:methodId?",
                element: <ThreadsPage/>,
                handle: {label: "Threads", show: true, icon: <ClearAllIcon />} as RouteHandle
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
                element: <MethodDetailsPage />,
                children: [
                    // makes sure that never only /method/{id} is rendered but always /method/{id}/details
                    {
                        index: true,
                        element: <Navigate to="details" replace />
                    },
                    {
                        path: "details",
                        element: <ErrorDetails/>,
                        handle: {label: "Error Details", show: true, icon: <CenterFocusStrongIcon />} as RouteHandle
                    },
                    {
                        path: "steps/:stepId?",
                        element: <Steps/>,
                        handle: {label: "Steps", show: true, icon: <ListIcon />} as RouteHandle
                    },
                    {
                        path: "browser-info",
                        element: <BrowserInfo/>,
                        handle: {label: "Browser Info", show: true, icon: <DevicesIcon />} as RouteHandle
                    },
                    {
                        path: "video",
                        element: <Video/>,
                        handle: {label: "Video ", show: true, icon: <PlayCircleIcon />} as RouteHandle
                    },
                    {
                        path: "dependencies",
                        element: <Dependencies/>,
                        handle: {label: "Dependencies ", show: true, icon: <AccountTreeIcon />} as RouteHandle
                    }
                ]
            },
            {
                path: '*',
                element: <NotFoundPage />,
            },
        ],
    },
];

export const router = createHashRouter(routesConfig);
