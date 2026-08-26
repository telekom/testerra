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

import {useSearchParams} from "react-router-dom";
import { StatusService } from "../model/status-service";
import type { ResultStatus } from "../model/status-service";
import * as React from "react";
import {useReportData} from "../provider/DataProvider";
import {ClassName, classNameConverter} from "../utils/classNameConverter";
import {useMemo} from "react";
import {ResultStatusType} from "../model/report-model/framework_pb";
import type {ExecutionStatisticsManager} from "../provider/ExecutionStatisticsManager";

export type ChipColor = "blue" | "green" | "purple" | "pink" | "lightGrey" | "default";

export type FilterType = "status" | "class" | "customText" | "failureAspect" | "methods" | "method";

export type FilterValueMap = {
    status: ResultStatus[];
    class: string[];
    customText: string[];
    failureAspect: string[];
    methods: string[];
    method: string[];
};

// Everything a chip needs to be rendered - the view does not have to know about filters at all
export type FilterChip = {
    id: string;
    label: string;
    color?: ChipColor;
    tooltipText: string;
    onDelete: () => void;
};

type FilterDef<K extends FilterType> = {
    filterType: string;
    // URL -> value
    parse: (raw: string | null) => FilterValueMap[K];
    // value -> URL string (or null -> remove)
    convertToURLString: (value: FilterValueMap[K]) => string | null;
    color?: ChipColor;
    // executionMngr is only needed by filters whose URL value is an id instead of a label
    getLabel: (value?: string | ResultStatus, executionMngr?: ExecutionStatisticsManager | null) => string;
    tooltipText: string;
};

export const FILTERS: { [K in FilterType]: FilterDef<K> } = {
    status: {
        filterType: "status",
        parse: (statusParam) => {
            return statusParam
            ? statusParam
                .split("~")
                .map(statusKey => StatusService.getStatusByKey(statusKey))
                .filter(Boolean) as ResultStatus[]
            : [];
        },
        convertToURLString: (statuses) => {
            if (!statuses.length) return null;
            const value = statuses
                .map(s => StatusService.get(s).key)
                .filter(Boolean)
                .join("~");
            return value || null;
        },
        color: "blue",
        getLabel: (value) => {
            return StatusService.get(value as ResultStatus).label ?? String(value);
        },
        tooltipText: "Status"
    },

    class: {
        filterType: "class",
        parse: (classParam) => {
            return classParam
            ? classParam.split("~") as string[]
            : []
        },
        convertToURLString: (classes) => (classes.length > 0 ? classes.join("~") : null),
        color: "green",
        getLabel: (value) => {
            return String(value);
        },
        tooltipText: "Class"
    },

    customText: {
        filterType: "customText",
        parse: (textParam) => {
            return textParam
                ? textParam.split("~") as string[]
                : []
        },
        convertToURLString: (texts) => (texts.length > 0 ? texts.join("~") : null),
        color: "purple",
        getLabel: (value) => {
            return String(value);
        },
        tooltipText: "Custom Filter"
    },

    failureAspect: {
        filterType: "failureAspect",
        parse: (failureAspectParam) => {
            return failureAspectParam ? [failureAspectParam] : [];
        },
        convertToURLString: (failureAspects) => {
            if(!failureAspects.length) return null;
            return failureAspects[0];
        },
        getLabel: () => "Custom Filter",
        tooltipText: "Custom Filter Failure Aspects"
    },

    methods: {
        filterType: "methods",
        parse: (methodsParam) => {
            return methodsParam
                ? methodsParam.split("~") as string[]
                : []
        },
        convertToURLString: (methods) => (methods.length > 0 ? methods.join("~") : null),
        getLabel: () => "Custom Filter",
        tooltipText: "Selected Methods (from Timings chart)"
    },

    method: {
        filterType: "method",
        parse: (methodParam) => {
            return methodParam
                ? methodParam
                    .split("~") as string[]
                : []
        },
        convertToURLString: (methods) => (methods.length > 0 ? methods.join("~") : null),
        color: "pink",
        getLabel: (value, executionMngr) => executionMngr?.getMethodName(String(value)) ?? String(value),
        tooltipText: "Method"
    }
};

export type FiltersState = Partial<FilterValueMap>;     // Partial because not every filter has to be set always

export function useChipListFilters(filterTypes: readonly FilterType[]) {
    const {executionMngr} = useReportData();

    // only update classMenuItems if executionManager changes
    const classMenuItems = useMemo(() => {
        if (!executionMngr) return [];
        return executionMngr.getExecutionStatistics()
            .classStatistics
            .map(cs => classNameConverter(cs.classIdentifier, ClassName.simpleName))
            .sort((a, b) => a.localeCompare(b));
    }, [executionMngr]);

    const statusMenuItems = useMemo(() => {
        if (!executionMngr) return [];
        const resultStatuses: ResultStatusType[] | number[] = [];

        executionMngr.getExecutionStatistics().classStatistics.forEach(classStat => {
            classStat.methodContexts.forEach(context => {
                if (!resultStatuses.includes(context.resultStatus!)) {
                    resultStatuses.push(context.resultStatus!);
                }
            });
        });
        return resultStatuses;
    }, [executionMngr]);

    const [searchParams, setSearchParams] = useSearchParams();

    const configurationMethodsChecked = React.useMemo(
        () => searchParams.get("config") === "true",
        [searchParams]
    );

    // read filters from URL when searchParam changes (useMemo)
    const filters: FiltersState = React.useMemo(() => Object.fromEntries(
        filterTypes.map((filterType) => {
            const filterDefinition = FILTERS[filterType];
            return [filterType, filterDefinition.parse(searchParams.get(filterDefinition.filterType))];
        }),
    ) as FiltersState, [filterTypes, searchParams]);

    // helper: update URL
    const setFilter = <T extends FilterType>(filter: T, updatedFilter: FilterValueMap[T]) => {
        const filterDefinition = FILTERS[filter];
        const params = new URLSearchParams(searchParams);
        const convertedFilter = filterDefinition.convertToURLString(updatedFilter);

        if (convertedFilter === null) {
            params.delete(filterDefinition.filterType);
        }
        else {
            params.set(filterDefinition.filterType, convertedFilter);
        }

        setSearchParams(params);
    };

    const clearAll = () => {
        const params = new URLSearchParams(searchParams);
        filterTypes.forEach((type) => {
            params.delete(FILTERS[type].filterType);
        });
        setSearchParams(params);
    };

    const handleConfigurationMethodsChecked = (event: React.ChangeEvent<HTMLInputElement>) => {
        const checked = event.target.checked;
        const params = new URLSearchParams(searchParams);

        if (checked) {
            params.set("config", "true");
        } else {
            params.delete("config");
        }

        setSearchParams(params);
    };

    // generic remove function (for arrays)
    const handleDelete = (filterType: FilterType, filterToRemove?: string | ResultStatus) => {
        // filterToRemove is undefined for combined chips (e.g. "methods"):
        // they represent multiple values and therefore clear the whole filter at once.
        const currentFilters = filters[filterType] ?? [];
        const updatedFilters = filterToRemove === undefined
            ? []
            : currentFilters.filter(value => value !== filterToRemove);
        setFilter(filterType, updatedFilters as FilterValueMap[FilterType]);
    };

    // Turns the currently selected filters into renderable chip descriptions.
    // Keeps label and styling resolution out of the presentational chip components.
    const createChip = (filterType: FilterType, value?: string | ResultStatus): FilterChip => {
        const definition = FILTERS[filterType];
        return {
            id: value === undefined ? filterType : `${filterType}-${String(value)}`,
            label: definition.getLabel(value, executionMngr),
            color: definition.color,
            tooltipText: definition.tooltipText,
            onDelete: () => handleDelete(filterType, value),
        };
    };

    const chips = filterTypes.flatMap(filterType => {
        const values = filters[filterType];
        if (!values || values.length === 0) return [];
        if (filterType === "methods") return [createChip(filterType)];
        return values.map(value => createChip(filterType, value));
    });

    return {
        statusMenuItems,
        classMenuItems,
        filters,
        chips,
        configurationMethodsChecked,
        setFilter,
        handleConfigurationMethodsChecked,
        clearAll,
    };
}
