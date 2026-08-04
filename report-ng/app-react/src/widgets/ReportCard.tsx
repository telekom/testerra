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
    sxHeader?: SxProps<Theme>;
    sxContent?: SxProps<Theme>;
    tooltipText?: string;
}

const ReportCard = ({label, content, details, footer, sxCard, sxHeader, sxContent = {pt: 1, pb: 1}, tooltipText}: ReportCardProps) => {
    const sections = [details, content, footer]
        .filter((section): section is Exclude<React.ReactNode, boolean | null | undefined> =>
            section !== undefined && section !== null && typeof section !== "boolean"
        );
    const labelContent = (typeof label === "string" || typeof label === "number")
        ? <Typography variant="subtitle2" color="primary">{label}</Typography>
        : label;

    return (
        <Card sx={{p: 0, ...sxCard}}>
            <CardContent
                sx={{pt: 1, pb: 1, ...sxHeader}}
            >
                <Stack direction="row" sx={{justifyContent: "space-between", alignItems: "center"}}>
                    {labelContent}
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