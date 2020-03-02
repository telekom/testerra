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
package eu.tsystems.mms.tic.testframework.dbconnector.test.connectors;

import eu.tsystems.mms.tic.testframework.dbconnector.DBConnector;
import eu.tsystems.mms.tic.testframework.dbconnector.ProjectConnections;
import eu.tsystems.mms.tic.testframework.dbconnector.connection.Driver;

/**
 * Class holding DBConnections for TesterraTest-Project.
 * <p>
 * Created with IntelliJ IDEA. User: pele Date: 18.05.12 Time: 12:43
 */
public class TestDBConnections extends ProjectConnections {

    /**
     * Connection to MySQL DB -  Used by DBConnector tests.
     */
    public static final TestDBConnections DB1 =
            new TestDBConnections(
                    new DBConnector<TestDBConnections>("localhost", "3306", Driver.MYSQL,
                            "Testerradbconnector", "testerra", "your_passwd"));

    /**
     * Connection to H2 DB that stores Results.
     */
    public static final TestDBConnections H2RESULTS =
            new TestDBConnections(
                    new DBConnector<TestDBConnections>(System.getProperty("user.dir") + "/testresultsDB",
                            null, Driver.H2, null, "", ""));

    /**
     * Default constructor for DBConnections.
     *
     * @param dbConnection DBConnector object to be used for connection.
     */
    TestDBConnections(final DBConnector<?> dbConnection) {
        super(dbConnection);
    }
}
