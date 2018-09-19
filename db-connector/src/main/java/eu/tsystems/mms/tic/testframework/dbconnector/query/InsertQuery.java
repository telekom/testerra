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
 * Created on 17.09.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.dbconnector.query;

import eu.tsystems.mms.tic.testframework.dbconnector.Table;

/**
 * Class representing insert queries.
 * 
 * @param <T>
 *            Table to insert data into.
 * 
 * @author sepr
 */
public class InsertQuery<T extends Table> extends Query<T> implements INonSelectQuery {

    /**
     * Method to create an INSERT SQL query statement.
     * 
     * @param intoTable
     *            Table to insert data into.
     * @param cols
     *            Columns to insert data into. If null values for all Columns must be stated.
     * @param values
     *            Values to insert.
     */
    public InsertQuery(final T intoTable, final String[] cols, final String[] values) {
        this.setFromTable(intoTable);

        final String commaSepValues = getCommaSeparatedString(values, true, false);

        if (cols == null || cols.length == 0) {
            setQueryString(String.format("INSERT INTO %s VALUES (%s)", intoTable.getTableName(), commaSepValues));
        } else {
            final String commaSepCols = getCommaSeparatedString(cols, false, true);
            setQueryString(String.format("INSERT INTO %s (%s) VALUES (%s)", intoTable.getTableName(),
                    commaSepCols, commaSepValues));
        }
    }
}
