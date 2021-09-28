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
package eu.tsystems.mms.tic.testframework.test.webdrivermanager;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextController;
import eu.tsystems.mms.tic.testframework.utils.CertUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import java.awt.Desktop;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.Optional;

/**
 * Tests for WebDriverManager
 * <p>
 * Date: 27.04.2020
 * Time: 06:49
 *
 * @author Eric Kubenka
 */
public class WebDriverManagerTest extends AbstractWebDriverTest {

    @Test
    public void testT01_isJavaScriptActivated() {

        final WebDriver webDriver = WebDriverManager.getWebDriver();
        final boolean javaScriptActivated = WebDriverManager.isJavaScriptActivated(webDriver);

        Assert.assertTrue(javaScriptActivated, "JavaScript activated by default.");
    }

    @Test
    public void testT02_introduceOwnDriver() {

        final String webDriverMode = PropertyManager.getProperty(TesterraProperties.WEBDRIVERMODE, "local");
        final String browser = PropertyManager.getProperty(TesterraProperties.BROWSER, Browsers.chromeHeadless);

        // Exit options for testcase
        if (!browser.equalsIgnoreCase(Browsers.chrome) && !browser.equalsIgnoreCase(Browsers.chromeHeadless)) {
            throw new RuntimeException("This testcase only runs for chrome or chrome headless");
        }

        if (!webDriverMode.equalsIgnoreCase("local")) {
            throw new RuntimeException("This testcase only runs for local executions.");
        }

        // create new options
        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");

        if (browser.equalsIgnoreCase(Browsers.chromeHeadless)) {
            chromeOptions.setHeadless(true);
        }

        // create driver - window is open now
        final WebDriver driver = new ChromeDriver(chromeOptions);

        // assert that webdrivermanager does not know about this session.
        Assert.assertFalse(WebDriverManager.hasSessionsActiveInThisThread(), "WebDriver Session active in this thread.");

        // introduce it.
        WebDriverManager.introduceWebDriver(driver);

        // assert that webdrivermanager does know about the session.
        Assert.assertTrue(WebDriverManager.hasSessionsActiveInThisThread(), "WebDriver Session active in this thread.");
    }

    @Test
    public void testT03_MakeSessionExclusive() {
        WebDriver exclusiveDriver = WebDriverManager.getWebDriver();
        Assert.assertEquals(WebDriverSessionsManager.getWebDriversFromCurrentThread().count(), 1);

        String sessionId = WebDriverManager.makeSessionExclusive(exclusiveDriver);

        Assert.assertEquals(WebDriverSessionsManager.getWebDriversFromCurrentThread().count(), 0);

        Assert.assertNotNull(WebDriverSessionsManager.getSessionContext(exclusiveDriver).get());

        WebDriver driver2 = WebDriverManager.getWebDriver("Session2");
        WebDriver exclusiveDriverActual = WebDriverManager.getWebDriver(sessionId);

        Assert.assertEquals(exclusiveDriver, exclusiveDriverActual, "Got the same WebDriver!");
        WebDriverManager.shutdownExclusiveSession(sessionId);
    }

    @Test
    public void testT04_ManageGlobalCapabilities() {

        WebDriverManager.setGlobalExtraCapability("foo", "bar");

        Map<String, Object> globalExtraCapabilities = WebDriverManager.getGlobalExtraCapabilities();
        Assert.assertTrue(globalExtraCapabilities.containsKey("foo"));
        Assert.assertEquals(globalExtraCapabilities.get("foo"), "bar");

        WebDriver driver = WebDriverManager.getWebDriver();

        WebDriverManager.removeGlobalExtraCapability("foo");
        globalExtraCapabilities = WebDriverManager.getGlobalExtraCapabilities();
        Assert.assertFalse(globalExtraCapabilities.containsKey("foo"));
    }

    @Test
    public void testT05_ManageThreadCapabilities() {

        WebDriverManager.addThreadCapability("foo", "bar");

        Assert.assertTrue(WebDriverManager.getThreadCapabilities().containsKey("foo"));
        Assert.assertEquals(WebDriverManager.getThreadCapabilities().get("foo"), "bar");

        WebDriver driver = WebDriverManager.getWebDriver();
        WebDriverManager.getThreadCapabilities().clear();
    }

    @Test
    public void testT06_clonedWebDriverRequest() {
        final String sessionKey = "testT06";
        final String capKey = "MyCap";
        final String capVal = "myValue";

        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setSessionKey(sessionKey);
        DesiredCapabilities baseCaps = request.getDesiredCapabilities();
        baseCaps.setCapability(capKey, capVal);

        WebDriver webDriver = WebDriverManager.getWebDriver(request);
        SessionContext sessionContext = WebDriverSessionsManager.getSessionContext(webDriver).get();
        DesktopWebDriverRequest clonedRequest = (DesktopWebDriverRequest) sessionContext.getWebDriverRequest();

        // Test for cloned primitives
        Assert.assertEquals(sessionKey, clonedRequest.getSessionKey());
        clonedRequest.setSessionKey("NewSessionKey");
        Assert.assertNotEquals(sessionKey, clonedRequest.getSessionKey());

        // Test for not shallow copy of capabilities
        DesiredCapabilities clonedCaps = clonedRequest.getDesiredCapabilities();
        Assert.assertEquals(capVal, clonedCaps.getCapability(capKey));
        Assert.assertEquals(clonedCaps.getCapability(capKey), baseCaps.getCapability(capKey));

        clonedCaps.setCapability(capKey, "newValue");
        Assert.assertNotEquals(clonedCaps.getCapability(capKey), baseCaps.getCapability(capKey));
    }

    @Test
    public void test_WindowSize() {
        assertNewWebDriverWindowSize(new Dimension(800, 600));
        PropertyManager.getThreadLocalProperties().setProperty(TesterraProperties.WINDOW_SIZE, "katze");
        assertNewWebDriverWindowSize(new Dimension(1920, 1080));
        PropertyManager.getThreadLocalProperties().setProperty(TesterraProperties.WINDOW_SIZE, "1024x768");
        assertNewWebDriverWindowSize(new Dimension(1024, 768));

        PropertyManager.clearThreadlocalProperties();
    }

    private void assertNewWebDriverWindowSize(Dimension expected) {
        WebDriver webDriver = WebDriverManager.getWebDriver();
        Dimension size = webDriver.manage().window().getSize();
        Assert.assertEquals(size.getWidth(), expected.getWidth());
        Assert.assertEquals(size.getHeight(), expected.getHeight());
        WebDriverManager.shutdown();
    }

    @Test
    public void test_acceptInSecureCertificates() {
        CertUtils certUtils = CertUtils.getInstance();
        certUtils.setTrustAllHosts(true);
        Assert.assertTrue(certUtils.isTrustAllHosts());

        DesktopWebDriverRequest request = new DesktopWebDriverRequest();

        WebDriverManager.getWebDriver(request);

        Assert.assertTrue(request.getDesiredCapabilities().acceptInsecureCerts());
    }
}
