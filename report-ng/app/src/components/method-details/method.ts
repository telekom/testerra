import {autoinject, PLATFORM} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig, Router, RouterConfiguration} from "aurelia-router";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import {ScreenshotsDialog} from "../screenshots-dialog/screenshots-dialog";
import {MdcDialogService} from '@aurelia-mdc-web/dialog';
import IClassContext = data.IClassContext;
import ITestContext = data.ITestContext;
import ISuiteContext = data.ISuiteContext;
import IMethodContext = data.IMethodContext;
import IFile = data.IFile;


@autoinject()
export class Method {

    private _classContext:IClassContext;
    private _testContext:ITestContext;
    private _suiteContext:ISuiteContext;
    private _router:Router;
    private _methodContext:IMethodContext;
    private _screenshots:IFile[];
    private _lastScreenshot:IFile;

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
                moduleId: PLATFORM.moduleName('./steps'),
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
            this._statistics.getScreenshotsFromMethodContext(this._methodContext).then(screenshots => {
                this._screenshots = Object.values(screenshots);
                this._lastScreenshot = this._screenshots.find(() => true);
            })
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
                screenshots: this._screenshots
            },
            class: "screenshot-dialog"
        });
    }
}
