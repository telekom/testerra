/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.useragents.UserAgentConfig;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverFactory;
import eu.tsystems.mms.tic.testframework.webdriver.WebDriverRetainer;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.decorators.Decorated;

import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Replacement for static {@link WebDriverManager}
 *
 * @todo Rename to {@link WebDriverManager}
 */
public interface IWebDriverManager extends
        WebDriverRetainer,
        Loggable {

    enum Properties implements IProperties {
        BROWSER("tt.browser", ""),
        BROWSER_VERSION("tt.browser.version", ""),
        BROWSER_SETTING("tt.browser.setting", ""),
        BROWSER_PLATFORM("tt.browser.platform", null),
        ;
        private final String property;
        private final Object defaultValue;

        Properties(String property, Object defaultValue) {
            this.property = property;
            this.defaultValue = defaultValue;
        }

        @Override
        public String toString() {
            return property;
        }

        @Override
        public Object getDefault() {
            return defaultValue;
        }
    }

    @Override
    default WebDriver getWebDriver() {
        return WebDriverManager.getWebDriver();
    }

    default Optional<UserAgentConfig> getUserAgentConfig(String browser) {
        return Optional.ofNullable(WebDriverManager.getUserAgentConfig(browser));
    }

    default String makeExclusive(WebDriver webDriver) {
        return WebDriverManager.makeSessionExclusive(webDriver);
    }

    default WebDriverFactory getWebDriverFactoryForBrowser(String browser) {
        return WebDriverSessionsManager.getWebDriverFactory(browser);
    }

    default void shutdownSession(String sessionKey) {
        WebDriverSessionsManager.shutdownSessionKey(sessionKey);
    }

    default void shutdownSession(WebDriver webDriver) {
        WebDriverSessionsManager.shutdownWebDriver(webDriver);
    }

    /**
     * Requests to shutdown all sessions, that are able to shutdown by its {@link WebDriverRequest} configuration.
     */
    default void requestShutdownAllSessions() {
        WebDriverSessionsManager.WEBDRIVER_SESSIONS_CONTEXTS_MAP.forEach((webDriver, sessionContext) -> {
            if (sessionContext.getWebDriverRequest().getShutdownAfterExecution()) {
                WebDriverSessionsManager.shutdownWebDriver(webDriver);
            }
        });
    }

    /**
     * This will shutdown all thread sessions, no matter how they are configured.
     */
    default void shutdownAllThreadSessions() {
        WebDriverManager.forceShutdown();
    }

    /**
     * This will shutdown all sessions, no matter how they are configured.
     */
    default void shutdownAllSessions() {
        WebDriverManager.forceShutdownAllThreads();
    }

    default WebDriver getWebDriver(String sessionKey) {
        return WebDriverManager.getWebDriver(sessionKey);
    }

    default WebDriver getWebDriver(WebDriverRequest request) {
        return WebDriverManager.getWebDriver(request);
    }

    default Optional<SessionContext> getSessionContext(WebDriver webDriver) {
        return WebDriverSessionsManager.getSessionContext(webDriver);
    }

    default Optional<WebDriver> getWebDriver(SessionContext sessionContext) {
        return WebDriverSessionsManager.getWebDriver(sessionContext);
    }

    default Optional<String> getRequestedBrowser(WebDriver webDriver) {
        return WebDriverSessionsManager.getRequestedBrowser(webDriver);
    }

    default String getSessionKey(WebDriver webDriver) {
        return WebDriverManager.getSessionKeyFrom(webDriver);
    }

    @Deprecated
    default WebDriverManagerConfig getConfig() {
        return WebDriverManager.getConfig();
    }

    default Stream<WebDriver> readWebDriversFromCurrentThread() {
        return WebDriverSessionsManager.getWebDriversFromCurrentThread();
    }

    default Stream<WebDriver> readExclusiveWebDrivers() {
        return WebDriverSessionsManager.readExclusiveWebDrivers();
    }

    default Stream<WebDriver> readWebDrivers() {
        return WebDriverSessionsManager.readWebDrivers();
    }

    default IWebDriverManager setUserAgentConfig(String browser, UserAgentConfig configurator) {
        WebDriverManager.setUserAgentConfig(browser, configurator);
        return this;
    }

    default void registerWebDriverBeforeShutdownHandler(Consumer<WebDriver> beforeQuit) {
        WebDriverSessionsManager.registerWebDriverBeforeShutdownHandler(beforeQuit);
    }

    default void registerWebDriverAfterShutdownHandler(Consumer<WebDriver> afterQuit) {
        WebDriverSessionsManager.registerWebDriverAfterShutdownHandler(afterQuit);
    }

    default void registerWebDriverAfterStartupHandler(Consumer<WebDriver> afterStart) {
        WebDriverSessionsManager.registerWebDriverAfterStartupHandler(afterStart);
    }

    default void registerWebDriverRequestConfigurator(Consumer<WebDriverRequest> handler) {
        WebDriverSessionsManager.webDriverRequestConfigurators.add(handler);
    }

    default boolean switchToWindow(Predicate<WebDriver> predicate) {
        return WebDriverUtils.switchToWindow(getWebDriver(), predicate);
    }

    default boolean switchToWindowTitle(String windowTitle) {
        return switchToWindowTitle(getWebDriver(), windowTitle);
    }

    default boolean switchToWindow(WebDriver mainWebDriver, Predicate<WebDriver> predicate) {
        return WebDriverUtils.switchToWindow(mainWebDriver, predicate);
    }

    default boolean switchToWindowTitle(WebDriver mainWebDriver, String windowTitle) {
        boolean switched = WebDriverUtils.switchToWindow(mainWebDriver, webDriver -> {
            return webDriver.getTitle().equals(windowTitle);
        });
        if (!switched) {
            throw new RuntimeException(String.format("Window title \"%s\" not found", windowTitle));
        }
        return switched;
    }

    default boolean switchToWindowHandle(WebDriver mainWebDriver, String windowHandle) {
        boolean switched = WebDriverUtils.switchToWindow(mainWebDriver, webDriver -> {
            return webDriver.getWindowHandle().equals(windowHandle);
        });
        if (!switched) {
            throw new RuntimeException(String.format("Window handle \"%s\" not found", windowHandle));
        }
        return switched;
    }

    default void keepAlive(WebDriver webDriver, int intervalSleepTimeInSeconds, int durationInSeconds) {
        WebDriverUtils.keepWebDriverAlive(webDriver, intervalSleepTimeInSeconds, durationInSeconds);
    }

    default void stopKeepingAlive(WebDriver webDriver) {
        WebDriverUtils.removeKeepAliveForWebDriver(webDriver);
    }

    /**
     * @deprecated Use {@link #setUserAgentConfig(String, UserAgentConfig)} or {@link DesktopWebDriverRequest#getMutableCapabilities()} for custom capabilities instead
     */
    @Deprecated
    default void setGlobalCapability(String key, Object value) {
        WebDriverManager.setGlobalExtraCapability(key, value);
    }

    /**
     * @deprecated Use {@link #setUserAgentConfig(String, UserAgentConfig)} or {@link DesktopWebDriverRequest#getMutableCapabilities()} instead.
     */
    @Deprecated
    default void removeGlobalCapability(String key) {
        WebDriverManager.removeGlobalExtraCapability(key);
    }

    /**
     * Returns the orignal {@link WebDriver} from the decorated WebDriver instance.
     * and tries to cast it to the target class implementation.
     */
    default <WEBDRIVER> Optional<WEBDRIVER> unwrapWebDriver(WebDriver webDriver, Class<WEBDRIVER> targetWebDriverClass) {
        WebDriver originalDriver = this.getOriginalFromDecorated(webDriver);

        if (targetWebDriverClass.isInstance(originalDriver)) {
            return Optional.of((WEBDRIVER) originalDriver);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns the original WebDriver instance from a decorated.
     */
    default WebDriver getOriginalFromDecorated(WebDriver decoratedWebDriver) {
        if (decoratedWebDriver instanceof Decorated) {
            return ((Decorated<WebDriver>) decoratedWebDriver).getOriginal();
        }
        return decoratedWebDriver;
    }

    /**
     * Sets the locale for a specified session
     *
     * @param webDriver
     * @param locale
     * @return TRUE if locale has been set
     */
    default boolean setSessionLocale(WebDriver webDriver, Locale locale) {
        return WebDriverManager.setSessionLocale(webDriver, locale);
    }

    /**
     * Returns the session locale
     *
     * @param webDriver
     */
    default Optional<Locale> getSessionLocale(WebDriver webDriver) {
        return WebDriverManager.getSessionLocale(webDriver);
    }
}
