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
import {data} from "../../services/report-model";
import ILogMessage = data.ILogMessage;

@autoinject()
export class Logs extends AbstractViewModel {
    private _searchRegexp: RegExp;
    private _loading = false;
    private _logMessages:ILogMessage[];
    private _availableLogLevels;
    private _selectedLogLevel;

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

        this._loading = true;
        this._statistics.getExecutionStatistics().then(executionStatistics => {
            const logMessages:ILogMessage[] = [];
            const logLevels = {};

            const filterPredicate = (this._selectedLogLevel?(logMessage:ILogMessage) => this._selectedLogLevel == logMessage.type:(logMessage:ILogMessage) => logMessage);

            const addLevel = (value:ILogMessage) => {
                logLevels[value.type] = 1;
                return value;
            }

            executionStatistics.executionAggregate.executionContext.logMessages
                .map(addLevel)
                .filter(filterPredicate)
                .forEach(value => {
                    logMessages.push(value);
                });

            Object.values(executionStatistics.executionAggregate.methodContexts)
                .flatMap(value => value.testSteps)
                .flatMap(value => value.actions)
                .flatMap(value => value.entries)
                .filter(value => value.logMessage)
                .map(value => value.logMessage)
                .map(addLevel)
                .filter(filterPredicate)
                .forEach(value => {
                    logMessages.push(value)
                });

            this._logMessages = logMessages.sort((a, b) => a.timestamp-b.timestamp);

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
        });
    }

    private _logLevelChanged() {
        this._filter();
    }
}
