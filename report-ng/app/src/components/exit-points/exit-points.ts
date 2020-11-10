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
import {ExitPointStatistics} from "../../services/statistic-models";

@autoinject()
export class ExitPoints extends AbstractViewModel {

    private _searchQuery: string;
    private _searchRegexp:RegExp;
    private _filteredExitPoints:ExitPointStatistics[];
    private _hljs = hljs;

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter: StatusConverter
    ) {
        super();
        this._hljs.registerLanguage("java", java);
        this._hljs.registerLanguage("plaintext", plaintext);
        //this._hljs.registerLanguage("xml", xml);
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        if (params.q) {
            this._searchQuery = params.q;
        }
        this._filter();
    }

    private _filter() {
        this._filteredExitPoints = [];
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            executionStatistics.classStatistics.forEach(classStatistics => {
                classStatistics.classAggregate.methodContexts
                    .filter(methodContext => {
                        return this._statusConverter.failedStatuses.indexOf(methodContext.contextValues.resultStatus) >= 0;
                    })
                    .map(methodContext => {
                        return new ExitPointStatistics().addMethodContext(methodContext);
                    })
                    .filter(exitPointStatistics => {
                        return (!this._searchRegexp || exitPointStatistics.fingerprint.match(this._searchRegexp));
                    })
                    .forEach(exitPointStatistics => {
                        const foundExitPointStatistics = this._filteredExitPoints.find(existingExitPointStatistics => {
                            return existingExitPointStatistics.fingerprint == exitPointStatistics.fingerprint;
                        });
                        if (foundExitPointStatistics) {
                            foundExitPointStatistics.addMethodContext(exitPointStatistics.methodContext);
                        } else {
                            this._filteredExitPoints.push(exitPointStatistics);
                        }
                    });
            })
        });
    }
}
