/*
 * Testerra
 *
 * (C) 2026, Selina Natschke, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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


// One duration bucket represents one bar in the chart:
// all methods whose duration falls into the same interval.
export interface DurationBucket<T> {
    label: string;
    durationAmount: number;
    methodList: T[];
}

export function buildDurationBuckets<T extends { duration: number }>(
    methods: T[],
    rangeNum: number
): { bars: DurationBucket<T>[]; labels: string[]; chartData: number[] } {
    if (methods.length === 0) {
        return {bars: [], labels: [], chartData: []};
    }

    // Build equal-width intervals from 0 to the rounded-up maximum duration.
    const sorted = [...methods.map(m => m.duration)].sort((a, b) => a - b);
    let maxDuration = sorted[sorted.length - 1];
    const remainder = maxDuration % rangeNum;
    maxDuration = remainder === 0 ? maxDuration : maxDuration + (rangeNum - remainder);
    const sectionRange = maxDuration / rangeNum;

    const sectionLabels: string[] = [];
    const sectionValues: number[] = [];

    // calculate ranges
    for (let i = 0; i < rangeNum; i++) {
        const start = i * sectionRange;
        const end = (i + 1) * sectionRange - 1;
        sectionLabels.push(`${start}-${end}s`);
        sectionValues.push(end);
    }

    const bars = sectionValues.map((sectionValue, i) => {
        const methodList = methods.filter(m =>
            m.duration <= sectionValue &&
            (sectionValues[i - 1] === undefined || m.duration > sectionValues[i - 1])
        );
        return {label: sectionLabels[i], durationAmount: methodList.length, methodList};
    });

    return {bars, labels: sectionLabels, chartData: bars.map(bar => bar.durationAmount)};
}
