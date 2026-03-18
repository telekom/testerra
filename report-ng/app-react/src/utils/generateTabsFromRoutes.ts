import type { RouteObject } from "react-router-dom";
import type { ReactElement } from "react";

export type TabConfig = {
    label: string;
    route: string;
    icon: ReactElement;
};

// generates tabs based on the children of a route
export const generateTabsFromRoutes = (children: RouteObject[] | undefined): TabConfig[] => {
    if (!children) return [];
    return children
        .filter((child): child is RouteObject & { path: string } => !!child.path && child.handle?.show)     // only takes children with "show: true"; makes sure path is always a string
        .map((child) => ({
            label: child.handle.label,
            route: child.path,
            icon: child.handle.icon,
        }));
};
