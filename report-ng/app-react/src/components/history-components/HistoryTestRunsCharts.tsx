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

import ReportCard from "../../widgets/ReportCard";
import type {SxProps, Theme} from "@mui/material/styles";
import type {HistoryStatistics} from "../../model/HistoryStatistics.ts";
import HistoryTestRuns from "../history-components/HistoryTestRuns.tsx";

interface HistoryChartProps {
    histStatistics: HistoryStatistics
    selectedStatus: string | null;
    sx?: SxProps<Theme>
}

const HistoryTestRunCharts = ({histStatistics, selectedStatus, sx}: HistoryChartProps) => {
    return (
        <ReportCard
            label="History test runs"
            sxContent={{p: 0}}
            sxCard={sx}
            content={<HistoryTestRuns histStatistics={histStatistics} selectedStatus={selectedStatus}/>}
        />
    );
};
export default HistoryTestRunCharts;
