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
 * Created on 01.08.2012
 *
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.dbconnector.query;

import eu.tsystems.mms.tic.testframework.dbconnector.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Objects of this class represent SQL queries. Instances can be parametrized with a Table class to restrict queries to
 * that table or instances of subclasses of the table.
 *
 * @param <T> Table to restrict queries to.
 * @author sepr
 */
public class Query<T extends Table> {

    public static boolean explicitTableName = false;
    public static boolean quote = true;

    /**
     * Logger for this class.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(Query.class);
    /**
     * String representing the SQL query.
     */
    private String queryString;
    /**
     * Table to query.
     */
    private T fromTable;

    /**
     * Parse the WHERE statement to a usable string.
     *
     * @param where Statement to use.
     * @return Complete WHERE statement or empty String.
     */
    protected String completeWhere(final String where) {
        String whereClause;
        if (where == null || where.length() == 0) {
            whereClause = "";
        } else {
            whereClause = "WHERE " + where;
        }
        return whereClause;
    }

    /**
     * Getter.
     *
     * @return Table
     */
    public T getFromTable() {
        return fromTable;
    }

    /**
     * Getter.
     *
     * @return Query as String.
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * sets the fromTable
     *
     * @param fromTable the fromTable to set
     */
    public void setFromTable(final T fromTable) {
        this.fromTable = fromTable;
    }

    /**
     * sets the query string
     *
     * @param queryString the queryString to set
     */
    public void setQueryString(final String queryString) {
        this.queryString = queryString;
    }
}
