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
import org.h2.jdbc.JdbcSQLException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

/**
 * Integration Tests for TesterraDBConnector.
 * 
 * @author sepr
 */
public class TestDBConnector extends AbstractDBConnectorTest {

    /**
     * Test implementation of "DELETE FROM table [WHERE x=y]" of tt. DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT01_Delete() throws SQLException {
        final DBConnector<?> conn = getDBConnector();

        final List<HashMap<String, String>> res =
                conn.select(new SelectQuery<>("*", TableDefinitions.TESTTABLE, null));

        printTableToLog(res);
        final int result = conn.query(new DeleteQuery<>(TableDefinitions.TESTTABLE,
                TestTable.getUser() + "='" + USER1.getUser() + "'"));
        final List<HashMap<String, String>> resAfter =
                conn.select(new SelectQuery<>("*", TableDefinitions.TESTTABLE, null));
        printTableToLog(resAfter);
    }

    /**
     * Test implementation of "Select row From table [where x=y]" of tt. DBConnector, where 'row' contains BLOBs.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test(expectedExceptions = JdbcSQLException.class, expectedExceptionsMessageRegExp = ".*LARGEOBJECTS.*")
    public void testT02_GetBlob() throws SQLException {
        // Use test.props from resources
        final long fileSize = 1007;
        final DBConnector<?> conn = getDBConnector();
        final Blob blob = conn.selectBlob(
                new SelectQuery<>(LobTable.getBlob(), TableDefinitions.LOBTABLE, null));
        Assert.assertEquals(fileSize, blob.length());

        final Blob blob2 = conn.selectBlob(
                new SelectQuery<>(LobTable.getBlob(), TableDefinitions.LOBTABLE, LobTable
                        .getIdField() + "=1"));
        Assert.assertEquals(fileSize, blob2.length());
    }

    /**
     * Test implementation of "Select row From table [where x=y]" of tt. DBConnector, where 'row' contains CLOBs.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test(expectedExceptions = JdbcSQLException.class, expectedExceptionsMessageRegExp = ".*LARGEOBJECTS.*")
    public void testT03_GetClob() throws SQLException {
        final long clobSize = 14688;
        final DBConnector<?> conn = getDBConnector();

        final Clob clob = conn.selectClob(
                new SelectQuery<>(LobTable.getClob(), TableDefinitions.LOBTABLE, null));
        Assert.assertEquals(clobSize, clob.length());

        final Clob clob2 = conn.selectClob(
                new SelectQuery<>(LobTable.getClob(), TableDefinitions.LOBTABLE, LobTable
                        .getIdField() + "=1"));
        Assert.assertEquals(clobSize, clob2.length());
    }

    /**
     * Test implementation of "INSERT INTO table [(col1,col2,...)] VALUES(...)" of tt. DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT04_Insert() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        int result = conn.query(new InsertQuery<>(TableDefinitions.TESTTABLE, TableDefinitions.TESTTABLE.getIdentifiers(),
                new String[] {
                        "4", "user04", "Max", "Mustermann", "25", USER1.getDate().toString(),
                        USER1.getCreationTime().toString()
                }));
        result = conn.query(new InsertQuery<>(TableDefinitions.TESTTABLE,
                new String[] { TestTable.getAge(), TestTable.getLastname() },
                new String[] { "40", "Hans" }));
    }

    /**
     * Test implementation of "Select * From table [where x=y]" of tt. DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT06_SelectAll() throws SQLException {
        final DBConnector<?> conn = getDBConnector();

        final List<HashMap<String, String>> res =
                conn.select(new SelectQuery<>("*", TableDefinitions.TESTTABLE, null));
        printTableToLog(res);

        final List<HashMap<String, String>> res2 =
                conn.select(new SelectQuery<>("*", TableDefinitions.TESTTABLE,
                        TestTable.getAge() + "< 100"));
        printTableToLog(res2);
    }

    /**
     * Test implementation of "Select col From table [where x=y]" of tt. DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT07_SelectCol() throws SQLException {
        final DBConnector<?> conn = getDBConnector();

        final List<HashMap<String, String>> result = conn
                .select(new SelectQuery<>(
                        new String[] { TestTable.getAge(), TestTable.getDate() },
                        TableDefinitions.TESTTABLE, null));
        this.printTableToLog(result);

        final List<HashMap<String, String>> result2 = conn.select(new SelectQuery<>(
                new String[] { TestTable.getAge(), TestTable.getDate() }, TableDefinitions.TESTTABLE,
                TestTable.getFirstname() + "='" + USER3.getFirstname() + "'"));
        this.printTableToLog(result);
    }

    /**
     * Test implementation of "Select row From table [where x=y]" of tt. DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT08_SelectObject() throws SQLException {
        final DBConnector<?> conn = getDBConnector();

        final Object age = conn.selectSingleObject(new SelectQuery<>(
                TestTable.getAge(), TableDefinitions.TESTTABLE, null));

        final Object age2 = conn.selectSingleObject(new SelectQuery<>(
                TestTable.getAge(), TableDefinitions.TESTTABLE, TestTable.getFirstname() +
                        "='" + USER3.getFirstname() + "'"));
    }

    /**
     * Test implementation of "Select row From table [where x=y]" of tt. DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT09_SelectRow() throws SQLException {
        final DBConnector<?> conn = getDBConnector();

        List<String> userNames = conn.selectSingleColumn(new SelectQuery<>(
                new String[] { TestTable.getUser(), TestTable.getFirstname() },
                TableDefinitions.TESTTABLE, null));
        userNames = conn.selectSingleColumn(new SelectQuery<>(
                new String[] { TestTable.getUser(), TestTable.getFirstname() },
                TableDefinitions.TESTTABLE,
                TestTable.getId() + "=1"));
    }

    /**
     * Test implementation of "TRUNCATE table" of tt. DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT10_Truncate() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        final int result = conn.query(new TruncateQuery<>(TableDefinitions.TESTTABLE));
        final List<HashMap<String, String>> out = conn.select(
                new SelectQuery<>("*", TableDefinitions.TESTTABLE, ""));
    }

    /**
     * Test implementation of "UPDATE table SET (col1=x,col2=y,...) WHERE s=t" of tt. DBConnector.
     * 
     * @throws SQLException Error while querying the db.
     */
    @Test
    public void testT12_Update() throws SQLException {
        final DBConnector<?> conn = getDBConnector();
        final int result = conn.query(new UpdateQuery<>(TableDefinitions.TESTTABLE,
                new String[] { TestTable.getFirstname() }, new String[] { "ursel" },
                TestTable.getUser() + "='" + USER1.getUser() + "'"));
        final List<String> out = conn.selectSingleColumn(new SelectQuery<>(
                new String[] { TestTable.getFirstname() },
                TableDefinitions.TESTTABLE, ""));
    }
}
