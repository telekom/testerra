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

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import eu.tsystems.mms.tic.testerra.bup.BrowserUpRemoteProxyManager;
import eu.tsystems.mms.tic.testerra.bup.BrowserUpRemoteProxyServer;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.binaryEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.removeStub;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

/**
 * Tests for {@link BrowserUpRemoteProxyManager}
 * <p>
 * Date:25.05.2020
 * Time:10:43
 *
 * @author Eric Kubenka
 */

public class BupRemoteProxyManagerTest extends TesterraTest {

    private static final int WIREMOCK_SERVER_PORT = 81;
    private static final String WIREMOCK_SERVER_HOST = "localhost";

    private static final String LOCAL_PROXY_FOR_TEST = "http://" + WIREMOCK_SERVER_HOST + ":" + WIREMOCK_SERVER_PORT;

    private static WireMockServer WIREMOCK_SERVER = null;

    private enum Response {

        GET_PROXY_EMPTY("{\"proxyList\":[]}"),
        GET_PROXY_8081("{\"proxyList\":[{\"port\":8081}]}"),
        GET_PROXY_8088("{\"proxyList\":[{\"port\":8088}]}"),
        POST_PROXY_8081("{\"port\":8081}"),
        POST_PROXY_8088("{\"port\":8088}"),
        DELETE_PROXY_8088(""),
        PUT_HAR_8081(""),
        POST_HOSTS_8081(""),
        GET_HAR_8081("{\"log\":{\"version\":\"1.1\",\"creator\":{\"name\":\"BrowserUp Proxy\",\"version\":\"${project.version}\"},\"pages\":[{\"startedDateTime\":\"2020-05-26T10:46:40.901+0200\",\"id\":\"Page 0\",\"title\":\"Page 0\",\"pageTimings\":{\"onContentLoad\":-1,\"onLoad\":-1}}],\"entries\":[]}}"),
        GET_HAR_8081_TWO_PAGES("{\"log\":{\"version\":\"1.1\",\"creator\":{\"name\":\"BrowserUp Proxy\",\"version\":\"${project.version}\"},\"pages\":[{\"startedDateTime\":\"2020-05-26T10:56:30.077+0200\",\"id\":\"Page 0\",\"title\":\"Page 0\",\"pageTimings\":{\"onContentLoad\":-1,\"onLoad\":9}},{\"startedDateTime\":\"2020-05-26T10:56:30.088+0200\",\"id\":\"Page 2\",\"title\":\"Page 2\",\"pageTimings\":{\"onContentLoad\":-1,\"onLoad\":-1}}],\"entries\":[]}}"),
        POST_HEADERS_8081(""),
        PUT_HAR_PAGEREF_8081(""),
        POST_AUTH_BASIC_8081("");

        final String response;

        Response(String response) {
            this.response = response;
        }

        public String getResponse() {
            return response;
        }
    }

    @BeforeMethod
    private void setupWireMockServer() {

        WireMock.configureFor(WIREMOCK_SERVER_HOST, WIREMOCK_SERVER_PORT);
        WIREMOCK_SERVER = new WireMockServer(new WireMockConfiguration().port(WIREMOCK_SERVER_PORT));
        WIREMOCK_SERVER.start();
    }

    @AfterMethod()
    public void tearDownWireMockServer() {

        WireMock.reset();
        WIREMOCK_SERVER.stop();
    }

    @Test
    public void testT01_GetCurrentProxyServersWhenNoProxyIsRunning() throws MalformedURLException {

        stubFor(get(urlEqualTo("/proxy"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.GET_PROXY_EMPTY.getResponse())));

        // ### Test Start ###

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpRemoteProxyManager browserUpRemoteProxyManager = new BrowserUpRemoteProxyManager(apiBaseUrl);

        final List<Integer> servers = browserUpRemoteProxyManager.getProxies();
        Assert.assertEquals(servers.size(), 0, "Servers empty after start");
    }

    @Test
    public void testT02_StartProxyServerOnRandomPortAndVerifyRunning() throws IOException {

        stubFor(post(urlMatching("/proxy\\?.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.POST_PROXY_8081.getResponse())));

        stubFor(get(urlEqualTo("/proxy"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.GET_PROXY_8081.getResponse())));

        // ### Test Start ###

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpRemoteProxyManager browserUpRemoteProxyManager = new BrowserUpRemoteProxyManager(apiBaseUrl);

        final BrowserUpRemoteProxyServer browserUpRemoteProxyServer = browserUpRemoteProxyManager.startServer();
        Assert.assertNotNull(browserUpRemoteProxyServer, "BrowserUp Proxy started.");
        Assert.assertEquals(browserUpRemoteProxyServer.getPort().intValue(), 8081, "Created proxy on first free port.");

        final boolean running = browserUpRemoteProxyManager.isRunning(browserUpRemoteProxyServer);
        Assert.assertTrue(running, "BrowserUp Proxy is running.");
    }

    @Test
    public void testT03_StartProxyServerOnDesiredPortAndVerifyRunning() throws IOException {

        stubFor(post(urlMatching("/proxy\\?port=8088.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.POST_PROXY_8088.getResponse())));

        final StubMapping emptyListStub = stubFor(get(urlEqualTo("/proxy"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.GET_PROXY_EMPTY.getResponse())));

        // ### Test Start ###

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpRemoteProxyManager browserUpRemoteProxyManager = new BrowserUpRemoteProxyManager(apiBaseUrl);

        BrowserUpRemoteProxyServer browserUpRemoteProxyServer = new BrowserUpRemoteProxyServer();
        browserUpRemoteProxyServer.setPort(8088);

        browserUpRemoteProxyServer = browserUpRemoteProxyManager.startServer(browserUpRemoteProxyServer);
        Assert.assertNotNull(browserUpRemoteProxyServer, "Proxy object generated.");

        Assert.assertEquals(browserUpRemoteProxyServer.getPort().intValue(), 8088, "Port equals desired.");

        // ### Change stubbing ###
        removeStub(emptyListStub);
        stubFor(get(urlEqualTo("/proxy"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.GET_PROXY_8088.getResponse())));

        final boolean running = browserUpRemoteProxyManager.isRunning(browserUpRemoteProxyServer);
        Assert.assertTrue(running, "BrowserUp Proxy is running.");
    }

    @Test
    public void testT04_StopProxyServer() throws IOException {

        stubFor(delete(urlEqualTo("/proxy/8088"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.DELETE_PROXY_8088.getResponse())));

        // ### Test Start ###
        BrowserUpRemoteProxyServer browserUpRemoteProxyServer = new BrowserUpRemoteProxyServer();
        browserUpRemoteProxyServer.setPort(8088);

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpRemoteProxyManager browserUpRemoteProxyManager = new BrowserUpRemoteProxyManager(apiBaseUrl);
        browserUpRemoteProxyManager.stopServer(browserUpRemoteProxyServer);
    }

    @Test
    public void testT05_AddHeader() throws MalformedURLException {

        byte[] bytes = "{\"foo\":\"bar\"}".getBytes(Charset.defaultCharset());

        stubFor(post(urlEqualTo("/proxy/8081/headers"))
                .withRequestBody(binaryEqualTo(bytes))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.POST_HEADERS_8081.getResponse())));

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpRemoteProxyManager browserUpRemoteProxyManager = new BrowserUpRemoteProxyManager(apiBaseUrl);

        final BrowserUpRemoteProxyServer bup1 = new BrowserUpRemoteProxyServer();
        bup1.setPort(8081);

        Assert.assertTrue(browserUpRemoteProxyManager.addHeader(bup1, "foo", "bar"), "Headers set");
    }

    @Test
    public void testT06_Capture() throws MalformedURLException {

        final BrowserUpRemoteProxyServer bup1 = new BrowserUpRemoteProxyServer();
        bup1.setPort(8081);

        stubFor(put(urlEqualTo("/proxy/8081/har"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.PUT_HAR_8081.getResponse())));

        stubFor(get(urlEqualTo("/proxy/8081/har"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.GET_HAR_8081.getResponse())));


        // create capture
        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpRemoteProxyManager browserUpRemoteProxyManager = new BrowserUpRemoteProxyManager(apiBaseUrl);
        browserUpRemoteProxyManager.startCapture(bup1, null, false, false);

        // stop capture
        final JsonObject jsonHarResponse = (JsonObject) browserUpRemoteProxyManager.stopCapture(bup1);
        final JsonObject jsonLog = jsonHarResponse.getAsJsonObject("log");
        final JsonArray jsonPages = jsonLog.getAsJsonArray("pages");

        Assert.assertEquals(jsonPages.size(), 1, "Pages created on har.");
    }

    @Test
    public void testT07_AddHostMapping() throws MalformedURLException {

        final String expectedContent = "{\"example.com\":\"127.0.0.1\"}";

        stubFor(post(urlEqualTo("/proxy/8081/hosts"))
                .withRequestBody(binaryEqualTo(expectedContent.getBytes()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.POST_HOSTS_8081.getResponse())));


        final BrowserUpRemoteProxyServer bup1 = new BrowserUpRemoteProxyServer();
        bup1.setPort(8081);

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpRemoteProxyManager browserUpRemoteProxyManager = new BrowserUpRemoteProxyManager(apiBaseUrl);

        final HashMap<String, String> hostNameMap = new HashMap<>();
        hostNameMap.put("example.com", "127.0.0.1");

        boolean b = browserUpRemoteProxyManager.setHostMapping(bup1, hostNameMap);
        Assert.assertTrue(b, "Host Mapping set.");
    }

    @Test
    public void testT08_IsProxyRunningExpectedFalse() throws IOException {

        stubFor(get(urlEqualTo("/proxy"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.GET_PROXY_8088.getResponse())));

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpRemoteProxyManager browserUpRemoteProxyManager = new BrowserUpRemoteProxyManager(apiBaseUrl);

        BrowserUpRemoteProxyServer browserUpRemoteProxyServer = new BrowserUpRemoteProxyServer();
        browserUpRemoteProxyServer.setPort(8081);

        final boolean running = browserUpRemoteProxyManager.isRunning(browserUpRemoteProxyServer);
        Assert.assertFalse(running, "BrowserUp Session running on port 8081");
    }

    @Test
    public void testT09_AddNewPageWhileCapturing() throws MalformedURLException {

        stubFor(put(urlEqualTo("/proxy/8081/har/pageRef?pageRef=Page+2"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.PUT_HAR_PAGEREF_8081.getResponse())));

        stubFor(get(urlEqualTo("/proxy/8081/har"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.GET_HAR_8081_TWO_PAGES.getResponse())));

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpRemoteProxyManager browserUpRemoteProxyManager = new BrowserUpRemoteProxyManager(apiBaseUrl);

        BrowserUpRemoteProxyServer bup1 = new BrowserUpRemoteProxyServer();
        bup1.setPort(8081);

        boolean b = browserUpRemoteProxyManager.addNewPage(bup1, "Page 2");
        Assert.assertTrue(b, "New HAR page created.");

        final JsonObject jsonHarResponse = (JsonObject) browserUpRemoteProxyManager.stopCapture(bup1);
        final JsonObject jsonLog = jsonHarResponse.getAsJsonObject("log");
        final JsonArray jsonPages = jsonLog.getAsJsonArray("pages");

        Assert.assertEquals(jsonPages.size(), 2, "Pages created on har.");
    }

    @Test
    public void testT10_AddBasicAuth() throws MalformedURLException {

        final String expectedContent = "{\"username\":\"test\",\"password\":\"test\"}";

        stubFor(post(urlEqualTo("/proxy/8081/auth/basic/example.com"))
                .withRequestBody(binaryEqualTo(expectedContent.getBytes()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.POST_HOSTS_8081.getResponse())));


        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpRemoteProxyManager browserUpRemoteProxyManager = new BrowserUpRemoteProxyManager(apiBaseUrl);

        final BrowserUpRemoteProxyServer bup1 = new BrowserUpRemoteProxyServer();
        bup1.setPort(8081);

        boolean b = browserUpRemoteProxyManager.setBasicAuth(bup1, "example.com", "test", "test");
        Assert.assertTrue(b, "Basic auth set.");
    }

    @Test
    public void testT11_AddUpstreamProxy() throws MalformedURLException {

        stubFor(post(urlMatching("/proxy.*httpProxy=proxy.example%3A8080"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.POST_PROXY_8081.getResponse())));

        BrowserUpRemoteProxyServer bup1 = new BrowserUpRemoteProxyServer();
        bup1.setUpstreamProxy(new URL("http://proxy.example:8080"));

        final URL apiBaseUrl = new URL(LOCAL_PROXY_FOR_TEST);
        final BrowserUpRemoteProxyManager browserUpRemoteProxyManager = new BrowserUpRemoteProxyManager(apiBaseUrl);
        bup1 = browserUpRemoteProxyManager.startServer(bup1);
    }

}
