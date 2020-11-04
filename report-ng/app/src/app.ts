import {autoinject, PLATFORM} from "aurelia-framework";
import {Router, RouterConfiguration} from 'aurelia-router';
import {DataLoader} from "./services/data-loader";
import {StatusConverter} from "./services/status-converter";
import {data} from "./services/report-model";
import {MdcDrawer} from "@aurelia-mdc-web/drawer";
import IExecutionContext = data.IExecutionContext;

@autoinject()
export class App {
    private _router: Router;
    private _drawer:MdcDrawer;
    private _executionContext: IExecutionContext;
    private _routeConfig: RouterConfiguration;

    constructor(
        private _dataLoader: DataLoader,
        private _statusConverter: StatusConverter,
    ) {

        this._dataLoader.getExecutionAggregate().then(value => {
            this._executionContext = value.executionContext;
            this._routeConfig.title = this._executionContext.runConfig.reportName;
            this._router.routes.filter(route => route.route == "failure-aspects").find(route => {
                route.settings.count = this._executionContext.failureAscpects.length;
            });
            this._router.routes.filter(route => route.route == "exit-points").find(route => {
                route.settings.count = this._executionContext.exitPoints.length;
            });
        })

    }

    attached() {

    }


    configureRouter(config: RouterConfiguration, router: Router): void {
        this._router = router;
        this._routeConfig = config;
        config.map([
            {
                route: '',
                moduleId: PLATFORM.moduleName('components/dashboard/dashboard'),
                nav: true,
                title: 'Dashboard'
            },
            {
                route: 'classes',
                moduleId: PLATFORM.moduleName('components/classes/classes'),
                nav: true,
                title: 'Classes'
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
                settings: {
                    count: 0
                }
            },
            {
                route: 'exit-points',
                moduleId: PLATFORM.moduleName('components/exit-points/exit-points'),
                nav: true,
                title: 'Exit Points',
                settings: {
                    count: 0
                }
            },
            // {
            //     route: 'logs',
            //     name: 'Logs',
            //     moduleId: PLATFORM.moduleName('components/logs'),
            //     nav: true,
            //     title: 'Logs'
            // },
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

    navigateTo(nav) {
        this._router.navigate(nav.href)
        this._drawer.open = false;
    }
}


