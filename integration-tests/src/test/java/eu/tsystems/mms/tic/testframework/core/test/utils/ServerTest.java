/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
 *
 * Deutsche Telekom AG and all other contributors /
 * copyright owners license this file to you under the Apache
 * License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.core.test.Server;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.BindException;

public class ServerTest {

    private Server server;

    @Test
    public void test_StartServer() throws Exception {
        server = new Server();
        server.start(1234);
    }

    @Test(dependsOnMethods = "test_StartServer")
    public void test_AlreadyStartedServer() throws Exception{
        Server newServer = new Server();
        try {
            newServer.start(1234);
        } catch (BindException e) {
            Assert.assertEquals(newServer.getPort(), 1234);
        }
    }

    @Test
    public void test_NewRandomPort() throws Exception {
        Server newServer = new Server();
        newServer.start();
        Assert.assertTrue(newServer.getPort() > 0);
    }
}
