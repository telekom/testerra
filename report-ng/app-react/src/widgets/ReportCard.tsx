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

const cardContentStyles = (sx: SxProps<Theme> = {}) =>
    (Array.isArray(sx) ? sx : [sx]).map(style =>
        typeof style === "boolean" ? style : (theme: Theme) => ({
            "&, &:last-child": typeof style === "function" ? style(theme) : style,
        })
    );

const ReportCard = ({
                        label,
                        content,
                        details,
                        footer,
                        sxCard,
                        sxHeader,
                        sxContent,
                        tooltipText
                    }: ReportCardProps) => {
    const sections = [
        details !== undefined && details !== null && typeof details !== "boolean" ? {
            key: "details",
            content: details
        } : null,
        {key: "content", content},
        footer !== undefined && footer !== null && typeof footer !== "boolean" ? {
            key: "footer",
            content: footer
        } : null,
    ].filter((section): section is {
        key: "details" | "content" | "footer";
        content: React.ReactNode
    } => section !== null);
    const labelContent = (typeof label === "string" || typeof label === "number")
        ? <Typography variant="subtitle2" color="primary">{label}</Typography>
        : label;

    return (
        <Card sx={[
            {p: 0, display: "flex", flexDirection: "column", height: "100%"},
            ...(Array.isArray(sxCard) ? sxCard : [sxCard]),
        ]}>
            {label && (
                <>
                    <CardContent sx={[
                        ...cardContentStyles({pt: 1, pb: 1}),
                        ...cardContentStyles(sxHeader),
                    ]}>
                        <Stack direction="row" sx={{justifyContent: "space-between", alignItems: "center"}}>
                            {labelContent}
                            {tooltipText && <InfoTooltip text={tooltipText}/>}
                        </Stack>
                    </CardContent>
                    <Divider/>
                </>
            )}

            <Box sx={{display: "flex", flexDirection: "column", flex: 1, minHeight: 0}}>
                {sections.map((section, index) => (
                    <React.Fragment key={index}>
                        {index > 0 && <Divider/>}
                        <CardContent
                            sx={[
                                ...cardContentStyles({p: 0}),
                                ...cardContentStyles(sxContent),
                                section.key === "content"
                                    ? {flex: 1, minHeight: 0, display: "flex", flexDirection: "column"}
                                    : {flex: "0 0 auto"},
                            ]}
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
