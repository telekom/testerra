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
import eu.tsystems.mms.tic.testframework.exceptions.SystemException;
import eu.tsystems.mms.tic.testframework.internal.utils.DriverStorage;
import eu.tsystems.mms.tic.testframework.pageobjects.POConfig;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.report.utils.ExecutionContextUtils;
import eu.tsystems.mms.tic.testframework.useragents.UserAgentConfig;
import eu.tsystems.mms.tic.testframework.utils.UITestUtils;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverFactory;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides threadsafe WebDriver and Selenium objects. These objects are needed for correct logging and reporting.
 *
 * @author sepr
 * @todo Migrate to {@link DefaultWebDriverManager}
 */
@Deprecated
public final class WebDriverManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebDriverManager.class);

    static {
        UITestUtils.initializePerfTest();
    }

    /**
     * @deprecated Use {@link WebDriverRequest#DEFAULT_SESSION_KEY} instead
     */
    public static final String DEFAULT_SESSION_KEY = WebDriverRequest.DEFAULT_SESSION_KEY;

    /**
     * WebDriverManager configuration set. Modify by config() call!
     */
    @Deprecated
    private static WebDriverManagerConfig webdriverManagerConfig;

    private static final HashMap<String, UserAgentConfig> userAgentConfigurators = new HashMap<>();
    private static final String SESSION_LOCALE = "locale";

    /**
     * Private constructor to hide the public one since this a static only class.
     */
    WebDriverManager() {
    }

    /**
     * Adds extra capability to desired capabilities.
     *
     * @param key   The key of the capability to set.
     * @param value The value of the capability to set.
     * @deprecated Configure capabilities on your {@link WebDriverRequest}
     */
    public static void setGlobalExtraCapability(final String key, final Object value) {
        addGlobalCapability(key, value);
    }

    /**
     * Adds extra capabilities to desired capabilities.
     *
     * @param desiredCapabilities .
     * @deprecated Configure capabilities on your {@link WebDriverRequest}
     */
    @Deprecated
    public static void setGlobalExtraCapabilities(final DesiredCapabilities desiredCapabilities) {
        WebDriverCapabilities.setGlobalExtraCapabilities(desiredCapabilities);
    }

    /**
     * Get all set extra capabilities (not the DesiredCapabilities that are set automatically
     *
     * @return
     * @deprecated Configure capabilities on your {@link WebDriverRequest}
     */
    public static Map<String, Object> getGlobalExtraCapabilities() {
        return WebDriverCapabilities.getGlobalExtraCapabilities();
    }

    /**
     * Adds a capability.
     *
     * @param key   The key of the capability to set.
     * @param value The value of the capability to set.
     * @deprecated Configure capabilities on your {@link WebDriverRequest}
     */
    private static void addGlobalCapability(String key, Object value) {
        WebDriverCapabilities.addGlobalCapability(key, value);
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
    public static EventFiringWebDriver getWebDriver(final String sessionKey) {
        UnspecificWebDriverRequest webDriverRequest = new UnspecificWebDriverRequest();
        webDriverRequest.setSessionKey(sessionKey);
        return getWebDriver(webDriverRequest);
    }

    public static EventFiringWebDriver getWebDriver(WebDriverRequest webDriverRequest) {
        return WebDriverSessionsManager.getWebDriver(webDriverRequest);
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
     * checks if web driver is active
     *
     * @return true if webdriver is active, else false.
     */
    protected static Boolean isWebDriverActive() {
        return WebDriverSessionsManager.hasAnySessionActive();
    }

    /**
     * Closes all windows and active Selenium and/or WebDriver instances.
     * @deprecated Use {@link IWebDriverManager#shutdownAllThreadSessions()} instead
     */
    public static void forceShutdown() {
        WebDriverSessionsManager.shutdownAllThreadSessions();
        if (Testerra.Properties.REUSE_DATAPROVIDER_DRIVER_BY_THREAD.asBool()) {
            String testMethodName = ExecutionContextUtils.getMethodNameFromCurrentTestResult();
            DriverStorage.removeSpecificDriver(testMethodName);
        }
    }

    /**
     * @deprecated Use {@link IWebDriverManager#shutdownAllThreadSessions()} instead
     */
    public static void shutdown() {
        forceShutdown();
    }

    /**
     * Returns the WebDriverManagerConfig Object.
     *
     * @return .
     * @deprecated Use {@link #getConfig()} instead
     */
    @Deprecated
    public static WebDriverManagerConfig config() {
        if (webdriverManagerConfig == null) {
            webdriverManagerConfig = new WebDriverManagerConfig();
        }
        return webdriverManagerConfig;
    }

    /**
     * @deprecated Use {@link #getWebDriver(WebDriverRequest)} instead
     */
    @Deprecated
    public static WebDriverManagerConfig getConfig() {
        return config();
    }

    /**
     * Returns a boolean status of the javascript activation in the browser.
     *
     * @return true is js is activated, false otherwise
     */
    public static boolean isJavaScriptActivated(final WebDriver driver) {


        return pIsJavaScriptActivated(driver);
    }

    /**
     * Returns a boolean status of the javascript activation in the browser.
     *
     * @return true is js is activated, false otherwise
     */
    private static boolean pIsJavaScriptActivated(final WebDriver driver) {

        JavascriptExecutor rawJsExecutorDriver;
        if (driver instanceof JavascriptExecutor) {
            rawJsExecutorDriver = (JavascriptExecutor) driver;
        } else {
            throw new SystemException("WebDriver object is not a JavascriptExecutor");
        }

        try {
            rawJsExecutorDriver.executeScript("return true;");
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
     * Returns true if any session is active.
     *
     * @return .
     */
    public static boolean hasAnySessionActive() {
        return WebDriverSessionsManager.hasAnySessionActive();
    }

    public static boolean hasSessionsActiveInThisThread() {
        return WebDriverSessionsManager.hasSessionActiveInThisThread();
    }

    /**
     * Are you sure you want do that?? This action quits all browser sessions in all threads.
     * Does not close windows when executeCloseWindows == false.
     *
     * @deprecated Use forceShotDownAllThreads, does the same thing, but sounds more dangerous.
     */
//    @Deprecated
//    public static void shutdownAllThreads() {
//        pRealShutdownAllThreads(false);
//    }

    /**
     * Are you sure you want do that?? This action quits all browser sessions in all threads.
     */
    @Deprecated
    public static void forceShutdownAllThreads() {
        LOGGER.info("Forcing all WebDrivers to shutdown (close all windows)");
        WebDriverSessionsManager.shutdownAllSessions();
        WDInternal.cleanupDriverReferencesInCurrentThread();
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
        return WebDriverSessionsManager.makeSessionExclusive(driver);
    }

    public static void shutdownExclusiveSession(final String key) {
        WebDriverSessionsManager.shutdownSessionKey(key);
    }

    public static Stream<EventFiringWebDriver> getWebDriversFromThread(final long threadId) {
        return WebDriverSessionsManager.getWebDriversFromThread(threadId);
    }

    @Deprecated
    public static void registerWebDriverFactory(WebDriverFactory webDriverFactory, String... browsers) {
        WebDriverSessionsManager.registerWebDriverFactory(webDriverFactory, browsers);
    }

    public static String getSessionKeyFrom(WebDriver driver) {
        return WebDriverSessionsManager.getSessionKey(driver);
    }

    public static void setUserAgentConfig(String browser, UserAgentConfig configurator) {
        userAgentConfigurators.put(browser, configurator);
    }

    static UserAgentConfig getUserAgentConfig(String browser) {
        return userAgentConfigurators.get(browser);
    }

    /**
     * Sets the locale for a specified session
     * @param webDriver
     * @param locale
     * @return TRUE if locale has been set
     */
    public static boolean setSessionLocale(WebDriver webDriver, Locale locale) {
        Optional<Map<String, Object>> optionalMetaData = WebDriverSessionsManager.getSessionContext(webDriver).map(SessionContext::getMetaData);
        optionalMetaData.ifPresent(map -> {
            map.put(SESSION_LOCALE, locale);
        });
        return optionalMetaData.isPresent();
    }

    /**
     * Returns the session locale
     * @param webDriver
     */
    public static Optional<Locale> getSessionLocale(WebDriver webDriver) {
        return WebDriverSessionsManager.getSessionContext(webDriver)
                .map(SessionContext::getMetaData)
                .map(map -> (Locale)map.get(SESSION_LOCALE));
    }
}
