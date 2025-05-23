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

import eu.tsystems.mms.tic.testframework.common.PropertyManagerProvider;
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.DefaultExecutionContextController;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.utils.CertUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerConfig;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.decorators.Decorated;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Tests for WebDriverManager
 * <p>
 * Date: 27.04.2020
 * Time: 06:49
 *
 * @author Eric Kubenka
 */
public class WebDriverManagerTest extends TesterraTest implements WebDriverManagerProvider, PropertyManagerProvider {

    private IExecutionContextController executionContextController = Testerra.getInjector().getInstance(IExecutionContextController.class);

    @Test
    public void testT01_isJavaScriptActivated() {

        final WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver();
        final boolean javaScriptActivated = WebDriverManager.isJavaScriptActivated(webDriver);

        Assert.assertTrue(javaScriptActivated, "JavaScript activated by default.");
    }

    @Test
    public void testT02_introduceOwnDriver() {
        String browser = IWebDriverManager.Properties.BROWSER.asString();

        // Exit options for testcase
        if (!browser.equalsIgnoreCase(Browsers.chrome) && !browser.equalsIgnoreCase(Browsers.chromeHeadless)) {
            throw new RuntimeException("This testcase only runs for chrome or chrome headless");
        }

        // create new options
        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");

        if (browser.equalsIgnoreCase(Browsers.chromeHeadless)) {
            chromeOptions.addArguments("--headless");
        }

        // create driver - window is open now
        final WebDriver driver = new ChromeDriver(chromeOptions);

        // assert that webdrivermanager does not know about this session.
        Assert.assertEquals(WebDriverSessionsManager.getWebDriversFromCurrentThread().count(), 0, "No WebDriver sessions should active in this thread.");
        Assert.assertTrue(executionContextController.getCurrentSessionContext().isEmpty(), "The current session context should empty");
        // introduce it.
        WebDriverManager.introduceWebDriver(driver);

        // assert that webdrivermanager does know about the session.
        Assert.assertEquals(WebDriverSessionsManager.getWebDriversFromCurrentThread().count(), 1, "One WebDriver session should active in this thread.");
        Assert.assertTrue(executionContextController.getCurrentSessionContext().isPresent(), "There should exist a current session context.");
    }

    @Test
    public void testT03_MakeSessionExclusive() {
        WebDriver exclusiveDriver = WEB_DRIVER_MANAGER.getWebDriver();
        Assert.assertTrue(executionContextController.getCurrentSessionContext().isPresent(), "There should exist a current session context.");

        String sessionId = WEB_DRIVER_MANAGER.makeExclusive(exclusiveDriver);

        Assert.assertTrue(executionContextController.getCurrentSessionContext().isEmpty(), "The current session context should be empty.");

        Assert.assertNotNull(WebDriverSessionsManager.getSessionContext(exclusiveDriver).get());

        WebDriver driver2 = WEB_DRIVER_MANAGER.getWebDriver("Session2");
        WebDriver exclusiveDriverActual = WEB_DRIVER_MANAGER.getWebDriver(sessionId);

        Assert.assertEquals(exclusiveDriver, exclusiveDriverActual, "Got the same WebDriver!");
        WEB_DRIVER_MANAGER.shutdownSession(sessionId);
    }

    private String t03aSessionId = "";

    @Test
    public void testT03a_SessionExclusivePrepareTest() {
        WebDriver exclusiveDriver = WEB_DRIVER_MANAGER.getWebDriver();
        t03aSessionId = WEB_DRIVER_MANAGER.makeExclusive(exclusiveDriver);
    }

    @Test(dependsOnMethods = "testT03a_SessionExclusivePrepareTest")
    public void testT03b_SessionExclusiveCheckLinkedSession() {
        Assert.assertEquals(this.readSessionContextFromMethodContext().count(), 0, "No session should link to current method context");

        WEB_DRIVER_MANAGER.getWebDriver(t03aSessionId);

        Assert.assertEquals(this.readSessionContextFromMethodContext().count(), 1, "One session should link to current method context");
        if (this.readSessionContextFromMethodContext()
                .map(SessionContext::getSessionKey)
                .noneMatch(t03aSessionId::equals)) {
            Assert.fail("Exclusive session not found in current method context");
        }
    }

    // Testcase is replaced by DesktopWebDriverFactoryTest.testT04_EndPointCapabilities_Global
    // Does not work with Selenium 4 (see custom caps)
    @Test(enabled = false)
    public void testT04_ManageGlobalCapabilities() {
        WEB_DRIVER_MANAGER.setGlobalCapability("foo", "bar");

        Map<String, Object> globalExtraCapabilities = WebDriverManager.getGlobalExtraCapabilities();
        Assert.assertTrue(globalExtraCapabilities.containsKey("foo"));
        Assert.assertEquals(globalExtraCapabilities.get("foo"), "bar");

        WebDriver driver = WebDriverManager.getWebDriver();

        WEB_DRIVER_MANAGER.removeGlobalCapability("foo");
        globalExtraCapabilities = WebDriverManager.getGlobalExtraCapabilities();
        Assert.assertFalse(globalExtraCapabilities.containsKey("foo"));
    }

    @Test
    public void testT05_ManageThreadCapabilities() {

//        WebDriverManager.addThreadCapability("foo", "bar");
//
//        Assert.assertTrue(WebDriverManager.getThreadCapabilities().containsKey("foo"));
//        Assert.assertEquals(WebDriverManager.getThreadCapabilities().get("foo"), "bar");
//
//        WebDriver driver = WebDriverManager.getWebDriver();
//        WebDriverManager.getThreadCapabilities().clear();
    }

    @Test(groups = "SEQUENTIAL_SINGLE")
    public void testT06_clonedWebDriverRequest() {
        final String sessionKey = "testT06";
        final String capKey = "MyCap";
        final String capVal = "myValue";

        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setSessionKey(sessionKey);
        DesiredCapabilities baseCaps = request.getDesiredCapabilities();
        Map<String, Object> customCaps = new HashMap<>();
        customCaps.put(capKey, capVal);
        baseCaps.setCapability("custom:caps", customCaps);

        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        SessionContext sessionContext = WEB_DRIVER_MANAGER.getSessionContext(webDriver).get();
        DesktopWebDriverRequest clonedRequest = (DesktopWebDriverRequest) sessionContext.getWebDriverRequest();

        // Test for cloned primitives
        Assert.assertEquals(sessionKey, clonedRequest.getSessionKey());
        clonedRequest.setSessionKey("NewSessionKey");
        Assert.assertNotEquals(sessionKey, clonedRequest.getSessionKey());

        // Test for not shallow copy of capabilities
        DesiredCapabilities clonedCaps = clonedRequest.getDesiredCapabilities();
        Map<String, Object> clonedCustomCaps = (Map<String, Object>) clonedCaps.getCapability("custom:caps");
        Assert.assertEquals(capVal, clonedCustomCaps.get(capKey));
        Assert.assertEquals(clonedCustomCaps.get(capKey), customCaps.get(capKey));

        clonedCustomCaps.put(capKey, "newValue");
        Assert.assertNotEquals(clonedCustomCaps.get(capKey), customCaps.get(capKey));
    }

    @Test
    public void testT07_WindowSize() {
        assertNewWebDriverWindowSize(new Dimension(1024, 768));

        String newScreenSize = "1280x1024";

        PROPERTY_MANAGER.setTestLocalProperty(DesktopWebDriverRequest.Properties.WINDOW_SIZE, newScreenSize);
        String property = PROPERTY_MANAGER.getProperty(DesktopWebDriverRequest.Properties.WINDOW_SIZE, PROPERTY_MANAGER.getProperty(DesktopWebDriverRequest.Properties.DISPLAY_RESOLUTION));
        Assert.assertEquals(property, newScreenSize);

        assertNewWebDriverWindowSize(new Dimension(1280, 1024));
    }

    @Test
    public void testT07a_WindowSizeRequest() {
        Dimension expected = new Dimension(1280, 1024);
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setWindowSize(expected);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        Dimension size = webDriver.manage().window().getSize();
        Assert.assertEquals(size.getWidth(), expected.getWidth());
        Assert.assertEquals(size.getHeight(), expected.getHeight());
    }

    @Test
    public void testT08_invalidWindowSize() {
        PROPERTY_MANAGER.setTestLocalProperty(DesktopWebDriverRequest.Properties.WINDOW_SIZE, "katze");
        String property = PROPERTY_MANAGER.getProperty(DesktopWebDriverRequest.Properties.WINDOW_SIZE);
        Assert.assertEquals(property, "katze");

        assertNewWebDriverWindowSize(this.getDefaultDimension());
    }

    @Test
    public void testT09_emptyWindowSize() {
        PROPERTY_MANAGER.setTestLocalProperty(DesktopWebDriverRequest.Properties.WINDOW_SIZE, "");
        String property = PROPERTY_MANAGER.getProperty(DesktopWebDriverRequest.Properties.WINDOW_SIZE);
        Assert.assertEquals(property, "");

        assertNewWebDriverWindowSize(this.getDefaultDimension());
    }

    private Dimension getDefaultDimension() {
        // Should '1920x1080'
        String defaultWindowSize = DesktopWebDriverRequest.Properties.WINDOW_SIZE.getDefault().toString();
        Pattern pattern = Pattern.compile("(\\d+)x(\\d+)");
        Matcher matcher = pattern.matcher(defaultWindowSize);
        if (matcher.find()) {
            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));
            return new Dimension(width, height);
        } else {
            return null;
        }
    }

    private void assertNewWebDriverWindowSize(Dimension expected) {
        WebDriverManagerConfig config = WebDriverManager.getConfig();
        config.reset();
        Assert.assertFalse(config.shouldMaximizeViewport());

        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        Dimension windowSize = request.getWindowSize();
        Assert.assertEquals(windowSize.getWidth(), expected.getWidth());
        Assert.assertEquals(windowSize.getHeight(), expected.getHeight());

        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        Dimension size = webDriver.manage().window().getSize();
        Assert.assertEquals(size.getWidth(), expected.getWidth());
        Assert.assertEquals(size.getHeight(), expected.getHeight());
        WEB_DRIVER_MANAGER.shutdownAllThreadSessions();
    }

    @Test
    public void testT10_acceptInsecureCertificates() {
        CertUtils certUtils = CertUtils.getInstance();
        certUtils.setTrustAllHosts(true);
        Assert.assertTrue(certUtils.isTrustAllHosts());

        DesktopWebDriverRequest request = new DesktopWebDriverRequest();

        WEB_DRIVER_MANAGER.getWebDriver(request);

        Assert.assertTrue(request.getDesiredCapabilities().acceptInsecureCerts());
    }

    @Test
    public void testT11_SessionLocale() {
        Locale defaultLocale = Locale.getDefault();
        Locale sessionLocale = Locale.KOREAN;

        Assert.assertNotEquals(defaultLocale, sessionLocale);

        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver();
        Assert.assertTrue(WebDriverManager.setSessionLocale(webDriver, sessionLocale));

        Assert.assertEquals(WebDriverManager.getSessionLocale(webDriver).orElse(null), sessionLocale);
    }

    private String reusedSessionId;
    private final String sessionKey = "reuse";

    @Test
    public void testT12_ReuseSession1() {
        DesktopWebDriverRequest desktopWebDriverRequest = new DesktopWebDriverRequest();
        desktopWebDriverRequest.setShutdownAfterTest(false);
        desktopWebDriverRequest.setSessionKey(sessionKey);
        WEB_DRIVER_MANAGER.getWebDriver(desktopWebDriverRequest);
        new DefaultExecutionContextController().getCurrentSessionContext().ifPresent(sessionContext -> reusedSessionId = sessionContext.getRemoteSessionId().get());
    }

    @Test(dependsOnMethods = "testT12_ReuseSession1")
    public void testT13_ResuseSession2() {
        DefaultExecutionContextController executionContextController = new DefaultExecutionContextController();
        WEB_DRIVER_MANAGER.getWebDriver(sessionKey);

        Assert.assertEquals(this.reusedSessionId, executionContextController.getCurrentSessionContext().get().getRemoteSessionId().get());
        Optional<SessionContext> foundSessionContext = this.readSessionContextFromMethodContext().filter(
                sessionContext -> sessionContext.getSessionKey().equals(sessionKey)).findFirst();
        Assert.assertTrue(foundSessionContext.isPresent(), "Method context should contain the reused session context");
        Assert.assertEquals(reusedSessionId, foundSessionContext.get().getRemoteSessionId().get(),
                "Session ID of session context should the same of session context of method `testT12_ReuseSession1`");

        // Second access to 'reuse' session has no impact on methodContext
        WEB_DRIVER_MANAGER.getWebDriver(sessionKey);

        // Create a second session 'default'
        WEB_DRIVER_MANAGER.getWebDriver();
        Assert.assertEquals(this.readSessionContextFromMethodContext().count(), 2, "Current method context should have 2 sessions.");

        WEB_DRIVER_MANAGER.shutdownAllThreadSessions();
    }

    @Test
    public void testT14_UnwrapFromDecorated() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chromeHeadless);
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);
        Assert.assertTrue(driver instanceof Decorated);

        Optional<ChromeDriver> chromeDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(driver, ChromeDriver.class);
        Assert.assertTrue(chromeDriver.isPresent());
    }

    @Test
    public void testT15_UnwrapWrongTypeFromDecorated() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chromeHeadless);
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);
        Assert.assertTrue(driver instanceof Decorated);

        Optional<FirefoxDriver> chromeDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(driver, FirefoxDriver.class);
        Assert.assertTrue(chromeDriver.isEmpty());
    }

    private Stream<SessionContext> readSessionContextFromMethodContext() {
        return executionContextController.getCurrentMethodContext().get().readSessionContexts();
    }
}
