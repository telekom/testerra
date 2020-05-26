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
 *     Eric Kubenka
 */
package eu.tsystems.mms.tic.testerra.bup.playground;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.tsystems.mms.tic.testerra.bup.BrowserUpClient;
import eu.tsystems.mms.tic.testerra.bup.BupRemoteProxyServer;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

/**
 * Tests for {@link BrowserUpClient}
 * <p>
 * Date:25.05.2020
 * Time:10:43
 *
 * @author Eric Kubenka
 */

public class BupClientPlaygroundTest extends TesterraTest {

    private static final String LOCAL_PROXY_FOR_TEST = "http://localhost:8080";

    @AfterMethod()
    public void tearDownAllProxies() throws MalformedURLException {

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpClient browserUpClient = new BrowserUpClient(apiBaseUrl);

        for (final Integer proxyPort : browserUpClient.getProxies()) {
            final BupRemoteProxyServer bupToStop = new BupRemoteProxyServer();
            bupToStop.setPort(proxyPort);
            browserUpClient.stopServer(bupToStop);
        }
    }

    @Test
    public void testT01_GetCurrentProxyServersWhenNoProxyIsRunning() throws MalformedURLException {

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpClient browserUpClient = new BrowserUpClient(apiBaseUrl);

        final List<Integer> servers = browserUpClient.getProxies();
        Assert.assertEquals(servers.size(), 0);
    }

    @Test
    public void testT02_StartProxyServerOnRandomPortAndVerifyRunning() throws IOException {

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpClient browserUpClient = new BrowserUpClient(apiBaseUrl);

        final BupRemoteProxyServer bupRemoteProxyServer = browserUpClient.startServer();
        Assert.assertNotNull(bupRemoteProxyServer, "Browser Up Proxy started.");
        Assert.assertEquals(bupRemoteProxyServer.getPort().intValue(), 8081, "Created proxy on first free port.");

        final boolean running = browserUpClient.isRunning(bupRemoteProxyServer);
        Assert.assertTrue(running, "Browser Up Proxy is running.");
    }

    @Test
    public void testT03_StartProxyServerOnDesiredPortAndVerifyRunning() throws IOException {

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpClient browserUpClient = new BrowserUpClient(apiBaseUrl);

        BupRemoteProxyServer bupRemoteProxyServer = new BupRemoteProxyServer();
        bupRemoteProxyServer.setPort(8088);

        bupRemoteProxyServer = browserUpClient.startServer(bupRemoteProxyServer);
        Assert.assertNotNull(bupRemoteProxyServer, "Proxy object generated.");

        Assert.assertEquals(bupRemoteProxyServer.getPort().intValue(), 8088, "Port equals desired.");

        final boolean running = browserUpClient.isRunning(bupRemoteProxyServer);
        Assert.assertTrue(running, "Browser Up Proxy is running.");
    }

    @Test
    public void testT04_StopProxyServer() throws IOException {

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpClient browserUpClient = new BrowserUpClient(apiBaseUrl);

        final BupRemoteProxyServer bup1 = browserUpClient.startServer();
        Assert.assertNotNull(bup1, "Browser Up Proxy Session 1 started");


        final BupRemoteProxyServer bup2 = browserUpClient.startServer();
        Assert.assertNotNull(bup2, "Browser Up Proxy Session 2 started");

        Assert.assertNotEquals(bup1.getPort(), bup2.getPort(), "Ports dont match.");

        browserUpClient.stopServer(bup2);
        Assert.assertTrue(browserUpClient.isRunning(bup1), "Browser Up Proxy Session 1 running.");
        Assert.assertFalse(browserUpClient.isRunning(bup2), "Browser Up Proxy Session 2 running.");
    }

    @Test
    public void testT05_AddHeader() throws MalformedURLException {

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpClient browserUpClient = new BrowserUpClient(apiBaseUrl);

        final BupRemoteProxyServer bup1 = browserUpClient.startServer();
        Assert.assertNotNull(bup1, "Browser Up Proxy Session 1 started");
        Assert.assertTrue(browserUpClient.addHeader(bup1, "foo", "bar"), "Headers set");
    }

    @Test
    public void testT06_Capture() throws MalformedURLException {

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpClient browserUpClient = new BrowserUpClient(apiBaseUrl);

        final BupRemoteProxyServer bup1 = browserUpClient.startServer();

        // create capture
        browserUpClient.startCapture(bup1, null, false, false);

        // stop capture
        final JsonObject jsonHarResponse = (JsonObject) browserUpClient.stopCapture(bup1);
        final JsonObject jsonLog = jsonHarResponse.getAsJsonObject("log");
        final JsonArray jsonPages = jsonLog.getAsJsonArray("pages");

        Assert.assertEquals(jsonPages.size(), 1, "Pages created on har.");
    }

    @Test
    public void testT07_AddHostMapping() throws MalformedURLException {


        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpClient browserUpClient = new BrowserUpClient(apiBaseUrl);

        final BupRemoteProxyServer bup1 = browserUpClient.startServer();

        final HashMap<String, String> hostNameMap = new HashMap<>();
        hostNameMap.put("example.com", "127.0.0.1");
        boolean b = browserUpClient.setHostMapping(bup1, hostNameMap);

        Assert.assertTrue(b, "Host Mapping set.");
    }

    @Test
    public void testT08_IsProxyRunningExpectedFalse() throws IOException {

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpClient browserUpClient = new BrowserUpClient(apiBaseUrl);

        BupRemoteProxyServer bupRemoteProxyServer = new BupRemoteProxyServer();
        bupRemoteProxyServer.setPort(8081);

        final boolean running = browserUpClient.isRunning(bupRemoteProxyServer);
        Assert.assertFalse(running, "Browser Up Session running on port 8081");
    }

    @Test
    public void testT09_AddNewPageWhileCapturing() throws MalformedURLException {

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpClient browserUpClient = new BrowserUpClient(apiBaseUrl);

        final BupRemoteProxyServer bup1 = browserUpClient.startServer();

        // create capture
        browserUpClient.startCapture(bup1, null, false, false);

        boolean b = browserUpClient.addNewPage(bup1, "Page 2");
        Assert.assertTrue(b, "New HAR page created.");

        final JsonObject jsonHarResponse = (JsonObject) browserUpClient.stopCapture(bup1);
        final JsonObject jsonLog = jsonHarResponse.getAsJsonObject("log");
        final JsonArray jsonPages = jsonLog.getAsJsonArray("pages");

        Assert.assertEquals(jsonPages.size(), 2, "Pages created on har.");
    }

    @Test
    public void testT10_AddBasicAuth() throws MalformedURLException {

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpClient browserUpClient = new BrowserUpClient(apiBaseUrl);

        final BupRemoteProxyServer bup1 = browserUpClient.startServer();

        boolean b = browserUpClient.setBasicAuth(bup1, "example.com", "test", "test");
        Assert.assertTrue(b, "Basic auth set.");
    }

}
