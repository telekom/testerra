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

import Box from "@mui/material/Box";
import {visuallyHidden} from "@mui/utils";
import TableSortLabel from "@mui/material/TableSortLabel";
import type {OrderDirection, SortableColumn} from "../hooks/useTestListSort";

interface TableSortProps {
    orderBy: SortableColumn;
    orderDirection: OrderDirection;
    onRequestSort: (property: SortableColumn) => void;
    headerProperty: SortableColumn;
    label: string;
}

const TableSort = ({orderBy, orderDirection, onRequestSort, headerProperty, label}: TableSortProps ) => {
    return (
        <TableSortLabel active={orderBy === headerProperty}
                        direction={orderBy === headerProperty ? orderDirection : "asc"}
                        onClick={() => onRequestSort(headerProperty)}>
            {label}
            {orderBy === headerProperty && (
                <Box component="span" sx={visuallyHidden}>
                    {orderDirection === "desc" ? "sorted descending" : "sorted ascending"}
                </Box>
            )}
        </TableSortLabel>
    );
};
export default TableSort;
