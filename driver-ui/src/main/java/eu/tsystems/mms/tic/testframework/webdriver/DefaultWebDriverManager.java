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
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;
import org.openqa.selenium.WebDriver;

public class DefaultWebDriverManager implements IWebDriverManager {
    @Override
    public WebDriver getWebDriver() {
        return WebDriverManager.getWebDriver();
    }

    @Override
    public String createExclusiveSessionId(WebDriver webDriver) {
        return WebDriverManager.makeSessionExclusive(webDriver);
    }

    @Override
    public IWebDriverFactory getFactoryForBrowser(String browser) {
        return WebDriverSessionsManager.getWebDriverFactory(browser);
    }

    @Override
    public WebDriver getWebDriverBySessionId(String sessionId) {
        return WebDriverManager.getWebDriver(sessionId);
    }

    @Override
    public void shutdownAllSessions(boolean force) {
        if (force) {
            WebDriverManager.forceShutdown();
        } else {
            WebDriverManager.shutdown();
        }
    }
}
