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

/**
 * Former StatusConverter
 */

import React from "react";
import CancelIcon from "@mui/icons-material/Cancel";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import RemoveCircleIcon from "@mui/icons-material/RemoveCircle";
import {ResultStatusType} from "./report-model/framework_pb";
import InfoOutlineIcon from "@mui/icons-material/InfoOutline";

// Union of numeric status values -> ResultStatus = 0 | 1 | 2 | ...
export type ResultStatus = typeof ResultStatusType[keyof typeof ResultStatusType];

interface StatusInformation {
    label: string;
    color: string;
    icon?: React.ElementType;
    key: string;
    group?: ResultStatus[];
}

// Central status configuration
// - STATUS_CONFIG is an object that contains a value of type StatusInformation for each ResultStatus
// - Record<KeyType, ValueType> -> creates object type with keys of type ResultStatus and values of type StatusInformation (each ResultType must have entry here)
// - { 0: StatusInformation, 1: StatusInformation, 2: ... }
const STATUS_CONFIG: Record<ResultStatus, StatusInformation> = {
    [ResultStatusType.RST_NOT_SET]: {
        label: "Not set",
        color: "black",
        key: "na",
    },
    [ResultStatusType.NO_RUN]: {
        label: "Running",
        color: "#5d6f81",
        key: "running",
    },
    [ResultStatusType.INFO]: {
        label: "Info",
        color: "black",
        icon: InfoOutlineIcon,
        key: "info",
    },
    [ResultStatusType.SKIPPED]: {
        label: "Skipped",
        color: "#f7af3e",
        icon: RemoveCircleIcon,
        key: "skipped",
    },
    [ResultStatusType.PASSED]: {
        label: "Passed",
        color: "#417336",
        icon: CheckCircleIcon,
        key: "passed",
        group: [
            ResultStatusType.PASSED,
            ResultStatusType.PASSED_RETRY,
            ResultStatusType.REPAIRED,
        ],
    },
    [ResultStatusType.MINOR]: {
        label: "Minor",
        color: "#417336",
        icon: CheckCircleIcon,
        key: "minor",
    },
    [ResultStatusType.FAILED]: {
        label: "Failed",
        color: "#e63946",
        icon: CancelIcon,
        key: "failed",
    },
    [ResultStatusType.FAILED_MINOR]: {
        label: "Failed Minor",
        color: "#e63946",
        icon: CancelIcon,
        key: "failed-minor",
    },
    [ResultStatusType.FAILED_RETRIED]: {
        label: "Retried",
        color: "#e63946",
        icon: CancelIcon,
        key: "retried",
    },
    [ResultStatusType.FAILED_EXPECTED]: {
        label: "Expected Failed",
        color: "#4f031b",
        icon: CancelIcon,
        key: "failed-expected",
    },
    [ResultStatusType.PASSED_RETRY]: {
        label: "Recovered",
        color: "#417336",
        icon: CheckCircleIcon,
        key: "recovered",
    },
    [ResultStatusType.MINOR_RETRY]: {
        label: "Minor Retry",
        color: "#417336",
        icon: CancelIcon,
        key: "minor-retry",
    },
    [ResultStatusType.REPAIRED]: {
        label: "Repaired",
        color: "#417336",
        icon: CheckCircleIcon,
        key: "repaired",
    },
};

// Normalize string status to ResultStatus (= number/index of ResultStatusType)
function normalizeStatus(status: ResultStatus | string): ResultStatus {
    if (typeof status === "string") {
        return Number.parseInt(status, 10) as ResultStatus;
    }
    return status;
}

// Public API
export const StatusService = {
    //Returns the full status information object
    get(status: ResultStatus | string): StatusInformation | null {
        const normalized = normalizeStatus(status);
        return STATUS_CONFIG[normalized] ?? null;
    },

    getLabel(status: ResultStatus | string): string {
        return this.get(status)?.label ?? "no label available";
    },

    getColor(status: ResultStatus | string): string {
        return this.get(status)?.color ?? "black";
    },

    // Returns rendered MUI icon
    getIcon(status: ResultStatus | string): React.ReactNode {
        const statusInformation = this.get(status);
        if (!statusInformation) return null;

        const Icon = statusInformation.icon;
        return Icon ? <Icon sx={{color: statusInformation.color}}/> : "";
    },

    // Returns grouped statuses (used for filtering etc.)
    getGroup(status: ResultStatus): ResultStatus[] {
        return STATUS_CONFIG[status]?.group ?? [status];
    },

    getRelevantStatuses(): ResultStatus[] {
        return [
            ResultStatusType.PASSED,
            ResultStatusType.FAILED,
            ResultStatusType.FAILED_EXPECTED,
            ResultStatusType.SKIPPED,
        ];
    },

    getPassedStatuses(): ResultStatus[] {
        return [
            ResultStatusType.PASSED,
            ResultStatusType.PASSED_RETRY,
            ResultStatusType.REPAIRED,
        ];
    },

    getStatusByKey(key: string): ResultStatus | null {
        const entry = Object.entries(STATUS_CONFIG).find(
            ([, statusInformation]) => statusInformation.key === key
        );
        return entry ? Number(entry[0]) as ResultStatus : null;
    }
};