/*
 * Testerra
 *
 * (C) 2024, Martin Großmann, Deutsche Telekom MMS GmbH, Deutsche Telekom AG
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
package io.testerra.test.bidi;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.LogInspector;
import org.openqa.selenium.bidi.Network;
import org.openqa.selenium.bidi.log.ConsoleLogEntry;
import org.openqa.selenium.bidi.log.GenericLogEntry;
import org.openqa.selenium.bidi.log.JavascriptLogEntry;
import org.openqa.selenium.bidi.network.ResponseDetails;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.Augmenter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created on 2024-02-27
 *
 * @author mgn
 */
public class W3cBidiTests extends AbstractTestSitesTest {

    @BeforeMethod
    public void initBrowser() {
        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chrome, (ChromeConfig) options -> {
            options.setCapability("webSocketUrl", true);
        });
        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chromeHeadless, (ChromeConfig) options -> {
            options.setCapability("webSocketUrl", true);
            options.addArguments("--disable-dev-shm-usage");
        });

//        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.firefox, (FirefoxConfig) options -> {
//            options.setCapability("webSocketUrl", true);
//        });
    }

    @Test
    public void testT01_LogListener_ConsoleLogs() throws MalformedURLException {
        WebDriver webDriver = getWebDriver(TestPage.BIDI_JS_LOGS);

        LogInspector logInspector = getLogInspector(webDriver);
        List<ConsoleLogEntry> logEntryList = new ArrayList<>();
        logInspector.onConsoleEntry(logEntryList::add);

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);
        uiElementFinder.find(By.id("consoleLog")).click();
        uiElementFinder.find(By.id("consoleError")).click();

        CONTROL.retryTimes(5, () -> {
            TimerUtils.sleepSilent(1000);
            ASSERT.assertTrue(logEntryList.size() > 1);
        });

        ASSERT.assertEquals(logEntryList.size(), 2, "LogEntry list");
        this.assertEntryInList(logEntryList, "Hello, world");
        this.assertEntryInList(logEntryList, "I am console error");
    }

    @Test
    public void testT02_LogListener_JSLogs() throws MalformedURLException {
        WebDriver webDriver = getWebDriver(TestPage.BIDI_JS_LOGS);

        LogInspector logInspector = getLogInspector(webDriver);
        List<JavascriptLogEntry> logEntryList = new ArrayList<>();
        logInspector.onJavaScriptLog(logEntryList::add);

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);
        uiElementFinder.find(By.id("jsException")).click();
        uiElementFinder.find(By.id("logWithStacktrace")).click();

        CONTROL.retryTimes(5, () -> {
            TimerUtils.sleepSilent(1000);
            ASSERT.assertTrue(logEntryList.size() > 1);
        });

        ASSERT.assertEquals(logEntryList.size(), 2, "JS LogEntry list");
        this.assertEntryInList(logEntryList, "Error: Not working js exception");
        this.assertEntryInList(logEntryList, "Error: Not working js with stacktrace");
    }

    @Test
    public void testT03_Response_BrokenImages() {
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver();

        Network network = getNetwork(webDriver);
        List<ResponseDetails> responseList = new ArrayList<>();
        network.onResponseCompleted(responseList::add);

        webDriver.get(String.format("http://localhost:%d/%s", 80, TestPage.BIDI_BROKEN_IMAGE.getPath()));

        CONTROL.retryTimes(10, () -> {
            TimerUtils.sleepSilent(1000);
            ASSERT.assertTrue(responseList.stream().filter(response -> response.getResponseData().getStatus() == 404).count() == 1);
        });

        List<ResponseDetails> list404 = responseList.stream().filter(response -> response.getResponseData().getStatus() == 404).collect(Collectors.toList());
        ASSERT.assertEquals(list404.size(), 1);
        ASSERT.assertTrue(list404.get(0).getRequest().getUrl().contains("not-found-image"));
    }


    private void assertEntryInList(List<? extends GenericLogEntry> list, String entry) {
        Optional<GenericLogEntry> any = (Optional<GenericLogEntry>) list.stream().filter(elem -> elem.getText().contains(entry)).findAny();
        ASSERT.assertTrue(any.isPresent(), String.format("%s is in log list", entry));
    }

    private LogInspector getLogInspector(WebDriver webDriver) throws MalformedURLException {
        WebDriver originalFromDecorated = WEB_DRIVER_MANAGER.getOriginalFromDecorated(webDriver);
        WebDriverRequest webDriverRequest = WEB_DRIVER_MANAGER.getSessionContext(webDriver).get().getWebDriverRequest();

        // Check for RemoteWebDriver
        if (webDriverRequest.getServerUrl().isPresent()) {
            Augmenter augmenter = new Augmenter();
            return new LogInspector(augmenter.augment(originalFromDecorated));
        }
        return new LogInspector(originalFromDecorated);
    }

    private Network getNetwork(WebDriver webDriver) {
        WebDriver originalFromDecorated = WEB_DRIVER_MANAGER.getOriginalFromDecorated(webDriver);
        WebDriverRequest webDriverRequest = WEB_DRIVER_MANAGER.getSessionContext(webDriver).get().getWebDriverRequest();

        // Check for RemoteWebDriver
        if (webDriverRequest.getServerUrl().isPresent()) {
            Augmenter augmenter = new Augmenter();
            return new Network(augmenter.augment(originalFromDecorated));
        }
        return new Network(originalFromDecorated);
    }


}
