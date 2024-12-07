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

import {autoinject} from 'aurelia-framework';
import {NavigationInstruction, RouteConfig, Router} from "aurelia-router";
import {AbstractViewModel} from "../../abstract-view-model";
import "./run-history.scss";
import {HistoryStatistics, MethodHistoryStatistics} from "../../../services/statistic-models";
import {IFilter, StatusConverter} from "../../../services/status-converter";
import {StatisticsGenerator} from "../../../services/statistics-generator";
import {ResultStatusType} from "../../../services/report-model/framework_pb";
import {MdcSelect} from "@aurelia-mdc-web/select";
import {ClassName, ClassNameValueConverter} from "../../../value-converters/class-name-value-converter";

@autoinject()
export class RunHistory extends AbstractViewModel {

    totalRunCount: number = 0;
    avgRunDuration: number = 0;
    overallSuccessRate: number = 0;
    private _historyStatistics: HistoryStatistics;
    statusData: any[] = [];
    private _filter: IFilter;
    private _selectedStatus: ResultStatusType = null;
    private _availableStatuses: ResultStatusType[] = [];
    private _topFlakyTests: any[] = [];
    private statusSelect: MdcSelect;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
        private _router: Router,
        private _classNameValueConverter: ClassNameValueConverter
    ) {
        super();
    }

    async activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;

        this._historyStatistics = await this._statisticsGenerator.getHistoryStatistics();

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

        let overallTestCount = 0;
        statusCount.forEach((count, status) => {
            overallTestCount += count;
            if (count) {
                this.statusData.push({
                    status: status,
                    statusName: this._statusConverter.getLabelForStatus(status),
                    value: count,
                    itemStyle: {color: this._statusConverter.getColorForStatus(status)}
                })
                this._availableStatuses.push(status);
            }
        });

        this.totalRunCount = this._historyStatistics.getTotalRuns();
        this.avgRunDuration = this._historyStatistics.getAverageDuration();
        this.overallSuccessRate = (statusCount.get(ResultStatusType.PASSED) / overallTestCount) * 100;

        this._getTopFlakyMethods();

        if (this.queryParams.status) {
            this._filter = {
                status: this._statusConverter.getStatusForClass(this.queryParams.status)
            }
            this._selectedStatus = this.queryParams.status;
        }

        const self = this
        if (this.queryParams.status || params.status) {
            window.setTimeout(() => {
                self._selectedStatus = self._statusConverter.getStatusForClass(params.status);
                self.statusSelect.value = self._statusConverter.normalizeStatus(self._statusConverter.getStatusForClass(self.queryParams.status)).toString();       // necessary to keep selection after refreshing the page
            }, 200)
        } else {
            this._selectedStatus = null;
        }
    }

    private _getTopFlakyMethods(): void {
        const methods = this._historyStatistics.getMethodHistoryStatistics();

        const flakyMethods = methods
            .filter(method => method.flakiness > 0.2)
            .sort((a, b) => b.flakiness - a.flakiness)
            .slice(0, 3);

        flakyMethods.forEach(method => {
            const className = this._classNameValueConverter.toView(method.classIdentifier, ClassName.simpleName);
            this._topFlakyTests.push({
                name: method.getIdentifier() + " (" + className + ")",
                statistics: method
            });
        });
        console.log(this._topFlakyTests);
    }

    private _statusChanged() {
        if (!this._selectedStatus) {
            this._setFilter(null);
        } else {
            this._setFilter({
                status: this._selectedStatus
            });
        }
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

    private _gotoMethodHistory(methodHistoryStatistics: MethodHistoryStatistics) {
        this._router.navigateToRoute('method', {
            methodId: methodHistoryStatistics.getIdOfLatestRun()
        });
    }
}
