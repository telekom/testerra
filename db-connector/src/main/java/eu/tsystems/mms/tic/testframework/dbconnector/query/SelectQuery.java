/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
/* 
 * Created on 01.08.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.dbconnector.query;

import eu.tsystems.mms.tic.testframework.dbconnector.Table;

/**
 * Objects of this class represent SQL SELECT queries. Instances can be parametrized with a Table class to restrict
 * queries to that table or instances of subclasses of the table.
 * 
 * @author sepr
 * @param <T> Table to restrict queries to.
 */
public class SelectQuery<T extends Table> extends Query<T> {

    /**
     * First column of select statement. Used at queries where a special column is needed to get data from (e.g.
     * getBLOB).
     */
    private String column;

    /**
     * Constructor creating a SelectQuery object with a single column to select.
     * 
     * @param what String containing the column to select (leave emtpy or null to select all - '*').
     * @param fromTable Table to select data from.
     * @param where Where clause to constrain selected data.
     */
    public SelectQuery(final String what, final T fromTable, final String where) {
        this.setFromTable(fromTable);
        column = what;
        if (what == null || what.length() == 0 || "*".equals(what)) {
            column = "*";
        }
        final String whereClause = super.completeWhere(where);
        if ("*".equals(column)) {
            setQueryString(String.format("SELECT %s FROM %s %s", column, fromTable.getTableName(), whereClause));
        } else {
            setQueryString(String.format("SELECT %s FROM %s %s", fromTable.getTableName() + "." + column,
                    fromTable.getTableName(), whereClause));
        }
    }

    /**
     * Constructor to create a Select Query with multiple columns to select.
     * 
     * @param columns Array of Columns to select. Leave empty to select all ('*'). Queries returning values of one
     *            column will use the first one.
     * @param fromTable Table to select data from.
     * @param where Where clause to constrain selected data.
     */
    public SelectQuery(final String[] columns, final T fromTable, final String where) {
        final String whereClause = super.completeWhere(where);
        final StringBuffer allCols = new StringBuffer();
        this.setFromTable(fromTable);
        if (columns == null || columns.length < 1) {
            column = "*";
            allCols.append(column);
        } else {
            column = columns[0];
            for (final String col : columns) {
                allCols.append(fromTable.getTableName() + "." + col + ",");
            }
            allCols.setLength(allCols.length() - 1);
        }
        setQueryString(String.format("SELECT %s FROM %s %s", allCols, fromTable.getTableName(), whereClause));
    }

    /**
     * String defining the name of the first column to select. This column is chosen if the result for a query should
     * only contain one column.
     * 
     * @return Column nmae as string.
     */
    public String getColumn() {
        return column;
    }
}
