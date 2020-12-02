import {autoinject} from 'aurelia-framework';
import {StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import {DataLoader} from "../../services/data-loader";
import {Config} from "../../services/config";
import {MdcDialogService} from '@aurelia-mdc-web/dialog';
import {ScreenshotsDialog} from "../screenshots-dialog/screenshots-dialog";
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import IMethodContext = data.IMethodContext;
import IFile = data.IFile;

@autoinject()
export class Steps {
    private _methodContext:IMethodContext;
    private _screenshots:IFile[] = [];
    private _router:Router;

    constructor(
        private _statistics: StatisticsGenerator,
        private _dataLoader:DataLoader,
        private _config:Config,
        private _dialogService:MdcDialogService,
    ) {

    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        this._router = navInstruction.router;
        this._statistics.getMethodDetails(params.id).then(methodDetails => {
            this._methodContext = methodDetails.methodContext;
            this._statistics.getScreenshotsFromMethodContext(this._methodContext).then(screenshots => {
                screenshots.forEach(screenshot => {
                    this._screenshots.push(screenshot);
                })
            })
        });
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

    private _findScreenshot(id:string) {
        return this._screenshots?.find(value => value.id == id);
    }

    private _gotoStep(stepIndex:number, actionIndex:number) {
        let elementsByName = window.document.getElementsByName(stepIndex+"."+actionIndex);
        if (elementsByName.length > 0) {
            elementsByName[0].scrollIntoView()
        }
    }

    private _stepVisibility($event:Event) {
        const anchor = $event.target as HTMLAnchorElement;
        const parts = anchor.getAttribute("name").split(".");
        console.log("enable", parts);

    }
}
