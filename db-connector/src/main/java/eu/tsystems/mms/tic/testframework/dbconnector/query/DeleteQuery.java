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
 *     Peter Lehmann
 *     pele
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
 * Class representing Queries to delete something.
 *
 * @param <T>
 *            Table to delete data from.
 *
 * @author sepr
 */
public class DeleteQuery<T extends Table> extends Query<T> implements INonSelectQuery {

    /**
     * Creates a query to delete rows from a table.
     *
     * @param table
     *            Table to delete rows from.
     * @param where
     *            Where clause to select rows to delete.
     */
    public DeleteQuery(final T table, final String where) {
        this.setFromTable(table);
        final String whereString = completeWhere(where);
        setQueryString(String.format("DELETE FROM %s %s", table.getTableName(), whereString));
    }
}
