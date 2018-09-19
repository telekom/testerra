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

import eu.tsystems.mms.tic.testframework.dbconnector.DBConnector;
import eu.tsystems.mms.tic.testframework.dbconnector.query.DeleteQuery;
import eu.tsystems.mms.tic.testframework.dbconnector.query.InsertQuery;
import eu.tsystems.mms.tic.testframework.dbconnector.query.SelectQuery;
import eu.tsystems.mms.tic.testframework.dbconnector.query.TruncateQuery;
import eu.tsystems.mms.tic.testframework.dbconnector.query.UpdateQuery;
import eu.tsystems.mms.tic.testframework.dbconnector.test.connectors.LobTable;
import eu.tsystems.mms.tic.testframework.dbconnector.test.connectors.TableDefinitions;
import eu.tsystems.mms.tic.testframework.dbconnector.test.connectors.TestTable;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Integration Tests for FennecDBConnector.
 * 
 * @author sepr
 */
public class TestDBConnector extends AbstractDBConnectorTest {

    /**
     * Test implementation of "DELETE FROM table [WHERE x=y]" of fennec DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT01_Delete() throws SQLException {
        final DBConnector<?> conn = getDBConnector();

        final List<HashMap<String, String>> res =
                conn.select(new SelectQuery<TableDefinitions>("*", TableDefinitions.TESTTABLE, null));

        Assert.assertEquals(3, res.size());
        printTableToLog(res);
        final int result = conn.query(new DeleteQuery<TableDefinitions>(TableDefinitions.TESTTABLE,
                TestTable.getUser() + "='" + USER1.getUser() + "'"));
        Assert.assertEquals(1, result);
        final List<HashMap<String, String>> resAfter =
                conn.select(new SelectQuery<TableDefinitions>("*", TableDefinitions.TESTTABLE, null));
        Assert.assertEquals(2, resAfter.size());
        printTableToLog(resAfter);
    }

    /**
     * Test implementation of "Select row From table [where x=y]" of fennec DBConnector, where 'row' contains BLOBs.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT02_GetBlob() throws SQLException {
        // Use test.props from resources
        final long fileSize = 1007;
        final DBConnector<?> conn = getDBConnector();
        final Blob blob = conn.selectBlob(
                new SelectQuery<TableDefinitions>(LobTable.getBlob(), TableDefinitions.LOBTABLE, null));
        Assert.assertEquals(fileSize, blob.length());

        final Blob blob2 = conn.selectBlob(
                new SelectQuery<TableDefinitions>(LobTable.getBlob(), TableDefinitions.LOBTABLE, LobTable
                        .getIdField() + "=1"));
        Assert.assertEquals(fileSize, blob2.length());
    }

    /**
     * Test implementation of "Select row From table [where x=y]" of fennec DBConnector, where 'row' contains CLOBs.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT03_GetClob() throws SQLException {
        final long clobSize = 14688;
        final DBConnector<?> conn = getDBConnector();

        final Clob clob = conn.selectClob(
                new SelectQuery<TableDefinitions>(LobTable.getClob(), TableDefinitions.LOBTABLE, null));
        Assert.assertEquals(clobSize, clob.length());

        final Clob clob2 = conn.selectClob(
                new SelectQuery<TableDefinitions>(LobTable.getClob(), TableDefinitions.LOBTABLE, LobTable
                        .getIdField() + "=1"));
        Assert.assertEquals(clobSize, clob2.length());
    }

    /**
     * Test implementation of "INSERT INTO table [(col1,col2,...)] VALUES(...)" of fennec DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT04_Insert() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        int result = conn.query(new InsertQuery<TableDefinitions>(TableDefinitions.TESTTABLE, null,
                new String[] {
                        "4", "user04", "Max", "Mustermann", "25", USER1.getDate().toString(),
                        USER1.getCreationTime().toString()
                }));
        Assert.assertEquals(1, result);
        result = conn.query(new InsertQuery<TableDefinitions>(TableDefinitions.TESTTABLE,
                new String[] { TestTable.getAge(), TestTable.getLastname() },
                new String[] { "40", "Hans" }));
        Assert.assertEquals(1, result);

        final List<HashMap<String, String>> out = conn.select(
                new SelectQuery<TableDefinitions>("*", TableDefinitions.TESTTABLE, ""));
        Assert.assertEquals(5, out.size());
    }

    /**
     * Test the keyword 'into' as tablename. SQL Exception will be thrown.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT05_InsertSQLKeyword() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        boolean exception = false;

        // tablename = 'into'
        try {
            conn.query(new InsertQuery<TableDefinitions>(new TestTable("into"), null,
                    new String[] { "", "", "", "", "", "", "" }));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("You have an error in your SQL syntax")) {
                exception = true;
            }
        }
        Assert.assertTrue(exception);
    }

    /**
     * Test implementation of "Select * From table [where x=y]" of fennec DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT06_SelectAll() throws SQLException {
        final DBConnector<?> conn = getDBConnector();

        final List<HashMap<String, String>> res =
                conn.select(new SelectQuery<TableDefinitions>("*", TableDefinitions.TESTTABLE, null));
        Assert.assertEquals(3, res.size());
        printTableToLog(res);

        final List<HashMap<String, String>> res2 =
                conn.select(new SelectQuery<TableDefinitions>("*", TableDefinitions.TESTTABLE,
                        TestTable.getAge() + "< 100"));
        Assert.assertEquals(2, res2.size());
        printTableToLog(res2);
    }

    /**
     * Test implementation of "Select col From table [where x=y]" of fennec DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT07_SelectCol() throws SQLException {
        final DBConnector<?> conn = getDBConnector();

        final List<HashMap<String, String>> result = conn
                .select(new SelectQuery<TableDefinitions>(
                        new String[] { TestTable.getAge(), TestTable.getDate() },
                        TableDefinitions.TESTTABLE, null));
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(2, result.get(0).size());
        this.printTableToLog(result);

        final List<HashMap<String, String>> result2 = conn.select(new SelectQuery<TableDefinitions>(
                new String[] { TestTable.getAge(), TestTable.getDate() }, TableDefinitions.TESTTABLE,
                TestTable.getFirstname() + "='" + USER3.getFirstname() + "'"));
        Assert.assertNotNull(result2);
        this.printTableToLog(result);
        Assert.assertEquals(1, result2.size());
        Assert.assertEquals(2, result2.get(0).size());
    }

    /**
     * Test implementation of "Select row From table [where x=y]" of fennec DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT08_SelectObject() throws SQLException {
        final DBConnector<?> conn = getDBConnector();

        final Object age = conn.selectSingleObject(new SelectQuery<TableDefinitions>(
                TestTable.getAge(), TableDefinitions.TESTTABLE, null));
        Assert.assertNotNull(age);
        LOG.debug(age.toString());

        final Object age2 = conn.selectSingleObject(new SelectQuery<TableDefinitions>(
                TestTable.getAge(), TableDefinitions.TESTTABLE, TestTable.getFirstname() +
                        "='" + USER3.getFirstname() + "'"));
        Assert.assertNotNull(age2);
        LOG.debug(age2.toString());
    }

    /**
     * Test implementation of "Select row From table [where x=y]" of fennec DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT09_SelectRow() throws SQLException {
        final DBConnector<?> conn = getDBConnector();

        List<String> userNames = conn.selectSingleColumn(new SelectQuery<TableDefinitions>(
                new String[] { TestTable.getUser(), TestTable.getFirstname() },
                TableDefinitions.TESTTABLE, null));
        Assert.assertTrue(userNames.contains(USER1.getUser()));
        Assert.assertTrue(userNames.contains(USER2.getUser()));
        Assert.assertTrue(userNames.contains(USER3.getUser()));
        Assert.assertFalse(userNames.contains(USER1.getFirstname()));
        LOG.debug("UserNames: " + userNames.toString());

        userNames = conn.selectSingleColumn(new SelectQuery<TableDefinitions>(
                new String[] { TestTable.getUser(), TestTable.getFirstname() },
                TableDefinitions.TESTTABLE,
                TestTable.getIdField() + "=1"));
        Assert.assertTrue(userNames.contains(USER1.getUser()));
        Assert.assertFalse(userNames.contains(USER2.getUser()));
        Assert.assertFalse(userNames.contains(USER3.getUser()));
        Assert.assertFalse(userNames.contains(USER1.getFirstname()));
        LOG.debug("UserNames: " + userNames.toString());
    }

    /**
     * Test implementation of "TRUNCATE table" of fennec DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT10_Truncate() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        final int result = conn.query(new TruncateQuery<TableDefinitions>(TableDefinitions.TESTTABLE));
        Assert.assertEquals(3, result);
        final List<HashMap<String, String>> out = conn.select(
                new SelectQuery<TableDefinitions>("*", TableDefinitions.TESTTABLE, ""));
        Assert.assertTrue(out.isEmpty());
    }

    /**
     * Test the keyword 'table' as tablename. No SQL syntax exception should be thrown.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT11_TruncateSQLKeyword() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        boolean exception = false;
        try {
            conn.query(new TruncateQuery<TableDefinitions>(new TestTable("table")));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("You have an error in your SQL syntax")) {
                exception = true;
            }
        }
        Assert.assertTrue(exception);
    }

    /**
     * Test implementation of "UPDATE table SET (col1=x,col2=y,...) WHERE s=t" of fennec DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT12_Update() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        final int result = conn.query(new UpdateQuery<TableDefinitions>(TableDefinitions.TESTTABLE,
                new String[] { TestTable.getFirstname() }, new String[] { "ursel" },
                TestTable.getUser() + "='" + USER1.getUser() + "'"));
        Assert.assertEquals(1, result);
        final List<String> out = conn.selectSingleColumn(new SelectQuery<TableDefinitions>(
                new String[] { TestTable.getFirstname() },
                TableDefinitions.TESTTABLE, ""));
        Assert.assertTrue(out.contains("ursel"));
    }

    /**
     * Test the SQL update command with SQL keywords for tablename, columnname and where clause. No SQL syntax exception
     * should ne thrown.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT13_UpdateSQLKeywords() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        boolean exception = false;

        // tablename = 'update'
        try {
            conn.query(new UpdateQuery<TableDefinitions>(new TestTable("update"),
                    new String[] { "firstname" },
                    new String[] { "horst" }, TestTable.getUser() + "='" + USER1.getUser() + "'"));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("You have an error in your SQL syntax")) {
                exception = true;
            }
        }

        // columnname = 'set'
        try {
            conn.query(new UpdateQuery<TableDefinitions>(TableDefinitions.TESTTABLE, new String[] { "set" },
                    new String[] { "horst" }, TestTable.getUser() + "='" + USER1.getUser() + "'"));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("You have an error in your SQL syntax")) {
                exception = true;
            }
        }

        // where clause = 'where'
        try {
            conn.query(new UpdateQuery<TableDefinitions>(TableDefinitions.TESTTABLE,
                    new String[] { "firstname" },
                    new String[] { "horst" }, "where ='" + USER1.getUser() + "'"));
        } catch (final SQLException e) {
            LOG.debug("Expected Exception thrown: " + e.toString());
            if (e.toString().contains("You have an error in your SQL syntax")) {
                exception = true;
            }
        }

        Assert.assertTrue(exception);
    }
}
