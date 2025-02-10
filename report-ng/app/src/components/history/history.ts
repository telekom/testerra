/*
 * Testerra
 *
 * (C) 2024, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {autoinject, PLATFORM} from 'aurelia-framework';
import {RouteConfig, Router, RouterConfiguration} from "aurelia-router";

@autoinject()
export class History {
    private _router: Router;

    configureRouter(config: RouterConfiguration, router: Router) {
        this._router = router;
        config.map([
            {
                route: '',
                moduleId: PLATFORM.moduleName('t-systems-aurelia-components/src/components/empty/empty'),
            },
            {
                route: 'run-history',
                moduleId: PLATFORM.moduleName('./run-history/run-history'),
                nav: true,
                name: "run-history",
                title: 'Test run',
                settings: {
                    icon: "area_chart"
                }
            },
            {
                route: 'duration-history',
                moduleId: PLATFORM.moduleName('./duration-history/duration-history'),
                nav: true,
                name: "duration-history",
                title: 'Run duration',
                settings: {
                    icon: "timeline",
                }
            },
            {
                route: 'classes-history',
                moduleId: PLATFORM.moduleName('./classes-history/classes-history'),
                nav: true,
                name: "classes-history",
                title: 'Test classes',
                settings: {
                    icon: "view_comfy",
                }
            },
            {
                route: 'run-comparison',
                moduleId: PLATFORM.moduleName('./run-comparison/run-comparison'),
                nav: true,
                name: "run-comparison",
                title: 'Run comparison',
                settings: {
                    icon: "compare_arrows",
                }
            },
        ]);
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
    ) {
        if (!routeConfig.hasChildRouter) {
            const navOptions = {replace: true};
            const enabledRouteConfig = this._router.routes.find(routeConfig => routeConfig.nav);
            this._router.navigateToRoute(enabledRouteConfig.name, {}, navOptions);
            if (params.subPage) {
                this._router.navigateToRoute(params.subPage, {}, navOptions);
            }
        }
    }

    private _tabClicked(routeConfig: RouteConfig) {
        if (this._router.currentInstruction.config.name !== routeConfig.name) {
            this._router.navigateToRoute(routeConfig.name);
        }
    }
}
