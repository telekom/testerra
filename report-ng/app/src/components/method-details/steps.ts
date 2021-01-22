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

import {autoinject} from 'aurelia-framework';
import {MethodDetails, StatisticsGenerator} from "services/statistics-generator";
import {data} from "services/report-model";
import {DataLoader} from "services/data-loader";
import {Config} from "services/config-dev";
import {MdcDialogService} from '@aurelia-mdc-web/dialog';
import {IScreenshotsDialogParams, ScreenshotsDialog} from "../screenshots-dialog/screenshots-dialog";
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
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
    private _screenshotIds:string[]=[];
    private _clickPathEvents:IClickPathEvent[]=[];
    private _logMessages:ILogMessage[]=[];
    private _assertions:IErrorContext[]=[];

    addScreenshotId(screenshotId:string) {
        this._screenshotIds.push(screenshotId);
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

    get screenshotIds() {
        return this._screenshotIds;
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
    private _methodDetails:MethodDetails;
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
                                currentActionGroup.addScreenshotId(entry.screenshotId);
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

            if (params.step) {
                window.setTimeout(() => {
                    const stepHeadline = window.document.getElementById("step"+params.step);
                    stepHeadline?.scrollIntoView();
                }, 0);
            }
        });
    }

    private _showScreenshot(ev:CustomEvent) {
        this._dialogService.open({
            viewModel: ScreenshotsDialog,
            model: <IScreenshotsDialogParams> {
                current: ev.detail,
                screenshotIds: this._statistics.getScreenshotIdsFromMethodContext(this._methodDetails.methodContext)
            },
            class: "screenshot-dialog"
        });
    }
}
