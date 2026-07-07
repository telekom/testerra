import {Card, CardContent, Divider, Stack, Typography} from "@mui/material";
import type {SxProps, Theme} from '@mui/material/styles';
import InfoTooltip from "./InfoTooltip"
import React from "react";

export interface ReportCardProps {
    label: React.ReactNode;
    content: React.ReactNode;
    details?: React.ReactNode;
    footer?: React.ReactNode;
    sxCard?: SxProps<Theme>;
    sxContent?: SxProps<Theme>;
    tooltipText?: string;
}

const ReportCard = ({label, content, details, footer, sxCard, sxContent = {pt: 1, pb: 1}, tooltipText}: ReportCardProps) => {
    const sections = [details, content, footer].filter((section): section is React.ReactNode => section !== undefined && section !== null);

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
            {sections.map((section, index) => (
                <React.Fragment key={index}>
                    {index > 0 && <Divider/>}
                    <CardContent sx={sxContent}>
                        {section}
                    </CardContent>
                </React.Fragment>
            ))}
        </Card>
    )
};

export default ReportCard;