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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.Constants;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.enums.Position;
import eu.tsystems.mms.tic.testframework.exceptions.SetupException;
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.internal.utils.DriverStorage;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.model.NodeInfo;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import eu.tsystems.mms.tic.testframework.sikuli.SikuliWebDriver;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.useragents.UserAgentConfig;
import eu.tsystems.mms.tic.testframework.utils.CapabilityUtils;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import eu.tsystems.mms.tic.testframework.utils.TimerUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
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

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DesktopWebDriverFactory extends WebDriverFactory<DesktopWebDriverRequest> implements Loggable {

    //public static final TimingInfosCollector STARTUP_TIME_COLLECTOR = new TimingInfosCollector();

    private static File phantomjsFile = null;

    @Override
    protected DesktopWebDriverRequest buildRequest(AbstractWebDriverRequest request) {
        DesktopWebDriverRequest finalRequest;
        if (request instanceof DesktopWebDriverRequest) {
            finalRequest = (DesktopWebDriverRequest) request;
        } else if (request instanceof UnspecificWebDriverRequest) {
            finalRequest = new DesktopWebDriverRequest();
            finalRequest.setSessionKey(request.getSessionKey());
            finalRequest.setBrowser(request.getBrowser());
            finalRequest.setBrowserVersion(request.getBrowserVersion());
            request.getBaseUrl().ifPresent(finalRequest::setBaseUrl);
        } else {
            throw new SystemException(request.getClass().getSimpleName() + " is not allowed here");
        }

        /*
        set webdriver mode
         */
        if (finalRequest.getWebDriverMode() == null) {
            finalRequest.setWebDriverMode(WebDriverManager.getConfig().getWebDriverMode());
        }
        return finalRequest;
    }

    @Override
    protected DesiredCapabilities buildCapabilities(DesiredCapabilities preSetCaps, DesktopWebDriverRequest desktopWebDriverRequest) {
        return DesktopWebDriverCapabilities.createCapabilities(WebDriverManager.getConfig(), preSetCaps, desktopWebDriverRequest);
    }

    @Override
    public WebDriver getRawWebDriver(DesktopWebDriverRequest request, DesiredCapabilities desiredCapabilities, SessionContext sessionContext) {
        /*
        start the session
         */
        WebDriver driver = startSession(request, desiredCapabilities, sessionContext);

        request.getBaseUrl().ifPresent(baseUrl -> {
            try {
                log().info("Opening baseUrl: " + baseUrl.toString());
                StopWatch.startPageLoad(driver);
                driver.get(baseUrl.toString());
            } catch (Exception e) {
                log().error("Unable to open baseUrl", e);
            }
        });
        return driver;
    }

    private WebDriver startSession(
            DesktopWebDriverRequest desktopWebDriverRequest,
            DesiredCapabilities desiredCapabilities,
            SessionContext sessionContext
    ) {

        /*
        if there is a factories entry for the requested browser, then create the new (raw) instance here and wrap it directly in EventFiringWD
         */
        if (Flags.REUSE_DATAPROVIDER_DRIVER_BY_THREAD) {
            String threadName = Thread.currentThread().getId() + "";
            String testMethodName = ExecutionContextUtils.getMethodNameFromCurrentTestResult();

            if (testMethodName != null) {
                WebDriver driver = DriverStorage.getDriverByTestMethodName(testMethodName, threadName);
                if (driver != null) {
                    log().info("Re-Using WebDriver for " + testMethodName + ": " + threadName + " driver: " + driver);

                    // cleanup session
                    driver.manage().deleteAllCookies();

                    /*
                    Open url
                     */
                    desktopWebDriverRequest.getBaseUrl().ifPresent(baseUrl -> {
                        log().info("Opening baseUrl with reused driver: " + baseUrl);
                        StopWatch.startPageLoad(driver);
                        driver.get(baseUrl.toString());
                    });
                    return driver;
                } else {
                    return newWebDriver(desktopWebDriverRequest, desiredCapabilities, sessionContext);
                }
            }
        } else {
            /*
            regular branch to create a new web driver session
             */
            return newWebDriver(desktopWebDriverRequest, desiredCapabilities, sessionContext);
        }

        throw new SystemException("WebDriverManager is in a bad state. Please report this to the tt. developers.");
    }

    @Override
    public void setupSession(EventFiringWebDriver eventFiringWebDriver, DesktopWebDriverRequest request) {
        final String browser = request.getBrowser();

        // add event listeners
        eventFiringWebDriver.register(new VisualEventDriverListener());
        eventFiringWebDriver.register(new EventLoggingEventDriverListener());

        /*
         start StopWatch
          */
        StopWatch.startPageLoad(eventFiringWebDriver);

        WebDriverManagerConfig config = WebDriverManager.getConfig();
        WebDriver.Window window = eventFiringWebDriver.manage().window();
        /*
         Maximize
         */
        if (config.shouldMaximizeViewport()) {
            log().debug("Trying to maximize window");
            try {
                Dimension originWindowSize = window.getSize();
                // Maximize to detect window size
                window.maximize();
                if (config.getMaximizePosition() != Position.CENTER) {
                    log().debug(String.format("Setting maximized window position to: %s", config.getMaximizePosition()));
                    Point targetPosition = new Point(0, 0);
                    switch (config.getMaximizePosition()) {
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
                setWindowSizeBasedOnDisplayResolution(window, request);
            }
        } else {
            setWindowSizeBasedOnDisplayResolution(window, request);
        }

        if (!Browsers.safari.equalsIgnoreCase(browser)) {
            int pageLoadTimeout = Constants.PAGE_LOAD_TIMEOUT_SECONDS;
            int scriptTimeout = PropertyManager.getIntProperty(TesterraProperties.WEBDRIVER_TIMEOUT_SECONDS_SCRIPT, 120);
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

    private void setWindowSize(WebDriver.Window window, Dimension dimension) {
        window.setPosition(new Point(0, 0));
        window.setSize(dimension);
    }

    private void setWindowSizeBasedOnDisplayResolution(WebDriver.Window window, DesktopWebDriverRequest request) {
        Dimension dimension = request.getWindowSize();

        try {
            setWindowSize(window, dimension);
        } catch (Throwable t) {
            log().error("Could not set window size", t);

            if (Browsers.edge.equals(request.getBrowser())) {
                log().debug("Edge Browser was requested, trying workaround");

                Timer timer = new Timer(500, 5000);
                ThrowablePackedResponse<Object> response = timer.executeSequence(new Timer.Sequence<Object>() {
                    @Override
                    public void run() throws Throwable {
                        setSkipThrowingException(true);
                        setWindowSize(window, dimension);
                    }
                });

                if (!response.isSuccessful()) {
                    log().error("Finally, could not set Edge Window size", response.getThrowable());
                }
            }
        }
    }

    private WebDriver newWebDriver(
            DesktopWebDriverRequest desktopWebDriverRequest,
            DesiredCapabilities capabilities,
            SessionContext sessionContext
    ) {
        final String browser = desktopWebDriverRequest.getBrowser();

        /*
         * Remote or local
         */
        WebDriver newDriver;
        URL remoteAddress = null;
        if (desktopWebDriverRequest.getWebDriverMode() == WebDriverMode.remote) {
            remoteAddress = desktopWebDriverRequest.getSeleniumServerUrl();
        }

        /*
         * Start a new web driver session.
         */
        try {
            if (browser.equals(Browsers.htmlunit)) {
                capabilities.setBrowserName(BrowserType.HTMLUNIT);
                capabilities.setJavascriptEnabled(false);
                log().info("Starting HtmlUnitRemoteWebDriver.");
                newDriver = new RemoteWebDriver(remoteAddress, capabilities);
            } else {
                newDriver = startNewWebDriverSession(desktopWebDriverRequest, capabilities, remoteAddress, sessionContext);
            }
        } catch (final SetupException e) {
            int ms = Constants.WEBDRIVER_START_RETRY_TIME_IN_MS;
            log().error(String.format("Error starting WebDriver. Trying again in %d seconds", (ms / 1000)), e);
            TimerUtils.sleep(ms);
            newDriver = startNewWebDriverSession(desktopWebDriverRequest, capabilities, remoteAddress, sessionContext);
        }

        //STARTUP_TIME_COLLECTOR.add(new TimingInfo("SessionStartup", "", sw.getTime(TimeUnit.MILLISECONDS), System.currentTimeMillis()));

        return newDriver;
    }

    /**
     * Remote when remoteAdress != null, local need browser to be set.
     */
    private WebDriver startNewWebDriverSession(
            AbstractWebDriverRequest request,
            DesiredCapabilities capabilities,
            URL remoteAddress,
            SessionContext sessionContext
    ) {

        String browser = request.getBrowser();
        UserAgentConfig userAgentConfig = WebDriverManager.getUserAgentConfig(browser);
        Capabilities finalCapabilities;
        Class<? extends RemoteWebDriver> driverClass;

        switch (browser) {
            case Browsers.firefox:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (capabilities.getCapabilityNames().contains(FirefoxOptions.FIREFOX_OPTIONS)) {
                    log().warn("Do not add FirefoxOptions via 'WebDriverManager.setGlobalExtraCapability(..)'! It will be ignored.");
                    log().warn("Use 'WebDriverManager.setUserAgentConfig(Browsers.firefox, (FirefoxConfig) options -> {...}' instead.");
//                    final TreeMap predefinedFirefoxOptions = (TreeMap) capabilities.getCapability(FirefoxOptions.FIREFOX_OPTIONS);
//                    predefinedFirefoxOptions.forEach((s, o) -> firefoxOptions.setCapability(s.toString(), o));
                }
                if (userAgentConfig != null) {
                    userAgentConfig.configure(firefoxOptions);
                }
                firefoxOptions.merge(capabilities);
                finalCapabilities = firefoxOptions;
                driverClass = FirefoxDriver.class;
                break;
            case Browsers.ie:
                InternetExplorerOptions ieOptions = new InternetExplorerOptions();
                if (userAgentConfig != null) {
                    userAgentConfig.configure(ieOptions);
                }
                ieOptions.merge(capabilities);
                ieOptions.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                finalCapabilities = ieOptions;
                driverClass = InternetExplorerDriver.class;
                break;
            case Browsers.chrome:
            case Browsers.chromeHeadless:
                ChromeOptions chromeOptions = new ChromeOptions();
                if (capabilities.getCapabilityNames().contains(ChromeOptions.CAPABILITY)) {
                    log().warn("Do not add ChromeOptions via 'WebDriverManager.setGlobalExtraCapability(..)'! It will be ignored.");
                    log().warn("Use 'WebDriverManager.setUserAgentConfig(Browsers.chrome, (ChromeConfig) options -> {...}' instead.");
//                    final TreeMap predefinedChromeOptions = (TreeMap) capabilities.getCapability(ChromeOptions.CAPABILITY);
//                    predefinedChromeOptions.forEach((s, o) -> chromeOptions.setCapability(s.toString(), o));
                }

                if (userAgentConfig != null) {
                    userAgentConfig.configure(chromeOptions);
                }

                chromeOptions.merge(capabilities);
                chromeOptions.addArguments("--no-sandbox");
                if (browser.equals(Browsers.chromeHeadless)) {
                    chromeOptions.setHeadless(true);
                }
                finalCapabilities = chromeOptions;
                driverClass = ChromeDriver.class;
                break;
            case Browsers.phantomjs:
                File phantomjsFile = getPhantomJSBinary();
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjsFile.getAbsolutePath());
                capabilities.setBrowserName(BrowserType.PHANTOMJS);
                capabilities.setJavascriptEnabled(true);

                String[] args = {
                        "--ssl-protocol=any"
                };
                capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, args);
                finalCapabilities = capabilities;
                driverClass = PhantomJSDriver.class;
                break;
            case Browsers.safari:
                SafariOptions safariOptions = new SafariOptions();
                if (userAgentConfig != null) {
                    userAgentConfig.configure(safariOptions);
                }
                safariOptions.merge(capabilities);
                finalCapabilities = safariOptions;
                driverClass = SafariDriver.class;
                break;
            case Browsers.edge:
                EdgeOptions edgeOptions = new EdgeOptions();
                if (userAgentConfig != null) {
                    userAgentConfig.configure(edgeOptions);
                }
                edgeOptions.merge(capabilities);
                finalCapabilities = edgeOptions;
                driverClass = EdgeDriver.class;
                break;
            default:
                throw new SystemException("Browser must be set through SystemProperty 'browser' or in test.properties file! + is: " + browser);
        }

        Map<String, Object> cleanedCapsMap = new CapabilityUtils().clean(finalCapabilities);
        sessionContext.setCapabilities(cleanedCapsMap);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        log().info(String.format(
                "Requesting%s WebDriver (sessionKey=%s)%s with capabilities:\n%s",
                (remoteAddress != null ? " remote" : ""),
                sessionContext.getSessionKey(),
                (remoteAddress != null ? " on host " + remoteAddress : ""),
                gson.toJson(cleanedCapsMap)
        ));
        log().debug(String.format("Starting (session key=%s) here", sessionContext.getSessionKey()), new Throwable());

        WebDriver driver;
        try {
            if (remoteAddress != null) {
                final HttpCommandExecutor httpCommandExecutor = new HttpCommandExecutor(new HashMap<>(), remoteAddress, new HttpClientFactory());
                driver = new SikuliWebDriver(httpCommandExecutor, finalCapabilities);
                ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
                sessionContext.setNodeInfo(new NodeInfo(remoteAddress.getHost(), remoteAddress.getPort()));
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
}
