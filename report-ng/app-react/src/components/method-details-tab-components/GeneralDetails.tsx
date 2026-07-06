import ReportCard from "../../widgets/ReportCard.tsx";
import ReportChip from "../../widgets/ReportChip.tsx";
import {StatusService} from "../../model/status-service.tsx";
import type {MethodDetails} from "../../model/MethodDetails.ts";
import Stack from "@mui/material/Stack";
import {Typography} from "@mui/material";

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
            Here is some detail text about MethodDetail {methodDetail.identifier}.
        </ReportCard>
    );
};
export default GeneralDetails;
