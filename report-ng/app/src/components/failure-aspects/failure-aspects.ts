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
import {FailureAspectStatistics} from "../../services/statistic-models";
import {data} from "../../services/report-model";
import ResultStatusType = data.ResultStatusType;

@autoinject()
export class FailureAspects extends AbstractViewModel {
    private _filteredFailureAspects: FailureAspectStatistics[];
    private _searchRegexp: RegExp;
    private _loading = false;
    private _showExpectedFailed = true;
    private _router:Router;

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter:StatusConverter
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        if (params.expectedFailed === "false") {
            this._showExpectedFailed = false;
        }
        this._filter();
    }

    private _filter() {
        if (this.queryParams?.q?.trim().length > 0) {
            this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this.queryParams.q);
        } else {
            this._searchRegexp = null;
            delete this.queryParams.q;
        }

        this._filteredFailureAspects = [];
        this._loading = true;
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            this._filteredFailureAspects = executionStatistics.failureAspectStatistics
                .filter(failureAspectStatistics => {
                    if (this._showExpectedFailed) {
                        return true;
                    } else {
                        return failureAspectStatistics.availableStatuses.filter(status => status != ResultStatusType.FAILED_EXPECTED).length > 0;
                    }
                })
                .filter(failureAspectStatistics => {
                    return (!this.queryParams.type || (
                        (this.queryParams.type == "major" && !failureAspectStatistics.isMinor)
                        || (this.queryParams.type == "minor" && failureAspectStatistics.isMinor)
                    ));
                })
                .filter(failureAspectStatistics => {
                    return (!this._searchRegexp || failureAspectStatistics.identifier.match(this._searchRegexp));
                })

            this._loading = false;
            if (this._showExpectedFailed) {
                delete this.queryParams.expectedFailed;
            } else {
                this.queryParams.expectedFailed = this._showExpectedFailed;
            }
            this.updateUrl(this.queryParams);
        });
    }

    private _calcFontSize(index:number) {
        const min = 1;
        const max = 3;
        const count = Math.min(10, this._filteredFailureAspects.length);
        let size = ((count-index)/count) * max;
        if (size < min) size = min;
        return size;
    }

    private _showExpectedFailedChanged() {
        this._filter();
    }
}
