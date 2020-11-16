import {autoinject, PLATFORM} from 'aurelia-framework';
import {RouteConfig, Router, RouterConfiguration} from "aurelia-router";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import {AbstractMethod} from "./abstract-method";
import IClassContext = data.IClassContext;
import ITestContext = data.ITestContext;
import ISuiteContext = data.ISuiteContext;

@autoinject()
export class Method extends AbstractMethod {

    private _classContext:IClassContext;
    private _testContext:ITestContext;
    private _suiteContext:ISuiteContext;
    private _router:Router;

    constructor(
        statistics: StatisticsGenerator,
    ) {
        super(statistics);
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

    protected loaded() {
        this._classContext = this.classStatistic.classAggregate.classContext;
        this._testContext = this.executionStatistic.executionAggregate.testContexts.find(testContext => testContext.classContextIds.find(id => this._classContext.contextValues.id == id));
        this._suiteContext = this.executionStatistic.executionAggregate.suiteContexts.find(suiteContext => suiteContext.testContextIds.find(id => this._testContext.contextValues.id));
    }

    private _tabClicked(routeConfig:RouteConfig) {
        this._router.navigateToRoute(routeConfig.name);
    }
}
