import {Card, CardContent, Divider, Stack, Typography} from "@mui/material";
import type {SxProps, Theme} from '@mui/material/styles';
import InfoTooltip from "./InfoTooltip"
import React from "react";

export interface CardProps {
    label: string | React.ReactNode;
    children: any;
    sxCard?: SxProps<Theme>;
    sxContent?: SxProps<Theme>;
    tooltipText?: string;
}

const ReportCard = ({label, children, sxCard, sxContent = {pt: 1, pb: 1}, tooltipText}: CardProps) => {

    return (
        <Card sx={{p: 0, ...sxCard}}>
            <CardContent
                sx={{pt: 1, pb: 1}}
            >
                <Stack direction="row" sx={{justifyContent: "space-between", alignItems: "center"}}>
                    <Typography variant="subtitle2" color="primary">{label}</Typography>
                    {tooltipText && <InfoTooltip text={tooltipText}/>}
                </Stack>
            </CardContent>
            <Divider/>
            <CardContent sx={sxContent}>
                {children}
            </CardContent>
        </Card>
    )
};

export default ReportCard;