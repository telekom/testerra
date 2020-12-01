import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {StatusConverter} from "../../services/status-converter";
import {FailureAspectStatistics} from "../../services/statistic-models";
import { DataSet, Timeline } from "vis-timeline/standalone";
import "vis-timeline/styles/vis-timeline-graph2d.css";
import {data} from "../../services/report-model";
import MethodContext = data.MethodContext;

@autoinject()
export class Threads extends AbstractViewModel {
    private _searchRegexp: RegExp;
    private _loading = false;

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
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            executionStatistics.executionAggregate.methodContexts.forEach(value => {
                console.log(value.threadName, value.contextValues.startTime, value.contextValues.endTime);
            })
            this._loading = false;
            this.updateUrl(this.queryParams);
            this._prepareTimelineData(executionStatistics.executionAggregate.methodContexts)
        });
    }

    _prepareTimelineData(methodContexts) {
        // DOM element where the Timeline will be attached
        const container = document.getElementById("container");
        let data: Array<Object> = [];

        console.log(methodContexts);

        methodContexts.forEach(function(context: MethodContext, index) {
            let thread: Object;
            thread = {
                id: index,
                content: context.contextValues.name,
                start: context.contextValues.startTime,
                end: context.contextValues.endTime
            };
            data.push(thread);
        })

        // Create a DataSet (allows two way data-binding)
        const items = new DataSet(data);

        console.log("items: ", items);

        // Configuration for the Timeline
        const options = {};

        // Create a Timeline
        const timeline = new Timeline(container, items, options);
    }
}
