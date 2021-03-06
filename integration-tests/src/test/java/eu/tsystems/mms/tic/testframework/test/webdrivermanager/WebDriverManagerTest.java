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
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import java.util.Map;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

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

        final WebDriver exclusiveDriver = WebDriverManager.getWebDriver();
        final String sessionId = WebDriverManager.makeSessionExclusive(exclusiveDriver);

        Assert.assertNotNull(WebDriverSessionsManager.getSessionContext(exclusiveDriver).get());

        final WebDriver driver2 = WebDriverManager.getWebDriver("Session2");
        final WebDriver exclusiveDriverActual = WebDriverManager.getWebDriver(sessionId);

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

}
