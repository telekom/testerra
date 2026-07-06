import ReportCard from "../../widgets/ReportCard.tsx";
import ReportChip from "../../widgets/ReportChip.tsx";
import {StatusService} from "../../model/status-service.tsx";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import Stack from "@mui/material/Stack";
import {Typography} from "@mui/material";
import List from "@mui/material/List";
import ListItem from "@mui/material/ListItem";
import {ClassName, classNameConverter} from "../../utils/classNameConverter.ts";
import Link from "@mui/material/Link";
import {Link as RouterLink} from "react-router-dom";

interface GeneralDetailsProps {
    methodDetail?: MethodDetails;
}

const GeneralDetails = ({methodDetail}: GeneralDetailsProps) => {
    if (!methodDetail) {
        return <ReportCard label="Method details">No method selected.</ReportCard>;
    }

    const header =
        <Stack direction="row" spacing={2} alignItems="center">
            <ReportChip key={methodDetail?.methodContext.resultStatus}
                        label={StatusService.getLabel(methodDetail?.methodContext.resultStatus ?? "")}
                        size="medium"
                        sx={{
                            background: StatusService.getColor(methodDetail?.methodContext.resultStatus ?? ""),
                            color: "white",
                            fontSize: "16px",
                            fontWeight: "400"
                        }}/>
            <Typography color="black">{methodDetail.identifier}</Typography>
        </Stack>

    return (
        <ReportCard label={header}>
            <Stack direction="row" spacing={10}>
                <List>
                    <ListItem sx={{gap: "8px", alignItems: "end"}} disablePadding>
                        <Typography variant="caption" color="textSecondary">Class</Typography>
                        <Link
                            component={RouterLink}
                            to={{
                                pathname: "/Tests",
                                search: `class=${classNameConverter(methodDetail.classStatistics.classIdentifier, ClassName.simpleName)}`,
                            }}
                        >
                            <Typography
                                variant="caption">{classNameConverter(methodDetail.classStatistics.classIdentifier, ClassName.simpleName)}</Typography>
                        </Link>
                    </ListItem>
                    <ListItem sx={{gap: "8px", alignItems: "end"}} disablePadding>
                        <Typography variant="caption" color="textSecondary">Package</Typography>
                        <Typography
                            variant="caption">{classNameConverter(methodDetail.classStatistics.classIdentifier, ClassName.package)}</Typography>
                    </ListItem>
                    <ListItem sx={{gap: "8px", alignItems: "end"}} disablePadding>
                        <Typography variant="caption" color="textSecondary">Test Context</Typography>
                        <Typography variant="caption">{methodDetail?.testContext?.contextValues?.name}</Typography>
                    </ListItem>
                    <ListItem sx={{gap: "8px", alignItems: "end"}} disablePadding>
                        <Typography variant="caption" color="textSecondary">Suite</Typography>
                        <Typography variant="caption">{methodDetail?.suiteContext?.contextValues?.name}</Typography>
                    </ListItem>
                </List>

                <List>
                    {methodDetail.failedStep && (
                        <ListItem sx={{gap: "8px", alignItems: "end"}} disablePadding>
                            <Typography variant="caption" color="textSecondary">Failed in</Typography>
                            <Link
                                component={RouterLink}
                                to={{
                                    pathname: `steps/${methodDetail?.methodContext?.failedStepIndex as number + 1}`,
                                }}
                            >
                                <Typography variant="caption">{methodDetail.failedStep.name}</Typography>
                            </Link>
                        </ListItem>
                    )}
                    <ListItem sx={{gap: "8px", alignItems: "end"}} disablePadding>
                        <Typography variant="caption" color="textSecondary">Thread</Typography>
                        <Link
                            component={RouterLink}
                            to={{
                                pathname: `/threads`,
                                search: `methodId=${methodDetail.methodContext.contextValues?.id}`
                            }}
                        >
                            <Typography variant="caption">{methodDetail.methodContext.threadName}</Typography>
                        </Link>
                    </ListItem>
                    <ListItem sx={{gap: "8px", alignItems: "end"}} disablePadding>
                        <Typography variant="caption" color="textSecondary">Run index</Typography>
                        <Typography variant="caption">#{methodDetail.methodContext.methodRunIndex}</Typography>
                    </ListItem>

                </List>
            </Stack>
        </ReportCard>

    );
};
export default GeneralDetails;
