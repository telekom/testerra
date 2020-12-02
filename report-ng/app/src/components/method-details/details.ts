/**
 * Optimized highlight.js import
 * @see https://github.com/highlightjs/highlight.js#es6-modules
 */
import hljs from 'highlight.js/lib/core';
import plaintext from 'highlight.js/lib/languages/plaintext';
import java from 'highlight.js/lib/languages/java';
import 'highlight.js/styles/darcula.css';
import {autoinject} from 'aurelia-framework';
import {IMethodDetails, StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import {FailureAspectStatistics} from "../../services/statistic-models";
import {DataLoader} from "../../services/data-loader";
import {Config} from "../../services/config";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import IStackTraceCause = data.IStackTraceCause;
import IMethodContext = data.IMethodContext;
import {StatusConverter} from "../../services/status-converter";

@autoinject()
export class Details {
    private _hljs = hljs;
    private _stackTrace:IStackTraceCause[];
    private _failureAspect:FailureAspectStatistics;
    /**
     * @deprecated
     */
    private _methodContext:IMethodContext;
    private _methodDetails:IMethodDetails;

    constructor(
        private _statistics: StatisticsGenerator,
        private _dataLoader:DataLoader,
        private _config:Config,
        private _statusConverter:StatusConverter,
    ) {
        this._hljs.registerLanguage("java", java);
        this._hljs.registerLanguage("plaintext", plaintext);
        //this._hljs.registerLanguage("xml", xml);
    }

    activate(
        params: any,
        routeConfig: RouteConfig,
        navInstruction: NavigationInstruction
    ) {
        this._statistics.getMethodDetails(params.id).then(methodDetails => {
            this._methodDetails = methodDetails;
            if (methodDetails.methodContext.errorContext?.causeId) {
                this._stackTrace = this._statusConverter.flattenStackTrace(methodDetails.methodContext.errorContext.causeId, methodDetails.executionStatistics.executionAggregate.executionContext);
                this._failureAspect = new FailureAspectStatistics().setErrorContext(methodDetails.methodContext.errorContext, methodDetails.executionStatistics.executionAggregate.executionContext);
            }
        });
    }
}
