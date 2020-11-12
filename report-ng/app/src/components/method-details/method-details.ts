/**
 * Optimized highlight.js import
 * @see https://github.com/highlightjs/highlight.js#es6-modules
 */
import hljs from 'highlight.js/lib/core';
import plaintext from 'highlight.js/lib/languages/plaintext';
import java from 'highlight.js/lib/languages/java';
import 'highlight.js/styles/darcula.css';
import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {StatusConverter} from "../../services/status-converter";
import {data} from "../../services/report-model";
import {FailureAspectStatistics} from "../../services/statistic-models";
import IMethodContext = data.IMethodContext;
import IClassContext = data.IClassContext;
import ITestContext = data.ITestContext;
import ISuiteContext = data.ISuiteContext;
import IStackTraceCause = data.IStackTraceCause;

@autoinject()
export class MethodDetails extends AbstractViewModel {

    private _hljs = hljs;
    private _methodContext:IMethodContext;
    private _classContext:IClassContext;
    private _testContext:ITestContext;
    private _suiteContext:ISuiteContext;
    private _stackTrace:IStackTraceCause[];
    private _failureAspect:FailureAspectStatistics;

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter: StatusConverter,
    ) {
        super();
        this._hljs.registerLanguage("java", java);
        this._hljs.registerLanguage("plaintext", plaintext);
        //this._hljs.registerLanguage("xml", xml);
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);

        this._statistics.getExecutionStatistics().then(executionStatistics => {
            for (const classStatistic of executionStatistics.classStatistics) {
                this._methodContext = classStatistic.classAggregate.methodContexts
                    .find(methodContext => methodContext.contextValues.id == this.queryParams.id);

                if (this._methodContext) {
                    this._classContext = classStatistic.classAggregate.classContext;
                    this._testContext = executionStatistics.executionAggregate.testContexts.find(testContext => testContext.classContextIds.find(id => this._classContext.contextValues.id == id));
                    this._suiteContext = executionStatistics.executionAggregate.suiteContexts.find(suiteContext => suiteContext.testContextIds.find(id => this._testContext.contextValues.id));
                    if (this._methodContext.errorContext?.cause) {
                        this._stackTrace = [];
                        let cause = this._methodContext.errorContext.cause;
                        do {
                            this._stackTrace.push(cause);
                            cause = cause.cause;
                        } while (cause);
                    }

                    this._failureAspect = new FailureAspectStatistics().addMethodContext(this._methodContext);

                    console.log(this._methodContext);

                    break;
                }
            }
        });
    }
}
