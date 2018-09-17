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

import eu.tsystems.mms.tic.testframework.dbconnector.connection.Driver;

/**
 * This abstract class is used to parametrize the DBConnector to allow only project specific DB connections. Therefor create your
 * project specific project connections class which inherits from this one. In that class you can define your connections which are
 * then allowed to be used if you parametrize the generic DBConnector with your project specific connections class.
 * 
 * This is an optional feature.
 */
public class ProjectConnections {

    /**
     * This is an example for a project specific connection profile.
     */
    public static final ProjectConnections DEFAULT = new ProjectConnections(
            new DBConnector<ProjectConnections>("localhost", "123", Driver.valueOf("MYSQL"), null, "bla", "blubb")) {
    };

    /**
     * DBConnector holding connection details.
     */
    private final DBConnector<?> dbConnection;

    /**
     * Default constuctor.
     * 
     * @param dbConnection
     *            DBConnector holding connection details.
     */
    public ProjectConnections(final DBConnector<?> dbConnection) {
        this.dbConnection = dbConnection;
    }

    /**
     * Getter.
     * 
     * @return DBConnector.
     */
    public DBConnector<?> getDbConnection() {
        return dbConnection;
    }
}

/**
 * Sample class to show usage of ProjectConnections for ow profiles.
 * 
 * @author sepr
 */
class MyConnections extends ProjectConnections {

    /**
     * Example connection profile.
     */
    public static final MyConnections MYDB = new MyConnections(
            new DBConnector<ProjectConnections>("myHost", "myPort", Driver.MYSQL, "mySchema", "myUser", "myPass"));

    /**
     * Default constructor.
     * 
     * @param dbConnection
     *            DBConnector holding connection details.
     */
    MyConnections(final DBConnector<?> dbConnection) {
        super(dbConnection);
    }
}
