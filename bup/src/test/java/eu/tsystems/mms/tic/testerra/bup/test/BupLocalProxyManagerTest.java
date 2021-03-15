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
package eu.tsystems.mms.tic.testerra.bup.test;

import com.browserup.bup.BrowserUpProxyServer;
import eu.tsystems.mms.tic.testerra.bup.BrowserUpLocalProxyManager;
import eu.tsystems.mms.tic.testerra.bup.BrowserUpNoFreePortException;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * test for {@link BrowserUpLocalProxyManager}
 * Date: 26.05.2020
 * Time: 11:36
 *
 * @author Eric Kubenka
 */
public class BupLocalProxyManagerTest extends TesterraTest {

    private static List<Integer> ports = new ArrayList<>();
    private static BrowserUpLocalProxyManager bupLocalManager;

    @BeforeMethod
    public void setUpPortPool() {

        ports.add(8090);
        ports.add(8091);
        ports.add(8092);
        ports.add(8093);
        ports.add(8094);
        ports.add(8095);

        bupLocalManager = new BrowserUpLocalProxyManager(ports);
    }

    @AfterMethod
    public void tearDownProxies() {

        bupLocalManager.stopAllServer();
    }

    @Test
    public void testT01_StartProxyOnFreePort() throws BrowserUpNoFreePortException {

        BrowserUpProxyServer browserUpProxyServer = new BrowserUpProxyServer();
        browserUpProxyServer = bupLocalManager.startServer(browserUpProxyServer);

        int port = browserUpProxyServer.getPort();
        Assert.assertTrue(ports.contains(port), "Port of range was used.");
    }

    @Test
    public void testT02_StartMultipleProxies() throws BrowserUpNoFreePortException {

        BrowserUpProxyServer bup1 = new BrowserUpProxyServer();
        BrowserUpProxyServer bup2 = new BrowserUpProxyServer();
        bup1 = bupLocalManager.startServer(bup1);
        bup2 = bupLocalManager.startServer(bup2);

        Assert.assertTrue(ports.contains(bup1.getPort()), "Port of range was used.");
        Assert.assertTrue(ports.contains(bup2.getPort()), "Port of range was used.");

        Assert.assertNotEquals(bup1.getPort(), bup2.getPort(), "Start on different ports.");
    }

    @Test(expectedExceptions = {BrowserUpNoFreePortException.class})
    public void testT03_StartProxiesUntilPoolEmpty() throws BrowserUpNoFreePortException {

        // fill complete port range.
        for (int i = 0; i < ports.size(); i++) {
            bupLocalManager.startServer(new BrowserUpProxyServer());
        }

        // add one more --> exception
        bupLocalManager.startServer(new BrowserUpProxyServer());
    }

    @Test
    public void testT04_StartAndStopServer() throws BrowserUpNoFreePortException {

        BrowserUpProxyServer bup1 = new BrowserUpProxyServer();
        bup1 = bupLocalManager.startServer(bup1);

        Assert.assertTrue(ports.contains(bup1.getPort()), "Port of range was used.");
        Assert.assertTrue(bup1.isStarted(), "Proxy started");
        Assert.assertFalse(bup1.isStopped(), "Proxy stopped");

        bupLocalManager.stopServer(bup1);
        Assert.assertTrue(bup1.isStopped(), "Proxy stopped");

        BrowserUpProxyServer bup2 = new BrowserUpProxyServer();
        bup2 = bupLocalManager.startServer(bup2);

        Assert.assertEquals(bup2.getPort(), bup1.getPort(), "Ports reused.");
    }

}
