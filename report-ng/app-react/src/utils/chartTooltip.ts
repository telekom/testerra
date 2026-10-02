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
