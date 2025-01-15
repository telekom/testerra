/*
 * Testerra
 *
 * (C) 2024, Tobias Adler, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {autoinject, observable} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import {AbstractViewModel} from "../../abstract-view-model";
import "./run-history.scss";
import {HistoryStatistics, MethodHistoryStatistics} from "../../../services/statistic-models";
import {IFilter, StatusConverter} from "../../../services/status-converter";
import {StatisticsGenerator} from "../../../services/statistics-generator";
import {ResultStatusType} from "../../../services/report-model/framework_pb";
import {MdcSelect} from "@aurelia-mdc-web/select";

@autoinject()
export class RunHistory extends AbstractViewModel {
    totalRunCount: number = 0;
    avgRunDuration: number = 0;
    overallSuccessRate: number = 0;
    statusData: any[] = [];
    @observable viewport: number[] = [];
    private _fullViewport: number[] = [];
    private _historyStatistics: HistoryStatistics;
    private _filter: IFilter;
    private _selectedStatus: ResultStatusType = null;
    private _availableStatuses: ResultStatusType[] = [];
    private _topFlakyTests: any[] = [];
    private _topFailingTests: any[] = [];
    private _statusSelect: MdcSelect;
    private _historyAvailable = false;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _router: Router
    ) {
        super();
    }

    async activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;

        this._historyStatistics = await this._statisticsGenerator.getHistoryStatistics();
        this.totalRunCount = this._historyStatistics.getTotalRunCount();
        if (this.totalRunCount > 1) {
            this._historyAvailable = true;
        }

        const availableRuns = this._historyStatistics.availableRuns;
        this._fullViewport = [Math.min(...availableRuns), Math.max(...availableRuns)];
        this.viewport = this._fullViewport;

        let statusCount = new Map<ResultStatusType, number>();
        this._historyStatistics.getHistoryAggregateStatistics().forEach(aggregate => {
            const currentFailed = statusCount.get(ResultStatusType.FAILED) || 0;
            const currentExpectedFailed = statusCount.get(ResultStatusType.FAILED_EXPECTED) || 0;
            const currentSkipped = statusCount.get(ResultStatusType.SKIPPED) || 0;
            const currentPassed = statusCount.get(ResultStatusType.PASSED) || 0;

            statusCount.set(ResultStatusType.FAILED, currentFailed + aggregate.getStatusCount(ResultStatusType.FAILED));
            statusCount.set(ResultStatusType.FAILED_EXPECTED, currentExpectedFailed + aggregate.getStatusCount(ResultStatusType.FAILED_EXPECTED));
            statusCount.set(ResultStatusType.SKIPPED, currentSkipped + aggregate.getStatusCount(ResultStatusType.SKIPPED));
            statusCount.set(ResultStatusType.PASSED, currentPassed + aggregate.overallPassed);
        });
        const overallTestCount = Array.from(statusCount.values()).reduce((acc, value) => acc + value, 0);

        statusCount.forEach((value, key) => {
            if (value > 0) {
                this._availableStatuses.push(key);
            }
        });

        this.avgRunDuration = this._historyStatistics.getAverageDuration();
        this.overallSuccessRate = (statusCount.get(ResultStatusType.PASSED) / overallTestCount) * 100;

        if (this.queryParams.status) {
            this._filter = {
                status: this._statusConverter.getStatusForClass(this.queryParams.status)
            }
            this._selectedStatus = this.queryParams.status;
        }

        if (this.queryParams.status || params.status) {
            this._selectedStatus = this._statusConverter.getStatusForClass(params.status);
        } else {
            this._selectedStatus = null;
        }
    }

    private _statusChanged() {
        if (!this._selectedStatus) {
            this._setFilter(null);
        } else {
            this._setFilter({
                status: this._selectedStatus
            });
        }
        this.viewport = this._fullViewport;
    }

    viewportChanged() {
        if (this.viewport.length > 1) {
            this._updateTopFlakyTests();
            this._updateTopFailingTests();
            this._updateStatusData();
        }
    }

    private _updateTopFailingTests() {
        const methods = this._historyStatistics.getClassHistory().flatMap(classItem => classItem.methods);
        const startIndex = this.viewport[0];
        const endIndex = this.viewport[1];

        const failingMethods = methods
            .filter(method => method.getFailingStreakInRange(startIndex, endIndex) > 0)
            .filter(method => method.isTestMethod())
            .map(method => ({
                method,
                failingStreak: method.getFailingStreakInRange(startIndex, endIndex),
            }))
            .sort((a, b) => b.failingStreak - a.failingStreak)
            .slice(0, 3);

        this._topFailingTests = failingMethods.map(({method, failingStreak}) => ({
            name: method.identifier,
            failingStreak: failingStreak,
            statistics: method,
        }));
    }

    private _updateTopFlakyTests() {
        const methods = this._historyStatistics.getClassHistory().flatMap(classItem => classItem.methods);
        const startIndex = this.viewport[0];
        const endIndex = this.viewport[1];

        const flakyMethods = methods
            .map(method => ({
                method,
                flakiness: method.getFlakinessInRange(startIndex, endIndex),
            }))
            .filter(({flakiness}) => flakiness > 0.1)
            .sort((a, b) => b.flakiness - a.flakiness)
            .slice(0, 3);

        this._topFlakyTests = flakyMethods.map(({method, flakiness}) => ({
            name: method.identifier,
            flakiness: flakiness.toFixed(1),
            statistics: method,
        }));
    }

    private _updateStatusData() {
        let statusCount = new Map<ResultStatusType, number>();
        this._historyStatistics.getHistoryAggregateStatistics().forEach(aggregate => {
            if ((aggregate.historyIndex >= this.viewport[0]) && (aggregate.historyIndex <= this.viewport[1])) {
                const currentFailed = statusCount.get(ResultStatusType.FAILED) || 0;
                const currentExpectedFailed = statusCount.get(ResultStatusType.FAILED_EXPECTED) || 0;
                const currentSkipped = statusCount.get(ResultStatusType.SKIPPED) || 0;
                const currentPassed = statusCount.get(ResultStatusType.PASSED) || 0;

                statusCount.set(ResultStatusType.FAILED, currentFailed + aggregate.getStatusCount(ResultStatusType.FAILED));
                statusCount.set(ResultStatusType.FAILED_EXPECTED, currentExpectedFailed + aggregate.getStatusCount(ResultStatusType.FAILED_EXPECTED));
                statusCount.set(ResultStatusType.SKIPPED, currentSkipped + aggregate.getStatusCount(ResultStatusType.SKIPPED));
                statusCount.set(ResultStatusType.PASSED, currentPassed + aggregate.overallPassed);
            }
        });

        let statusData = [];
        statusCount.forEach((count, status) => {
            if (count) {
                statusData.push({
                    status: status,
                    statusName: this._statusConverter.getLabelForStatus(status),
                    value: count,
                    itemStyle: {color: this._statusConverter.getColorForStatus(status)}
                });
            }
        });
        this.statusData = statusData;
    }

    private _setFilter(filter: IFilter, updateUrl: boolean = true) {
        this._filter = filter;
        if (filter) {
            this.queryParams.status = this._statusConverter.getClassForStatus(this._filter.status);
        } else {
            delete this.queryParams.status;
        }
        if (updateUrl) {
            this.updateUrl(this.queryParams);
        }
    }

    private _navigateToMethodHistory(methodHistoryStatistics: MethodHistoryStatistics) {
        this._router.navigateToRoute('method', {
            methodId: methodHistoryStatistics.getIdOfRun(this._historyStatistics.getLastEntry().historyIndex),
            subPage: "method-history"
        });
    }
}
