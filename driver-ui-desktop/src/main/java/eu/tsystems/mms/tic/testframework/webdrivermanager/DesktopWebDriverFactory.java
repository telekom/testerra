/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.constants.Constants;
import eu.tsystems.mms.tic.testframework.constants.ErrorMessages;
import eu.tsystems.mms.tic.testframework.constants.FennecProperties;
import eu.tsystems.mms.tic.testframework.exceptions.FennecRuntimeException;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSetupException;
import eu.tsystems.mms.tic.testframework.exceptions.FennecSystemException;
import eu.tsystems.mms.tic.testframework.internal.Defaults;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.StopWatch;
import eu.tsystems.mms.tic.testframework.internal.TimingInfo;
import eu.tsystems.mms.tic.testframework.internal.utils.DriverStorage;
import eu.tsystems.mms.tic.testframework.internal.utils.TimingInfosCollector;
import eu.tsystems.mms.tic.testframework.model.NodeInfo;
import eu.tsystems.mms.tic.testframework.pageobjects.clickpath.ClickpathEventListener;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import eu.tsystems.mms.tic.testframework.sikuli.FennecWebDriver;
import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.FileUtils;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.TestUtils;
import eu.tsystems.mms.tic.testframework.utils.Timer;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import net.anthavio.phanbedder.Phanbedder;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Created by pele on 19.07.2017.
 */
public class DesktopWebDriverFactory extends WebDriverFactory<DesktopWebDriverRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DesktopWebDriverFactory.class);

    @Override
    protected DesktopWebDriverRequest buildRequest(WebDriverRequest request) {
        DesktopWebDriverRequest r;
        if (request instanceof DesktopWebDriverRequest) {
            r = (DesktopWebDriverRequest) request;
        }
        else if (request instanceof UnspecificWebDriverRequest) {
            r = new DesktopWebDriverRequest();
            r.copyFrom(request);
        }
        else {
            throw new FennecSystemException(request.getClass().getSimpleName() +  " is not allowed here");
        }

        /*
        set webdriver mode
         */
        if (r.webDriverMode == null) {
            r.webDriverMode = WebDriverManager.config().webDriverMode;
        }

        return r;
    }

    @Override
    protected DesiredCapabilities buildCapabilities(DesiredCapabilities preSetCaps, DesktopWebDriverRequest desktopWebDriverRequest) {
        return DesktopWebDriverCapabilities.createCapabilities(WebDriverManager.config(), preSetCaps, desktopWebDriverRequest);
    }

    @Override
    public WebDriver getRawWebDriver(DesktopWebDriverRequest request, DesiredCapabilities desiredCapabilities) {
        /*
        start the session
         */
        WebDriver driver = startSession(request, desiredCapabilities);

        /*
        Open url
         */
        final String baseUrl = request.baseUrl;
        LOGGER.info(logSCID() + "Opening baseUrl: " + baseUrl);
        StopWatch.startPageLoad(driver);
        try {
            driver.get(baseUrl);
        } catch (Exception e) {
            if (StringUtils.containsAll(e.getMessage(), true, "Reached error page", "connectionFailure")) {
                throw new FennecRuntimeException("Could not start driver session, because of unreachable url: " + request.baseUrl, e);
            }
            throw e;
        }

        return driver;
    }

    private WebDriver startSession(DesktopWebDriverRequest desktopWebDriverRequest, DesiredCapabilities desiredCapabilities) {

        /*
        if there is a factories entry for the requested browser, then create the new (raw) instance here and wrap it directly in EventFiringWD
         */
        if (Flags.REUSE_DATAPROVIDER_DRIVER_BY_THREAD) {
            String threadName = Thread.currentThread().getId() + "";
            String testMethodName = ExecutionContextUtils.getMethodNameFromCurrentTestResult();

            if (testMethodName != null) {
                WebDriver driver = DriverStorage.getDriverByTestMethodName(testMethodName, threadName);
                if (driver != null) {
                    LOGGER.info(logSCID() + "Re-Using WebDriver for " + testMethodName + ": " + threadName + " driver: " + driver);

                    // cleanup session
                    driver.manage().deleteAllCookies();

                    /*
                    Open url
                     */
                    final String baseUrl = WebDriverManagerUtils.getBaseUrl(desktopWebDriverRequest.baseUrl);
                    LOGGER.info(logSCID() + "Opening baseUrl with reused driver: " + baseUrl);
                    StopWatch.startPageLoad(driver);
                    driver.get(baseUrl);

                    return driver;
                } else {
                    return newWebDriver(desktopWebDriverRequest, desiredCapabilities);
                }
            }
        } else {
            /*
            regular branch to create a new web driver session
             */
            return newWebDriver(desktopWebDriverRequest, desiredCapabilities);
        }

        throw new FennecSystemException("WebDriverManager is in a bad state. Please report this to the fennec developers.");
    }

    @Override
    public void setupSession(EventFiringWebDriver eventFiringWebDriver, DesktopWebDriverRequest request) {
        final String browser = request.browser;

        // activate clickpath event listener
        eventFiringWebDriver.register(new ClickpathEventListener());

        // add event listeners
        eventFiringWebDriver.register(new VisualEventDriverListener());
        eventFiringWebDriver.register(new EventLoggingEventDriverListener());

        /*
         start StopWatch
          */
        StopWatch.startPageLoad(eventFiringWebDriver);

        /*
         Maximize
         */
        if (WebDriverManager.config().maximize) {
            LOGGER.info(logSCID() + "Trying to maximize window");
            try {
                eventFiringWebDriver.manage().window().maximize();
            } catch (Throwable t1) {
                LOGGER.info(logSCID() + "Could not maximize window", t1);

                String res = Defaults.DISPLAY_RESOLUTION;
                LOGGER.info(logSCID() + "Trying to execute setSize() to " + res + " as a workaround");
                String[] split = res.split("x");
                int width = Integer.valueOf(split[0]);
                int height = Integer.valueOf(split[1]);
                try {
                    eventFiringWebDriver.manage().window().setSize(new Dimension(width, height));
                } catch (Throwable t2) {
                    LOGGER.error(logSCID() + "Could not set window size", t2);

                    if (Browsers.edge.equals(browser)) {
                        LOGGER.info(logSCID() + "Edge Browser was requested, trying a second workaround");

                        Timer timer = new Timer(500, 5000);
                        ThrowablePackedResponse<Object> response = timer.executeSequence(new Timer.Sequence<Object>() {
                            @Override
                            public void run() throws Throwable {
                                setSkipThrowingException(true);
                                LOGGER.info(logSCID() + "Trying setPosition() and setSize()");
                                try {
                                    eventFiringWebDriver.manage().window().setPosition(new Point(0, 0));
                                    eventFiringWebDriver.manage().window().setSize(new Dimension(width, height));
                                    LOGGER.info(logSCID() + "Yup, success!");
                                } catch (Exception e) {
                                    LOGGER.warn(logSCID() + "Nope. Got error: " + e.getMessage());
                                    throw e;
                                }
                            }
                        });

                        if (!response.isSuccessful()) {
                            LOGGER.error("Finally, could not set Edge Window size", response.getThrowable());
                        }
                    }
                }
            }
        }

        if (!Browsers.safari.equalsIgnoreCase(browser)) {
            int pageLoadTimeout = Constants.PAGE_LOAD_TIMEOUT_SECONDS;
            int scriptTimeout = PropertyManager.getIntProperty(FennecProperties.WEBDRIVER_TIMEOUT_SECONDS_SCRIPT, 120);
            try {
                eventFiringWebDriver.manage().timeouts().pageLoadTimeout(pageLoadTimeout, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.error(logSCID() + "Could not set Page Load Timeout", e);
            }
            try {
                eventFiringWebDriver.manage().timeouts().setScriptTimeout(scriptTimeout, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.error(logSCID() + "Could not set Script Timeout", e);
            }
        } else {
            LOGGER.warn(logSCID() + "Not setting timeouts for Safari.");
        }
    }

    private WebDriver newWebDriver(DesktopWebDriverRequest desktopWebDriverRequest, DesiredCapabilities capabilities) {
        String sessionKey = desktopWebDriverRequest.sessionKey;

        final String url = getRemoteServerUrl(desktopWebDriverRequest);

        final String browser = desktopWebDriverRequest.browser;
        /*
         * Remote or local
         */
        WebDriver newDriver;
        if (desktopWebDriverRequest.webDriverMode == WebDriverMode.remote) {
            /*
             ##### Remote
             */

            URL remoteAddress;
            try {
                remoteAddress = new URL(url);
            } catch (final MalformedURLException e) {
                throw new FennecRuntimeException("MalformedUrlException while building Remoteserver URL: " + url, e);
            }

            /*
             * Start a new web driver session.
             */

            String msg = "on " + remoteAddress;
            Object ffprofile = capabilities.getCapability(FirefoxDriver.PROFILE);
            if (ffprofile != null && ffprofile instanceof FirefoxProfile) {
                try {
                    double size = ((double) ((FirefoxProfile) ffprofile).toJson().getBytes().length / 1024);
                    long sizeInKB = Math.round(size);
                    msg += "\n ffprofile size=" + sizeInKB + " KB";
                } catch (IOException e) {
                    // ignore silently
                }
            }

            try {
                switch (browser) {
                    case Browsers.htmlunit:
                        LOGGER.info(logSCID() + "Starting HtmlUnitRemoteWebDriver.");
                        newDriver = new RemoteWebDriver(remoteAddress, capabilities);
                        break;
                    /*
                    Rest: fallthrough
                     */
                    case Browsers.firefox:
                    case Browsers.chrome:
                    case Browsers.chromeHeadless:
                    case Browsers.ie:
                    case Browsers.safari:
                    default:
                        newDriver = startNewWebDriverSession(browser, capabilities, remoteAddress, msg, sessionKey);
                }
            } catch (final FennecSetupException e) {
                int ms = Constants.WEBDRIVER_START_RETRY_TIME_IN_MS;
                LOGGER.error(logSCID() + "Error starting WebDriver. Trying again in "
                        + (ms / 1000) + " seconds.", e);
                TestUtils.sleep(ms);
                newDriver = startNewWebDriverSession(browser, capabilities, remoteAddress, msg, sessionKey);
            }

        } else {
            /*
             ##### Local
             */
            String msg = "locally";
            newDriver = startNewWebDriverSession(browser, capabilities, null, msg, sessionKey);
        }

        /*
        Log session id
         */
        SessionId webDriverSessionId = ((RemoteWebDriver) newDriver).getSessionId();
        desktopWebDriverRequest.storedSessionId = webDriverSessionId.toString();
        LOGGER.info(logSCID() + "Remote Session ID: " + webDriverSessionId);

        /*
        Log User Agent and executing host
         */
        NodeInfo nodeInfo = DesktopWebDriverUtils.getNodeInfo(desktopWebDriverRequest);
        desktopWebDriverRequest.storedExecutingNode = nodeInfo;
        LOGGER.info(logSCID() + "Executing Node " + nodeInfo.toString());
        WebDriverManager.addExecutingSeleniumHostInfo(sessionKey + ": " + nodeInfo.toString());
        WebDriverManagerUtils.logUserAgent(sessionKey, newDriver, nodeInfo);

        return newDriver;
    }

    public static String getRemoteServerUrl(DesktopWebDriverRequest desktopWebDriverRequest) {
        String host = StringUtils.getFirstValidString(desktopWebDriverRequest.seleniumServerHost, PropertyManager.getProperty(FennecProperties.SELENIUM_SERVER_HOST), "localhost");
        String port = StringUtils.getFirstValidString(desktopWebDriverRequest.seleniumServerPort, PropertyManager.getProperty(FennecProperties.SELENIUM_SERVER_PORT), "4444");
        String url = StringUtils.getFirstValidString(desktopWebDriverRequest.seleniumServerURL, PropertyManager.getProperty(FennecProperties.SELENIUM_SERVER_URL), "http://" + host + ":" + port + "/wd/hub");

        // set backwards
        try {
            URL url1 = new URL(url);
            host = url1.getHost();
            port = url1.getPort() + "";
        } catch (MalformedURLException e) {
            LOGGER.error("INTERNAL ERROR: Could not parse URL", e);
        }
        desktopWebDriverRequest.seleniumServerHost = host;
        desktopWebDriverRequest.seleniumServerPort = port;
        desktopWebDriverRequest.seleniumServerURL = url;
        return url;
    }

    /**
     * Remote when remoteAdress != null, local need browser to be set.
     *
     * @param browser       .
     * @param capabilities  .
     * @param remoteAddress .
     * @return.
     */
    private WebDriver startNewWebDriverSession(String browser, DesiredCapabilities capabilities, URL remoteAddress, String msg, String sessionKey) {
        WebDriver driver;
        LOGGER.info(logSCID() + "Starting WebDriver (" + sessionKey + ") " + msg, new NewSessionMarker());
        org.apache.commons.lang3.time.StopWatch sw = new org.apache.commons.lang3.time.StopWatch();
        sw.start();

        final String errorMessage = "Error starting browser session";
        if (remoteAddress != null) {
            /*
            remote mode
             */
            try {
                driver = new FennecWebDriver(remoteAddress, capabilities);
            } catch (Exception e) {
                throw new FennecSetupException(errorMessage, e);
            }

            // set local file detector
            ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
        } else if (browser != null) {
            /*
             local mode
              */
            switch (browser) {
                case Browsers.firefox:
                    driver = new FirefoxDriver(capabilities);
                    break;
                case Browsers.ie:
                    driver = new InternetExplorerDriver(capabilities);
                    break;
                case Browsers.chrome:
                    driver = new ChromeDriver(capabilities);
                    break;
                case Browsers.chromeHeadless:
                    driver = new ChromeDriver(capabilities);
                    break;
                case Browsers.htmlunit:
                    driver = new HtmlUnitDriver(capabilities);
                    break;
                case Browsers.phantomjs:
                    File phantomjsFile = getPhantomJSBinary();
                    capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, phantomjsFile.getAbsolutePath());

                    String[] args = {
                        "--ssl-protocol=any"
                    };
                    capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, args);
                    driver = new PhantomJSDriver(capabilities);
                    break;
                case Browsers.safari:
                    driver = new SafariDriver(capabilities);
                    break;
                case Browsers.edge:
                    driver = new EdgeDriver(capabilities);
                    break;
                default:
                    throw new FennecSystemException(ErrorMessages.browserNotSupportedHere(browser));
            }
        } else {
            throw new FennecSystemException("Internal Error when starting webdriver.");
        }

        sw.stop();
        LOGGER.info(logSCID() + "Session startup time: " + sw.toString());
        STARTUP_TIME_COLLECTOR.add(new TimingInfo("SessionStartup", "", sw.getTime(TimeUnit.MILLISECONDS), System.currentTimeMillis()));

        return driver;
    }

    public static final TimingInfosCollector STARTUP_TIME_COLLECTOR = new TimingInfosCollector();

    private static File phantomjsFile = null;

    private File getPhantomJSBinary() {
        if (phantomjsFile == null) {
            LOGGER.info(logSCID() + "Unpacking phantomJS...");
            try {
                phantomjsFile = Phanbedder.unpack(); //Phanbedder to the rescue!
            } catch (Exception e) {
                if (e.getMessage() != null && e.getMessage().toLowerCase().contains("failed to make target directory")) {
                    File tmp = new File(FileUtils.getTempDirectory(), "phantomjs" + System.currentTimeMillis());
                    phantomjsFile = Phanbedder.unpack(tmp);
                }
                else {
                    throw e;
                }
            }
            LOGGER.info(logSCID() + "Unpacked phantomJS to: " + phantomjsFile);
        }
        return phantomjsFile;
    }

}
