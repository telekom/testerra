/*
 * Testerra
 *
 * (C) 2021, Mike Reiche,  T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.common;

import eu.tsystems.mms.tic.testframework.report.model.context.SessionContext;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverManager;
import java.util.Optional;
import org.openqa.selenium.WebDriver;

public class WebDriverPropertyResolver implements PropertyResolver, WebDriverManagerProvider {

    private final WebDriver webDriver;

    public WebDriverPropertyResolver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    @Override
    public Optional<String> resolveProperty(String property) {
        if (property.equals(IWebDriverManager.Properties.BROWSER.toString())) {
            return WEB_DRIVER_MANAGER.getSessionContext(webDriver).flatMap(SessionContext::getActualBrowserName);
        } else if (property.equals(IWebDriverManager.Properties.BROWSER_VERSION.toString())) {
            return WEB_DRIVER_MANAGER.getSessionContext(webDriver).flatMap(SessionContext::getActualBrowserVersion);
        }
        return Optional.empty();
    }
}
