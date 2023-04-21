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
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
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
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Objects;

public class DesktopWebDriverFactoryTest extends TesterraTest implements WebDriverManagerProvider, UiElementFinderFactoryProvider {

    @Test
    public void testT01_BaseURL() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");
        //request.webDriverMode = WebDriverMode.local;
        //request.browser = Browsers.phantomjs;

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("google"), "Current URL contains google - actual: " + currentUrl);
    }

    @Test
    public void testT02_BaseURL_NotSet() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        //request.webDriverMode = WebDriverMode.local;
        //request.browser = Browsers.phantomjs;

        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);
        String currentUrl = driver.getCurrentUrl();
        // Empty baseUrl of Chrome
        Assert.assertTrue(currentUrl.contains("data"), "Current URL is invalid - actual: " + currentUrl);
    }

    @Test
    public void testT03_EndPointCapabilities() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");
        //request.webDriverMode = WebDriverMode.local;
        //request.browser = Browsers.phantomjs;

        /*
        create caps
         */
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("enableVideo", true);
        caps.setCapability("enableVNC", true);

        // start session
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);
        SessionContext sessionContext = WEB_DRIVER_MANAGER.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = sessionContext.getWebDriverRequest().getCapabilities();

        Assert.assertEquals(sessionCapabilities.get("tap:projectId"), caps.getCapability("tap:projectId"), "EndPoint Capability is set");
    }

    @Test
    public void testT04_PlatformCaps() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setPlatformName(Platform.LINUX.toString());
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);

        WebDriverRequest webDriverRequest = WEB_DRIVER_MANAGER.getSessionContext(driver).get().getWebDriverRequest();

        Assert.assertEquals(webDriverRequest.getCapabilities().get(CapabilityType.PLATFORM_NAME), Platform.LINUX);
    }

    @Test
    public void testT05_ChromeExtensions() throws MalformedURLException {

        File chromeExtensionFile = new File(
                Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("testfiles/Simple_Translate_2.8.1.0.crx")).getFile());

        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chrome, (ChromeConfig) options -> {
            options.setAcceptInsecureCerts(true);
        });


        ChromeOptions options = new ChromeOptions();
        options.addExtensions(chromeExtensionFile);
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
//        request.setBaseUrl("chrome://extensions-internals/");
        request.getDesiredCapabilities().merge(options);

        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        webDriver.get("chrome://extensions-internals/");
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);
        UiElement chromeExtensionJson = uiElementFinder.find(By.xpath("//pre"));
        String content = chromeExtensionJson.waitFor().text().getActual();
        Assert.assertTrue(content.contains("Simple Translate"));

    }

}
