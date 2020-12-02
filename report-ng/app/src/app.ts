import {autoinject, PLATFORM} from "aurelia-framework";
import {NavModel, Router, RouterConfiguration} from 'aurelia-router';
import {DataLoader} from "./services/data-loader";
import {StatusConverter} from "./services/status-converter";
import {data} from "./services/report-model";
import {MdcDrawer} from "@aurelia-mdc-web/drawer";
import "./app.scss"
import {StatisticsGenerator} from "./services/statistics-generator";
import Logo from 'assets/logo.png'
import IExecutionContext = data.IExecutionContext;

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
    ) {
    }

    attached() {
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            this._executionContext = executionStatistics.executionAggregate.executionContext;
            this._router.title = this._executionContext.runConfig.reportName;
            this._router.routes.filter(route => route.route == "failure-aspects").find(route => {
                route.settings.count = executionStatistics.failureAspectStatistics.length;
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
            // {
            //     route: 'threads',
            //     moduleId: PLATFORM.moduleName('components/threads'),
            //     nav: true,
            //     title: 'Threads'
            // },
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
                route: 'method/:id',
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
                name: 'Logs',
                moduleId: PLATFORM.moduleName('components/logs/logs'),
                nav: true,
                title: 'Logs'
            },
            {
                route: 'threads',
                name: 'Threads',
                moduleId: PLATFORM.moduleName('components/threads/threads'),
                nav: true,
                title: 'Threads'
            },
            // {
            //     route: 'timings',
            //     name: 'Timings',
            //     moduleId: PLATFORM.moduleName('components/timings'),
            //     nav: true,
            //     title: 'Timings'
            // },
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
}


