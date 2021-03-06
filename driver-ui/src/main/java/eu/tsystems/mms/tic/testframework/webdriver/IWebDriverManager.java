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

package eu.tsystems.mms.tic.testframework.webdriver;

import eu.tsystems.mms.tic.testframework.common.IProperties;
import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.useragents.UserAgentConfig;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.AbstractWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerConfig;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.openqa.selenium.WebDriver;

/**
 * Replacement for static {@link WebDriverManager}
 * @todo Rename to {@link WebDriverManager}
 */
public interface IWebDriverManager extends WebDriverRetainer {

    enum Properties implements IProperties {
        BROWSER("tt.browser", ""),
        BROWSER_VERSION("tt.browser.version", ""),
        BROWSER_SETTING("tt.browser.setting", ""),
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

    default String makeExclusive(WebDriver webDriver) {
        return WebDriverManager.makeSessionExclusive(webDriver);
    }

    default WebDriverFactory getWebDriverFactoryForBrowser(String browser) {
        return WebDriverSessionsManager.getWebDriverFactory(browser);
    }

    default void shutdownSession(String sessionKey) {
        WebDriverManager.shutdownExclusiveSession(sessionKey);
    }

    default void shutdownSession(WebDriver webDriver) {
        WebDriverSessionsManager.shutdownWebDriver(webDriver);
    }

    default void shutdownAllThreadSessions() {
        WebDriverManager.forceShutdown();
    }

    default void shutdownAllSessions() {
        WebDriverManager.forceShutdownAllThreads();
    }

    default WebDriver getWebDriver(String sessionKey) {
        return WebDriverManager.getWebDriver(sessionKey);
    }

    default WebDriver getWebDriver(AbstractWebDriverRequest request) {
        return WebDriverManager.getWebDriver(request);
    }

    default Optional<SessionContext> getSessionContext(WebDriver webDriver) {
        return WebDriverSessionsManager.getSessionContext(webDriver);
    }

    default Optional<String>getRequestedBrowser(WebDriver webDriver) {
        return WebDriverSessionsManager.getRequestedBrowser(webDriver);
    }

    default String getSessionKey(WebDriver webDriver) {
        return WebDriverManager.getSessionKeyFrom(webDriver);
    }

    default WebDriverManagerConfig getConfig() {
        return WebDriverManager.getConfig();
    }

    default Stream<WebDriver> readWebDriversFromCurrentThread() {
        return WebDriverSessionsManager.getWebDriversFromCurrentThread();
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

    default Optional<WebDriver> switchToWindow(Predicate<WebDriver> predicate) {
        return WebDriverUtils.switchToWindow(getWebDriver(), predicate);
    }

    default WebDriver switchToWindowTitle(String windowTitle) {
        return switchToWindowTitle(getWebDriver(), windowTitle);
    }

    default Optional<WebDriver> switchToWindow(WebDriver mainWebDriver, Predicate<WebDriver> predicate) {
        return WebDriverUtils.switchToWindow(mainWebDriver, predicate);
    }

    default WebDriver switchToWindowTitle(WebDriver mainWebDriver, String windowTitle) {
        Optional<WebDriver> optionalWebDriver = WebDriverUtils.switchToWindow(mainWebDriver, webDriver -> {
            return webDriver.getTitle().equals(windowTitle);
        });
        if (!optionalWebDriver.isPresent()) {
            throw new RuntimeException(String.format("Window title \"%s\" not found", windowTitle));
        }
        return optionalWebDriver.get();
    }

    default WebDriver switchToWindowHandle(WebDriver mainWebDriver, String windowHandle) {
        Optional<WebDriver> optionalWebDriver = WebDriverUtils.switchToWindow(mainWebDriver, webDriver -> {
            return webDriver.getWindowHandle().equals(windowHandle);
        });
        if (!optionalWebDriver.isPresent()) {
            throw new RuntimeException(String.format("Window handle \"%s\" not found", windowHandle));
        }
        return optionalWebDriver.get();
    }

    default void keepAlive(WebDriver webDriver, int intervalSleepTimeInSeconds, int durationInSeconds) {
        WebDriverUtils.keepWebDriverAlive(webDriver, intervalSleepTimeInSeconds, durationInSeconds);
    }

    default void stopKeepingAlive(WebDriver webDriver) {
        WebDriverUtils.removeKeepAliveForWebDriver(webDriver);
    }

    default void setThreadCapability(String key, Object value) {
        WebDriverManager.addThreadCapability(key, value);
    }

    default void setGlobalCapability(String key, Object value) {
        WebDriverManager.setGlobalExtraCapability(key, value);
    }
    default void removeGlobalCapability(String key) {
        WebDriverManager.removeGlobalExtraCapability(key);
    }
}
