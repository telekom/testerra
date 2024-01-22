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
import {ILogEntry, StatisticsGenerator} from "services/statistics-generator";
import {StatusConverter} from "services/status-converter";
import {data} from "services/report-model";
import "./logs.scss"
import {VirtualLogView} from "../log-view/virtual-log-view";

@autoinject()
export class Logs extends AbstractViewModel {
    private _loading = false;
    private _logMessages:ILogEntry[];
    private _availableLogLevels;
    private _selectedLogLevel;
    private _logView:VirtualLogView;
    private _prevSearch:string;
    private _searchRegexp:RegExp = null;

    constructor(
        private _statistics: StatisticsGenerator,
        private _statusConverter:StatusConverter
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        if (this.queryParams.q) {
            this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this.queryParams.q);
        }
        this._filter();
    }

    private _search() {
        if (this.queryParams.q) {
            this._searchRegexp = this._statusConverter.createRegexpFromSearchString(this.queryParams.q);

            if (this._prevSearch != this.queryParams.q) {
                this._logView.resetSearch();
            }
            this._prevSearch = this.queryParams.q;
            if (this.queryParams.q.trim().length <= 0) {
                delete this.queryParams.q;
            }
            this.updateUrl(this.queryParams);
        } else {
            this._searchRegexp = undefined;
        }
    }

    private async _filter() {
        this._loading = true;
        const logMessages: ILogEntry[] = [];
        const logLevels = {};

        const filterPredicate = (this._selectedLogLevel ? (logMessage: data.LogMessage) => this._selectedLogLevel == logMessage.type : (logMessage: data.LogMessage) => logMessage);

        const collectLogLevel = (logMessage: data.LogMessage) => {
            logLevels[logMessage.type] = 1;
            return logMessage;
        }

        const add = (logEntry:ILogEntry) => {
            logMessages.push(logEntry)
        }

        const logs = await this._statistics.getLogs()
        Object.values(logs)
            .map(collectLogLevel)
            .filter(filterPredicate)
            .forEach(add)

        this._logMessages = logMessages.sort((a, b) => a.timestamp - b.timestamp);

        if (!this._availableLogLevels) {
            this._availableLogLevels = [];
            for (const level in logLevels) {
                this._availableLogLevels.push({
                    level: Number.parseInt(level),
                });
            }
        }

        this._loading = false;
        this.updateUrl(this.queryParams);
    }

    private _logLevelChanged() {
        this._filter();
    }
}
