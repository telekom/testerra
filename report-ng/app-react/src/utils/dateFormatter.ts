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
type LogTimestampFormat = "short" | "long";

const timestampCache = new Map<string, string>();

// convert to string and fill with starting 0
const pad = (value: number, length = 2): string =>
    value.toString().padStart(length, "0");

const formatShortTimestamp = (date: Date): string => {
    const day = pad(date.getDate());
    const month = pad(date.getMonth() + 1);
    const year = date.getFullYear();
    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());
    const seconds = pad(date.getSeconds());
    const milliseconds = pad(date.getMilliseconds(), 3);

    return `${day}.${month}.${year} ${hours}:${minutes}:${seconds}.${milliseconds}`;
};

const longTimestampFormatter = new Intl.DateTimeFormat("en-GB", {
    weekday: "short",
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    second: "2-digit",
    hour12: false,
});

export const dateFormatter = (
    timestamp?: number,
    format: LogTimestampFormat = "short"
): string => {
    if (!timestamp) {
        return "0";
    }

    const cacheKey = `${format}-${timestamp}`;
    const cachedValue = timestampCache.get(cacheKey);

    if (cachedValue) {
        return cachedValue;
    }

    const date = new Date(timestamp);
    const formattedTimestamp =
        format === "short"
            ? formatShortTimestamp(date)
            : longTimestampFormatter.format(date);

    timestampCache.set(cacheKey, formattedTimestamp);
    return formattedTimestamp;
};