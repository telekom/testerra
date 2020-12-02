import {autoinject, PLATFORM} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig, Router, RouterConfiguration} from "aurelia-router";
import {IMethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import {ScreenshotsDialog} from "../screenshots-dialog/screenshots-dialog";
import {MdcDialogService} from '@aurelia-mdc-web/dialog';
import IFile = data.IFile;


@autoinject()
export class Method {
    private _router:Router;
    private _screenshots:IFile[];
    private _lastScreenshot:IFile;
    private _methodDetails:IMethodDetails;

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
                route: 'steps',
                moduleId: PLATFORM.moduleName('./steps'),
                nav: true,
                name: "steps",
                title: 'Steps',
                settings: {
                    icon: "list"
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
                moduleId: PLATFORM.moduleName('./dependencies'),
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
            this._methodDetails = methodDetails;
            this._statistics.getScreenshotsFromMethodContext(methodDetails.methodContext).then(screenshots => {
                this._screenshots = screenshots;
                this._lastScreenshot = this._screenshots.find(() => true);
            })

            if (!routeConfig.hasChildRouter) {
                console.log("route to default content");
                const navOptions = {replace:true};
                if (this._methodDetails.hasDetails) {
                    this._router.navigateToRoute("details", {}, navOptions);
                } else {
                    this._router.navigateToRoute("steps", {}, navOptions);
                }
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
                screenshots: this._screenshots
            },
            class: "screenshot-dialog"
        });
    }
}
