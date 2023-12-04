/*
 * Testerra
 *
 * (C) 2023, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.playground;

import eu.tsystems.mms.tic.testframework.AbstractWebDriverTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.useragents.FirefoxConfig;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.LogInspector;
import org.openqa.selenium.bidi.log.ConsoleLogEntry;
import org.openqa.selenium.bidi.log.JavascriptLogEntry;
import org.openqa.selenium.bidi.log.LogEntry;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2023-06-22
 *
 * @author mgn
 */
public class SeleniumBiDiApiTests extends AbstractWebDriverTest {

    @BeforeClass
    public void initBrowser() {
        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chrome, (ChromeConfig) options -> {
            options.setCapability("webSocketUrl", true);
        });

        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.firefox, (FirefoxConfig) options -> {
            options.setCapability("webSocketUrl", true);
        });
    }

    private LogInspector getLogInspector(WebDriver webDriver) throws MalformedURLException {
        WebDriverRequest webDriverRequest = WEB_DRIVER_MANAGER.getSessionContext(webDriver).get().getWebDriverRequest();
        final String browser = webDriverRequest.getBrowser();
        if (webDriverRequest.getServerUrl().isEmpty()) {
            switch (browser) {
                case Browsers.chrome:
                case Browsers.chromeHeadless:
                    return new LogInspector(WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, ChromeDriver.class).get());
                case Browsers.firefox:
                    return new LogInspector(WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, FirefoxDriver.class).get());
                default:
                    ASSERT.fail("Unsupported browser: " + browser);
            }
        }
        Augmenter augmenter = new Augmenter();
        RemoteWebDriver remoteWebDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, RemoteWebDriver.class).get();
        return new LogInspector(augmenter.augment(remoteWebDriver));
    }

    @Test
    public void testT01_LogListener_ConsoleLogs() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        request.setBaseUrl("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        LogInspector logInspector = getLogInspector(webDriver);
        List<ConsoleLogEntry> logEntryList = new ArrayList<>();
        logInspector.onConsoleEntry(logEntryList::add);

        uiElementFinder.find(By.id("consoleLog")).click();      // --> working
        uiElementFinder.find(By.id("consoleError")).click();    // --> working
//        uiElementFinder.find(By.id("jsException")).click();     // --> not working
//        uiElementFinder.find(By.id("logWithStacktrace")).click(); // --> not working

        CONTROL.retryTimes(5, () -> {
            ASSERT.assertTrue(logEntryList.size() > 1);
            TimerUtils.sleepSilent(1000);
        });

        logEntryList.forEach(logEntry ->
                log().info(
                        "LOG_ENTRY: {} {} {} - {}",
                        logEntry.getTimestamp(),
                        logEntry.getLevel(),
                        logEntry.getMethod(),
                        logEntry.getText())
        );

        ASSERT.assertEquals(logEntryList.size(), 2, "LogEntry list");
    }

    @Test
    public void testT02_LogListener_JSLogs() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
//        request.setBrowser(Browsers.chrome);
        request.setBrowser(Browsers.firefox);
        request.setBaseUrl("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        LogInspector logInspector = getLogInspector(webDriver);
        List<JavascriptLogEntry> logEntryList = new ArrayList<>();
        logInspector.onJavaScriptLog(logEntryList::add);
//        logInspector.onJavaScriptException(logEntryList::add);    --> has the same result

//        uiElementFinder.find(By.id("consoleLog")).click();      // --> not working
//        uiElementFinder.find(By.id("consoleError")).click();    // --> not working
        uiElementFinder.find(By.id("jsException")).click();     // --> working
        uiElementFinder.find(By.id("logWithStacktrace")).click(); // --> working

        CONTROL.retryTimes(5, () -> {
            ASSERT.assertTrue(logEntryList.size() > 1);
            TimerUtils.sleepSilent(1000);
        });

        for (JavascriptLogEntry logEntry : logEntryList) {
            log().info("LOG_ENTRY: {} {} {} - {}", logEntry.getTimestamp(), logEntry.getLevel(), logEntry.getText(), logEntry.getStackTrace().toString());
        }
        ASSERT.assertEquals(logEntryList.size(), 2, "JS LogEntry list");
    }

    @Test
    public void testT03_LogListener_AllLogs() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        request.setBaseUrl("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        LogInspector logInspector = getLogInspector(webDriver);
        List<LogEntry> logEntryList = new ArrayList<>();
        logInspector.onLog(logEntryList::add);

        uiElementFinder.find(By.id("consoleLog")).click();      // --> working
        uiElementFinder.find(By.id("consoleError")).click();    // --> working
        uiElementFinder.find(By.id("jsException")).click();     // --> working
        uiElementFinder.find(By.id("logWithStacktrace")).click(); // --> working

        CONTROL.retryTimes(5, () -> {
            ASSERT.assertTrue(logEntryList.size() >= 4);
            TimerUtils.sleepSilent(1000);
        });

        ASSERT.assertEquals(logEntryList.size(), 4, "LogEntry list");
    }


}
