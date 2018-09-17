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
package eu.tsystems.mms.tic.testframework.dbconnector.test.connectors;

import eu.tsystems.mms.tic.testframework.dbconnector.DBConnector;
import eu.tsystems.mms.tic.testframework.dbconnector.ProjectConnections;
import eu.tsystems.mms.tic.testframework.dbconnector.connection.Driver;

/**
 * Class holding DBConnections for fennecTest-Project.
 * 
 * Created with IntelliJ IDEA. User: pele Date: 18.05.12 Time: 12:43
 */
public class fennecTestDBConnections extends ProjectConnections {

    /**
     * Connection to MySQL DB on 192.168.60.239. Used by DBConnector tests.
     */
    public static final fennecTestDBConnections DB1 =
            new fennecTestDBConnections(
                    new DBConnector<fennecTestDBConnections>("localhost", "3306", Driver.MYSQL,
                            "fennecdbconnector", "fennec", "mas4test"));

    /**
     * Connection to H2 DB that stores fennec Results.
     */
    public static final fennecTestDBConnections H2RESULTS =
            new fennecTestDBConnections(
                    new DBConnector<fennecTestDBConnections>(System.getProperty("user.dir") + "/testresultsDB",
                            null, Driver.H2, null, "", ""));

    /**
     * Default constructor for DBConnections.
     * 
     * @param dbConnection
     *            DBConnector object to be used for connection.
     */
    fennecTestDBConnections(final DBConnector<?> dbConnection) {
        super(dbConnection);
    }
}
