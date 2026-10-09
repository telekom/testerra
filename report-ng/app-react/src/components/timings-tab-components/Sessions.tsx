/*
 * Testerra
 *
 * (C) 2023, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {useMemo} from "react";
import Echart from "../../widgets/Echart";
import ReportCard from "../../widgets/ReportCard";
import {useReportData} from "../../provider/DataProvider";
import {formatDate} from "../../utils/dateFormatter";
import {escapeHtml} from "../../utils/escapeHtml";
import {MethodDetails} from "../../model/MethodDetails";
import {MetricType} from "../../model/report-model/framework_pb";
import {buildChartTooltip} from "../../utils/chartTooltip";
import {reportTheme} from "../../layout/reportTheme.tsx";
import {formatDuration} from "../../utils/durationFormatter.ts";

const SESSION_COLOR = '#6897EA';
const BASEURL_COLOR = '#75C6CB';

interface ISessionInformation {
    sessionName: string;
    sessionId: string;
    browserName?: string;
    browserVersion?: string;
    methodNames: string[];
    sessionDuration: number;
    baseurlDuration?: number;
    sessionStartTime: number;
    baseurlStartTime?: number;
}

interface IDots {
    sessionValues: [number, number];
    baseUrlValues: [number, number] | undefined;
    information: ISessionInformation;
}

const Sessions = () => {
    const {executionMngr} = useReportData();

    const {dots, testStartTime, testEndTime} = useMemo(() => {
        if (!executionMngr) {
            return {dots: [] as IDots[], testStartTime: 0, testEndTime: 0};
        }

        const executionAggregate = executionMngr.getExecutionAggregate();
        const executionStatistics = executionMngr.getExecutionStatistics();

        const testStartTime = executionAggregate.executionContext?.contextValues?.startTime ?? 0;
        const testEndTime = executionAggregate.executionContext?.contextValues?.endTime ?? 0;

        // map: sessionContextId -> list of test method names
        const sessionMethodMap = new Map<string, string[]>();
        for (const classStats of executionStatistics.classStatistics) {
            for (const methodContext of classStats.methodContexts) {
                const md = new MethodDetails(methodContext, classStats);
                for (const sessionId of methodContext.sessionContextIds ?? []) {
                    if (!sessionMethodMap.has(sessionId)) {
                        sessionMethodMap.set(sessionId, []);
                    }
                    sessionMethodMap.get(sessionId)!.push(md.identifier);
                }
            }
        }

        const dots: IDots[] = [];
        const sessionMetrics = executionAggregate.testMetrics?.sessionMetrics ?? [];

        for (const metric of sessionMetrics) {
            const sessionData = metric.metricsValues?.find(v => v.metricType === MetricType.SESSION_LOAD);
            const baseurlData = metric.metricsValues
                ?.filter(v => v.metricType === MetricType.BASEURL_LOAD)
                .filter(v => (v.endTimestamp ?? 0) > 0)
                .find(() => true);

            if (!((sessionData?.endTimestamp ?? 0) > 0)) {
                continue;
            }

            const sessionContext = executionAggregate.sessionContexts?.[metric.sessionContextId ?? ""];
            if (!sessionContext) continue;

            const sessionContextId = sessionContext.contextValues?.id ?? "";
            const methodNames = sessionMethodMap.get(sessionContextId) ?? [];
            const sessionDuration = ((sessionData?.endTimestamp ?? 0) - (sessionData?.startTimestamp ?? 0)) / 1000;
            const baseurlDuration = baseurlData
                ? ((baseurlData.endTimestamp ?? 0) - (baseurlData.startTimestamp ?? 0)) / 1000
                : undefined;

            dots.push({
                sessionValues: [sessionData?.startTimestamp ?? 0, sessionDuration],
                baseUrlValues: baseurlData
                    ? [baseurlData.startTimestamp ?? 0, baseurlDuration!]
                    : undefined,
                information: {
                    sessionName: sessionContext.contextValues?.name ?? "",
                    sessionId: sessionContext.sessionId ?? "",
                    browserName: sessionContext.browserName,
                    browserVersion: sessionContext.browserVersion,
                    methodNames,
                    sessionDuration,
                    baseurlDuration,
                    sessionStartTime: sessionData?.startTimestamp ?? 0,
                    baseurlStartTime: baseurlData?.startTimestamp,
                },
            });
        }

        return {dots, testStartTime, testEndTime};
    }, [executionMngr]);

    const option = useMemo(() => {
        const seriesList: object[] = [
            {
                name: 'Session load',
                type: 'scatter',
                data: [],
                itemStyle: {color: SESSION_COLOR},
            },
            {
                name: 'Base URL load',
                type: 'scatter',
                data: [],
                itemStyle: {color: BASEURL_COLOR},
            },
        ];

        for (const dot of dots) {
            const dataPoints: object[] = [
                {value: dot.sessionValues, itemStyle: {color: SESSION_COLOR}},
            ];
            if (dot.baseUrlValues) {
                dataPoints.push({value: dot.baseUrlValues, itemStyle: {color: BASEURL_COLOR}});
            }
            seriesList.push({
                name: 'Session',
                type: 'scatter',
                data: dataPoints,
                cursor: 'default',
                emphasis: {focus: 'series'},
                showInLegend: false,
            });
        }

        return {
            legend: {
                top: 0,
                data: [
                    {name: 'Session load', itemStyle: {color: SESSION_COLOR}},
                    {name: 'Base URL load', itemStyle: {color: BASEURL_COLOR}},
                ],
            },
            dataZoom: [
                {type: 'inside', yAxisIndex: [0]},
                {type: 'slider', xAxisIndex: [0]},
                {type: 'slider', yAxisIndex: [0], right: '4%'},
            ],
            toolbox: {
                itemSize: 25,
                feature: {
                    dataZoom: {},
                    restore: {},
                },
            },
            grid: {
                left: 50,
                right: 100,
                top: 40,
                bottom: 60,
                outerBoundsMode: 'same',
                outerBoundsContain: 'axisLabel',
            },
            tooltip: {
                textStyle: {fontSize: 13},
                formatter: (params: any) => {
                    if (!params.data) return "";
                    // offset by 2 because of the two legend placeholder series
                    const seriesIdx = params.seriesIndex - 2;
                    if (seriesIdx < 0 || seriesIdx >= dots.length) return "";
                    const info = dots[seriesIdx].information;
                    const contentListItemStyle ="display:grid;grid-template-columns:20ch auto;column-gap:8px;align-items:center;margin:2px 0;";
                    const sessionInfo =
                        `<li style="${contentListItemStyle}"><span>Session name</span><span>${escapeHtml(info.sessionName)}</span></li>
                        <li style="${contentListItemStyle}"><span>Session start duration</span><span>${formatDuration(Number(info.sessionDuration) * 1000)}</span></li>
                        <li style="${contentListItemStyle}"><span>Session start time</span><span>${formatDate(Number(info.sessionStartTime), "time")}</span></li>`;

                    const baseUrlInfo = info.baseurlStartTime
                        ? `<li style="${contentListItemStyle}"><span>Base URL start duration</span><span>${formatDuration(Number(info.baseurlDuration) * 1000)}</span></li>
                           <li style="${contentListItemStyle}"><span>Base URL start time</span><span>${formatDate(Number(info.baseurlStartTime), "time")}</span></li>`
                        : "";

                    const testcaseList: string[]= [];
                    const testcaseListItemStyle ="margin:0 0 2px;";
                    info.methodNames.forEach(name => {
                        testcaseList.push(`<li style="${testcaseListItemStyle}">${escapeHtml(name)}</li>`);
                    });

                    return buildChartTooltip({
                        header: {
                            content: `${escapeHtml(info.browserName ?? "")} v${escapeHtml(info.browserVersion ?? "(na)")}`,
                            style: {
                                backgroundColor: reportTheme.palette.lightGrey.light
                            },
                        },
                        body: {
                            content: [
                                `<ul style="list-style:none;padding:0;margin:0;">${sessionInfo}${baseUrlInfo}</ul>`,
                                `<div style="display:flex; align-items:baseline; gap:20px; margin-top:8px;padding-top:6px;border-top:1px solid ${reportTheme.palette.lightGrey.main};">
                                    <span style="">Testcase(s)</span>
                                    <ul style="list-style:none;padding:0;margin:0;">${testcaseList.join("")}</ul>                  
                                </div>`,
                            ],
                        },
                    });
                },
            },
            xAxis: {
                type: 'time',
                min: testStartTime,
                max: testEndTime,
                axisLabel: {
                    formatter: (val: number) =>
                        `${formatDate(val, "time")}\n\n${formatDate(val, "date")}`,
                },
            },
            yAxis: {
                type: 'value',
                name: 'Load duration in seconds',
                nameLocation: 'end',
            },
            series: seriesList,
        };
    }, [dots, testStartTime, testEndTime]);

    return (
        <ReportCard
            label="Session durations"
            content={<Echart option={option} height="60dvh" autoResize notMerge/>}
        />
    );
};

export default Sessions;
