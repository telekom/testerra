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
import {NavigationInstruction, RouteConfig} from "aurelia-router";
import {AbstractViewModel} from "../abstract-view-model";
import {StatisticsGenerator} from "../../services/statistics-generator";
import {StatusConverter} from "../../services/status-converter";
import {FailureAspectStatistics} from "../../services/statistic-models";
import './failure-aspects.scss'

@autoinject()
export class FailureAspects extends AbstractViewModel {
    private _filteredFailureAspects: FailureAspectStatistics[];
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
            delete this.queryParams.q;
        }

        this._filteredFailureAspects = [];
        this._loading = true;
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            this._filteredFailureAspects = executionStatistics.failureAspectStatistics
                .filter(failureAspectStatistics => {
                    return (!this.queryParams.type || (
                        (this.queryParams.type == "major" && !failureAspectStatistics.isMinor)
                        || (this.queryParams.type == "minor" && failureAspectStatistics.isMinor)
                    ));
                })
                .filter(failureAspectStatistics => {
                    return (!this._searchRegexp || failureAspectStatistics.name.match(this._searchRegexp));
                })

            this._loading = false;
            this.updateUrl(this.queryParams);
        });
    }

    private _calcFontSize(index:number) {
        const min = 1;
        const max = 3;
        const count = this._filteredFailureAspects.length;
        let size = ((count-index)/count) * max;
        if (size < min) size = min;
        return size;
    }
}
