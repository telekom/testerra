import {autoinject, PLATFORM} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig, Router, RouterConfiguration} from "aurelia-router";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import IClassContext = data.IClassContext;
import ITestContext = data.ITestContext;
import ISuiteContext = data.ISuiteContext;
import IMethodContext = data.IMethodContext;

@autoinject()
export class Method {

    private _classContext:IClassContext;
    private _testContext:ITestContext;
    private _suiteContext:ISuiteContext;
    private _router:Router;
    private _methodContext:IMethodContext;

    constructor(
        private _statistics: StatisticsGenerator,
    ) {
    }

    configureRouter(config: RouterConfiguration, router: Router) {
        this._router = router;
        config.map([
            {
                route: '',
                redirect: 'details',
            },
            {
                route: 'details',
                moduleId: PLATFORM.moduleName('./details'),
                nav: true,
                name: "details",
                title: 'Details',
                settings: {
                    icon: "timeline"
                }
            },
            {
                route: 'steps',
                moduleId: PLATFORM.moduleName('./details'),
                nav: true,
                name: "steps",
                title: 'Steps',
                settings: {
                    icon: "reorder"
                }
            },
            {
                route: 'videos',
                moduleId: PLATFORM.moduleName('./details'),
                nav: true,
                name: "videos",
                title: 'Videos',
                settings: {
                    icon: "videocam"
                }
            },
            {
                route: 'dependencies',
                moduleId: PLATFORM.moduleName('./details'),
                nav: true,
                name: "dependencies",
                title: 'Dependencies',
                settings: {
                    icon: "sync_alt"
                }
            },
        ]);
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        this._statistics.getMethodDetails(params.id).then(methodDetails => {
            this._classContext = methodDetails.classStatistics.classAggregate.classContext;
            this._methodContext = methodDetails.methodContext;
            this._testContext = methodDetails.testContext;
            this._suiteContext = methodDetails.suiteContext;
        });
    }

    private _tabClicked(routeConfig:RouteConfig) {
        this._router.navigateToRoute(routeConfig.name);
    }
}
