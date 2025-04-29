/*
 * Testerra
 *
 * (C) 2021, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */

package eu.tsystems.mms.tic.testframework.test.webdrivermanager;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.testing.TesterraTest;
import eu.tsystems.mms.tic.testframework.testing.UiElementFinderFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.useragents.FirefoxConfig;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DesktopWebDriverFactoryTest extends TesterraTest implements WebDriverManagerProvider, UiElementFinderFactoryProvider {

    @Test
    public void testT01_BaseURL() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("google"), "Current URL contains google - actual: " + currentUrl);
    }

    @Test
    public void testT02_BaseURL_NotSet() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);
        String currentUrl = driver.getCurrentUrl();
        // Empty baseUrl
        String browser = IWebDriverManager.Properties.BROWSER.asString();
        String expectedURL = browser.equals(Browsers.firefox) ? "about:blank" : "data";
        Assert.assertTrue(currentUrl.contains(expectedURL), "Current URL is invalid - actual: " + currentUrl);
    }

    @Test
    public void testT03_EndPointCapabilities_WebDriverRequest() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");

        DesiredCapabilities caps = request.getDesiredCapabilities();
        Map<String, Object> selenoidCaps = new HashMap<>();
        selenoidCaps.put("enableVideo", true);
        selenoidCaps.put("enableVNC", true);
        caps.setCapability("selenoid:options", selenoidCaps);

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);

        SessionContext sessionContext = WEB_DRIVER_MANAGER.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = (Map<String, Object>) sessionContext.getWebDriverRequest().getCapabilities().getCapability("selenoid:options");

        Assert.assertEquals(sessionCapabilities.get("enableVideo"), selenoidCaps.get("enableVideo"), "EndPoint Capability via WebDriverRequest is set");
        Assert.assertEquals(sessionCapabilities.get("enableVNC"), selenoidCaps.get("enableVNC"), "EndPoint Capability via WebDriverRequest is set");
    }

    // Global caps! -> needs to be independent of other tests
    @Test(groups = "SEQUENTIAL_SINGLE")
    public void testT04_EndPointCapabilities_Global() {
        Map<String, Object> customCaps = new HashMap<>();
        customCaps.put("t04Global", "yes");
        WEB_DRIVER_MANAGER.setGlobalCapability("custom:caps", customCaps);

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        WEB_DRIVER_MANAGER.removeGlobalCapability("custom:caps");

        SessionContext sessionContext = WEB_DRIVER_MANAGER.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = (Map<String, Object>) sessionContext.getWebDriverRequest().getCapabilities().asMap().get("custom:caps");

        Assert.assertEquals(sessionCapabilities.get("t04Global"), "yes", "EndPoint Capability is set");
    }

    // Global caps! -> needs to be independent of other tests
    @Test(groups = "SEQUENTIAL_SINGLE")
    public void test05_EndPointCapabilities_UserAgent() {
        Map<String, Object> customCaps = new HashMap<>();
        customCaps.put("t05UserAgent", "yesyes");
        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chromeHeadless,
                (ChromeConfig) options -> options.setCapability("custom:caps", customCaps));
        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chrome,
                (ChromeConfig) options -> options.setCapability("custom:caps", customCaps));
        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.firefox,
                (FirefoxConfig) options -> options.setCapability("custom:caps", customCaps));

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        SessionContext sessionContext = WEB_DRIVER_MANAGER.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = (Map<String, Object>) sessionContext.getWebDriverRequest().getCapabilities().asMap().get("custom:caps");

        Assert.assertEquals(sessionCapabilities.get("t05UserAgent"), "yesyes", "EndPoint Capability is set");
    }

    // TODO Can only run with Remote Selenium server
    @Test(enabled = false)
    public void testT06_PlatformCaps() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setPlatformName(Platform.WINDOWS.toString());
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);

        WebDriverRequest webDriverRequest = WEB_DRIVER_MANAGER.getSessionContext(driver).get().getWebDriverRequest();

        Assert.assertEquals(webDriverRequest.getCapabilities().asMap().get(CapabilityType.PLATFORM_NAME), Platform.WINDOWS);
    }

    // Global caps!! -> needs to be independent of other tests
    @Test(groups = "SEQUENTIAL_SINGLE")
    public void testT07_OverwriteCaps() {
        Map<String, Object> customCaps = new HashMap<>();
        customCaps.put("t07Overwrite", "agentCaps");

        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chromeHeadless,
                (ChromeConfig) options -> {
                    options.setCapability("custom:caps", customCaps);
                });

        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        Map<String, Object> customCaps2 = new HashMap<>();
        customCaps2.put("t07Overwrite", "requestCaps");
        request.getDesiredCapabilities().setCapability("custom:caps", customCaps2);

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);

        SessionContext sessionContext = WEB_DRIVER_MANAGER.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = (Map<String, Object>) sessionContext.getWebDriverRequest().getCapabilities().asMap().get("custom:caps");

        Assert.assertEquals(sessionCapabilities.get("t07Overwrite"), "requestCaps", "Request capability is set");
    }

    // TODO Does not work in Chrome Headless
    @Test(enabled = false)
    public void testT08_ChromeExtensions() {

        File chromeExtensionFile = new File(
                Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("testfiles/Simple_Translate_2.8.1.0.crx")).getFile());

        ChromeOptions options = new ChromeOptions();
        options.addExtensions(chromeExtensionFile);
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.getDesiredCapabilities().merge(options);

        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        webDriver.get("chrome://extensions-internals/");
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);
        UiElement chromeExtensionJson = uiElementFinder.find(By.xpath("//pre"));
        String content = chromeExtensionJson.waitFor().text().getActual();
        Assert.assertTrue(content.contains("Simple Translate"));

    }

}
