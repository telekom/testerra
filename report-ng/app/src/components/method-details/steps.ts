import {autoinject} from 'aurelia-framework';
import {IMethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import {DataLoader} from "../../services/data-loader";
import {Config} from "../../services/config";
import {MdcDialogService} from '@aurelia-mdc-web/dialog';
import {ScreenshotsDialog} from "../screenshots-dialog/screenshots-dialog";
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import IFile = data.IFile;
import './timeline.scss'
import ITestStepAction = data.ITestStepAction;

interface TestStepActionDetails extends ITestStepAction {
    screenshots:IFile[];
}

@autoinject()
export class Steps {
    private _methodDetails:IMethodDetails;
    private _screenshots:IFile[];
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
        this._statistics.getMethodDetails(params.methodId).then(methodDetails => {
            this._methodDetails = methodDetails;
            this._screenshots = [];
            this._statistics.getScreenshotsFromMethodContext(methodDetails.methodContext).then(screenshots => {
                this._methodDetails.methodContext.testSteps
                    .flatMap(testStep => testStep.testStepActions)
                    .forEach(testStepAction => {
                        const details = testStepAction as TestStepActionDetails;
                        details.screenshots = screenshots.filter(screenshot => testStepAction.screenshotIds.indexOf(screenshot.id));
                    });
                this._screenshots = this._screenshots.concat(screenshots);
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
}
