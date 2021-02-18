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

import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.useragents.UserAgentConfig;
import eu.tsystems.mms.tic.testframework.utils.WebDriverUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.AbstractWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerConfig;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
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
    @Override
    default WebDriver getWebDriver() {
        return WebDriverManager.getWebDriver();
    }

    default String makeExclusive(WebDriver webDriver) {
        return WebDriverManager.makeSessionExclusive(webDriver);
    }

    default IWebDriverFactory getWebDriverFactoryForBrowser(String browser) {
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

    default Stream<WebDriver> getWebDriversFromCurrentThread() {
        return WebDriverSessionsManager.getWebDriversFromCurrentThread();
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
        return switchToWindow(getWebDriver(), predicate);
    }

    default Optional<WebDriver> switchToWindow(WebDriver mainWebDriver, Predicate<WebDriver> predicate) {
        return WebDriverUtils.switchToWindow(mainWebDriver, predicate);
    }
}
