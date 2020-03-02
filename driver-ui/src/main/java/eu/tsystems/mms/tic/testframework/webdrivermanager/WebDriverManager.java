/*
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
 *     Peter Lehmann
 *     pele
*/
package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.PropertyManager;
import eu.tsystems.mms.tic.testframework.constants.TesterraProperties;
import eu.tsystems.mms.tic.testframework.exceptions.TesterraSystemException;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.WebDriverSessionHandler;
import eu.tsystems.mms.tic.testframework.execution.worker.finish.WebDriverSessionsAfterMethodWorker;
import eu.tsystems.mms.tic.testframework.internal.Flags;
import eu.tsystems.mms.tic.testframework.internal.utils.DriverStorage;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import eu.tsystems.mms.tic.testframework.useragents.UserAgentConfig;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.watchdog.WebDriverWatchDog;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides threadsafe WebDriver and Selenium objects. These objects are needed for correct logging and reporting.
 *
 * @author sepr
 */
public final class WebDriverManager {

    static {
        UITestUtils.initializePerfTest();

        // start WatchDog for hanging sessions
        boolean watchdogEnabled = PropertyManager.getBooleanProperty(TesterraProperties.WATCHDOG_ENABLE, true);
        if (watchdogEnabled) {
            WebDriverWatchDog.start();
        }
    }

    public static final String DEFAULT_SESSION_KEY = "default";

    /**
     * WebDriverManager configuration set. Modify by config() call!
     */
    private static WebDriverManagerConfig webdriverManagerConfig;
    /**
     * Executing selenium hosts. Package local access for WDInternal class.
     */
    static final ThreadLocal<String> EXECUTING_SELENIUM_HOSTS_PER_THREAD = new ThreadLocal<>();

    /**
     * The preset baseURL. Set by setBaseURL().
     */
    private static String presetBaseURL = null;

    private static final HashMap<String, UserAgentConfig> userAgentConfigurators = new HashMap<>();

    /**
     * Private constructor to hide the public one since this a static only class.
     */
    private WebDriverManager() {
    }

    /**
     * Adds extra capability to desired capabilities.
     *
     * @param key   The key of the capability to set.
     * @param value The value of the capability to set.
     */
    public static void setGlobalExtraCapability(final String key, final Object value) {
        addGlobalCapability(key, value);
    }

    /**
     * Adds extra capabilities to desired capabilities.
     *
     * @param desiredCapabilities .
     */
    public static void setGlobalExtraCapabilities(final DesiredCapabilities desiredCapabilities) {
        WebDriverCapabilities.setGlobalExtraCapabilities(desiredCapabilities);
    }

    /**
     * Get all set extra capabilities (not the DesiredCapabilities that are set automatically
     *
     * @return
     */
    public static Map<String, Object> getGlobalExtraCapabilities() {
        return WebDriverCapabilities.getGlobalExtraCapabilities();
    }

    /**
     * Adds a capability.
     *
     * @param key   The key of the capability to set.
     * @param value The value of the capability to set.
     */
    private static void addGlobalCapability(String key, Object value) {
        WebDriverCapabilities.addGlobalCapability(key, value);
    }

    /**
     * Get set ThreadCapabilities or null.
     *
     * @return .
     */
    public static Map<String, Object> getThreadCapabilities() {
        return WebDriverCapabilities.getThreadCapabilities();
    }

    /**
     * Add a thread local capability. Will be removed when shutdown is called.
     *
     * @param key   .
     * @param value .
     */
    public static void addThreadCapability(String key, Object value) {
        WebDriverCapabilities.addThreadCapability(key, value);
    }

    /**
     * Remove extra capability from capabilities.
     *
     * @param key The key of the capability to remove.
     */
    public static void removeGlobalExtraCapability(final String key) {
        WebDriverCapabilities.removeGlobalExtraCapability(key);
    }

    /**
     * Sets the baseURL.
     *
     * @param baseURL Base URL for tests.
     */
    public static void setBaseURL(final String baseURL) {
        presetBaseURL = baseURL;
    }

    /**
     * Returns the tt. test base url.
     *
     * @return base url as string.
     */
    public static String getBaseURL() {
        return WebDriverManagerUtils.getBaseUrl(presetBaseURL);
    }

    /**
     * Checks that EVENTFIRINGWEBDRIVER_MAP and SELENIUM_MAP are not null.
     */
    private static void checkMaps() {
    }

    /**
     * Getter for webDriver instance.
     *
     * @return instance of WebDriver object.
     */
    public static WebDriver getWebDriver() {
        return getWebDriver(DEFAULT_SESSION_KEY);
    }

    /**
     * Getter for webDriver instance.
     *
     * @param sessionKey The storedSessionId to get the webDriver instance from map.
     * @return instance of WebDriver object.
     */
    public static WebDriver getWebDriver(final String sessionKey) {
        UnspecificWebDriverRequest webDriverRequest = new UnspecificWebDriverRequest();
        webDriverRequest.sessionKey = sessionKey;
        return getWebDriver(webDriverRequest);
    }

    public static WebDriver getWebDriver(WebDriverRequest webDriverRequest) {
        return WebDriverSessionsManager.getWebDriver(webDriverRequest);
    }

    public static void registerWebDriverStartUpHandler(WebDriverSessionHandler webDriverSessionHandler) {
        WebDriverSessionsManager.addWebDriverStartUpHandler(webDriverSessionHandler);
    }

    public static void registerWebDriverShutDownHandler(WebDriverSessionHandler webDriverSessionHandler) {
        WebDriverSessionsAfterMethodWorker.register(webDriverSessionHandler);
    }

    public static void registerWebDriverShutDownHandler(Runnable afterQuit) {
        WebDriverSessionsManager.afterQuitActions.add(afterQuit);
    }

    public static void registerWebDriverMethodShutDownHandler(WebDriverSessionHandler webDriverSessionHandler) {
        WebDriverSessionsAfterMethodWorker.register(webDriverSessionHandler);
    }


    /**
     * Introduce own Webdriver and Selenium drivers.
     *
     * @param sessionKey .
     * @param driver     .
     */
    public static void introduceDrivers(final String sessionKey, final WebDriver driver) {
        introduceWebDriver(sessionKey, driver);
    }

    /**
     * Introduce own Webdriver and Selenium drivers.
     *
     * @param driver   .
     */
    public static void introduceDrivers(final WebDriver driver) {
        introduceDrivers(DEFAULT_SESSION_KEY, driver);
    }

    /**
     * Introduce an own webdriver object. Selenium session will be released in this case.
     *
     * @param driver .
     */
    public static void introduceWebDriver(final WebDriver driver) {
        introduceWebDriver(DEFAULT_SESSION_KEY, driver);
    }

    /**
     * Introduce an own webdriver object. Selenium session will be released in this case.
     *
     * @param driver     .
     * @param sessionKey .
     */
    public static void introduceWebDriver(final String sessionKey, final WebDriver driver) {
        WebDriverSessionsManager.introduceWebDriver(sessionKey, driver);
    }

    /**
     * Cleanup all threadlocals.
     */
    public static void cleanupThreadlocals() {
        resetExecutingSeleniumHosts();
    }

    /**
     * checks if web driver is active
     *
     * @return true if webdriver is active, else false.
     */
    protected static Boolean isWebDriverActive() {
        return WebDriverSessionsManager.hasAnySessionActive();
    }

    /**
     * Closes all windows and active Selenium and/or WebDriver instances.
     */
    public static void forceShutdown() {
        realShutdown(true);
    }

    /**
     * Closes all windows and active Selenium and/or WebDriver instances.
     *
     * @param force Handles the report of Windows beside WebDriverManagerConfig.executeCloseWindows.
     */
    private static void realShutdown(final boolean force) {
        if (config().executeCloseWindows || force) {
            if (WebDriverManager.isWebDriverActive()) {
                WebDriverSessionsManager.shutDownAllThreadSessions();
                WebDriverCapabilities.clearThreadCapabilities();
            }

            if (Flags.REUSE_DATAPROVIDER_DRIVER_BY_THREAD) {
                String testMethodName = ExecutionContextUtils.getMethodNameFromCurrentTestResult();
                DriverStorage.removeSpecificDriver(testMethodName);
            }
        }
    }

    /**
     * Closes all windows and active Selenium and/or WebDriver instances.
     */
    public static void shutdown() {
        realShutdown(false);
    }

    /**
     * Returns the WebDriverManagerConfig Object.
     *
     * @return .
     */
    public static WebDriverManagerConfig config() {
        if (webdriverManagerConfig==null) {
            webdriverManagerConfig = new WebDriverManagerConfig();
        }
        return webdriverManagerConfig;
    }

    /**
     * Returns a boolean status of the javascript activation in the browser.
     *
     * @return true is js is activated, false otherwise
     */
    public static boolean isJavaScriptActivated(final String sessionId) {
        return pIsJavaScriptActivated(sessionId);
    }

    /**
     * Returns a boolean status of the javascript activation in the browser.
     *
     * @return true is js is activated, false otherwise
     */
    public static boolean isJavaScriptActivatedInDefaultSession() {
        return pIsJavaScriptActivated(DEFAULT_SESSION_KEY);
    }

    /**
     * Returns a boolean status of the javascript activation in the browser.
     *
     * @return true is js is activated, false otherwise
     */
    private static boolean pIsJavaScriptActivated(final String sessionId) {
        final WebDriver driver = getWebDriver(sessionId);
        RemoteWebDriver rawDriver;
        if (driver instanceof RemoteWebDriver) {
            rawDriver = (RemoteWebDriver) driver;
        }
        else if (driver instanceof EventFiringWebDriver){
            rawDriver = (RemoteWebDriver) ((EventFiringWebDriver) driver).getWrappedDriver();
        }
        else {
            throw new TesterraSystemException("WebDriver object is not a RemoteWebDriver");
        }

        try {
            rawDriver.executeScript("return true;");
        } catch (final WebDriverException e) {
                /*
                 * Javascript is not running. This call is throwing org.openqa.SELENIUM_MAP.WebDriverException: waiting
                 * for evaluate.js load failed
                 */
            return false;
        }
        return true;
    }


    /**
     * Returns the configured element timeout in seconds.
     *
     * @return element timeout in seconds.
     */
    public static int getElementTimeoutInSeconds() {
        return POConfig.getUiElementTimeoutInSeconds();
    }

    /**
     * Returns true if any session is active.
     *
     * @return .
     */
    public static boolean hasAnySessionActive() {
        checkMaps();
        return WebDriverSessionsManager.hasAnySessionActive();
    }

    public static boolean hasSessionsActiveInThisThread() {
        checkMaps();
        return WebDriverSessionsManager.hasSessionActiveInThisThread();
    }

    /**
     * Returns the executing selenium host for the current thread.
     *
     * @return Executing selenium host.
     */
    public static String getExecutingSeleniumHosts() {
        return EXECUTING_SELENIUM_HOSTS_PER_THREAD.get();
    }

    static void addExecutingSeleniumHostInfo(final String host) {
        String executingSeleniumHost = EXECUTING_SELENIUM_HOSTS_PER_THREAD.get();
        if (!StringUtils.isStringEmpty(executingSeleniumHost)) {
            EXECUTING_SELENIUM_HOSTS_PER_THREAD.set(executingSeleniumHost + "\n" + host);
        } else {
            EXECUTING_SELENIUM_HOSTS_PER_THREAD.set(host);
        }
    }

    /**
     * Reset selenium hosts list.
     */
    public static void resetExecutingSeleniumHosts() {
        EXECUTING_SELENIUM_HOSTS_PER_THREAD.set("");
    }

    /**
     * Are you sure you want do that?? This action quits all browser sessions in all threads.
     * Does not close windows when executeCloseWindows == false.
     *
     * @deprecated Use forceShotDownAllThreads, does the same thing, but sounds more dangerous.
     */
    @Deprecated
    public static void shutdownAllThreads() {
        pRealShutdownAllThreads(false);
    }

    /**
     * Are you sure you want do that?? This action quits all browser sessions in all threads.
     */
    public static void forceShutdownAllThreads() {
        pRealShutdownAllThreads(true);
    }

    private static void pRealShutdownAllThreads(final boolean force) {
        if (config().executeCloseWindows || force) {
            WebDriverSessionsManager.shutDownAllSessions();
            WDInternal.cleanupDriverReferencesInCurrentThread();
        }
    }

    /**
     * Reset config to initial state.
     */
    public static void resetConfig() {
        webdriverManagerConfig = new WebDriverManagerConfig();
    }

    /**
     * Make the session exclusive. This session will then never be closed during test execution. To close it call:
     * WebDriverManager.reportExclusiveSession(uuid);
     * From any steps you can call getWebDriver(uuid) or getSelenium(uuid) to get the sessions.
     *
     * @param driver .
     * @return UUID of this session.
     */
    public static String makeSessionExclusive(final WebDriver driver) {
        if (driver == null) {
            return null;
        }
        final String uuid = WebDriverSessionsManager.makeSessionExclusive(driver);
        return uuid;
    }

    public static void shutdownExclusiveSession(final String key) {
        WebDriverSessionsManager.shutdownExclusiveSession(key);
    }

    public static List<WebDriver> getWebDriversFromThread(final long threadId) {
        return WebDriverSessionsManager.getWebDriversFromThread(threadId);
    }

    public static void registerWebDriverFactory(WebDriverFactory webDriverFactory, String... browsers) {
        WebDriverSessionsManager.registerWebDriverFactory(webDriverFactory, browsers);
    }

    public static String getSessionKeyFrom(WebDriver driver) {
        return WebDriverSessionsManager.getSessionKey(driver);
    }

    public static WebDriverRequest getRelatedWebDriverRequest(WebDriver driver) {
        return WebDriverSessionsManager.DRIVER_REQUEST_MAP.get(driver);
    }

    public static SessionContext getSessionContextFromWebDriver(WebDriver driver) {
        String sessionId = WebDriverUtils.getSessionId(driver);
        if (sessionId != null) {
            return WebDriverSessionsManager.getSessionContext(sessionId);
        }
        return null;
    }

    public static void setUserAgentConfig(String browser, UserAgentConfig configurator) {
        userAgentConfigurators.put(browser, configurator);
    }

    static UserAgentConfig getUserAgentConfig(String browser) {
        return userAgentConfigurators.get(browser);
    }
}
