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
import IClickPathEvent = data.IClickPathEvent;
import ILogMessage = data.ILogMessage;
import IErrorContext = data.IErrorContext;
import ITestStepAction = data.ITestStepAction;
import ITestStepActionEntry = data.ITestStepActionEntry;

enum EntryType {
    SCREENSHOT,
    ASSERTION,
    LOG_MESSAGE,
    CLICK_PATH_EVENT
}

class TestStepActionGroup {
    private _screenshots:IFile[]=[];
    private _clickPathEvents:IClickPathEvent[]=[];
    private _logMessages:ILogMessage[]=[];
    private _assertions:IErrorContext[]=[];

    addScreenshot(screenshot:IFile) {
        this._screenshots.push(screenshot);
    }

    addAssertion(assertion:IErrorContext) {
        this._assertions.push(assertion);
    }

    addClickPathEvent(clickPathEvent:IClickPathEvent) {
        this._clickPathEvents.push(clickPathEvent);
    }

    addLogMessage(logMessage:ILogMessage) {
        this._logMessages.push(logMessage);
    }

    get screenshots() {
        return this._screenshots;
    }

    get logMessages() {
        return this._logMessages;
    }

    get assertions() {
        return this._assertions;
    }

    get clickPathEvents() {
        return this._clickPathEvents;
    }

    static getEntryType(entry:ITestStepActionEntry) {
        if (entry.screenshotId) {
            return EntryType.SCREENSHOT;
        } else if (entry.assertion) {
            return EntryType.ASSERTION;
        } else if (entry.logMessage) {
            return EntryType.LOG_MESSAGE;
        } else if (entry.clickPathEvent) {
            return EntryType.CLICK_PATH_EVENT;
        }
    }
}

interface TestStepActionDetails extends ITestStepAction {
    groups:TestStepActionGroup[];
}

@autoinject()
export class Steps {
    private _methodDetails:IMethodDetails;
    private _allScreenshots:IFile[];
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
            const screenshotsLoadingPromise = this._statistics.getScreenshotsFromMethodContext(methodDetails.methodContext);

            this._methodDetails.methodContext.testSteps
                .flatMap(value => value.actions)
                .map(value => value as TestStepActionDetails)
                .forEach(actionDetails => {
                    let currentActionGroup = null;
                    let lastEntryType = null;
                    actionDetails.groups = [];
                    actionDetails.entries.forEach(entry => {
                        const currentEntryType = TestStepActionGroup.getEntryType(entry);
                        if (currentEntryType !== lastEntryType) {
                            currentActionGroup = new TestStepActionGroup();
                            actionDetails.groups.push(currentActionGroup);
                        }
                        switch (currentEntryType) {
                            case EntryType.SCREENSHOT: {
                                screenshotsLoadingPromise.then(screenshots => {
                                    currentActionGroup.addScreenshot(screenshots.find(value => value.id===entry.screenshotId));
                                });
                                break;
                            }
                            case EntryType.ASSERTION: {
                                currentActionGroup.addAssertion(entry.assertion);
                                break;
                            }
                            case EntryType.CLICK_PATH_EVENT: {
                                currentActionGroup.addClickPathEvent(entry.clickPathEvent);
                                break;
                            }
                            case EntryType.LOG_MESSAGE: {
                                currentActionGroup.addLogMessage(entry.logMessage);
                            }
                        }
                        lastEntryType = currentEntryType;
                    })
                });

            screenshotsLoadingPromise.then(value => {
                this._allScreenshots = value;
                console.log(this._methodDetails.methodContext.testSteps);
            });
        });
    }

    private _showScreenshot(file:data.File) {
        this._dialogService.open({
            viewModel: ScreenshotsDialog,
            model: {
                current: file,
                screenshots: this._allScreenshots
            },
            class: "screenshot-dialog"
        });
    }
}
