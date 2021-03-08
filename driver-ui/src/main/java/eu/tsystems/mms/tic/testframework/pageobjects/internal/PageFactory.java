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
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.Component;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.testing.TestControllerProvider;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.units.qual.A;
import org.openqa.selenium.WebDriver;

public interface PageFactory extends WebDriverManagerProvider, TestControllerProvider, Loggable {
    @Deprecated
    PageFactory setGlobalPagesPrefix(String pagePrefix);
    @Deprecated
    PageFactory setThreadLocalPagesPrefix(String pagePrefix);
    @Deprecated
    PageFactory clearThreadLocalPagesPrefix();

    default <T extends Page> T createPage(Class<T> pageClass) {
        return createPage(pageClass, WEB_DRIVER_MANAGER.getWebDriver());
    }
    <T extends Page> T createPage(Class<T> pageClass, WebDriver webDriver);

    default <T extends Page> Optional<T> waitForPage(Class<T> pageClass, int seconds) {
        return waitForPage(pageClass, WEB_DRIVER_MANAGER.getWebDriver(), seconds);
    }

    default <T extends Page> Optional<T> waitForPage(Class<T> pageClass, WebDriver webDriver) {
        return waitForPage(pageClass, webDriver, -1);
    }

    default <T extends Page> Optional<T> waitForPage(Class<T> pageClass, WebDriver webDriver, int seconds) {
        AtomicReference<T> atomicPage = new AtomicReference<>();
        CONTROL.withTimeout(seconds, () -> {
            try {
                atomicPage.set(createPage(pageClass, webDriver));
            } catch (Exception e) {
                log().warn("Waiting for page ended: " + e.getMessage());
            }
        });
        return Optional.ofNullable(atomicPage.get());
    }

    <T extends Component> T createComponent(Class<T> componentClass, UiElement rootElement);

    /**
     * @deprecated Use {@link #createPage(Class, WebDriver)} instead
     */
    @Deprecated
    <T extends Page> T createPageWithCheckRule(Class<T> pageClass, WebDriver webDriver, CheckRule checkRule);
}
