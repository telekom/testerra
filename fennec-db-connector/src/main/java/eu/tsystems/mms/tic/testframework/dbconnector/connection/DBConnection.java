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
package eu.tsystems.mms.tic.testframework.dbconnector.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.fennecProperties;
import eu.tsystems.mms.tic.testframework.exceptions.fennecSystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for JDBC connection handling.
 * 
 * @author sepr
 */
public final class DBConnection {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBConnection.class);
    private static final int TIMEOUT_IN_MS = PropertyManager.getIntProperty(fennecProperties.fennec_DB_TIMEOUT, 10000);
    private static final int EXECUTOR_SERVICE_THREADCOUNT = 5;

    /**
     * Hide default constructor.
     */
    private DBConnection() {
    }

    /**
     * Creates a connection to the DB with the given attributes. The connection needs to be closed manually.
     * 
     * @param host
     *            Host where db lies (most likely web address).
     * @param port
     *            Port where db is accessable.
     * @param driver
     *            JDBC Driver to use.
     * @param schema
     *            (ORACLE) (DB2 = databaseName) (MYSQL = schema/dbName)
     * @param username
     *            DB Username to use for login.
     * @param password
     *            DB password to use for login.
     * @return JDBC Connection object.
     * @throws SQLException
     *          thrown if there are problems building the SQL connection.
     * @throws ClassNotFoundException
     *          thrown if class of database driver is not found.
     */
    public static Connection createConnection(final String host, final String port, final Driver driver,
                                              final String schema, final String username, final String password)
            throws SQLException, ClassNotFoundException {
        return pGetConnection(host, port, driver, schema, username, password);
    }
    
    /**
     * Creates a connection to the DB with the given attributes. The connection needs to be closed manually.
     * 
     * @param host
     *            Host where db lies (most likely web address).
     * @param port
     *            Port where db is accessable.
     * @param driver
     *            JDBC Driver to use.
     * @param schema
     *            (ORACLE) (DB2 = databaseName) (MYSQL = schema/dbName)
     * @param username
     *            DB Username to use for login.
     * @param password
     *            DB password to use for login.
     * @return JDBC Connection object.
     * @throws SQLException
     *          thrown if there are problems building the SQL connection.
     * @throws ClassNotFoundException
     *          thrown if class of database driver is not found.
     */
    private static Connection pGetConnection(final String host, final String port, final Driver driver,
            final String schema, final String username, final String password)
            throws SQLException, ClassNotFoundException {
        Connection connection = null;

        String msg = "Opening DB connection: " + username + "@";
        String url;

        switch (driver) {
            case MYSQL: {
                Class.forName("com.mysql.jdbc.Driver");
                url = "jdbc:mysql://" + host + ":" + port
                        + "/" + schema;
                LOGGER.info(msg + url);
            }
                break;

            case ORACLE: {
                Class.forName("oracle.jdbc.OracleDriver");
                url = "jdbc:oracle:thin:@" + host + ":" + port + ":" + schema;
                LOGGER.info(msg + url);
            }
                break;

            case DB2: {
                Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
                url = "jdbc:db2:" + schema;
                LOGGER.info(msg + url);
            }
                break;

            case H2: {
                Class.forName("org.h2.Driver");
                url = "jdbc:h2:" + host;
                LOGGER.info(msg + url);
            }
                break;

            default:
                throw new fennecSystemException("Unknown JDBC Driver was used. Connection could not be established: " +
                        driver.name());
        }

        // open connection
        Properties props = new Properties();
        props.setProperty("user", username);
        props.setProperty("password", password);
        props.setProperty("connectTimeout", "" + TIMEOUT_IN_MS);
        props.setProperty("socketTimeout", "" + TIMEOUT_IN_MS);
        DriverManager.setLoginTimeout(TIMEOUT_IN_MS);
        connection = DriverManager.getConnection(url, props);

// the following does not work an results in error (AbstractMethodError)
//        ExecutorService executorService = Executors.newFixedThreadPool(EXECUTOR_SERVICE_THREADCOUNT);
//        connection.setNetworkTimeout(executorService, TIMEOUT_IN_MS);

        return connection;
    }
}
