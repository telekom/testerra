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

import {useState} from "react";
import {MethodDetails} from "../model/MethodDetails";

export type OrderDirection = "asc" | "desc";
export type SortableColumn = "runIndex" | "class" | "startTime" | "method";

// sortAccessor extracts sorting-relevant data from methodDetails
const sortAccessors: Record<SortableColumn, (methodDetail: MethodDetails) => string | number> = {
    runIndex: methodDetail => methodDetail.methodContext.methodRunIndex ?? 0,
    class: methodDetail => methodDetail.classStatistics.classIdentifier ?? "",
    startTime: methodDetail => methodDetail.methodContext.contextValues?.startTime ?? 0,
    method: methodDetail => methodDetail.identifier ?? "",
};

export function useTestListSort() {
    const [orderDirection, setOrderDirection] = useState<OrderDirection>("asc");    // asc or desc sorting direction
    const [orderBy, setOrderBy] = useState<SortableColumn>("runIndex");             // currently sorted column

    // function for sorting; uses order direction and column that should be sorted
    function buildComparator(order: OrderDirection, orderBy: SortableColumn) {
        const getSortAccessor = sortAccessors[orderBy];
        const sortingDirection = order === "asc" ? 1 : -1;

        return (a: MethodDetails, b: MethodDetails) => { const aValue = getSortAccessor(a); const bValue = getSortAccessor(b);

            // case-insensitive with numeric sorting (e.g. "2" < "10") (multiplied by sortingDirection to invert sorting direction if necessary)
            if (typeof aValue === "string" && typeof bValue === "string") {
                return sortingDirection * aValue.localeCompare(bValue, undefined, { numeric: true, sensitivity: "base" });
            }
            // number comparison (multiplied by sortingDirection to invert sorting direction if necessary)
            return sortingDirection * ((aValue as number) - (bValue as number));
        };
    }

    const handleRequestSort = (property: SortableColumn) => {
        const isAsc = orderBy === property && orderDirection === "asc";
        setOrderDirection(isAsc ? "desc" : "asc");
        setOrderBy(property);
    };

    return {
        orderDirection,
        orderBy,
        handleRequestSort,
        buildComparator
    };
}
