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

import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {StatusConverter} from "../../services/status-converter";
import {Timeline, TimelineOptionsGroupHeightModeType} from "vis-timeline/standalone";
import "vis-timeline/styles/vis-timeline-graph2d.css";
import {data} from "../../services/report-model";
import MethodContext = data.MethodContext;
import ResultStatusType = data.ResultStatusType;
import IMethodContext = data.MethodContext;
import IContextValues = data.ContextValues;
import "./threads.scss";
import {ExecutionStatistics} from "../../services/statistic-models";

@autoinject()
export class Threads extends AbstractViewModel {
    private _searchRegexp: RegExp;
    private _classNamesMap:{[key:string]:string};
    private _loading: boolean;
    private _container:HTMLDivElement;
    private _methodNameInput:HTMLElement;
    private _inputValue;
    private _timeline;
    private _currentSelection;

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter: StatusConverter,
        private _router: Router
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;
    }

    attached() {
        this._loading = true;
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            this._classNamesMap = {};
            executionStatistics.classStatistics.forEach(classStatistic => {
                this._classNamesMap[classStatistic.classContext.contextValues.id] = classStatistic.classIdentifier;
            });
            this._prepareTimelineData(executionStatistics)
        });
    }

    selectionChanged(){
        if (this._inputValue.length == 0){
            this.updateUrl({});
            this._timeline.fit();
        }
    }

    private _focusOn(methodId:string) {
        //adjusts timeline zoom to selected method
        this._timeline.setSelection(methodId, {focus: "true"});
        window.setTimeout(() => {
            const methodElement = document.getElementById(methodId);
            methodElement?.scrollIntoView();
        }, 500);
    }

    private _getLookupOptions = async (filter: string, methodId: string): Promise<IContextValues[]>  => {
        return this._statistics.getExecutionStatistics().then(executionStatistics => {
            let methodContexts:IMethodContext[];
            if (methodId) {
                methodContexts = [executionStatistics.executionAggregate.methodContexts[methodId]];
                this._searchRegexp = null;
                delete this.queryParams.methodName;
                this._focusOn(methodId);
                this.updateUrl({methodId: methodId});
            } else if (filter?.length > 0) {
                this._searchRegexp = this._statusConverter.createRegexpFromSearchString(filter);
                delete this.queryParams.methodId;
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts).filter(methodContext => methodContext.contextValues.name.match(this._searchRegexp));
            } else {
                methodContexts = Object.values(executionStatistics.executionAggregate.methodContexts);
            }
            return methodContexts.map(methodContext => methodContext.contextValues);
        });
    };

    private _threadItemClicked(properties) {
        // console.log("timeline element selected.", properties);
        let methodId = properties.items[0].split("_")[0];
        this._router.navigateToRoute('method', {methodId: methodId})
    }

    private _prepareTimelineData(executionStatistics:ExecutionStatistics) {
        // DOM element where the Timeline will be attached
        const container = this._container;

        const style = new Map <string,string>();
        style.set("PASSED", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.PASSED) + "; color: #fff;");
        style.set("REPAIRED", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.REPAIRED) + "; color: #fff;");
        style.set("PASSED_RETRY", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.PASSED_RETRY) + "; color: #fff;");
        style.set("SKIPPED", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.SKIPPED) + "; color: #fff;");
        style.set("FAILED", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.FAILED) + "; color: #fff;");
        style.set("FAILED_EXPECTED", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.FAILED_EXPECTED) + "; color: #fff;");
        style.set("FAILED_MINOR", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.FAILED_MINOR) + "; color: #fff;");
        style.set("FAILED_RETRIED", "background-color: " + this._statusConverter.getColorForStatus(ResultStatusType.FAILED_RETRIED) + "; color: #fff;");

        const groupItems = [];
        const dataItems = [];
        const dataMap = new Map();

        Object.values(executionStatistics.executionAggregate.methodContexts).forEach(methodContext => {
            if (!dataMap.has(methodContext.threadName)) {
                dataMap.set(methodContext.threadName, []);
            }
            dataMap.get(methodContext.threadName).push(methodContext);
        });

        dataMap.forEach((methodContexts, threadName) => {
            let groupId: string = "group-" + threadName;
            groupItems.push({id: groupId, content: threadName});

            methodContexts.forEach((context: MethodContext) => {
                /*
                * workaround for XSS-protection update of vis-timeline by using an HTMLElement instead of injecting the html directly in which the XSS protection would remove the class names needed for our styling
                * @see: https://github.com/visjs/vis-timeline/issues/846#issuecomment-749691286
                */
                const element = document.createElement("content");
                element.innerHTML = `
                    <div class="item-content" id="${context.contextValues.id}">
                    <div class="item-content-head">${context.contextValues.name}</div>
                    <div class='item-content-body'>
                    <p class="m0">${this._classNamesMap[context.classContextId]}</p>
                    <p class="m0">(${context.methodRunIndex})</p>
                    </div>
                    </div>
                `;
                dataItems.push({
                    id: context.contextValues.id,
                    content: element,
                    start: context.contextValues.startTime,
                    end: context.contextValues.endTime,
                    group: groupId,
                    callbackInfos: [context.contextValues.id],
                    style: "background-color: " + this._statusConverter.getColorForStatus(context.resultStatus) + ";",
                    title: context.contextValues.name
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
                this._loading = false;

                if (this.queryParams.methodId?.length > 0) {
                    this._focusOn(this.queryParams.methodId);
                }
            },
            showTooltips:false,
            //max zoom out to be 1 Day
            zoomMax:8.64e+7,
            //Min Zoom set to be 10 Millisecond
            zoomMin:10,
            margin: {
                item: { horizontal: 2 }
            },
            groupHeightMode: 'fixed' as TimelineOptionsGroupHeightModeType
        };

        // Create a Timeline
        this._timeline = new Timeline(container, dataItems, groupItems, options);
        this._timeline.on('select',(event) => {
            this._threadItemClicked(event);
        });
    }
}
