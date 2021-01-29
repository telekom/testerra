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

/**
 * Optimized highlight.js import
 * @see https://github.com/highlightjs/highlight.js#es6-modules
 */
import hljs from 'highlight.js/lib/core';
import java from 'highlight.js/lib/languages/java';
import 'highlight.js/styles/darcula.css';
import {autoinject} from 'aurelia-framework';
import {MethodDetails, StatisticsGenerator} from "services/statistics-generator";
import {FailureAspectStatistics} from "services/statistic-models";
import {Config} from "services/config-dev";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {StatusConverter} from "services/status-converter";
import {data} from "../../services/report-model";
import {MdcSnackbarService} from '@aurelia-mdc-web/snackbar';
import IStackTraceCause = data.IStackTraceCause;

export interface ILayoutComparisonContext {
    name: string,
    image, mode, distance, actualScreenshot, annotatedScreenshot, distanceScreenshot, expectedScreenshot
}

@autoinject()
export class Details {
    private _hljs = hljs;
    private _failureAspect: FailureAspectStatistics;
    private _methodDetails: MethodDetails;
    private _layoutComparisonContext: ILayoutComparisonContext;

    constructor(
        private _statistics: StatisticsGenerator,
        private _config: Config,
        private _statusConverter: StatusConverter,
        private _snackBar: MdcSnackbarService
    ) {
        this._hljs.registerLanguage("java", java);
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        this._statistics.getMethodDetails(params.methodId).then(methodDetails => {
            this._methodDetails = methodDetails;
            this._layoutComparisonContext = methodDetails.customContexts.find(value => {
                return value.name == "LayoutCheckContext";
            })
            if (methodDetails.methodContext.errorContext) {
                this._failureAspect = new FailureAspectStatistics().setErrorContext(methodDetails.methodContext.errorContext);
            }
        });
    }

    private _copyStackTraceToClipboard(stackTrace:IStackTraceCause[]) {
        const msg = stackTrace.flatMap(cause => cause.stackTraceElements).join("\n");
        navigator.clipboard.writeText(msg).then(response => {
            this._snackbarNotification('Stacktrace copied to clipboard');
        });


    }

    private async _snackbarNotification(message: string) {
        await this._snackBar.open(message,undefined,{
            dismissible: true,
            classes: "snackbar--fill-color"
        });
    }
}
