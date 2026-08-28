import {Card, CardContent, Divider, Stack, Typography} from "@mui/material";
import type {SxProps, Theme} from '@mui/material/styles';
import InfoTooltip from "./InfoTooltip"
import React from "react";
import Box from "@mui/material/Box";

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
    const sections = [
        details !== undefined && details !== null && typeof details !== "boolean" ? {key: "details", content: details} : null,
        {key: "content", content},
        footer !== undefined && footer !== null && typeof footer !== "boolean" ? {key: "footer", content: footer} : null,
    ].filter((section): section is {key: "details" | "content" | "footer"; content: React.ReactNode} => section !== null);
    const labelContent = (typeof label === "string" || typeof label === "number")
        ? <Typography variant="subtitle2" color="primary">{label}</Typography>
        : label;

    return (
        <Card sx={{p: 0, display: "flex", flexDirection: "column", height: "100%", ...sxCard}}>
            <CardContent sx={{pt: 1, pb: 1, ...sxHeader}}>
                <Stack direction="row" sx={{justifyContent: "space-between", alignItems: "center"}}>
                    {labelContent}
                    {tooltipText && <InfoTooltip text={tooltipText}/>}
                </Stack>
            </CardContent>
            <Divider/>
            <Box sx={{display: "flex", flexDirection: "column", flex: 1, minHeight: 0}}>
                {sections.map((section, index) => (
                    <React.Fragment key={index}>
                        {index > 0 && <Divider/>}
                        <CardContent
                            sx={{
                                ...sxContent,
                                ...(section.key === "content"
                                    ? {flex: 1, minHeight: 0, display: "flex", flexDirection: "column"}
                                    : {flex: "0 0 auto"}),
                                ...(section.key === "footer"
                                    ? {pb: 1, "&:last-child": {pb: 1}}
                                    : {}),
                            }}
                        >
                            {section.content}
                        </CardContent>
                    </React.Fragment>
                ))}
            </Box>
        </Card>
    )
};

export default ReportCard;
