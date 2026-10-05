/*
 * Testerra
 *
 * (C) 2026, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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

import {type ResultStatus, StatusService} from "../model/status-service";
import {escapeHtml} from "./escapeHtml";

type TooltipContent = string | string[] | null | undefined;
type TooltipStyle = Record<string, string | number>;

interface TooltipSection {
    content?: TooltipContent;
    style?: TooltipStyle;
}

interface ChartTooltipOptions {
    header?: TooltipSection;
    body?: TooltipSection;
}

const DEFAULT_HEADER_STYLE: TooltipStyle = {
    padding: "5px",
    margin: "-10px -10px 10px -10px",
};

const toKebabCase = (value: string): string => value.replace(/[A-Z]/g, match => `-${match.toLowerCase()}`);

const styleToInlineCss = (style: TooltipStyle): string => Object.entries(style)
    .map(([property, propertyValue]) => `${toKebabCase(property)}:${String(propertyValue)}`)
    .join(";");

const normalizeContent = (content: TooltipContent): string => {
    if (Array.isArray(content)) {
        return content.filter(part => Boolean(part)).join("");
    }
    return content ?? "";
};

const renderSection = (section?: TooltipSection, defaultStyle: TooltipStyle = {}): string => {
    const content = normalizeContent(section?.content);
    if (!content) {
        return "";
    }

    const style = {...defaultStyle, ...section?.style};
    const inlineStyle = styleToInlineCss(style);
    return inlineStyle ? `<div style="${inlineStyle}">${content}</div>` : `<div>${content}</div>`;
};

export const buildChartTooltip = ({header, body}: ChartTooltipOptions): string => {
    return `${renderSection(header, DEFAULT_HEADER_STYLE)}${renderSection(body)}`;
};

const STATUS_BADGE_STYLE: TooltipStyle = {
    display: "inline-block",
    color: "#fff",
    padding: "2px 8px",
    borderRadius: "16px",
    whiteSpace: "nowrap",
    height: "24px",
    fontSize: "13px",
    justifyContent: "center",
    lineHeight: "19.5px",
};

export const buildTooltipStatusBadge = (status: ResultStatus): string => {
    const statusInformation = StatusService.get(status);
    const style = styleToInlineCss({
        ...STATUS_BADGE_STYLE,
        background: statusInformation.color,
    });
    return `<span style="${style}">${escapeHtml(statusInformation.label)}</span>`;
};
