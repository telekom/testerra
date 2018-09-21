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
package eu.tsystems.mms.tic.testframework.dbconnector;

import eu.tsystems.mms.tic.testframework.dbconnector.query.INonSelectQuery;
import eu.tsystems.mms.tic.testframework.dbconnector.query.Query;
import eu.tsystems.mms.tic.testframework.dbconnector.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class holding methods to send queries to the DB.
 * 
 * @author sepr
 */
public class DBMethods extends AbstractConnection {

    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DBMethods.class);

    /**
     * Returns a list of query results containing a mapping of column names and their respective values.
     * 
     * @param query Query object to query the DB.
     * @return List of table rows, containing mapping of column name and value.
     * @throws SQLException .
     */
    public List<HashMap<String, String>> select(final SelectQuery<?> query) throws SQLException {
        return this.lSelect(query);
    }
    
    /**
     * Returns a list of query results containing a mapping of column names and their respective values.
     * 
     * @param query Query object to query the DB.
     * @return List of table rows, containing mapping of column name and value.
     * @throws SQLException .
     */
    private List<HashMap<String, String>> lSelect(final SelectQuery<?> query) throws SQLException {
        return query(query.getQueryString());
    }

    /**
     * Returns the count of updated (deleted, inserted or truncated) rows.
     * 
     * @param query Query object to query the DB.
     * @return Count of updated rows or -1.
     * @throws SQLException .
     */
    public int query(final INonSelectQuery query) throws SQLException {
        return this.pQuery(query);
    }
    
    /**
     * Returns the count of updated (deleted, inserted or truncated) rows.
     * 
     * @param query Query object to query the DB.
     * @return Count of updated rows or -1.
     * @throws SQLException .
     */
    private int pQuery(final INonSelectQuery query) throws SQLException {
        if (query instanceof Query) {
            Object result = queryResultSet(((Query<?>) query).getQueryString());
            if (result instanceof Integer) {
                return (Integer) result;
            }
        } else {
            LOGGER.error("ConfigurationException: Can't use query Method without Query Object.");
        }
        return -1;
    }

    /**
     * Returns a list of query results containing a mapping of column names and their respective values.
     * 
     * @param query Query to run on DB.
     * @return List of table rows, containing mapping of column name and value.
     * @throws SQLException .
     */
    public List<HashMap<String, String>> query(final String query) throws SQLException {
        return this.pQuery(query);
    }
    
    /**
     * Returns a list of query results containing a mapping of column names and their respective values.
     * 
     * @param query Query to run on DB.
     * @return List of table rows, containing mapping of column name and value.
     * @throws SQLException .
     */
    private List<HashMap<String, String>> pQuery(final String query) throws SQLException {

        final ArrayList<HashMap<String, String>> out = new ArrayList<HashMap<String, String>>();
        ResultSet rset = null;
        try {
            Object result = queryResultSet(query);
            if (result instanceof ResultSet) {
                rset = (ResultSet) result;
                // get column names (Spaltennamen)
                final ResultSetMetaData metadata = rset.getMetaData();
                final int col = metadata.getColumnCount();
                final ArrayList<String> colNames = new ArrayList<String>();
                for (int i = 1; i <= col; i++) {
                    colNames.add(metadata.getColumnName(i));
                }
                // get data
                while (rset.next()) {
                    final HashMap<String, String> hmap = new HashMap<String, String>();
                    for (final String c : colNames) {
                        hmap.put(c, rset.getString(c));
                    }
                    out.add(hmap);
                }
            }
        } finally {
            if (rset != null) {
                rset.close();
            }
        }
        return out;
    }

    /**
     * Returns a list of all values of the first column specified in the select query.
     * 
     * @param query SelectQuery to query DB.
     * @return List of all values of the specified column.
     * @throws SQLException .
     */
    public List<String> selectSingleColumn(final SelectQuery<?> query) throws SQLException {
        return this.pSelectSingleColumn(query);
    }
    
    /**
     * Returns a list of all values of the first column specified in the select query.
     * 
     * @param query SelectQuery to query DB.
     * @return List of all values of the specified column.
     * @throws SQLException .
     */
    private List<String> pSelectSingleColumn(final SelectQuery<?> query) throws SQLException {
        final String col = query.getColumn();
        final ArrayList<String> ret = new ArrayList<String>();
        final List<HashMap<String, String>> result = select(query);
        for (final HashMap<String, String> map : result) {
            ret.add(map.get(col));
        }
        return ret;
    }

    /**
     * Returns the first object returned by query.
     * 
     * @param query SelectQuery object.
     * @return Returns first value of first specified column in query.
     * @throws SQLException .
     */
    public Object selectSingleObject(final SelectQuery<?> query) throws SQLException {
        return this.pSelectSingleObject(query);
    }
    
    /**
     * Returns the first object returned by query.
     * 
     * @param query SelectQuery object.
     * @return Returns first value of first specified column in query.
     * @throws SQLException .
     */
    private Object pSelectSingleObject(final SelectQuery<?> query) throws SQLException {
        final String col = query.getColumn();
        final List<HashMap<String, String>> result = select(query);
        if (!result.isEmpty()) {
            final HashMap<String, String> firstRes = result.get(0);
            return firstRes.get(col);
        }
        return null;
    }

    /**
     * Gets first Character Large Object of this queries result.
     * 
     * @param query Query object to get CLOB.
     * @return Returns first CLOB of the first specified column in select statement or null.
     * @throws SQLException .
     */
    public Clob selectClob(final SelectQuery<?> query) throws SQLException {
        return this.pSelectClob(query);
    }
    
    /**
     * Gets first Character Large Object of this queries result.
     * 
     * @param query Query object to get CLOB.
     * @return Returns first CLOB of the first specified column in select statement or null.
     * @throws SQLException .
     */
    private Clob pSelectClob(final SelectQuery<?> query) throws SQLException {
        Clob out = null;
        ResultSet set = null;
        try {
            Object result = queryResultSet(query.getQueryString());
            if (result instanceof ResultSet) {
                set = (ResultSet) result;
                if (set.next()) {
                    out = set.getClob(query.getColumn());
                } else {
                    out = null;
                }
            }
        } finally {
            if (set != null) {
                set.close();
            }
            if (isAutomaticConnectionHandling()) {
                close();
            }
        }
        return out;
    }

    /**
     * Gets first Binary Large Object of this queries result.
     * 
     * @param query Query object to get BLOB.
     * @return Returns first BLOB of the first specified column in select statement or null.
     * @throws SQLException .
     */
    public Blob selectBlob(final SelectQuery<?> query) throws SQLException {
        return this.pSelectBlob(query);
    }
    
    /**
     * Gets first Binary Large Object of this queries result.
     * 
     * @param query Query object to get BLOB.
     * @return Returns first BLOB of the first specified column in select statement or null.
     * @throws SQLException .
     */
    private Blob pSelectBlob(final SelectQuery<?> query) throws SQLException {
        Blob out = null;
        ResultSet set = null;
        try {
            Object result = queryResultSet(query.getQueryString());
            if (result instanceof ResultSet) {
                set = (ResultSet) result;
                if (set.next()) {
                    out = set.getBlob(query.getColumn());
                } else {
                    out = null;
                }
            }
        } finally {
            if (set != null) {
                set.close();
            }
            if (isAutomaticConnectionHandling()) {
                close();
            }
        }
        return out;
    }

    /**
     * With this method you can submit any query and get a JDBC result set back. <b>The returned resultset and statement
     * must be closed manually.</b> <code>Statement stmt = set.getStatement();
     *       set.close();
     *       if (stmt != null) {
     *           stmt.close();
     *       }
     *  </code> Try to use the predefined query methods which close the objects for you. Just use this method for
     * special cases (e.g. getting a list of BLOBs).
     * 
     * @param query SQL query to send to db.
     * @return JDBC ResultSet or int representing update Count.
     * @throws SQLException .
     */
    public Object queryResultSet(final String query) throws SQLException {
        return this.pQueryResultSet(query);
    }
    
    /**
     * With this method you can submit any query and get a JDBC result set back. <b>The returned resultset and statement
     * must be closed manually.</b> <code>Statement stmt = set.getStatement();
     *       set.close();
     *       if (stmt != null) {
     *           stmt.close();
     *       }
     *  </code> Try to use the predefined query methods which close the objects for you. Just use this method for
     * special cases (e.g. getting a list of BLOBs).
     * 
     * @param query SQL query to send to db.
     * @return JDBC ResultSet or int representing update Count.
     * @throws SQLException .
     */
    private synchronized Object pQueryResultSet(final String query) throws SQLException {
        if (getConnection() == null || getConnection().isClosed()) {
            this.open();
        }
        final Statement stmt = getConnection().createStatement();

        LOGGER.info("Execute Query: " + query);
        // Execute the query
        boolean hasResult;
        try {
            hasResult = stmt.execute(query);
        } catch (SQLException e) {
            LOGGER.info("Reopening session and trying again...");
            open();
            hasResult = stmt.execute(query);
        }

        if (hasResult) {
            LOGGER.info("Returning ResultSet");
            return stmt.getResultSet();

        } else {
            LOGGER.info("Returning updateCount");
            int cnt = stmt.getUpdateCount();
            stmt.close();
            if (isAutomaticConnectionHandling()) {
                close();
            }
            return cnt;
        }
    }
}
