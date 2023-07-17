/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
import {NavigationInstruction, RouteConfig, Router, RouterConfiguration} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";

@autoinject()
export class Timings extends AbstractViewModel {
    private _router: Router;

    constructor() {
        super();
    }

    configureRouter(config: RouterConfiguration, router: Router) {
        this._router = router;
        config.map([
            {
                route: '',
                moduleId: PLATFORM.moduleName('t-systems-aurelia-components/src/components/empty/empty'),
            },
            {
                route: 'test-duration-tab',
                moduleId: PLATFORM.moduleName('./test-duration-tab/test-duration-tab'),
                nav: true,
                name: "test duration tab",
                title: 'Test Duration',
                settings: {
                    icon: "center_focus_strong"
                }
            },
            {
                route: 'page-timings-tab',
                moduleId: PLATFORM.moduleName('./page-timings-tab/page-timings-tab'),
                nav: true,
                name: "page timings tab",
                title: 'Page Timings',
                settings: {
                    icon: "center_focus_strong"
                }
            },
        ]);
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
    }

    private _tabClicked(routeConfig: RouteConfig) {
        this._router.navigateToRoute(routeConfig.name);
    }

}
