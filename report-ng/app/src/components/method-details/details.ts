/**
 * Optimized highlight.js import
 * @see https://github.com/highlightjs/highlight.js#es6-modules
 */
import hljs from 'highlight.js/lib/core';
import plaintext from 'highlight.js/lib/languages/plaintext';
import java from 'highlight.js/lib/languages/java';
import 'highlight.js/styles/darcula.css';
import {autoinject} from 'aurelia-framework';
import {StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import {FailureAspectStatistics} from "../../services/statistic-models";
import {DataLoader} from "../../services/data-loader";
import {Config} from "../../services/config";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import IStackTraceCause = data.IStackTraceCause;
import IMethodContext = data.IMethodContext;

@autoinject()
export class Details {
    private _hljs = hljs;
    private _stackTrace:IStackTraceCause[];
    private _failureAspect:FailureAspectStatistics;
    private _methodContext:IMethodContext;
    private _methodDetails;

    constructor(
        private _statistics: StatisticsGenerator,
        private _dataLoader:DataLoader,
        private _config:Config,
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
            this._methodContext = methodDetails.methodContext;
            if (this._methodContext.errorContext) {
                if (this._methodContext.errorContext?.cause) {
                    this._stackTrace = [];
                    let cause = this._methodContext.errorContext.cause;
                    do {
                        this._stackTrace.push(cause);
                        cause = cause.cause;
                    } while (cause);
                }
                this._failureAspect = new FailureAspectStatistics().setErrorContext(this._methodContext.errorContext);
            }
        });
    }
}
