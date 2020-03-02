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

package eu.tsystems.mms.tic.testframework.dbconnector;

import eu.tsystems.mms.tic.testframework.dbconnector.connection.Driver;

/**
 * Class to wrap jdbc connection details.
 *
 * @param <T> Generic type to restrict connector to use only connections of the specified project.
 */
public class DBConnector<T extends ProjectConnections> extends DBMethods {

    /**
     * Constructor parametrized with connection profile.
     *
     * @param projectConnection Profile to use for connection.
     */
    public DBConnector(final T projectConnection) {
        setSelf(projectConnection.getDbConnection());
    }

    /**
     * Constructor parametrized with connection details.
     *
     * @param host Host to connect to.
     * @param port Port to use.
     * @param driver JDBC Driver to use.
     * @param schema Schema or table name to connect to.
     * @param username Username of DB connection.
     * @param password Password of DB connection.
     */
    public DBConnector(final String host, final String port, final Driver driver, final String schema,
                       final String username, final String password) {
        this.setHost(host);
        this.setPort(port);
        this.setDriver(driver);
        this.setUsername(username);
        this.setPassword(password);
        this.setSchema(schema);
    }
}
