/*
 * Created on 04.11.2019
 */
package eu.tsystems.mms.tic.testframework.bmp.playground;

import eu.tsystems.mms.tic.testframework.bmp.BmpRestClient;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.utils.ProxyUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

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

    @Test(expectedExceptions = TesterraRuntimeException.class)
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
