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

import {autoinject} from "aurelia-framework";
import {IFilter, StatusConverter} from "services/status-converter";
import {StatisticsGenerator} from "services/statistics-generator";
import {ExecutionStatistics, FailureAspectStatistics} from "services/statistic-models";
import {AbstractViewModel} from "../abstract-view-model";
import {data} from "../../services/report-model";
import "./dashboard.scss"
import {ClassBarClick} from "../test-classes-card/test-classes-card";
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {PieceClickedEvent} from "../test-results-chart/test-results-chart";
import FailureCorridorValue = data.FailureCorridorValue;
import ResultStatusType = data.ResultStatusType;

class FailureCorridor {
    count: number = 0;
    limit: number = 0;

    get matched() {
        return this.count <= this.limit;
    }
}

@autoinject()
export class Dashboard extends AbstractViewModel {
    private _executionStatistics: ExecutionStatistics;
    private _majorFailures = 0;
    private _minorFailures = 0;
    private _highCorridor = new FailureCorridor();
    private _midCorridor = new FailureCorridor();
    private _lowCorridor = new FailureCorridor();
    private _filter: IFilter;
    private _topFailureAspects: FailureAspectStatistics[];
    private _loading = true;
    private _promptLogs = undefined;
    private _numberOfTestcases = 0;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        if (this.queryParams.status) {
            this._filter = {
                status: this._statusConverter.getStatusForClass(this.queryParams.status)
            }
        }
    }

    async attached() {
        this._executionStatistics = await this._statisticsGenerator.getExecutionStatistics();
        this._topFailureAspects = this._executionStatistics.uniqueFailureAspects.slice(0, 3);
        this._numberOfTestcases = this._executionStatistics.overallFailed + this._executionStatistics.getStatusCount(ResultStatusType.FAILED_EXPECTED) + this._executionStatistics.overallSkipped + this._executionStatistics.overallPassed;

        this._executionStatistics.uniqueFailureAspects.forEach(failureAspectStatistics => {
            if (failureAspectStatistics.isMinor) {
                ++this._minorFailures;
            } else {
                ++this._majorFailures
            }
        })

        /**
         * @todo Move this to {@link ExecutionStatistics}?
         */
        const executionAggregate = this._executionStatistics.executionAggregate;
        this._highCorridor.limit = executionAggregate.executionContext.failureCorridorLimits[FailureCorridorValue.FCV_HIGH];
        this._highCorridor.count = executionAggregate.executionContext.failureCorridorCounts[FailureCorridorValue.FCV_HIGH] || 0;
        this._midCorridor.limit = executionAggregate.executionContext.failureCorridorLimits[FailureCorridorValue.FCV_MID];
        this._midCorridor.count = executionAggregate.executionContext.failureCorridorCounts[FailureCorridorValue.FCV_MID] || 0;
        this._lowCorridor.limit = executionAggregate.executionContext.failureCorridorLimits[FailureCorridorValue.FCV_LOW];
        this._lowCorridor.count = executionAggregate.executionContext.failureCorridorCounts[FailureCorridorValue.FCV_LOW] || 0;

        this._loading = false;

        this._promptLogs = await this.getPromptLogs();
    };

    async getPromptLogs() {
        const logMessages = await this._statisticsGenerator.getLogs()
        return Object.values(logMessages).filter(logMessage => logMessage.prompt && this._executionStatistics.executionContextLogMessageIds.includes(logMessage.id))
    }

    private _setFilter(filter: IFilter, updateUrl: boolean = true) {
        this._filter = filter;
        if (filter) {
            this.queryParams.status = this._statusConverter.getClassForStatus(this._filter.status);
            this.queryParams.class = this._filter.class;
        } else {
            delete this.queryParams.status;
        }
        if (updateUrl) {
            this.updateUrl(this.queryParams);
        }
    }

    private _piePieceClicked(ev: PieceClickedEvent) {
        if (ev.detail.filter?.status === this._filter?.status) {
            this._setFilter(null);
        } else {
            this._setFilter(ev.detail.filter);
        }
    }

    private _classBarClicked(ev: ClassBarClick) {
        this._setFilter(ev.detail.filter, false);
        if (ev.detail.mouseEvent.button == 0) {
            this.navInstruction.router.navigateToRoute("tests", this.queryParams);
        } else {
            // Open window in new tab
            const classView = window.open(this.navInstruction.router.generate("tests", this.queryParams));
            classView.blur();
            window.focus()
        }
    }

    private _compareRunsClicked() {
        this.navInstruction.router.navigateToRoute("history", {subPage: "run-comparison"});
    }

    private _gotoFailureAspect(failureAspect: FailureAspectStatistics) {
        this.navInstruction.router.navigateToRoute("tests", {
            failureAspect: failureAspect.index + 1,
            status: this._statusConverter.getClassForStatus(failureAspect.getUpmostStatus())
        });
    }
}
