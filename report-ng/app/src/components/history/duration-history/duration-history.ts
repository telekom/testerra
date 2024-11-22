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
import {ECharts, EChartsOption} from 'echarts';
import "./duration-history.scss";

@autoinject()
export class DurationHistory extends AbstractViewModel {
    @observable() private _chart: ECharts;
    private _option: EChartsOption;
    private _loading = false;

    constructor(
        private _router: Router
    ) {
        super();
    }

    activate(params: any, routeConfig: RouteConfig, navInstruction: NavigationInstruction) {
        super.activate(params, routeConfig, navInstruction);
        this._router = navInstruction.router;
        this._setChartOption();
    }

    private _setChartOption() {
        this._option = {
            title: {
                text: 'Example graph with scrollbar'
            },
            tooltip: {},
            xAxis: {
                type: 'category',
                data: ['Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun', 'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez']
            },
            yAxis: {
                type: 'value'
            },
            series: [{
                name: 'Umsatz',
                type: 'line',
                data: [120, 200, 150, 80, 70, 110, 130, 160, 200, 250, 300, 350]
            }],
            dataZoom: [
                {
                    type: 'slider',
                    xAxisIndex: [0],
                    start: 0,
                    end: 50,
                    brushSelect: false,
                    zoomLock: true
                }
            ]
        };
    }
}
