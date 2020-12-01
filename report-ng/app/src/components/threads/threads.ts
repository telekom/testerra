import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {StatusConverter} from "../../services/status-converter";
import {FailureAspectStatistics} from "../../services/statistic-models";
import { DataSet, Timeline } from "vis-timeline/standalone";
import "vis-timeline/styles/vis-timeline-graph2d.css";

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
            this._prepareTimelineData()
        });
    }

    _prepareTimelineData() {
        // DOM element where the Timeline will be attached
        const container = document.getElementById("container");

// Create a DataSet (allows two way data-binding)
        const items = new DataSet([
            { id: 1, content: "item 1", start: "2014-04-20" },
            { id: 2, content: "item 2", start: "2014-04-14" },
            { id: 3, content: "item 3", start: "2014-04-18" },
            { id: 4, content: "item 4", start: "2014-04-16", end: "2014-04-19" },
            { id: 5, content: "item 5", start: "2014-04-25" },
            { id: 6, content: "item 6", start: "2014-04-27", type: "point" }
        ]);

// Configuration for the Timeline
        const options = {};

// Create a Timeline
        const timeline = new Timeline(container, items, options);
    }
}
