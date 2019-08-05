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
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;

/**
 * Class representing an update query.
 * 
 * @param <T>
 *            Table to update.
 * 
 * @author sepr
 */
public class UpdateQuery<T extends Table> extends Query<T> implements INonSelectQuery {

    /**
     * Method to create an UPDATE SQL query statement.
     * 
     * @param table
     *            Table to update data.
     * @param cols
     *            Columns to insert data into.
     * @param values
     *            Values to update.
     * @param where
     *            WHERE clause to select rows that need to be updated.
     */
    public UpdateQuery(final T table, final String[] cols, final String[] values, final String where) {
        this.setFromTable(table);
        if (cols == null | values == null | cols.length != values.length | cols.length == 0) {
            LOG.error("columns and/or values not valid.");
            throw new TesterraRuntimeException("columns and/or values not valid.");
        } else if (where == null | where.length() == 0) {
            LOG.error("where clause needed to specifiy rows that need to be updated.");
            throw new TesterraRuntimeException("where clause needed to specifiy rows that need to be updated.");
        } else {
            final StringBuffer colValueString = new StringBuffer();
            for (int i = 0; i < cols.length; i++) {
                colValueString.append(table.getTableName() + "." + cols[i] + "='" + values[i] + "'");
                if (i != cols.length - 1) {
                    colValueString.append(", ");
                }
            }
            setQueryString(String.format("UPDATE %s SET %s WHERE %s", table.getTableName(), colValueString, where));
        }
    }
}
