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

import {autoinject, bindable} from 'aurelia-framework';
import {StatisticsGenerator} from "services/statistics-generator";
import {AbstractViewModel} from "../abstract-view-model";
import "./history-statistics-card.scss";
import {StatusConverter} from "../../services/status-converter";

@autoinject()
export class HistoryStatisticsCard extends AbstractViewModel {
    @bindable first: number;
    @bindable second: number;
    @bindable third: number;

    constructor(
        private _statusConverter: StatusConverter,
        private _statisticsGenerator: StatisticsGenerator,
    ) {
        super();
    }

    async attached() {

    };
}
