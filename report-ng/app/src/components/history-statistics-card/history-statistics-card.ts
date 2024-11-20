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
import {AbstractViewModel} from "../abstract-view-model";
import "./history-statistics-card.scss";

@autoinject()
export class HistoryStatisticsCard extends AbstractViewModel {
    @bindable total_run_count: number;
    @bindable average_run_duration: number;
    @bindable success_rate: number;

    constructor() {
        super();
    }

    async attached() {
    };
}
