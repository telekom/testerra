import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {StatusConverter} from "../../services/status-converter";
import { DataSet, Timeline } from "vis-timeline/standalone";
import "vis-timeline/styles/vis-timeline-graph2d.css";
import {data} from "../../services/report-model";
import {Router} from "aurelia-router";
import MethodContext = data.MethodContext;
import ResultStatusType = data.ResultStatusType;
import "./threads.scss";
import IMethodContext = data.IMethodContext;
import IContextValues = data.IContextValues;
import {ExecutionStatistics} from "../../services/statistic-models";

@autoinject()
export class Threads extends AbstractViewModel {
    private _searchRegexp: RegExp;
    private _classNamesMap:{[key:string]:string};
    private _endTime;
    private _startTime;
    private _container:HTMLDivElement;
    private _methodNameInput:HTMLElement;
    private _executionStatistics:ExecutionStatistics;
    private _methodLookupOptions:IContextValues[] = [];

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter: StatusConverter,
        private _element: Element,
        private _router: Router
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;
    }

    attached() {
        this._loadData();
    }

    private _focusOn(methodId:string) {
        console.log("focus on", methodId);
    }

    private _loadData() {
        this._classNamesMap = {};

        this._statistics.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
            this._startTime = executionStatistics.executionAggregate.executionContext.contextValues.startTime - 1000;
            this._endTime = executionStatistics.executionAggregate.executionContext.contextValues.endTime + 1000;
            this._methodLookupOptions.splice(0,-1);
            this._methodLookupOptions = this._methodLookupOptions.concat(executionStatistics.executionAggregate.methodContexts.map(methodContext => methodContext.contextValues));
            if (this.queryParams.methodId) {
                this._focusOn(this.queryParams.methodId);
            }
            executionStatistics.classStatistics.forEach(classStatistic => {
                this._classNamesMap[classStatistic.classContext.contextValues.id] = classStatistic.classIdentifier;
            });
            this._prepareTimelineData(executionStatistics.executionAggregate.methodContexts)
        });
    }

    private _getLookupOptions = async (filter: string, methodId: string): Promise<IContextValues[]>  => {
        console.log("filter", filter, "method", methodId);
        let methodContexts:IMethodContext[];
        if (methodId) {
            methodContexts = this._executionStatistics.executionAggregate.methodContexts.filter(methodContext => methodContext.contextValues.id == methodId);
            this._searchRegexp = null;
            delete this.queryParams.methodName;
            this._focusOn(methodId);
            this.updateUrl({methodId: methodId});
        } else {
            this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this.queryParams.methodName);
            delete this.queryParams.methodId;
            methodContexts = this._executionStatistics.executionAggregate.methodContexts.filter(methodContext => methodContext.contextValues.name.match(this._searchRegexp));
        }
        return methodContexts.map(methodContext => methodContext.contextValues);
    };

    private _threadItemClicked(properties) {
        console.log("timeline element selected.", properties);
        console.log(properties.items[0]);
        let methodId = properties.items[0].split("_")[0];
        this._router.navigateToRoute('method', {methodId: methodId})
    }

    private _prepareTimelineData(methodContexts) {
        // DOM element where the Timeline will be attached
        const container = this._container;

        const style = new Map <string,string>();
        style.set("PASSED", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.PASSED) + "; color: #fff;");
        style.set("PASSED_RETRY", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.PASSED_RETRY) + "; color: #fff;");
        style.set("SKIPPED", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED) + "; color: #fff;");
        style.set("FAILED", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.FAILED) + "; color: #fff;");
        style.set("FAILED_EXPECTED", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED) + "; color: #fff;");
        style.set("FAILED_MINOR", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.FAILED_MINOR) + "; color: #fff;");
        style.set("FAILED_RETRIED", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.FAILED_RETRIED) + "; color: #fff;");

        let resultStatusType: ResultStatusType,
            groupItems = [],
            dataItems = [],
            dataMap = new Map();

        methodContexts.forEach(methodContext => {
            if (!dataMap.has(methodContext.threadName)) {
                dataMap.set(methodContext.threadName, []);
            }
            dataMap.get(methodContext.threadName).push(methodContext);
        });

        dataMap.forEach((methodContexts, threadName) => {
            let groupId: string = "group-" + threadName;
            groupItems.push({id: groupId, content: threadName});

            methodContexts.forEach((context: MethodContext, index) => {
                let content: string = ''

                content += "<div class='item-content'>";
                content += "<div class='item-content-head'>" + context.contextValues.name + "</div>";
                content += "<div class='item-content-body'>"
                content += "<p class='m0'>" + this._classNamesMap[context.classContextId] + "</p>";
                content += "<p class='m0'>(" + context.methodRunIndex + ")</p>";
                content += "</div>";
                content += "</div>";

                dataItems.push({
                    id: context.contextValues.id + "_" + threadName + "_" + index,
                    content: content,
                    start: context.contextValues.startTime,
                    end: context.contextValues.endTime,
                    group: groupId,
                    callbackInfos: [context.contextValues.id],
                    style: "background-color: " + this._statusConverter.getColorForStatus(context.contextValues.resultStatus) + ";",
                    title: content
                });
            });
        });

        groupItems.sort((item1, item2) => {
            let contentA = item1.content.toUpperCase(),
                contentB = item2.content.toUpperCase();

            if (contentA < contentB) {
                return -1;
            }
            if (contentA > contentB) {
                return 1;
            }

            return 0;
        });

        // Configuration for the Timeline
        const options = {
            onInitialDrawComplete: () => {
            },
            start:this._startTime,
            end:this._endTime,
            showTooltips:false,
            //max zoom out to be 1 Day
            zoomMax:8.64e+7,
            //Min Zoom set to be 10 Millisecond
            zoomMin:10,
        };

        // Create a Timeline
        const timeline = new Timeline(container, dataItems, groupItems, options);
        timeline.on('select',(event)=> { this._threadItemClicked(event); });
    }

}
