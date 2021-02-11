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

import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerConfig;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import org.openqa.selenium.WebDriver;

public class DefaultWebDriverManager implements IWebDriverManager {
    @Override
    public WebDriver getWebDriver() {
        return WebDriverManager.getWebDriver();
    }

    @Override
    public String createExclusiveSessionKey(WebDriver webDriver) {
        return WebDriverManager.makeSessionExclusive(webDriver);
    }

    @Override
    public IWebDriverFactory getWebDriverFactoryForBrowser(String browser) {
        return WebDriverSessionsManager.getWebDriverFactory(browser);
    }

    @Override
    public WebDriver getWebDriver(String sessionKey) {
        return WebDriverManager.getWebDriver(sessionKey);
    }

    @Override
    public void shutdownSession(String sessionKey) {
        WebDriverManager.shutdownExclusiveSession(sessionKey);
    }

    @Override
    public void shutdownSession(WebDriver webDriver) {
        WebDriverSessionsManager.shutdownWebDriver(webDriver);
    }

    @Override
    public void shutdownAllThreadSessions() {
        WebDriverManager.forceShutdown();
    }

    @Override
    public void shutdownAllSessions() {
        WebDriverManager.forceShutdownAllThreads();
    }

    @Override
    public WebDriverRequest getWebDriverRequest(WebDriver webDriver) {
        return WebDriverManager.getRelatedWebDriverRequest(webDriver);
    }

    @Override
    public WebDriver getWebDriver(WebDriverRequest request) {
        return WebDriverManager.getWebDriver(request);
    }

    @Override
    public String getSessionKey(WebDriver webDriver) {
        return WebDriverManager.getSessionKeyFrom(webDriver);
    }

    @Override
    public WebDriverManagerConfig getConfig() {
        return WebDriverManager.getConfig();
    }
}
