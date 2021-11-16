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

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.utils.ProxyUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverProxyUtils;
import java.util.Properties;
import org.openqa.selenium.Proxy;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Tests class for {@link ProxyUtils} and {@link WebDriverProxyUtils}
 * <p>
 * Date: 27.03.2020
 * Time: 07:31
 *
 * @author Eric Kubenka
 */
public class ProxyUtilsTest extends TesterraTest {

    private static String PROXY_HOST_HTTP = "dummy-proxy-http.de";
    private static String PROXY_HOST_HTTPS = "dummy-proxy-https.de";
    private static String PROXY_PORT_HTTP = "8080";
    private static String PROXY_PORT_HTTPS = "8443";

    private static String BACKUP_PROXY_HTTP_HOST = "";
    private static String BACKUP_PROXY_HTTP_PORT = "";

    private static String BACKUP_PROXY_HTTPS_HOST = "";
    private static String BACKUP_PROXY_HTTPS_PORT = "";

    @BeforeMethod()
    public void provideProxySettings() {

        BACKUP_PROXY_HTTP_HOST = System.getProperty("http.proxyHost", "");
        BACKUP_PROXY_HTTP_PORT = System.getProperty("http.proxyPort", "");

        BACKUP_PROXY_HTTPS_HOST = System.getProperty("https.proxyHost", "");
        BACKUP_PROXY_HTTPS_PORT = System.getProperty("https.proxyPort", "");

        Properties testLocalProperties = PropertyManager.getTestLocalProperties();

        testLocalProperties.setProperty("http.proxyHost", PROXY_HOST_HTTP);
        testLocalProperties.setProperty("http.proxyPort", PROXY_PORT_HTTP);

        testLocalProperties.setProperty("https.proxyHost", PROXY_HOST_HTTPS);
        testLocalProperties.setProperty("https.proxyPort", PROXY_PORT_HTTPS);

    }

    @Test
    public void testT01_getHttpProxyString() {
        final String expectedUrlString = PROXY_HOST_HTTP + ":" + PROXY_PORT_HTTP;

        final URL actualProxyUrl = ProxyUtils.getSystemHttpProxyUrl();
        Assert.assertNotNull(actualProxyUrl, "Proxy url generated!");

        WebDriverProxyUtils utils = new WebDriverProxyUtils();
        Proxy proxy = utils.createSocksProxyFromUrl(actualProxyUrl);
        Assert.assertEquals(proxy.getHttpProxy(), expectedUrlString, "Generated proxy string equals.");
    }

    @Test
    public void testT02_getHttpsProxyString() {
        provideProxySettings();
        final String expectedUrlString = PROXY_HOST_HTTPS + ":" + PROXY_PORT_HTTPS;

        WebDriverProxyUtils utils = new WebDriverProxyUtils();
        Proxy proxy = utils.getDefaultSocksProxy();
        Assert.assertEquals(proxy.getSslProxy(), expectedUrlString, "Generated proxy string equals.");
    }

    @Test
    public void testT03_getHttpProxyUrl() {

        final String expectedUrlString = "http://" + PROXY_HOST_HTTP + ":" + PROXY_PORT_HTTP;

        final URL actualProxyUrl = ProxyUtils.getSystemHttpProxyUrl();
        Assert.assertNotNull(actualProxyUrl, "Proxy url generated!");

        final String actualProxyString = actualProxyUrl.toString();
        Assert.assertEquals(actualProxyString, expectedUrlString, "Generated proxy string equals.");
    }

    @Test
    public void testT04_getHttpsProxyUrl() {

        final String expectedUrlString = "http://" + PROXY_HOST_HTTPS + ":" + PROXY_PORT_HTTPS;

        final URL actualProxyUrl = ProxyUtils.getSystemHttpsProxyUrl();
        Assert.assertNotNull(actualProxyUrl, "Proxy url generated!");

        final String actualProxyString = actualProxyUrl.toString();
        Assert.assertEquals(actualProxyString, expectedUrlString, "Generated proxy string equals.");
    }

    @Test
    public void test05_createProxyFromUrl() throws MalformedURLException {
        WebDriverProxyUtils utils = new WebDriverProxyUtils();
        String proxyString = "http://proxyUser:secretPassword@my-proxy:3128";
        Proxy proxy = utils.createSocksProxyFromUrl(new URL(proxyString));
        Assert.assertEquals(proxy.getHttpProxy(), "my-proxy:3128");
        Assert.assertEquals(proxy.getSocksUsername(), "proxyUser");
        Assert.assertEquals(proxy.getSocksPassword(), "secretPassword");
    }

    @Test
    public void test06_createDefaultProxyFromUrl() {
        WebDriverProxyUtils utils = new WebDriverProxyUtils();
        Proxy proxy = utils.getDefaultHttpProxy();

        Assert.assertEquals(proxy.getHttpProxy(), PROXY_HOST_HTTPS + ":" + PROXY_PORT_HTTPS);
        Assert.assertEquals(proxy.getSslProxy(), PROXY_HOST_HTTPS + ":" + PROXY_PORT_HTTPS);
        Assert.assertNull(proxy.getFtpProxy());
        Assert.assertNull(proxy.getSocksProxy());
    }
}
