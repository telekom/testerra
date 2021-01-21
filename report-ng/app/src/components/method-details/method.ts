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
import {MethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import {ScreenshotsDialog} from "../screenshots-dialog/screenshots-dialog";
import {MdcDialogService} from '@aurelia-mdc-web/dialog';
import IFile = data.IFile
import ISessionContext = data.ISessionContext;

@autoinject()
export class Method {
    private _router:Router;
    private _allScreenshots:IFile[];
    private _lastScreenshot:IFile;
    private _methodDetails:MethodDetails;

    constructor(
        private _statistics: StatisticsGenerator,
        private _dialogService:MdcDialogService,
    ) {
    }

    configureRouter(config: RouterConfiguration, router: Router) {
        this._router = router;
        config.map([
            {
                route: '',
                moduleId: PLATFORM.moduleName('t-systems-aurelia-components/src/components/empty/empty'),
            },
            {
                route: 'details',
                moduleId: PLATFORM.moduleName('./details'),
                nav: true,
                name: "details",
                title: 'Details',
                settings: {
                    icon: "center_focus_strong"
                }
            },
            {
                route: 'assertions',
                moduleId: PLATFORM.moduleName('./assertions'),
                nav: true,
                name: "assertions",
                title: 'Assertions',
                settings: {
                    icon: "rule",
                }
            },
            {
                route: 'steps/:step?',
                href: "steps",
                moduleId: PLATFORM.moduleName('./steps'),
                nav: true,
                name: "steps",
                title: 'Steps',
                settings: {
                    icon: "list",
                }
            },
            {
                route: 'videos',
                moduleId: PLATFORM.moduleName('./videos'),
                nav: true,
                name: "videos",
                title: 'Videos',
                settings: {
                    icon: "videocam"
                }
            },
            {
                route: 'dependencies',
                moduleId: PLATFORM.moduleName('./dependency-network'),
                nav: true,
                name: "dependencies",
                title: 'Dependencies',
                settings: {
                    icon: "account_tree",
                    // icon: "sync_alt"
                }
            },
        ]);
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        this._statistics.getMethodDetails(params.methodId).then(methodDetails => {
            this._methodDetails = methodDetails;
            this._statistics.getScreenshotsFromMethodContext(methodDetails.methodContext).then(screenshots => {
                this._allScreenshots = screenshots;
                this._lastScreenshot = this._allScreenshots.reverse().find(() => true);
            })

            this._router.routes.forEach(routeConfig => {
                switch (routeConfig.name) {
                    case "steps": {
                        routeConfig.settings.count = methodDetails.methodContext.testSteps.length;
                        break;
                    }
                    case "dependencies": {
                        const count = methodDetails.methodContext.relatedMethodContextIds.length + methodDetails.methodContext.dependsOnMethodContextIds.length;
                        if (count > 1) {
                            routeConfig.nav = true;
                            routeConfig.settings.count = count;
                        } else {
                            routeConfig.nav = false;
                        }
                        break;
                    }
                    case "videos": {
                        const videos = methodDetails.sessionContexts.filter(value => value.videoId).map(value => value.videoId);
                        if (videos.length == 0) {
                            routeConfig.nav = false;
                        } else {
                            routeConfig.settings.count = videos.length;
                            routeConfig.nav = true;
                        }
                        break;
                    }
                    case "details": {
                        if (methodDetails.numDetails > 0) {
                            routeConfig.nav = true;
                            routeConfig.settings.count = methodDetails.numDetails;
                        } else {
                            routeConfig.nav = false;
                        }
                        break;
                    }
                    case "assertions": {
                        let allCollected = 0;
                        let allOptional = 0;
                        methodDetails.methodContext.testSteps
                            .flatMap(value => value.actions)
                            .flatMap(value => value.entries)
                            .filter(value => value.assertion)
                            .map(value => value.assertion)
                            .forEach(value => {
                                if (value.optional) {
                                    allOptional++;
                                } else {
                                    allCollected++;
                                }
                            });
                        if (allCollected > 0 || allOptional > 0) {
                            routeConfig.nav = true;
                            routeConfig.settings.count = `${allCollected}/${allOptional}`;
                        } else {
                            routeConfig.nav = false;
                        }
                        break;
                    }
                }
            });

            if (!routeConfig.hasChildRouter) {
                const navOptions = {replace:true};
                const enabledRouteConfig = this._router.routes.find(routeConfig => routeConfig.nav);
                this._router.navigateToRoute(enabledRouteConfig.name, {}, navOptions);
            }
        });
    }

    private _tabClicked(routeConfig:RouteConfig) {
        this._router.navigateToRoute(routeConfig.name);
    }

    private _showScreenshot(file:data.File) {
        this._dialogService.open({
            viewModel: ScreenshotsDialog,
            model: {
                current: file,
                screenshots: this._allScreenshots
            },
            class: "screenshot-dialog"
        });
    }
}
