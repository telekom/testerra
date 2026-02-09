import {Card, CardContent, Divider, Stack, Typography} from "@mui/material";
import type {SxProps, Theme} from '@mui/material/styles';
import InfoTooltip from "../tooltip-button/info-tooltip"
import React from "react";

export interface CardProps {
    label: string | React.ReactNode;
    children: any;
    sx?: SxProps<Theme>;
    className: string;
    tooltipText?: string;
}

const ReportCard = ({label, children, sx = {pt: 1, pb: 1}, className, tooltipText}: CardProps) => {

    return (
        <Card sx={{p: 0}} className={className}>
            <CardContent
                sx={{pt: 1, pb: 1}}
            >
                <Stack direction="row" sx={{justifyContent: "space-between", alignItems: "center"}}>
                    <Typography variant="subtitle2" color="primary">{label}</Typography>
                    {tooltipText && <InfoTooltip text={tooltipText}/>}
                </Stack>
            </CardContent>
            <Divider/>
            <CardContent sx={sx}>
                {children}
            </CardContent>
        </Card>
    )
};

export default ReportCard;