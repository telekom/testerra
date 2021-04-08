/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.enums.Position;
import eu.tsystems.mms.tic.testframework.exceptions.SetupException;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.internal.utils.DriverStorage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.DesktopGuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.Sequence;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.anthavio.phanbedder.Phanbedder;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class DesktopWebDriverFactory implements
        WebDriverFactory,
        Loggable,
        WebDriverManagerProvider
{
    //public static final TimingInfosCollector STARTUP_TIME_COLLECTOR = new TimingInfosCollector();

    private static File phantomjsFile = null;

    @Override
    public WebDriver createWebDriver(WebDriverRequest request, SessionContext sessionContext) {
        return startSession((DesktopWebDriverRequest)request, sessionContext);
    }

    private WebDriver startSession(DesktopWebDriverRequest desktopWebDriverRequest, SessionContext sessionContext) {

        /*
        if there is a factories entry for the requested browser, then create the new (raw) instance here and wrap it directly in EventFiringWD
         */
        if (Testerra.Properties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD.asBool()) {
            String threadName = Thread.currentThread().getId() + "";
            String testMethodName = ExecutionContextUtils.getMethodNameFromCurrentTestResult();
            WebDriver driver = DriverStorage.getDriverByTestMethodName(testMethodName, threadName);
            if (driver != null) {
                log().info("Re-Using WebDriver for " + testMethodName + ": " + threadName + " driver: " + driver);

                // cleanup session
                driver.manage().deleteAllCookies();
                return driver;
            } else {
                return newWebDriver(desktopWebDriverRequest, sessionContext);
            }
        } else {
            /*
            regular branch to create a new web driver session
             */
            return newWebDriver(desktopWebDriverRequest, sessionContext);
        }
    }

    @Override
    public WebDriverRequest prepareWebDriverRequest(WebDriverRequest webDriverRequest) {
        DesktopWebDriverRequest finalRequest;
        if (webDriverRequest instanceof DesktopWebDriverRequest) {
            finalRequest = (DesktopWebDriverRequest) webDriverRequest;
        } else {
            finalRequest = new DesktopWebDriverRequest();
            finalRequest.setSessionKey(webDriverRequest.getSessionKey());
            finalRequest.setBrowser(webDriverRequest.getBrowser());
            finalRequest.setBrowserVersion(webDriverRequest.getBrowserVersion());
        }
        return finalRequest;
    }

    @Override
    public void setupNewWebDriverSession(EventFiringWebDriver eventFiringWebDriver, SessionContext sessionContext) {

        DesktopWebDriverRequest desktopWebDriverRequest = (DesktopWebDriverRequest)sessionContext.getWebDriverRequest();
        final String browser = desktopWebDriverRequest.getBrowser();

        // add event listeners
        if (Testerra.Properties.DEMO_MODE.asBool()) {
            eventFiringWebDriver.register(new VisualEventDriverListener());
        }

        eventFiringWebDriver.register(new EventLoggingEventDriverListener());

        /*
         start StopWatch
          */
        desktopWebDriverRequest.getBaseUrl().ifPresent(baseUrl -> {
            try {
                StopWatch.startPageLoad(eventFiringWebDriver);
                eventFiringWebDriver.get(baseUrl.toString());
            } catch (Exception e) {
                if (StringUtils.containsAll(e.getMessage(), true, "Reached error page", "connectionFailure")) {
                    throw new RuntimeException("Could not start driver session, because of unreachable url: " + baseUrl, e);
                }
                throw e;
            }
        });

        WebDriver.Window window = eventFiringWebDriver.manage().window();
        /*
         Maximize
         */
        if (desktopWebDriverRequest.getMaximizeBrowser()) {
            log().debug("Trying to maximize window");
            try {
                Dimension originWindowSize = window.getSize();
                // Maximize to detect window size
                window.maximize();
                Position maximizePosition = desktopWebDriverRequest.getMaximizePosition();
                if (maximizePosition != Position.CENTER) {
                    log().debug(String.format("Setting maximized window position to: %s", maximizePosition));
                    Point targetPosition = new Point(0, 0);
                    switch (maximizePosition) {
                        case LEFT:
                            targetPosition.x = -originWindowSize.width;
                            break;
                        case RIGHT:
                            targetPosition.x = window.getSize().width + 1;
                            break;
                        case TOP:
                            targetPosition.y = -originWindowSize.height;
                            break;
                        case BOTTOM:
                            targetPosition.y = window.getSize().height + 1;
                            break;
                    }
                    log().debug(String.format("Move window to: %s", targetPosition));
                    window.setPosition(targetPosition);
                    // Re-maximize
                    window.maximize();
                }
            } catch (Throwable t1) {
                log().error("Could not maximize window", t1);
                setWindowSizeBasedOnDisplayResolution(window, browser);
            }
        } else {
            setWindowSizeBasedOnDisplayResolution(window, browser);
        }

        if (!Browsers.safari.equalsIgnoreCase(browser)) {
            int pageLoadTimeout = Testerra.Properties.WEBDRIVER_TIMEOUT_SECONDS_PAGELOAD.asLong().intValue();
            int scriptTimeout = Testerra.Properties.WEBDRIVER_TIMEOUT_SECONDS_SCRIPT.asLong().intValue();
            try {
                eventFiringWebDriver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
            } catch (Exception e) {
                log().error("Could not set Page Load Timeout", e);
            }
            try {
                eventFiringWebDriver.manage().timeouts().setScriptTimeout(scriptTimeout, TimeUnit.SECONDS);
            } catch (Exception e) {
                log().error("Could not set Script Timeout", e);
            }
        } else {
            log().warn("Not setting timeouts for Safari.");
        }
    }

    private void setWindowSizeBasedOnDisplayResolution(WebDriver.Window window, String browser) {
        log().debug("Trying to set window size to: " + Testerra.Properties.DISPLAY_RESOLUTION.asString());
        String[] split = Testerra.Properties.DISPLAY_RESOLUTION.asString().split("x");
        int width = Integer.parseInt(split[0]);
        int height = Integer.parseInt(split[1]);
        try {
            window.setSize(new Dimension(width, height));
        } catch (Throwable t2) {
            log().error("Could not set window size", t2);

            if (Browsers.edge.equals(browser)) {
                log().debug("Edge Browser was requested, trying a second workaround");

                Sequence sequence = new Sequence()
                        .setWaitMsAfterRun(500)
                        .setTimeoutMs(5000);
                sequence.run(() -> {
                    try {
                        window.setPosition(new Point(0, 0));
                        window.setSize(new Dimension(width, height));
                        return true;
                    } catch (Throwable throwable) {
                        log().warn("Got error: " + throwable.getMessage());
                        return false;
                    }
                });
            }
        }
    }

    private WebDriver newWebDriver(DesktopWebDriverRequest desktopWebDriverRequest, SessionContext sessionContext) {
        final String browser = desktopWebDriverRequest.getBrowser();
        DesiredCapabilities capabilities = desktopWebDriverRequest.getDesiredCapabilities();

        /*
         * Remote or local
         */
        WebDriver newDriver;
        URL seleniumUrl = null;
        if (desktopWebDriverRequest.getWebDriverMode() == WebDriverMode.remote) {
            seleniumUrl = desktopWebDriverRequest.getSeleniumServerUrl().get();
        }

        /*
         * Start a new web driver session.
         */
        try {
            if (browser.equals(Browsers.htmlunit)) {
                capabilities.setBrowserName(BrowserType.HTMLUNIT);
                capabilities.setJavascriptEnabled(false);
                log().info("Starting HtmlUnitRemoteWebDriver.");
                newDriver = new RemoteWebDriver(seleniumUrl, capabilities);
            } else {
                newDriver = startNewWebDriverSession(desktopWebDriverRequest, seleniumUrl, sessionContext);
            }
        } catch (final SetupException e) {
            int ms = Testerra.Properties.WEBDRIVER_TIMEOUT_SECONDS_RETRY.asLong().intValue()*1000;
            log().error(String.format("Error starting WebDriver. Trying again in %d seconds", (ms / 1000)), e);
            TimerUtils.sleep(ms);
            newDriver = startNewWebDriverSession(desktopWebDriverRequest, seleniumUrl, sessionContext);
        }

        /*
        Log User Agent and executing host
         */
        if (seleniumUrl != null && newDriver instanceof RemoteWebDriver) {
            DesktopWebDriverUtils utils = new DesktopWebDriverUtils();
            sessionContext.setNodeUrl(utils.getNodeInfo(seleniumUrl, ((RemoteWebDriver) newDriver).getSessionId().toString()));
        }
        //STARTUP_TIME_COLLECTOR.add(new TimingInfo("SessionStartup", "", sw.getTime(TimeUnit.MILLISECONDS), System.currentTimeMillis()));

        return newDriver;
    }

    /**
     * Remote when remoteAdress != null, local need browser to be set.
     */
    private WebDriver startNewWebDriverSession(
            AbstractWebDriverRequest request,
            URL remoteAddress,
            SessionContext sessionContext
    ) {

        final String browser = request.getBrowser();
        final DesiredCapabilities desiredCapabilities = request.getDesiredCapabilities();
        final Capabilities userAgentCapabilities;
        final Class<? extends RemoteWebDriver> driverClass;

        switch (browser) {
            case Browsers.firefox:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
//                if (capabilities.getCapabilityNames().contains(FirefoxOptions.FIREFOX_OPTIONS)) {
//                    final TreeMap predefinedFirefoxOptions = (TreeMap) capabilities.getCapability(FirefoxOptions.FIREFOX_OPTIONS);
//                    predefinedFirefoxOptions.forEach((s, o) -> firefoxOptions.setCapability(s.toString(), o));
//                }
                WEB_DRIVER_MANAGER.getUserAgentConfig(browser).ifPresent(userAgentConfig -> {
                    userAgentConfig.configure(firefoxOptions);
                });
                userAgentCapabilities = firefoxOptions;
                driverClass = FirefoxDriver.class;
                break;
            case Browsers.ie:
                InternetExplorerOptions ieOptions = new InternetExplorerOptions();
                WEB_DRIVER_MANAGER.getUserAgentConfig(browser).ifPresent(userAgentConfig -> {
                    userAgentConfig.configure(ieOptions);
                });
                ieOptions.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                userAgentCapabilities = ieOptions;
                driverClass = InternetExplorerDriver.class;
                break;
            case Browsers.chrome:
            case Browsers.chromeHeadless:
                ChromeOptions chromeOptions = new ChromeOptions();
//                if (capabilities.getCapabilityNames().contains(ChromeOptions.CAPABILITY)) {
//                    final TreeMap predefinedChromeOptions = (TreeMap) capabilities.getCapability(ChromeOptions.CAPABILITY);
//                    predefinedChromeOptions.forEach((s, o) -> chromeOptions.setCapability(s.toString(), o));
//                }

                WEB_DRIVER_MANAGER.getUserAgentConfig(browser).ifPresent(userAgentConfig -> {
                    userAgentConfig.configure(chromeOptions);
                });

                chromeOptions.addArguments("--no-sandbox");
                if (browser.equals(Browsers.chromeHeadless)) {
                    chromeOptions.setHeadless(true);
                }
                userAgentCapabilities = chromeOptions;
                driverClass = ChromeDriver.class;
                break;
            case Browsers.phantomjs:
                File phantomjsFile = getPhantomJSBinary();
                DesiredCapabilities phantomJsOptions = new DesiredCapabilities();
                phantomJsOptions.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjsFile.getAbsolutePath());
                phantomJsOptions.setBrowserName(BrowserType.PHANTOMJS);
                phantomJsOptions.setJavascriptEnabled(true);

                String[] args = {
                        "--ssl-protocol=any"
                };
                phantomJsOptions.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, args);
                userAgentCapabilities = phantomJsOptions;
                driverClass = PhantomJSDriver.class;
                break;
            case Browsers.safari:
                SafariOptions safariOptions = new SafariOptions();
                WEB_DRIVER_MANAGER.getUserAgentConfig(browser).ifPresent(userAgentConfig -> {
                    userAgentConfig.configure(safariOptions);
                });
                userAgentCapabilities = safariOptions;
                driverClass = SafariDriver.class;
                break;
            case Browsers.edge:
                EdgeOptions edgeOptions = new EdgeOptions();
                WEB_DRIVER_MANAGER.getUserAgentConfig(browser).ifPresent(userAgentConfig -> {
                    userAgentConfig.configure(edgeOptions);
                });
                userAgentCapabilities = edgeOptions;
                driverClass = EdgeDriver.class;
                break;
            default:
                throw new SystemException("Browser must be set through SystemProperty 'browser' or in test.properties file! + is: " + browser);
        }

        // Finalize capabilities
        final DesiredCapabilities finalCapabilities = new DesiredCapabilities();
        finalCapabilities.merge(desiredCapabilities);
        finalCapabilities.merge(userAgentCapabilities);

        WebDriverSessionsManager.logRequest(driverClass, finalCapabilities, sessionContext, remoteAddress);

        WebDriver driver;
        try {
            if (remoteAddress != null) {
                final HttpCommandExecutor httpCommandExecutor = new HttpCommandExecutor(new HashMap<>(), remoteAddress, new HttpClientFactory());
                driver = new RemoteWebDriver(httpCommandExecutor, finalCapabilities);
                ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
            } else {
                log().warn("Local WebDriver setups may cause side effects. It's highly recommended to use a remote Selenium configurations for all environments!");
                Constructor<? extends RemoteWebDriver> constructor = driverClass.getConstructor(Capabilities.class);
                driver = constructor.newInstance(finalCapabilities);
            }
        } catch (Exception e) {
            WebDriverSessionsManager.SESSION_STARTUP_ERRORS.put(new Date(), e);
            throw new SetupException("Error starting browser session", e);
        }

        return driver;
    }

    private File getPhantomJSBinary() {
        if (phantomjsFile == null) {
            log().info("Unpacking phantomJS...");
            try {
                phantomjsFile = Phanbedder.unpack(); //Phanbedder to the rescue!
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("failed to make target directory")) {
                    File tmp = new File(FileUtils.getTempDirectory(), "phantomjs" + System.currentTimeMillis());
                    phantomjsFile = Phanbedder.unpack(tmp);
                } else {
                    throw e;
                }
            }
            log().info("Unpacked phantomJS to: " + phantomjsFile);
        }
        return phantomjsFile;
    }

    @Override
    public List<String> getSupportedBrowsers() {
        return Arrays.asList(
            Browsers.safari,
            Browsers.ie,
            Browsers.chrome,
            Browsers.chromeHeadless,
            Browsers.edge,
            Browsers.firefox,
            Browsers.phantomjs
        );
    }

    @Override
    public GuiElementCore createCore(GuiElementData guiElementData) {
        return new DesktopGuiElementCore(guiElementData);
    }
}
