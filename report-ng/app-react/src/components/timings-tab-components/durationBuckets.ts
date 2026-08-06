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
        const end = (i + 1) * sectionRange;
        sectionLabels.push(`${Number(start.toFixed(2))}-${Number(end.toFixed(2))}s`);
        sectionValues.push(end);
    }

    const bars = sectionValues.map((sectionValue, i) => {
        const previousSectionValue = i > 0 ? sectionValues[i - 1] : 0;
        const isLastSection = i === sectionValues.length - 1;
        const methodList = methods.filter(m =>
            m.duration >= previousSectionValue &&
            (isLastSection ? m.duration <= sectionValue : m.duration < sectionValue)
        );
        return {label: sectionLabels[i], durationAmount: methodList.length, methodList};
    });

    return {bars, labels: sectionLabels, chartData: bars.map(bar => bar.durationAmount)};
}
