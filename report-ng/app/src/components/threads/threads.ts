import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {StatusConverter} from "../../services/status-converter";
import {ExecutionStatistics, FailureAspectStatistics} from "../../services/statistic-models";
import { DataSet, Timeline } from "vis-timeline/standalone";
import "vis-timeline/styles/vis-timeline-graph2d.css";
import {data} from "../../services/report-model";
import MethodContext = data.MethodContext;
import ResultStatusType = data.ResultStatusType;

@autoinject()
export class Threads extends AbstractViewModel {
    private _searchRegexp: RegExp;
    private _loading = false;
    private _classNamesMap: Map <string, string> = undefined;

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter:StatusConverter
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._filter();
    }

    private _filter() {
        if (this.queryParams?.q?.trim().length > 0) {
            this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this.queryParams.q);
        } else {
            this._searchRegexp = null;
        }

        this._loading = true;

        this._classNamesMap = new Map<string, string>();

        this._statistics.getExecutionStatistics().then(executionStatistics => {
            executionStatistics.executionAggregate.methodContexts.forEach(value => {
                console.log(value.threadName, value.contextValues.startTime, value.contextValues.endTime);
            })
            this._loading = false;
            this.updateUrl(this.queryParams);
            executionStatistics.classStatistics.forEach(classStatistic => {
                this._classNamesMap[classStatistic.classContext.contextValues.id] = classStatistic.classIdentifier;
            });
            this._prepareTimelineData(executionStatistics.executionAggregate.methodContexts)
        });
    }

    _prepareTimelineData(methodContexts) {
        // DOM element where the Timeline will be attached
        const container = document.getElementById("container");

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

            methodContexts.forEach((context: MethodContext, i) => {
                let style: string = '',
                    content: string = "";

                content += "<div class=''>";
                content += "<h5 class='text-center item-content-title py-1 mt-0 mb-0'>" + context.contextValues.name + "</h5>";
                content += "<hr>"
                content += "<div class='item-content-body'>"
                content += "<p class='text-center mt-0 mb-0'>" + this._classNamesMap[context.classContextId] + "</p>";
                content += "<p class='text-center mt-0 mb-0'>(" + context.methodRunIndex + ")</p>";
                content += "</div>"
                content += "</div>";

                dataItems.push({
                    id: "thread-" + threadName + "_" + i,
                    content: content,
                    start: context.contextValues.startTime,
                    end: context.contextValues.endTime,
                    group: groupId,
                    callbackInfos: [context.contextValues.id],
                    className: style,
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
        const options = {};

        // Create a Timeline
        const timeline = new Timeline(container, dataItems, groupItems, options);
    }
}
