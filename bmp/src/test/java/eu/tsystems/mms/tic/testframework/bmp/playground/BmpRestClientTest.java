/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.bmp.playground;

import eu.tsystems.mms.tic.testframework.bmp.BmpRestClient;
import eu.tsystems.mms.tic.testframework.utils.ProxyUtils;
import java.net.MalformedURLException;
import java.net.URL;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

/**
 * BmpRestClientTest
 * <p>
 * Date: 04.11.2019
 * Time: 08:44
 *
 * @author Eric Kubenka
 * @implNote Please start your local instance of https://github.com/lightbody/browsermob-proxy on your machine or override API_URL before testing
 */
public class BmpRestClientTest {

    private static URL API_URL;

    static {
        try {
            API_URL = new URL("http://localhost:8080");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private ThreadLocal<BmpRestClient> bmpRestClientNewThreadLocal = new ThreadLocal<>();

    @AfterMethod(alwaysRun = true)
    public void tearDownThreadedBmp() {

        final BmpRestClient bmpRestClient = bmpRestClientNewThreadLocal.get();
        bmpRestClientNewThreadLocal.remove();

        if (bmpRestClient != null) {
            bmpRestClient.stopServer();
        }
    }

    @Test
    public void testSetupValid() {

        // arrange / act / assert
        final BmpRestClient bmpRestClient = new BmpRestClient(API_URL);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testSetHeaderWithoutStartedProxyFails() {

        // arrange
        final BmpRestClient bmpRestClient = new BmpRestClient(API_URL);

        // act
        bmpRestClient.setHeader("headerkey", "value");

        // assert
        // done in annotation.
    }

    @Test
    public void testStartProxyOnRandomPort() {

        // arrange
        final BmpRestClient bmpRestClient = new BmpRestClient(API_URL);
        bmpRestClientNewThreadLocal.set(bmpRestClient);

        // act
        final int proxyPort = bmpRestClient.startServer();

        // assert
        Assert.assertTrue(proxyPort > 0, "Proxy started.");
    }

    @Test
    public void testStartProxyOnGivenPort() {

        final BmpRestClient bmpRestClient = new BmpRestClient(API_URL);
        bmpRestClientNewThreadLocal.set(bmpRestClient);

        final int portToUse = 10000;

        // act
        final int proxyPort = bmpRestClient.startServer(portToUse);

        // assert
        Assert.assertEquals(proxyPort, portToUse, "Proxy started.");
    }

    @Test
    public void testStartProxyOnGivenPortAlreadyUsed() {

        // arrange
        final BmpRestClient bmpRestClient = new BmpRestClient(API_URL);
        bmpRestClientNewThreadLocal.set(bmpRestClient);

        final int portToUse = 10000;
        bmpRestClient.startServer(portToUse);

        // act
        final BmpRestClient bmpRestClient1 = new BmpRestClient(API_URL);
        final int proxyPort = bmpRestClient1.startServer(portToUse);

        // assert
        Assert.assertEquals(proxyPort, portToUse, "Proxy started.");
        Assert.assertTrue(bmpRestClient.isProxyRunningAtPort(proxyPort));
        Assert.assertTrue(bmpRestClient1.isProxyRunningAtPort(proxyPort));
    }

    @Test
    public void testSetHeaderWithStartedProxy() {

        // Arrange
        final BmpRestClient bmpRestClient = new BmpRestClient(API_URL);
        bmpRestClientNewThreadLocal.set(bmpRestClient);

        // Act
        bmpRestClient.startServer();
        bmpRestClient.setHeader("headerkey", "value");

        // Assert
        // implictly done
    }

    @Test
    public void testSetUpstreamProxy() {

        final BmpRestClient bmpRestClient = new BmpRestClient(API_URL);
        bmpRestClientNewThreadLocal.set(bmpRestClient);

        bmpRestClient.setUpstreamProxy(ProxyUtils.getSystemHttpProxyUrl());
        final int currentPort = bmpRestClient.startServer();

        Assert.assertTrue(currentPort > 0, "Proxy started with upstream.");
    }

}
