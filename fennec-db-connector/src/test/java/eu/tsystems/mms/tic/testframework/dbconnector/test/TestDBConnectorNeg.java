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
 * Created on 13.06.2012
 * 
 * Copyright(c) 2011 - 2012 T-Systems Multimedia Solutions GmbH
 * Riesaer Str. 5, 01129 Dresden
 * All rights reserved.
 */
package eu.tsystems.mms.tic.testframework.dbconnector.test;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import eu.tsystems.mms.tic.testframework.dbconnector.DBConnector;
import eu.tsystems.mms.tic.testframework.dbconnector.query.DeleteQuery;
import eu.tsystems.mms.tic.testframework.dbconnector.query.InsertQuery;
import eu.tsystems.mms.tic.testframework.dbconnector.query.SelectQuery;
import eu.tsystems.mms.tic.testframework.dbconnector.query.TruncateQuery;
import eu.tsystems.mms.tic.testframework.dbconnector.query.UpdateQuery;
import eu.tsystems.mms.tic.testframework.dbconnector.test.connectors.LobTable;
import eu.tsystems.mms.tic.testframework.dbconnector.test.connectors.fennecTableDefinitions;
import eu.tsystems.mms.tic.testframework.dbconnector.test.connectors.fennecTestTable;

/**
 * Negative tests for fennec DBConnector.
 * 
 * @author sepr, mrgi
 */
public class TestDBConnectorNeg extends AbstractDBConnector {

    /**
     * Test DBConnector behaviour of various wrong select queries.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT01_SelectNegative() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        List<HashMap<String, String>> result;
        boolean allAssertsThrown = false;
        // stress column selection
        try {
            result = conn.select(new SelectQuery<fennecTableDefinitions>(
                    "unknown", fennecTableDefinitions.TESTTABLE, ""));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            Assert.assertTrue(e.getMessage().contains("Unknown column"));
            allAssertsThrown = true;
        }
        result = conn.select(new SelectQuery<fennecTableDefinitions>(
                "", fennecTableDefinitions.TESTTABLE, ""));
        printTableToLog(result);
        result = conn.select(new SelectQuery<fennecTableDefinitions>(
                "blob", fennecTableDefinitions.LOBTABLE, ""));
        Assert.assertTrue(!result.isEmpty());
        // stress where clause
        result = conn.select(new SelectQuery<fennecTableDefinitions>(
                "*", fennecTableDefinitions.TESTTABLE, null));
        printTableToLog(result);
        result = conn.select(new SelectQuery<fennecTableDefinitions>(
                "*", fennecTableDefinitions.TESTTABLE, fennecTestTable.getDate() + "='hallo'"));
        printTableToLog(result);
        try {
            result = conn.select(new SelectQuery<fennecTableDefinitions>(
                    "*", fennecTableDefinitions.LOBTABLE, "blob IS NOT NULL"));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            Assert.assertTrue(e.getMessage().contains("You have an error in your SQL syntax"));
            // sql keywords should be escaped with tablename.
            // this should work:
            Assert.assertNotNull(conn.select(new SelectQuery<fennecTableDefinitions>("*", fennecTableDefinitions.LOBTABLE,
                    fennecTableDefinitions.LOBTABLE.getTableName() + ".blob IS NOT NULL")));
            allAssertsThrown = allAssertsThrown & true;
        }
        // stress from table
        try {
            result = conn.select(new SelectQuery<fennecTableDefinitions>(
                    "*", new fennecTableDefinitions("unknown"), ""));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            Assert.assertTrue(e.getMessage().contains("Table 'fennecdbconnector.unknown' doesn't exist"));
            allAssertsThrown = allAssertsThrown & true;
        }
        try {
            result = conn.select(new SelectQuery<fennecTableDefinitions>(
                    "*", null, ""));
        } catch (final NullPointerException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            allAssertsThrown = allAssertsThrown & true;
        }
        Assert.assertTrue(allAssertsThrown);
    }

    /**
     * Test DBConnector behaviour of various wrong delete queries.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT02_DeleteNegative() throws SQLException {
        final DBConnector<?> conn = getDBConnector();

        boolean allAssertsThrown = false;
        // stress where clause
        conn.query(new DeleteQuery<fennecTableDefinitions>(fennecTableDefinitions.TESTTABLE, null));

        conn.query(new DeleteQuery<fennecTableDefinitions>(
                fennecTableDefinitions.TESTTABLE, fennecTestTable.getDate() + "='hallo'"));

        try {
            conn.query(new DeleteQuery<fennecTableDefinitions>(fennecTableDefinitions.LOBTABLE, "blob IS NOT NULL"));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown:" + e.toString());
            Assert.assertTrue(e.getMessage().contains("You have an error in your SQL syntax"));
            allAssertsThrown = true;
        }
        // stress from table
        try {
            conn.query(new DeleteQuery<fennecTableDefinitions>(new fennecTableDefinitions("unknown"), ""));
            allAssertsThrown = allAssertsThrown & false;
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown:" + e.toString());
            Assert.assertTrue(e.getMessage().contains("Table 'fennecdbconnector.unknown' doesn't exist"));
            allAssertsThrown = allAssertsThrown & true;
        }
        try {
            conn.query(new DeleteQuery<fennecTableDefinitions>(null, ""));
            allAssertsThrown = allAssertsThrown & false;
        } catch (final NullPointerException e) {
            LOG.debug("Expected Exception thrown:" + e.toString());
            allAssertsThrown = allAssertsThrown & true;
        }
        Assert.assertTrue(allAssertsThrown);
    }

    /**
     * Test DBConnector behaviour of various wrong getBlob queries.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT03_GetBlobNegative() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        final fennecTableDefinitions table = fennecTableDefinitions.LOBTABLE;
        Blob result;
        boolean allAssertsThrown = false;
        // stress from column
        try {
            conn.selectBlob(new SelectQuery<fennecTableDefinitions>("", table, null));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            allAssertsThrown = true;
        }

        // stress from table
        try {
            conn.selectBlob(new SelectQuery<fennecTableDefinitions>(LobTable.getBlob(), fennecTableDefinitions.TESTTABLE,
                    null));
            allAssertsThrown = allAssertsThrown & false;
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            allAssertsThrown = allAssertsThrown & true;
        }

        // stress where clause
        try {
            conn.selectBlob(new SelectQuery<fennecTableDefinitions>("*", table,
                    LobTable.getBlobType() + "IS NOT NULL"));
            allAssertsThrown = allAssertsThrown & false;
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            allAssertsThrown = allAssertsThrown & true;
        }

        result = conn.selectBlob(new SelectQuery<fennecTableDefinitions>(LobTable.getBlob(), table, null));
        Assert.assertNotNull(result);

        Assert.assertTrue(allAssertsThrown);
    }

    /**
     * Test DBConnector behaviour of various wrong getClob queries.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT04_GetClobNegative() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        final fennecTableDefinitions table = fennecTableDefinitions.LOBTABLE;
        Clob result;
        boolean allAssertsThrown = false;
        // stress from column
        try {
            conn.selectClob(new SelectQuery<fennecTableDefinitions>("", table, null));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            allAssertsThrown = true;
        }

        // stress from table
        try {
            conn.selectClob(new SelectQuery<fennecTableDefinitions>(LobTable.getBlob(), fennecTableDefinitions.TESTTABLE,
                    null));
            allAssertsThrown = allAssertsThrown & false;
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            allAssertsThrown = allAssertsThrown & true;
        }

        // stress where clause
        try {
            conn.selectClob(new SelectQuery<fennecTableDefinitions>("*", table,
                    LobTable.getBlobType() + "IS NOT NULL"));
            allAssertsThrown = allAssertsThrown & false;
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            allAssertsThrown = allAssertsThrown & true;
        }

        result = conn.selectClob(new SelectQuery<fennecTableDefinitions>(LobTable.getClob(), table, null));
        Assert.assertNotNull(result);

        Assert.assertTrue(allAssertsThrown);
    }

    /**
     * Test DBConnector behaviour of various wrong INSERT queries.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT05_InsertNegative() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        boolean exception = false;

        // stress tablename
        try {
            conn.query(new InsertQuery<fennecTableDefinitions>(new fennecTestTable("nicht_vorhanden"), null,
                    new String[] {
                            "4", "user04", "Max", "Mustermann", "25", USER1.getDate().toString(),
                            USER1.getCreationTime().toString()
                    }));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("Table 'fennecdbconnector.nicht_vorhanden' doesn't exist")) {
                exception = true;
            }
        }
        Assert.assertTrue(exception);

        exception = false;
        // stress columnnames
        try {
            conn.query(new InsertQuery<fennecTableDefinitions>(fennecTableDefinitions.TESTTABLE,
                    new String[] { "nicht_vorhanden" },
                    new String[] { "40" }));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("Unknown column 'testtable.nicht_vorhanden' in 'field list'")) {
                exception = true;
            }
        }
        Assert.assertTrue(exception);

        exception = false;
        // stress values
        try {
            conn.query(new InsertQuery<fennecTableDefinitions>(fennecTableDefinitions.TESTTABLE,
                    new String[] { fennecTestTable.getIdField() },
                    new String[] { "text" }));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("Incorrect integer value")) {
                exception = true;
            }
        }
        Assert.assertTrue(exception);

        exception = false;
        // different value than column number
        try {
            conn.query(new InsertQuery<fennecTableDefinitions>(fennecTableDefinitions.TESTTABLE, null,
                    new String[] { "", "" }));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("Column count doesn't match value count")) {
                exception = true;
            }
        }
        try {
            conn.query(new InsertQuery<fennecTableDefinitions>(fennecTableDefinitions.TESTTABLE,
                    new String[] { fennecTestTable.getAge(), fennecTestTable.getFirstname() },
                    new String[] { "50", "Horst", "fehler" }));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("Column count doesn't match value count")) {
                exception = true;
            }
        }
        Assert.assertTrue(exception);
    }

    /**
     * Test DBConnector behaviour of truncate query with wrong tablename. Exception should be thrown.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT06_TruncateNegative() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        boolean exceptionThrown = false;
        try {
            conn.query(new TruncateQuery<fennecTableDefinitions>(new fennecTestTable("nicht_vorhanden")));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("Table 'fennecdbconnector.nicht_vorhanden' doesn't exist")) {
                exceptionThrown = true;
            }
        }
        Assert.assertTrue(exceptionThrown);
    }

    /**
     * Test DBConnector behaviour of various wrong update queries.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT07_UpdateNegative() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        boolean exceptionThrown = false;

        // stress tablename
        try {
            conn.query(new UpdateQuery<fennecTableDefinitions>(new fennecTestTable("nicht_vorhanden"),
                    new String[] { fennecTestTable.getFirstname() }, new String[] { "ursel" },
                    fennecTestTable.getUser() + "='" + USER1.getUser() + "'"));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("Table 'fennecdbconnector.nicht_vorhanden' doesn't exist")) {
                exceptionThrown = true;
            }
        }
        Assert.assertTrue(exceptionThrown);

        // stress columnname
        try {
            conn.query(new UpdateQuery<fennecTableDefinitions>(fennecTableDefinitions.TESTTABLE,
                    new String[] { "nicht_vorhanden" }, new String[] { "ursel" },
                    fennecTestTable.getUser() + "='" + USER1.getUser() + "'"));
            exceptionThrown = false;
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("Unknown column 'nicht_vorhanden' in 'field list'")) {
                exceptionThrown = true;
            }
        }
        Assert.assertTrue(exceptionThrown);

        // stress where clause
        try {
            conn.query(new UpdateQuery<fennecTableDefinitions>(fennecTableDefinitions.TESTTABLE,
                    new String[] { fennecTestTable.getFirstname() }, new String[] { "ursel" },
                    "nicht_vorhanden" + "='" + USER1.getUser() + "'"));
            exceptionThrown = false;
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("Unknown column 'nicht_vorhanden' in 'field list'")) {
                exceptionThrown = true;
            }
        }
        Assert.assertTrue(exceptionThrown);

    }
}
