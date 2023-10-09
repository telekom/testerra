/*
 * Testerra
 *
 * (C) 2023, Selina Natschke, Telekom MMS GmbH, Deutsche Telekom AG
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
export class Timings {
    private _router: Router;

    configureRouter(config: RouterConfiguration, router: Router) {
        this._router = router;
        config.map([
            {
                route: '',
                moduleId: PLATFORM.moduleName('t-systems-aurelia-components/src/components/empty/empty'),
            },
            {
                route: 'tests',
                moduleId: PLATFORM.moduleName('./test-timings/test-timings'),
                nav: true,
                name: "tests",
                title: 'Tests',
                settings: {
                    icon: "center_focus_strong"
                }
            },
            {
                route: 'sessions',
                moduleId: PLATFORM.moduleName('./session-timings/session-timings'),
                nav: true,
                name: "sessions",
                title: 'Sessions',
                settings: {
                    icon: "list",
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
        }
    }

    private _tabClicked(routeConfig: RouteConfig) {
        if (this._router.currentInstruction.config.name !== routeConfig.name) {
            this._router.navigateToRoute(routeConfig.name);
        }
    }
}
