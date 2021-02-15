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
import eu.tsystems.mms.tic.testframework.webdrivermanager.AbstractWebDriverRequest;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManagerConfig;
import java.util.Optional;
import org.openqa.selenium.WebDriver;

/**
 * Replacement for static {@link WebDriverManager}
 * @todo Rename to {@link WebDriverManager}
 */
public interface IWebDriverManager extends WebDriverRetainer {
    String createExclusiveSessionKey(WebDriver webDriver);
    IWebDriverFactory getWebDriverFactoryForBrowser(String browser);
    void shutdownSession(String sessionKey);
    void shutdownSession(WebDriver webDriver);
    void shutdownAllThreadSessions();
    void shutdownAllSessions();
    WebDriver getWebDriver(String sessionKey);
    WebDriver getWebDriver(AbstractWebDriverRequest request);
    Optional<SessionContext> getSessionContext(WebDriver webDriver);
    Optional<String>getRequestedBrowser(WebDriver webDriver);
    String getSessionKey(WebDriver webDriver);
    WebDriverManagerConfig getConfig();
}
