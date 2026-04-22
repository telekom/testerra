import ReportCard from "../../widgets/ReportCard";
import ButtonList from "../../widgets/ButtonList";
import type {ButtonListItem} from "../../widgets/ButtonList";
import Link from '@mui/material/Link';
import type {SxProps, Theme} from "@mui/material/styles";
import {StatusService} from "../../model/status-service";
import {ClassName, classNameConverter} from "../../utils/classNameConverter";
import {FailureAspectStatistics} from "../../model/FailureAspectStatistics";
import { useNavigate } from "react-router-dom";
import {ExecutionStatistics} from "../../model/ExecutionStatistics";

interface DashboardFailureAspectsProps {
    sx?: SxProps<Theme>,
    execStatistics: ExecutionStatistics;
}

const DashboardFailureAspectsCard = ({sx, execStatistics}: DashboardFailureAspectsProps) => {
    const navigate = useNavigate();

    const topFailureAspects: FailureAspectStatistics[] = execStatistics.uniqueFailureAspects.slice(0,3);
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
            <Link href="#/Tests">{majorFailures} Major</Link>
            {" | "}
            <Link href="#/Tests">{minorFailures} Minor</Link>
            )
        </>
    );

    let itemList: {primaryText: string, icon: React.ReactNode}[] = []
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
            pathname: "tests",
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
