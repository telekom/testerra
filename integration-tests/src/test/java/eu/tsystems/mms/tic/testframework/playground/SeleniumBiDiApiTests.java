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
import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElementFinder;
import eu.tsystems.mms.tic.testframework.report.Report;
import eu.tsystems.mms.tic.testframework.report.model.context.Screenshot;
import eu.tsystems.mms.tic.testframework.report.utils.IExecutionContextController;
import eu.tsystems.mms.tic.testframework.testing.SeleniumBidiToolsProvider;
import eu.tsystems.mms.tic.testframework.useragents.ChromeConfig;
import eu.tsystems.mms.tic.testframework.useragents.FirefoxConfig;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.DesktopWebDriverRequest;
import org.openqa.selenium.By;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.bidi.browsingcontext.BrowsingContext;
import org.openqa.selenium.bidi.log.GenericLogEntry;
import org.openqa.selenium.bidi.log.LogEntry;
import org.openqa.selenium.bidi.module.LogInspector;
import org.openqa.selenium.bidi.module.Network;
import org.openqa.selenium.bidi.network.AddInterceptParameters;
import org.openqa.selenium.bidi.network.BeforeRequestSent;
import org.openqa.selenium.bidi.network.InterceptPhase;
import org.openqa.selenium.bidi.network.ResponseDetails;
import org.openqa.selenium.remote.Augmenter;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created on 2023-06-22
 *
 * @author mgn
 */
public class SeleniumBiDiApiTests extends AbstractWebDriverTest implements SeleniumBidiToolsProvider {

    @BeforeMethod
    public void initBrowser() {
        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chrome, (ChromeConfig) options -> {
            options.setCapability("webSocketUrl", true);
        });
        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.chromeHeadless, (ChromeConfig) options -> {
            options.setCapability("webSocketUrl", true);
        });

        WEB_DRIVER_MANAGER.setUserAgentConfig(Browsers.firefox, (FirefoxConfig) options -> {
            options.setCapability("webSocketUrl", true);
            options.setBinary("C:\\Program Files\\Mozilla Firefox\\firefox.exe");
        });
    }

    @Test
    public void testT03_LogListener_AllLogs() throws MalformedURLException {
        DesktopWebDriverRequest request = new DesktopWebDriverRequest();
        request.setBrowser(Browsers.chrome);
        request.setBaseUrl("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(request);

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);

        LogInspector logInspector = SELENIUM_BIDI_TOOLS.getLogInsepctor(webDriver);
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

        logEntryList.forEach(logEntry -> {
            AtomicReference<GenericLogEntry> genericLogEntry = new AtomicReference<>();
            logEntry.getConsoleLogEntry().ifPresent(genericLogEntry::set);
            logEntry.getJavascriptLogEntry().ifPresent(genericLogEntry::set);

            log().info("LOG_ENTRY: {} {} {} {} - {}",
                    genericLogEntry.get().getTimestamp(),
                    genericLogEntry.get().getType(),
                    genericLogEntry.get().getLevel(),
                    genericLogEntry.get().getType(),
                    genericLogEntry.get().getText()
            );
        });

    }

    @Test
    public void testT05_AllNetworkListener() {
        DesktopWebDriverRequest dwdRequest = new DesktopWebDriverRequest();
        dwdRequest.setBrowser(Browsers.chrome);
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver(dwdRequest);

        List<BeforeRequestSent> requestSentList = new ArrayList<>();
        List<ResponseDetails> responseList1 = new ArrayList<>();
        List<ResponseDetails> responseList2 = new ArrayList<>();

        Network network = SELENIUM_BIDI_TOOLS.getNetwork(webDriver);
        network.onBeforeRequestSent(requestSentList::add);
        network.onResponseCompleted(responseList1::add);
        network.onResponseStarted(responseList2::add);

        webDriver.get("https://the-internet.herokuapp.com/broken_images");
//        webDriver.get("https://www.selenium.dev/selenium/web/bidi/logEntryAdded.html");

        TimerUtils.sleep(1000);

        for (BeforeRequestSent request : requestSentList) {
            log().info("Request: {} {} - {}", request.getRequest().getRequestId(), request.getRequest().getMethod(), request.getRequest().getUrl());
        }

        for (ResponseDetails response : responseList1) {
            log().info("Response1: {} - [{}] {}", response.getRequest().getRequestId(), response.getResponseData().getStatus(), response.getResponseData().getStatusText());
        }

        for (ResponseDetails response : responseList2) {
            log().info("Response2: {} - [{}] {}", response.getRequest().getRequestId(), response.getResponseData().getStatus(), response.getResponseData().getStatusText());
        }
    }

    /**
     * Does not work
     * https://github.com/SeleniumHQ/seleniumhq.github.io/issues/1593
     * <p>
     * Seems to be not fully supported by Chrome and Firefox
     */
    @Test(enabled = false)
    public void testT06_BasicAuthentication() {
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver();
        Network network = SELENIUM_BIDI_TOOLS.getNetwork(webDriver);

        network.addIntercept(new AddInterceptParameters(InterceptPhase.AUTH_REQUIRED));
        network.onAuthRequired(responseDetails ->
                network.continueWithAuth(responseDetails.getRequest().getRequestId(), new UsernameAndPassword("admin", "admin")));

        webDriver.get("https://the-internet.herokuapp.com/basic_auth");

        UiElementFinder uiElementFinder = UI_ELEMENT_FINDER_FACTORY.create(webDriver);
        uiElementFinder.find(By.tagName("p")).assertThat().text().isContaining("Congratulations");
    }

    @Test
    public void testT07_CaptureScreenshot() throws IOException {
        WebDriver webDriver = WEB_DRIVER_MANAGER.getWebDriver();
        WebDriver originalFromDecorated = WEB_DRIVER_MANAGER.getOriginalFromDecorated(webDriver);
        Augmenter augmenter = new Augmenter();
        BrowsingContext browsingContext = new BrowsingContext(augmenter.augment(originalFromDecorated), webDriver.getWindowHandle());

        webDriver.get("https://the-internet.herokuapp.com/");

        browsingContext.setViewport(1024, 2000);
        String s = browsingContext.captureScreenshot();
        byte[] decode = Base64.getDecoder().decode(s);

        Screenshot screenshot = new Screenshot();
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(screenshot.getScreenshotFile()));
        outputStream.write(decode);

        Report reportInstance = Testerra.getInjector().getInstance(Report.class);
        IExecutionContextController iExecutionContextController = Testerra.getInjector().getInstance(IExecutionContextController.class);
        reportInstance.addScreenshot(screenshot, Report.FileMode.COPY);
        iExecutionContextController.getCurrentMethodContext().get().addScreenshot(screenshot);
    }

}
