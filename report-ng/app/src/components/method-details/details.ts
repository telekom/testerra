/**
 * Optimized highlight.js import
 * @see https://github.com/highlightjs/highlight.js#es6-modules
 */
import hljs from 'highlight.js/lib/core';
import java from 'highlight.js/lib/languages/java';
import 'highlight.js/styles/darcula.css';
import {autoinject} from 'aurelia-framework';
import {IMethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {FailureAspectStatistics} from "../../services/statistic-models";
import {Config} from "../../services/config";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {StatusConverter} from "../../services/status-converter";

@autoinject()
export class Details {
    private _hljs = hljs;
    private _failureAspect:FailureAspectStatistics;
    private _methodDetails:IMethodDetails;

    constructor(
        private _statistics: StatisticsGenerator,
        private _config:Config,
        private _statusConverter:StatusConverter,
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
            if (methodDetails.methodContext.errorContext) {
                this._failureAspect = new FailureAspectStatistics().setErrorContext(methodDetails.methodContext.errorContext);
            }
        });
    }
}
