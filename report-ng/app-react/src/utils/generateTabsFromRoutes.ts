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

import type { RouteObject } from "react-router-dom";
import type { ReactElement } from "react";

export type TabConfig = {
    label: string;
    route: string;
    icon: ReactElement;
    count?: number;
    visible?: boolean // this will be added dynamically
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
