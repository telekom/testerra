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
import eu.tsystems.mms.tic.testframework.report.model.steps.TestStep;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.useragents.FirefoxConfig;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.LogInspector;
import org.openqa.selenium.bidi.log.ConsoleLogEntry;
import org.openqa.selenium.bidi.log.LogEntry;
import org.openqa.selenium.bidi.log.LogLevel;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    @Test
    public void testT01_ListenToConsoleLogLocalWithException() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
//        request.setBrowser(Browsers.firefox);
        request.setBaseUrl("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        ChromeDriver chromeDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, ChromeDriver.class).get();
//        FirefoxDriver firefoxDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, FirefoxDriver.class).get();
        LogInspector logInspector = new LogInspector(chromeDriver);
//        LogInspector logInspector = new LogInspector(firefoxDriver);
        CompletableFuture<Void> future = new CompletableFuture<>();

//        future.exceptionally(ex -> {
//            log().error("Exception in future");
//            throw new RuntimeException(ex);
//        });

        logInspector.onConsoleEntry(entry -> {
            try {
                log().info("{} - {}: {}", entry.getTimestamp(), entry.getLevel().toString().toUpperCase(), entry.getText());
                ASSERT.assertNotEquals(entry.getLevel(), LogLevel.ERROR, "Console log type");
            } catch (AssertionError e) {
                future.completeExceptionally(e);
            }
        });
//        logInspector.onConsoleEntry(entry -> {
//            log().info("{} - {}: {}", entry.getTimestamp(), entry.getLevel().toString().toUpperCase(), entry.getText());
//            ASSERT.assertNotEquals(entry.getLevel(), LogLevel.ERROR, "Console log type");
//            Assert.fail("TestNG fail");
//        });
        TestStep.begin("clicks");
        log().info("Vor click");
        uiElementFinder.find(By.id("consoleLog")).click();
        log().info("Nach click");
        uiElementFinder.find(By.id("consoleError")).click();
        // Exception behandeln, ohne get() aufzurufen

        TestStep.begin("nach allem");
        log().info("Foo bar");

//        ConsoleLogEntry logEntry = future.get(5, TimeUnit.SECONDS);
//        log().info(logEntry.getText());
//        ASSERT.assertTrue(logEntry.getText().contains("Hello, world!"));
        // Cause that exception are thrown to current main thread
        CompletableFuture.allOf(future).join();
    }

    @Test
    public void testT02_ListenToConsoleLogRemote() throws ExecutionException, InterruptedException, TimeoutException {
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver();
        RemoteWebDriver remoteWebDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, RemoteWebDriver.class).get();
        LogInspector logInspector = new LogInspector(remoteWebDriver);
        CompletableFuture<ConsoleLogEntry> future = new CompletableFuture<>();
        logInspector.onConsoleEntry(future::complete);

        webDriver.get("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        webDriver.findElement(By.id("consoleLog")).click();

        ConsoleLogEntry logEntry = future.get(5, TimeUnit.SECONDS);
        log().info(logEntry.toString());
    }

    @Test
    public void testT03_ListenToConsoleLogWithList_LOCAL() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        request.setBaseUrl("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);

        ChromeDriver chromeDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, ChromeDriver.class).get();
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        LogInspector logInspector = new LogInspector(chromeDriver);
        List<ConsoleLogEntry> logEntryList = new ArrayList<>();
        logInspector.onConsoleEntry(logEntryList::add);

        uiElementFinder.find(By.id("consoleLog")).click();      // --> working
        uiElementFinder.find(By.id("consoleError")).click();    // --> working
        uiElementFinder.find(By.id("jsException")).click();     // --> not working
        uiElementFinder.find(By.id("logWithStacktrace")).click(); // --> not working

        for (ConsoleLogEntry logEntry : logEntryList) {
            System.out.println(String.format("LOG_ENTRY: %s %s %s - %s", logEntry.getTimestamp(), logEntry.getLevel(), logEntry.getMethod(), logEntry.getText()));
        }
        ASSERT.assertEquals(logEntryList.size(), 2, "LogEntry list");
    }

    @Test
    public void testT04_ListenToConsoleLogWithAssert_LOCAL() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        request.setBaseUrl("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);

        ChromeDriver chromeDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, ChromeDriver.class).get();
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        LogInspector logInspector = new LogInspector(chromeDriver);
        log().info("My main thread: {}", Thread.currentThread().getName());
        logInspector.onConsoleEntry(logEntry -> {
            log().info("LOG_ENTRY: {} {} {} - {}", logEntry.getTimestamp(), logEntry.getLevel(), logEntry.getMethod(), logEntry.getText());
            log().info("My listener thread: {}", Thread.currentThread().getName());
            ASSERT.assertNotEquals(logEntry.getLevel(), LogLevel.ERROR, "Log entry level");
        });

        uiElementFinder.find(By.id("consoleLog")).click();      // --> working
        uiElementFinder.find(By.id("consoleError")).click();    // --> working
        uiElementFinder.find(By.id("jsException")).click();     // --> not working
        uiElementFinder.find(By.id("logWithStacktrace")).click(); // --> not working
    }

    @Test
    public void testT04_ListenToConsoleLogWithList_REMOTE() throws MalformedURLException, ExecutionException, InterruptedException, TimeoutException {
//        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
//        request.setBrowser(Browsers.chrome);
//        request.setServerUrl("http://localhost:4444/wd/hub");
//        request.setBaseUrl("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
//        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver();
        RemoteWebDriver remoteWebDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, RemoteWebDriver.class).get();
//        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        LogInspector logInspector = new LogInspector(remoteWebDriver);

//        CompletableFuture<Void> future = new CompletableFuture<>();
        List<ConsoleLogEntry> logs = new ArrayList<>();
        logInspector.onConsoleEntry(logs::add);

        TestStep.begin("clicks");
        log().info("Vor click");
//        uiElementFinder.find(By.id("consoleLog")).click();
        log().info("Nach click");
//        uiElementFinder.find(By.id("consoleError")).click();
        // Exception behandeln, ohne get() aufzurufen

        TestStep.begin("nach allem");
        log().info("Foo bar");

//        ConsoleLogEntry logEntry = future.get(5, TimeUnit.SECONDS);
//        log().info(logEntry.getText());
//        ASSERT.assertTrue(logEntry.getText().contains("Hello, world!"));
        // Cause that exception are thrown to current main thread
//        CompletableFuture.allOf(future).join();
    }

}
