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
import eu.tsystems.mms.tic.testframework.testing.ChromeDevToolsProvider;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.JavascriptException;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.HasDevTools;
import org.openqa.selenium.devtools.events.ConsoleEvent;
import org.openqa.selenium.devtools.v118.emulation.Emulation;
import org.openqa.selenium.devtools.v118.log.Log;
import org.openqa.selenium.devtools.v118.log.model.LogEntry;
import org.openqa.selenium.devtools.v118.network.Network;
import org.openqa.selenium.devtools.v118.network.model.RequestWillBeSent;
import org.openqa.selenium.devtools.v118.network.model.ResponseReceived;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created on 2023-01-13
 *
 * @author mgn
 */
public class ChromeDevToolsTests extends AbstractWebDriverTest implements ChromeDevToolsProvider {

    /**
     * See here for examples:
     * https://www.selenium.dev/documentation/webdriver/bidirectional/bidi_api_remotewebdriver/
     * https://www.selenium.dev/documentation/webdriver/bidirectional/chrome_devtools/
     * <p>
     * <p>
     * Important for local selenium server:
     * - Use --host parameter, otherwise it could return a wrong IP address from another network interface (e.g. with active VPN)
     * <p>
     * Example
     * - java -Dwebdriver.chrome.driver=driver\chromedriver.exe  -jar selenium4\selenium-server-4.7.2.jar standalone --port 4444 --host localhost
     * <p>
     * <p>
     * Known issues:
     * - Issue with Selenoid: https://github.com/aerokube/selenoid/issues/1063
     */

    private Optional<Number> latitude = Optional.of(52.52084);
    private Optional<Number> longitude = Optional.of(13.40943);

    @Test
    public void testT01_GeoLocation_localDriver() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        ChromeDriver chromeDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, ChromeDriver.class).get();

        DevTools devTools = chromeDriver.getDevTools();
        devTools.createSession();
        devTools.send(Emulation.setGeolocationOverride(
                latitude,
                longitude,
                Optional.of(1)));
        webDriver.get("https://my-location.org/");
        uiElementFinder.find(By.id("latitude")).assertThat().text().isContaining(latitude.get().toString());
        uiElementFinder.find(By.id("longitude")).assertThat().text().isContaining(longitude.get().toString());

    }

    @Test
    public void testT02_GeoLocation_remoteDriver() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        RemoteWebDriver remoteWebDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, RemoteWebDriver.class).get();
        webDriver = new Augmenter().augment(remoteWebDriver);

        DevTools devTools = ((HasDevTools) webDriver).getDevTools();
        devTools.createSession();

        devTools.send(Emulation.setGeolocationOverride(
                latitude,
                longitude,
                Optional.of(1)));

        webDriver.get("https://my-location.org/");
        uiElementFinder.find(By.id("latitude")).assertThat().text().isContaining(latitude.get().toString());
        uiElementFinder.find(By.id("longitude")).assertThat().text().isContaining(longitude.get().toString());
    }

    @Test
    public void testT03_GeoLocation_generic() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        CHROME_DEV_TOOLS.setGeoLocation(webDriver, latitude.get().doubleValue(), longitude.get().doubleValue(), 1);

        webDriver.get("https://my-location.org/");
        uiElementFinder.find(By.id("latitude")).assertThat().text().isContaining(latitude.get().toString());
        uiElementFinder.find(By.id("longitude")).assertThat().text().isContaining(longitude.get().toString());
    }

    /**
     * The following example set basic authentication via driver augumentation. This solution works only remote, not local.
     * A more flexible solution is implemented in SeleniumChromeDevTools
     */
    @Test
    public void testT04_BasicAuth_remoteDriver() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
//        request.setBrowserVersion("106");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);

        WebDriver remoteWebDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, RemoteWebDriver.class).get();
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        AtomicReference<DevTools> devToolsAtomicReference = new AtomicReference<>();

        remoteWebDriver = new Augmenter()
                .addDriverAugmentation(
                        "chrome",
                        HasAuthentication.class,
                        (caps, exec) -> (whenThisMatches, useTheseCredentials) -> {
                            devToolsAtomicReference.get().createSessionIfThereIsNotOne();
                            devToolsAtomicReference.get().getDomains()
                                    .network()
                                    .addAuthHandler(whenThisMatches, useTheseCredentials);
                        })
                .augment(remoteWebDriver);

        DevTools devTools = ((HasDevTools) remoteWebDriver).getDevTools();
        devTools.createSession();
        devToolsAtomicReference.set(devTools);
        ((HasAuthentication) remoteWebDriver).register(UsernameAndPassword.of("admin", "admin"));

        webDriver.get("https://the-internet.herokuapp.com/basic_auth");
        uiElementFinder.find(By.tagName("p")).assertThat().text().isContaining("Congratulations");

    }

    @Test
    public void testT05_BasicAuth_DevTools() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        CHROME_DEV_TOOLS.setBasicAuthentication(webDriver, UsernameAndPassword.of("admin", "admin"));

        webDriver.get("https://the-internet.herokuapp.com/basic_auth");
        uiElementFinder.find(By.tagName("p")).assertThat().text().isContaining("Congratulations");
    }

    /**
     * The following example uses the BiDi implementation of Chrome to add basic authentication information
     * <p>
     * Works only with local ChromeDriver, RemoteWebDriver is not supported
     */
    @Test
    public void testT06_BasicAuth_ChromeBiDiAPI() {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
//        request.setBrowserVersion("106");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);
        Predicate<URI> uriPredicate = uri -> uri.getHost().contains("the-internet.herokuapp.com");

        ChromeDriver chromeDriver = WEB_DRIVER_MANAGER.unwrapWebDriver(webDriver, ChromeDriver.class).get();
        ((HasAuthentication) chromeDriver).register(uriPredicate, UsernameAndPassword.of("admin", "admin"));

        webDriver.get("https://the-internet.herokuapp.com/basic_auth");
        uiElementFinder.find(By.tagName("p")).assertThat().text().isContaining("Congratulations");
    }

    //
    // The following tests demonstrate Browser console listener
    //

    @Test
    public void testT10_LogListener_BrokenImages() {
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver();
        DevTools devTools = CHROME_DEV_TOOLS.getRawDevTools(webDriver);
        devTools.send(Log.enable());

        List<LogEntry> logEntries = new ArrayList<>();
        Consumer<LogEntry> addedLog = logEntries::add;
        devTools.addListener(Log.entryAdded(), addedLog);

        webDriver.get("http://the-internet.herokuapp.com/broken_images");
        TimerUtils.sleep(1000);     // Short wait to get delayed logs
        for (LogEntry logEntry : logEntries) {
            log().info("LOG_ENTRY: {} {} {} - {} ({})", logEntry.getTimestamp(), logEntry.getLevel(), logEntry.getSource(), logEntry.getText(), logEntry.getUrl());
            ASSERT.assertFalse(logEntry.getText().contains("404"));
        }
    }

    @Test
    public void testT11_LogListener_JsLogs() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        DevTools devTools = CHROME_DEV_TOOLS.getRawDevTools(webDriver);

        List<ConsoleEvent> consoleEvents = new ArrayList<>();
        Consumer<ConsoleEvent> addEntry = consoleEvents::add;
        devTools.getDomains().events().addConsoleListener(addEntry);

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);
        uiElementFinder.find(By.id("consoleLog")).click();          // --> working
        uiElementFinder.find(By.id("consoleError")).click();        // --> working
        uiElementFinder.find(By.id("jsException")).click();         // --> not working
        uiElementFinder.find(By.id("logWithStacktrace")).click();   // --> not working

        for (ConsoleEvent event : consoleEvents) {
            log().info("JS_LOGS: {} {} - {}", event.getTimestamp(), event.getType(), event.getMessages().toString());
        }
    }

    @Test
    public void testT12_LogListener_JsExceptions() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBaseUrl("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);
        DevTools devTools = CHROME_DEV_TOOLS.getRawDevTools(webDriver);

        List<JavascriptException> jsExceptionsList = new ArrayList<>();
        Consumer<JavascriptException> addEntry = jsExceptionsList::add;
        devTools.getDomains().events().addJavascriptExceptionListener(addEntry);

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);
        uiElementFinder.find(By.id("consoleLog")).click();          // --> not working
        uiElementFinder.find(By.id("consoleError")).click();        // --> not working
        uiElementFinder.find(By.id("jsException")).click();         // --> working
        uiElementFinder.find(By.id("logWithStacktrace")).click();   // --> working

        for (JavascriptException jsException : jsExceptionsList) {
            log().info("JS_EXCEPTION: {} {}", jsException.getMessage(), jsException.getSystemInformation());
        }
    }

    @Test
    public void testT13_NetworkListener() {
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver();
        DevTools devTools = CHROME_DEV_TOOLS.getRawDevTools(webDriver);

        List<ResponseReceived> responseReceivedList = new ArrayList<>();
        List<RequestWillBeSent> requestList = new ArrayList<>();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        devTools.addListener(Network.responseReceived(), responseReceivedList::add);
        devTools.addListener(Network.requestWillBeSent(), requestList::add);

        webDriver.get("https://the-internet.herokuapp.com/broken_images");

        for (RequestWillBeSent request : requestList) {
            log().info("Request: {} {} - {}", request.getRequestId().toString(), request.getRequest().getMethod(), request.getRequest().getUrl());
        }

        for (ResponseReceived response : responseReceivedList) {
            log().info("Response: {} - [{}] {}", response.getRequestId().toString(), response.getResponse().getStatus(), response.getResponse().getStatusText());
        }
    }

}
