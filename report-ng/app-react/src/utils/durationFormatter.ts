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

import { Duration } from "luxon";

export function formatDuration(value: number | string): string {
    const ms =
        typeof value === "string" ? Number.parseInt(value, 10) : value;

    const d = Duration.fromMillis(ms).shiftTo(
        "days",
        "hours",
        "minutes",
        "seconds"
    );

    const parts = [
        d.days ? `${Math.floor(d.days)}d` : null,
        d.hours ? `${Math.floor(d.hours)}h` : null,
        d.minutes ? `${Math.floor(d.minutes)}m` : null,
        d.seconds ? `${Math.floor(d.seconds)}s` : null,
    ].filter(Boolean); // removes 0 values

    return parts.length ? parts.join(" ") : "0s";
}