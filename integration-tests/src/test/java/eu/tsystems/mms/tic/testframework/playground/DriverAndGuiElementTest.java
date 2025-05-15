/*
 * Testerra
 *
 * (C) 2022, Martin Großmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.core.pageobjects.testdata.PageWithExistingElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.testing.PageFactoryProvider;
import eu.tsystems.mms.tic.testframework.testing.UiElementFinderFactoryProvider;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.useragents.FirefoxConfig;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class DriverAndGuiElementTest extends AbstractTestSitesTest implements UiElementFinderFactoryProvider, PageFactoryProvider {

    @Test
    public void testUiElement() {
        WebDriver driver = getWebDriver();

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(driver);
        UiElement element = uiElementFinder.find(By.id("1"));
        element.click();
        element.type("foo");
        element.waitFor().attribute("href").getActual();
        element.waitFor().tagName().getActual();
        element.waitFor().classes().getActual();
    }

    @Test
    public void testTapCapabilities() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");
        request.setBrowser(Browsers.chrome);

        /*
        create caps
         */
        DesiredCapabilities caps = new DesiredCapabilities();

        // start session
        WebDriver driver = WEB_DRIVER_MANAGER.getWebDriver(request);

        SessionContext sessionContext = WEB_DRIVER_MANAGER.getSessionContext(driver).get();
        Map<String, Object> sessionCapabilities = sessionContext.getWebDriverRequest().getCapabilities().asMap();

        Assert.assertEquals(sessionCapabilities.get("projectId"), caps.getCapability("projectId"), "EndPoint Capability is set");
    }

    @Test
    public void testFailing() throws Exception {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("http://google.de");
        request.setBrowser(Browsers.chrome);

        WEB_DRIVER_MANAGER.getWebDriver(request);
        Assert.assertTrue(false);
    }

    @Test
    public void testsimpleUiElement() {
        WebDriver driver = getWebDriver();

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(driver);
        UiElement element = uiElementFinder.find(By.id("1"));
        element.waitFor().attribute("href").getActual();
        element.waitFor().tagName().getActual();
        element.waitFor().classes().getActual();
    }

    @Test
    public void testFirefoxExtension() throws Exception {

        File chromeExtensionFile = new File(Objects.requireNonNull(
                Thread.currentThread().getContextClassLoader()
                        .getResource("testfiles/Simple_Translate_2.8.1.0.crx")).getFile());

        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chrome, (ChromeConfig) options -> {
            options.addExtensions(chromeExtensionFile);
        });

//        File firefoxExtensionFile = new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("testfiles/simple_translate-2.8.1.xpi")).getFile());
        File firefoxExtensionFile = new File(Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("testfiles/i_dont_care_about_cookies-3.4.6.xpi")).getFile());

        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.firefox, (FirefoxConfig) options -> {
            FirefoxProfile profile = new FirefoxProfile();
            profile.addExtension(firefoxExtensionFile);
            options.setProfile(profile);
        });

        WebDriver driver = getWebDriver();
    }

    @Test
    public void testT02_checkNotExistingElement() {
        WebDriver webDriver = this.getWebDriver();

//        PAGE_FACTORY.createPage(PageWithNotExistingElement.class);
        PAGE_FACTORY.createPage(PageWithExistingElement.class, webDriver);
        UITestUtils.takeScreenshot(webDriver, true);
    }

    @Test
    public void test_specialCaps() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--browser.download.folderList=2");
        options.addArguments("--browser.helperApps.neverAsk.saveToDisk=text/csv, application/pdf,application/pdf,application/x-pdf,application/zip,application/x-zip-compressed,multipart/x-zip");
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);
        final Map<String, Object> prefs = new HashMap<>();
        prefs.put("intl.accept_languages", "de-DE");
        options.setExperimentalOption("prefs", prefs);
        request.getMutableCapabilities().setCapability(ChromeOptions.CAPABILITY, options);

        WEB_DRIVER_MANAGER.getWebDriver(request);
    }

    @Test
    public void testLayoutCheckOutsideViewport() {
        WebDriver driver = getWebDriver();
        UiElementFinder finder = UI_ELEMENT_FINDER_FACTORY.create(driver);

        finder.getWebDriver().get("https://www.telekom.de/start");
        finder.find(By.id("rejectAll")).click();
        UiElement uiElement = finder.find(By.xpath("//div[@class='body-container']//section[@class='page_modules_page__3KcU-']//div[@id='PHX_home_r1c1']//p"));
        uiElement.screenshotToReport();

    }
}
