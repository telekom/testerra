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
import {activationStrategy, NavigationInstruction, RouteConfig, Router, RouterConfiguration} from "aurelia-router";
import {MethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {IScreenshotsDialogParams, ScreenshotsDialog} from "../screenshots-dialog/screenshots-dialog";
import {MdcDialogService} from '@aurelia-mdc-web/dialog';
import {data} from "../../services/report-model";
import MethodType = data.MethodType;
import "./method.scss"

@autoinject()
export class Method {
    private _router: Router;
    private _allScreenshotIds: string[];
    private _lastScreenshotId: string;
    private _methodDetails: MethodDetails;
    private _loading: boolean = false;
    private _routes: RouteConfig[];
    private _prevMethod: MethodDetails;
    private _nextMethod: MethodDetails;

    constructor(
        private _statistics: StatisticsGenerator,
        private _dialogService: MdcDialogService,
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
                title: 'Error Details',
                settings: {
                    icon: "center_focus_strong"
                }
            },
            // {
            //     route: 'assertions',
            //     moduleId: PLATFORM.moduleName('./assertions'),
            //     nav: true,
            //     name: "assertions",
            //     title: 'Assertions',
            //     settings: {
            //         icon: "rule",
            //     }
            // },
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
                route: 'browser-info',
                moduleId: PLATFORM.moduleName('./browser-info'),
                nav: true,
                name: "browser-info",
                title: 'Browser Info',
                settings: {
                    icon: "devices"
                }
            },
            {
                route: 'video',
                moduleId: PLATFORM.moduleName('./video'),
                nav: true,
                name: "video",
                title: 'Video',
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
        /**
         * We should not disable this route when it has been directly requested in the URL
         * because this will lead into an error in the mdc-tab.
         */
        function disableRoute(routeConfig: RouteConfig) {
            if (navInstruction.params.childRoute != routeConfig.name) {
                routeConfig.nav = false;
            }
        }

        this._loading = true;
        this._statistics.getMethodDetails(params.methodId).then(methodDetails => {
            this._methodDetails = methodDetails;
            this._allScreenshotIds = this._statistics.getScreenshotIdsFromMethodContext(methodDetails.methodContext);
            this._lastScreenshotId = this._allScreenshotIds.reverse().find(() => true);
            this._loading = false;

            this._router.routes.forEach(routeConfig => {
                switch (routeConfig.name) {
                    case "steps": {
                        routeConfig.settings.count = methodDetails.methodContext.testSteps.length;
                        break;
                    }
                    case "dependencies": {
                        const count = methodDetails.methodContext.relatedMethodContextIds.length + methodDetails.methodContext.dependsOnMethodContextIds.length;
                        if (count > 0) {
                            routeConfig.nav = true;
                            routeConfig.settings.count = count;
                        } else {
                            disableRoute(routeConfig);
                        }
                        break;
                    }
                    case "browser-info": {
                        if (methodDetails.sessionContexts.length > 0) {
                            routeConfig.settings.count = methodDetails.sessionContexts.length;
                            routeConfig.nav = true;
                        } else {
                            disableRoute(routeConfig);
                        }
                        break;
                    }
                    case "video":{
                        const contextsWithVideos = methodDetails.sessionContexts.filter(context => context.videoId?.length > 0)
                        if(contextsWithVideos.length > 0){
                            routeConfig.settings.count = contextsWithVideos.length;
                            routeConfig.nav = true;
                        } else {
                            disableRoute(routeConfig);
                        }
                        break;
                    }
                    case "details": {
                        if (methodDetails.numDetails > 0) {
                            routeConfig.nav = true;
                            //routeConfig.settings.count = methodDetails.numDetails;
                        } else {
                            disableRoute(routeConfig);
                        }
                        break;
                    }
                    // case "assertions": {
                    //     const numErrorContexts = methodDetails.errorContexts.length;
                    //     if (numErrorContexts > 0) {
                    //         routeConfig.nav = true;
                    //         routeConfig.settings.count = numErrorContexts;
                    //     } else {
                    //         disableRoute(routeConfig);
                    //     }
                    //     break;
                    // }
                }
                return routeConfig;
            });

            if (!routeConfig.hasChildRouter) {
                const navOptions = {replace: true};
                const enabledRouteConfig = this._router.routes.find(routeConfig => routeConfig.nav);
                this._router.navigateToRoute(enabledRouteConfig.name, {}, navOptions);
            }

            this._statistics.getExecutionStatistics().then(executionStatistics => {
                const myRunIndex = this._methodDetails.methodContext.methodRunIndex;
                /*
                * Handling all methodContexts
                * - filter for 'real' test methods (no setups)
                * - sort for run index
                * - create MethodDetails
                * - filter for failed test methods
                * - find the previous and the next failed test method to current.
                 */
                executionStatistics.classStatistics
                    .flatMap(methodContext => methodContext.methodContexts)
                    .filter(methodContext => methodContext.methodType == MethodType.TEST_METHOD)
                    .sort((a, b) => a.methodRunIndex - b.methodRunIndex)
                    .map(methodContext => new MethodDetails(methodContext, this._methodDetails.classStatistics))
                    .filter(methodDetails => methodDetails.numDetails != 0)
                    .forEach(methodDetails => {
                        if (methodDetails.methodContext.methodRunIndex < myRunIndex) {
                            this._prevMethod = methodDetails;
                        } else if (methodDetails.methodContext.methodRunIndex > myRunIndex && this._nextMethod === undefined) {
                            this._nextMethod = methodDetails;
                        }
                    });
            });
        });

    }

    private _tabClicked(routeConfig: RouteConfig) {
        this._router.navigateToRoute(routeConfig.name);
    }

    /**
     * The replace strategy is necessary to reinitialize the navigation,
     * when some tabs have been disabled
     */
    determineActivationStrategy() {
        return activationStrategy.replace;
    }

    private _showScreenshot(ev: CustomEvent) {
        this._dialogService.open({
            viewModel: ScreenshotsDialog,
            model: <IScreenshotsDialogParams>{
                current: ev.detail,
                screenshotIds: this._allScreenshotIds
            },
        });
    }
}
