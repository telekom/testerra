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

import type {SxProps, Theme} from "@mui/material/styles";
import type {MethodHistoryStatistics} from "../../model/MethodHistoryStatistics.ts";
import HistoryStatisticsCard from "../../widgets/HistoryStatisticsCard.tsx";

interface MethodHistoryStatisticsCardProps {
    methodHistoryStatistics: MethodHistoryStatistics;
    sx?: SxProps<Theme>;
}

const MethodHistoryStatisticsCard = ({methodHistoryStatistics, sx}: MethodHistoryStatisticsCardProps) => (
    <HistoryStatisticsCard
        totalRunCount={methodHistoryStatistics.getRunCount()}
        averageRunDuration={methodHistoryStatistics.averageDuration}
        flakiness={methodHistoryStatistics.flakiness}
        sx={sx}
    />
);

export default MethodHistoryStatisticsCard;
