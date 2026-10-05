/*
 * Testerra
 *
 * (C) 2026, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {reportTheme} from "./reportTheme";

export const ECHARTS_THEME_NAME = "testerra";

const textColor: string = reportTheme.palette.text.primary;

const axisTheme = {
    axisLabel: {color: textColor},
    nameTextStyle: {color: textColor},
};

// ECharts components define their own default text colors, so a global textStyle alone is not sufficient.
export const echartsTheme = {
    textStyle: {color: textColor},
    title: {
        textStyle: {color: textColor},
        subtextStyle: {color: textColor},
    },
    legend: {textStyle: {color: textColor}},
    categoryAxis: axisTheme,
    valueAxis: axisTheme,
    timeAxis: axisTheme,
    logAxis: axisTheme,
    tooltip: {textStyle: {color: textColor}},
};
