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

import ReportCard from "../../widgets/ReportCard";
import type {ButtonListItem} from "../../widgets/ButtonList";
import ButtonList from "../../widgets/ButtonList";
import Link from '@mui/material/Link';
import type {SxProps, Theme} from "@mui/material/styles";
import {StatusService} from "../../model/status-service";
import {ClassName, classNameConverter} from "../../utils/classNameConverter";
import {FailureAspectStatistics} from "../../model/FailureAspectStatistics";
import {useNavigate} from "react-router-dom";
import {ExecutionStatistics} from "../../model/ExecutionStatistics";

interface DashboardFailureAspectsProps {
    sx?: SxProps<Theme>,
    execStatistics: ExecutionStatistics;
}

const DashboardFailureAspectsCard = ({sx, execStatistics}: DashboardFailureAspectsProps) => {
    const navigate = useNavigate();

    const topFailureAspects: FailureAspectStatistics[] = execStatistics.uniqueFailureAspects.slice(0, 3);
    let majorFailures = 0;
    let minorFailures = 0;

    const uniqueFailureAspects = execStatistics.uniqueFailureAspects as FailureAspectStatistics[];
    uniqueFailureAspects.forEach(failureAspectStatistics => {
        if (failureAspectStatistics.isMinor) {
            ++minorFailures;
        } else {
            ++majorFailures
        }
    })

    const failureAspectsLabel = (
        <>
            Top 3 Failure Aspects (
            <Link href="#/failureAspects?type=major">{majorFailures} Major</Link>
            {" | "}
            <Link href="#/failureAspects?type=minor">{minorFailures} Minor</Link>
            )
        </>
    );

    let itemList: { primaryText: string, icon: React.ReactNode }[] = []
    topFailureAspects.forEach(failureAspect => {
        const item = {
            primaryText: classNameConverter(failureAspect.relevantCause!.className!, ClassName.simpleName) + ": " + failureAspect.message,
            icon: StatusService.getIcon(failureAspect.getUpmostStatus()),
            value: failureAspect
        }
        itemList.push(item);
    })

    const gotoFailureAspect = (item: ButtonListItem) => {
        const params = new URLSearchParams(location.search);
        params.set("failureAspect", item.value.index);
        navigate({
            pathname: "Tests",
            search: params.toString(),
        });
    }

    return (
        <ReportCard label={failureAspectsLabel} sxContent={{p: 0, ":last-child": {padding: 0}}}
                    tooltipText="The most critical errors that caused the highest number of failed test cases"
                    sxCard={sx}>
            <ButtonList list={itemList} disablePadding={true} handleClick={gotoFailureAspect}/>
        </ReportCard>
    );
};
export default DashboardFailureAspectsCard;
