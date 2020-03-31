/*
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
package eu.tsystems.mms.tic.testframework.dbconnector;

import eu.tsystems.mms.tic.testframework.dbconnector.connection.DBConnection;
import eu.tsystems.mms.tic.testframework.dbconnector.connection.Driver;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstract class to handle JDBC connections.
 */
public abstract class AbstractConnection {

    /** JDBC Connection object. */
    private Connection connection;
    /** Boolean to indicate if opening and closing should be handled automatically. */
    private static boolean automaticConnectionHandling = true;
    /** DBConnector object that holds connection details. */
    private DBConnector<?> self;

    /** Host of DB connection. */
    private String host;
    /** Port of DB connection. */
    private String port;
    /** Driver to use for DB connection. */
    private Driver driver;
    /** Username of DB connection. */
    private String username;
    /** Password of DB connection. */
    private String password;
    /** Schema of DB connection. */
    private String schema;

    /**
     * Open the JDBC connection with the details specified in this class (either DBConnector object or single
     * properties.
     *
     * @return DBConnector object with details that have been used to connect.
     * @throws SQLException .
     * @throws ClassNotFoundException .
     */
    public DBConnector<?> open() throws SQLException {
        try {
            return this.pOpen();
        } catch (ClassNotFoundException e) {
            throw new TesterraRuntimeException(e);
        }
    }

    /**
     * Closes the JDBC connection.
     *
     * @throws SQLException Exception while closing the DBConnection.
     */
    public void close() throws SQLException {
        Connection connection = getConnection();
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Getter.
     *
     * @return automaticConnectionHandling
     */
    public Boolean isAutomaticConnectionHandling() {
        return automaticConnectionHandling;
    }

    /**
     * Setter.
     *
     * @param automaticConnectionHandling value.
     */
    public static void setAutomaticConnectionHandling(final boolean automaticConnectionHandling) {
        AbstractConnection.automaticConnectionHandling = automaticConnectionHandling;
    }

    /**
     * gets the schema
     *
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * sets the schema
     *
     * @param schema the schema to set
     */
    public void setSchema(final String schema) {
        this.schema = schema;
    }

    /**
     * gets the username
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * sets the username
     *
     * @param username the username to set
     */
    public void setUsername(final String username) {
        this.username = username;
    }

    /**
     * gets the port
     *
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * sets the port
     *
     * @param port the port to set
     */
    public void setPort(final String port) {
        this.port = port;
    }

    /**
     * gets the host
     *
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * sets the host
     *
     * @param host the host to set
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * gets the password
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * sets the password
     *
     * @param password the password to set
     */
    public void setPassword(final String password) {
        this.password = password;
    }

    /**
     * gets the driver
     *
     * @return the driver
     */
    public Driver getDriver() {
        return driver;
    }

    /**
     * sets the driver
     *
     * @param driver the driver to set
     */
    public void setDriver(final Driver driver) {
        this.driver = driver;
    }

    /**
     * gets the self of the DB connector
     *
     * @return the self
     */
    public DBConnector<?> getSelf() {
        return self;
    }

    /**
     * sets the self of the DB connector
     *
     * @param self the self to set
     */
    public void setSelf(final DBConnector<?> self) {
        this.self = self;
    }

    /**
     * gets connection
     *
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * sets connection
     *
     * @param connection the connection to set
     */
    public void setConnection(final Connection connection) {
        this.connection = connection;
    }

    /**
     * Open the JDBC connection with the details specified in this class (either DBConnector object or single
     * properties.
     *
     * @return DBConnector object with details that have been used to connect.
     * @throws SQLException .
     * @throws ClassNotFoundException .
     */
    private DBConnector<?> pOpen() throws SQLException, ClassNotFoundException {
        DBConnector<?> con = getSelf();
        if (con != null) {
            Connection connection = DBConnection.createConnection(con.getHost(), con.getPort(), con.getDriver(),
                    con.getSchema(), con.getUsername(), con.getPassword());
            setConnection(connection);
        } else {
            Connection connection = DBConnection.createConnection(getHost(), getPort(), getDriver(), getSchema(), getUsername(),
                    getPassword());
            setConnection(connection);
            setSelf(new DBConnector<ProjectConnections>(getHost(), getPort(), getDriver(), getSchema(), getUsername(),
                    getPassword()));
        }
        return con;
    }
}
