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
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
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
        // Empty baseUrl of Chrome
        Assert.assertTrue(currentUrl.contains("data"), "Current URL is invalid - actual: " + currentUrl);
    }

    @Test
    public void testT03_EndPointCapabilities_WebDriverRequest() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");

        DesiredCapabilities caps = request.getDesiredCapabilities();
        caps.setCapability("enableVideo", true);
        caps.setCapability("enableVNC", true);

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);

        SessionContext sessionContext = WEB_DRIVER_MANAGER.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = sessionContext.getWebDriverRequest().getCapabilities();

        Assert.assertEquals(sessionCapabilities.get("enableVideo"), caps.getCapability("enableVideo"), "EndPoint Capability via WebDriverRequest is set");
        Assert.assertEquals(sessionCapabilities.get("enableVNC"), caps.getCapability("enableVNC"), "EndPoint Capability via WebDriverRequest is set");
    }

    @Test
    public void testT04_EndPointCapabilities_Global() {
        WEB_DRIVER_MANAGER.setGlobalCapability("t04Global", "yes");

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        WEB_DRIVER_MANAGER.removeGlobalCapability("t04Global");

        SessionContext sessionContext = WEB_DRIVER_MANAGER.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = sessionContext.getWebDriverRequest().getCapabilities();

        Assert.assertEquals(sessionCapabilities.get("t04Global"), "yes", "EndPoint Capability is set");
    }

    @Test
    public void test05_EndPointCapabilities_UserAgent() {
        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chromeHeadless,
                (ChromeConfig) options -> options.setCapability("t05UserAgent", "yesyes"));

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver();

        SessionContext sessionContext = WEB_DRIVER_MANAGER.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = sessionContext.getWebDriverRequest().getCapabilities();

        Assert.assertEquals(sessionCapabilities.get("t05UserAgent"), "yesyes", "EndPoint Capability is set");
    }

    @Test
    public void testT06_PlatformCaps() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setPlatformName(Platform.ANY.toString());
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);

        WebDriverRequest webDriverRequest = WEB_DRIVER_MANAGER.getSessionContext(driver).get().getWebDriverRequest();

        Assert.assertEquals(webDriverRequest.getCapabilities().get(CapabilityType.PLATFORM_NAME), Platform.ANY);
    }

    @Test
    public void testT07_OverwriteCaps() {
        Proxy agentProxy = new Proxy();
        agentProxy.setHttpProxy("my.agent.proxy:8080");
        Proxy requestProxy = new Proxy();
        requestProxy.setHttpProxy("my.request.proxy:8080");

        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chromeHeadless,
                (ChromeConfig) options -> {
                    options.setCapability("t07Overwrite", "agentCaps");
//                    options.setProxy(agentProxy);
                });

        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.getDesiredCapabilities().setCapability("t07Overwrite", "requestCaps");
//        request.getDesiredCapabilities().setCapability(CapabilityType.PROXY, requestProxy);

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);

        SessionContext sessionContext = WEB_DRIVER_MANAGER.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = sessionContext.getWebDriverRequest().getCapabilities();

        Assert.assertEquals(sessionCapabilities.get("t07Overwrite"), "requestCaps", "Request capability is set");
//        Assert.assertEquals(sessionCapabilities.get(CapabilityType.PROXY), requestProxy, "Request proxy is set");
    }

    @Test
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
