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
import {StatisticsGenerator} from "../../services/statistics-generator";
import {data} from "../../services/report-model";
import "./browser-info.scss"
import {MetricType} from "../../services/report-model/framework_pb";
import {ExecutionStatistics} from "../../services/statistic-models";
import {
    IntlDateFormatValueConverter
} from "t-systems-aurelia-components/src/value-converters/intl-date-format-value-converter";
import ISessionContext = data.SessionContext;

@autoinject()
export class BrowserInfo {
    private _sessionContexts:ISessionContext[];
    private _executionStatistics: ExecutionStatistics;
    private _sessionInformationArray: ISessionInformation[];

    constructor(
        private _statistics: StatisticsGenerator,
        private readonly _dateFormatter: IntlDateFormatValueConverter,
    ) {
        this._sessionInformationArray = [];
    }

    activate(
        params: any,
    ) {
        this._statistics.getMethodDetails(params.methodId).then(methodDetails => {
            this._sessionContexts = methodDetails.sessionContexts;
        });

        this._statistics.getExecutionStatistics().then(executionStatistics => {
            this._executionStatistics = executionStatistics;
        })

        this._statistics.getSessionMetrics().then(sessionMetrics => {
            sessionMetrics = sessionMetrics.filter(metric => this._sessionContexts.map(context => context.contextValues.id).includes(metric.sessionContextId))
            sessionMetrics.forEach(metric => {
                const sessionData = metric.metricsValues.find(value => value.metricType === MetricType.SESSION_LOAD);

                const baseurlData = metric.metricsValues
                    .filter(value => value.metricType === MetricType.BASEURL_LOAD)
                    .filter(value => value.endTimestamp > 0)    // if there is no baseurl endTimestamp the baseurl data will not be displayed
                    .find(() => true)

                if (!(sessionData?.endTimestamp > 0)){      // if there is no session endTimestamp the related metric will be skipped
                    return;
                }

                const sessionContext = this._executionStatistics.executionAggregate.sessionContexts[metric.sessionContextId];
                const sessionInformation: ISessionInformation = {
                    sessionName: sessionContext.contextValues.name,
                    sessionId: sessionContext.contextValues.id,
                    browserName: sessionContext.browserName,
                    browserVersion: sessionContext.browserVersion,
                    userAgent: sessionContext.userAgent,
                    serverUrl: sessionContext.serverUrl,
                    nodeUrl: sessionContext.nodeUrl,
                    baseUrl: sessionContext.baseUrl,
                    sessionDuration: (sessionContext.contextValues.endTime - sessionContext.contextValues.startTime) / 1000,
                    sessionStartDuration: (sessionData.endTimestamp - sessionData.startTimestamp) / 1000,
                    baseurlStartDuration: (baseurlData?.endTimestamp - baseurlData?.startTimestamp) / 1000,
                    sessionStartTime: sessionData.startTimestamp,
                    baseurlStartTime: baseurlData?.startTimestamp,
                    capabilities: sessionContext.capabilities,
                    videoId: sessionContext.videoId
                }
                this._sessionInformationArray.push(sessionInformation);
                this._sessionInformationArray.sort((a,b) => a.sessionId.localeCompare(b.sessionId));
            })
        })

        if(params.id){
            window.setTimeout(() => {
                // getting the DOM element that we found in logMessages
                const element = document.getElementById(params.id);
                element.scrollIntoView();
            }, 1);
        }
    }
}

interface ISessionInformation {
    sessionName: string,
    sessionId: string,
    browserName: string,
    browserVersion: string,
    userAgent,
    serverUrl,
    nodeUrl,
    baseUrl,
    sessionDuration: number,
    sessionStartDuration: number,
    baseurlStartDuration?: number,
    sessionStartTime: number,
    baseurlStartTime?: number,
    capabilities?,
    videoId?,
}
