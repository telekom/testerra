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

package eu.tsystems.mms.tic.testframework.test.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.RESTUtils;
import java.net.MalformedURLException;
import java.net.URL;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;


/**
 * Tests for RestUtils
 * <p>
 * Date: 29.04.2020
 * Time: 15:16
 *
 * @author Eric Kubenka
 */
public class RestUtilsTest extends TesterraTest implements Loggable {


    private static final int WIREMOCK_SERVER_PORT = 81;
    private static final String WIREMOCK_SERVER_HOST = "localhost";
    private static WireMockServer WIREMOCK_SERVER = null;

    private enum Response {

        SUCCESS_STATUS_200("{\"status\": 200}"),
        SUCCESS_POST_WITH_DATA("{\"status\": 200, \"withParam\": true}");

        final String response;

        Response(String response) {
            this.response = response;
        }

        public String getResponse() {
            return response;
        }
    }

    @BeforeClass
    public void setupWireMockServer() {

        WireMock.configureFor("localhost", WIREMOCK_SERVER_PORT);
        WIREMOCK_SERVER = new WireMockServer(new WireMockConfiguration().port(WIREMOCK_SERVER_PORT));
        WIREMOCK_SERVER.start();

        this.registerStubs();
    }

    @AfterClass
    public void tearDownWireMockServer() {

        // Do some stuff
        WireMock.reset();

        // Finish doing stuff
        WIREMOCK_SERVER.stop();
    }

    private void registerStubs() {

        stubFor(get(urlEqualTo("/resource"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.SUCCESS_STATUS_200.getResponse())));

        stubFor(post(urlEqualTo("/resource"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.SUCCESS_STATUS_200.getResponse())));

        stubFor(post(urlEqualTo("/resource"))
                .withQueryParam("param1", equalTo("value1"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/json")
                        .withBody(Response.SUCCESS_POST_WITH_DATA.getResponse())));
    }

    private URL getUrl() throws MalformedURLException {
        return new URL("http://" + WIREMOCK_SERVER_HOST + ":" + WIREMOCK_SERVER_PORT + "/resource");
    }

    @Test
    public void testT01_GetRequest() throws MalformedURLException {

        final String response = RESTUtils.requestGET(getUrl().toString());
        Assert.assertEquals(response, Response.SUCCESS_STATUS_200.getResponse());
    }

    @Test
    public void testT02_PostRequest() throws MalformedURLException {

        final String response = RESTUtils.requestPOST(getUrl().toString());
        Assert.assertEquals(response, Response.SUCCESS_STATUS_200.getResponse());
    }

    @Test(enabled = false)
    public void testT03_PostRequestWithParameter() throws MalformedURLException {


        final String response = RESTUtils.requestPOST(getUrl().toString(), "param1=value1");
        Assert.assertEquals(response, Response.SUCCESS_POST_WITH_DATA.getResponse());
    }

}
