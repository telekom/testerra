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

import {autoinject, PLATFORM} from "aurelia-framework";
import {NavModel, Router, RouterConfiguration} from 'aurelia-router';
import {DataLoader} from "./services/data-loader";
import {StatusConverter} from "./services/status-converter";
import {data} from "./services/report-model";
import {MdcDrawer} from "@aurelia-mdc-web/drawer";
import "./app.scss"
import {StatisticsGenerator} from "./services/statistics-generator";
import Logo from 'assets/logo.png'
import IExecutionContext = data.ExecutionContext;
import {MdcDialogService} from "@aurelia-mdc-web/dialog";
import {PrintDialog} from "./components/print-dialog/print-dialog";
PLATFORM.moduleName('./components/print-dialog/print-dialog')   // necessary to display dialog according to https://discourse.aurelia.io/t/solved-error-cannot-determine-default-view-strategy-for-object/3589/5

@autoinject()
export class App {
    private _router: Router;
    private _drawer:MdcDrawer;
    private _executionContext: IExecutionContext;
    private _routeConfig: RouterConfiguration;
    private _logo = Logo;

    constructor(
        private _dataLoader: DataLoader,
        private _statistics: StatisticsGenerator,
        private _statusConverter: StatusConverter,
        private _dialogService: MdcDialogService
    ) {
    }

    attached() {
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            this._executionContext = executionStatistics.executionAggregate.executionContext;
            this._router.title = this._executionContext.runConfig.reportName;
            this._router.routes.filter(route => route.route == "failure-aspects").find(route => {
                route.settings.count = executionStatistics.uniqueFailureAspects.length;
            });
            this._router.routes.filter(route => route.route == "tests").find(route => {
                route.settings.count = executionStatistics.overallTestCases;
            });
            // this._router.routes.filter(route => route.route == "exit-points").find(route => {
            //     route.settings.count = executionStatistics.exitPointStatistics.length;
            // });
        });
    }

    configureRouter(config: RouterConfiguration, router: Router) {
        router.title = "Report NG";
        this._router = router;
        this._routeConfig = config;
        config.map([
            {
                route: '',
                moduleId: PLATFORM.moduleName('components/dashboard/dashboard'),
                nav: true,
                name: "dashboard",
                title: 'Dashboard'
            },
            {
                route: 'tests',
                moduleId: PLATFORM.moduleName('components/classes/classes'),
                nav: true,
                name: "tests",
                title: 'Tests'
            },
            {
                route: 'failure-aspects',
                moduleId: PLATFORM.moduleName('components/failure-aspects/failure-aspects'),
                nav: true,
                title: 'Failure Aspects',
                name: "failureAspects",
                settings: {
                    count: 0
                }
            },
            {
                route: 'method/:methodId',
                moduleId: PLATFORM.moduleName('components/method-details/method'),
                nav: false,
                title: 'Method',
                name: "method",
            },
            // {
            //     route: 'exit-points',
            //     moduleId: PLATFORM.moduleName('components/exit-points/exit-points'),
            //     nav: true,
            //     name: "exit-points",
            //     title: 'Exit Points',
            //     settings: {
            //         count: 0
            //     }
            // },
            {
                route: 'logs',
                name: 'logs',
                moduleId: PLATFORM.moduleName('components/logs/logs'),
                nav: true,
                title: 'Logs'
            },
            {
                route: 'threads',
                name: 'threads',
                moduleId: PLATFORM.moduleName('components/threads/threads'),
                nav: true,
                title: 'Threads'
            },
            {
                route: 'timings',
                name: 'timings',
                moduleId: PLATFORM.moduleName('components/timings/timings'),
                nav: true,
                title: 'Timings'
            },
            {
                route: 'printable',
                name: 'printable',
                moduleId: PLATFORM.moduleName('components/print-dialog/printable'),
                nav: true,
                title: 'printable'
            },
            // {
            //     route: 'jvm-monitor',
            //     name: 'JVM Monitor',
            //     moduleId: PLATFORM.moduleName('components/jvm'),
            //     nav: true,
            //     title: 'JVM Monitor'
            // },
        ]);
    }

    navigateTo(nav:NavModel|string) {
        if (nav instanceof NavModel) {
            this._router.navigate(nav.href);
        } else {
            this._router.navigateToRoute(nav);
        }
        this._drawer.open = false;
    }

    private _printButtonClicked() {     // work around to get correct iframe source
        let currentPath = window.location.href;

        if (!currentPath.includes("#/")) {
            currentPath = currentPath.concat("#/");
        } else if (!currentPath.endsWith("#/")) {   // if we are not on dashboard, we need to cut off the active path
            const index = currentPath.indexOf("#/");
            currentPath = currentPath.slice(0, index + 2);
        }
        
        this._dialogService.open({ viewModel: PrintDialog, model: { title: this._router.title, iFrameSrc: currentPath + "printable"}});
    }
}


