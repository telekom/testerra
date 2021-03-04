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

package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import eu.tsystems.mms.tic.testframework.exceptions.PageFactoryException;
import eu.tsystems.mms.tic.testframework.pageobjects.Component;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import java.util.Optional;
import org.openqa.selenium.WebDriver;

public interface PageFactory extends WebDriverManagerProvider {
    @Deprecated
    PageFactory setGlobalPagesPrefix(String pagePrefix);
    @Deprecated
    PageFactory setThreadLocalPagesPrefix(String pagePrefix);
    @Deprecated
    PageFactory clearThreadLocalPagesPrefix();

    default <T extends Page> T createPage(Class<T> pageClass) {
        return createPage(pageClass, WEB_DRIVER_MANAGER.getWebDriver());
    }
    default <T extends Page> T createPage(Class<T> pageClass, WebDriver webDriver) {
        return createPageWithCheckRule(pageClass, webDriver, CheckRule.DEFAULT);
    }
    default <T extends Page> Optional<T> tryCreatePage(Class<T> pageClass) {
        return tryCreatePage(pageClass, WEB_DRIVER_MANAGER.getWebDriver());
    }
    default <T extends Page> Optional<T> tryCreatePage(Class<T> pageClass, WebDriver webDriver) {
        try {
            return Optional.of(createPage(pageClass, webDriver));
        } catch (PageFactoryException e) {
            return Optional.empty();
        }
    }

    @Deprecated
    <T extends Page> Class<T> findBestMatchingClass(Class<T> pageClass, WebDriver webDriver);
    <T extends Component> T createComponent(Class<T> componentClass, UiElement rootElement);

    @Deprecated
    <T extends Page> T createPageWithCheckRule(Class<T> pageClass, WebDriver webDriver, CheckRule checkRule);
}
