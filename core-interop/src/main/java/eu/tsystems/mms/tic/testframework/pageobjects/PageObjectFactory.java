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

package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.enums.CheckRule;
import org.openqa.selenium.WebDriver;

public interface PageObjectFactory {
    PageObjectFactory setGlobalPagePrefix(String pagePrefix);
    PageObjectFactory setThreadLocalPagePrefix(String pagePrefix);
    PageObjectFactory removeThreadLocalPagePrefix();

    /**
     * @todo Make default when WebDriverManager has an interface
     */
    <T extends PageObject> T createPage(Class<T> pageClass);
    default <T extends PageObject> T createPage(Class<T> pageClass, WebDriver webDriver) {
        return createPageWithCheckRule(pageClass, webDriver, CheckRule.DEFAULT);
    }
    <T extends PageObject> Class<T> findBestMatchingClass(Class<T> pageClass, WebDriver webDriver);
    <T extends Component> T createComponent(Class<T> componentClass, UiElement rootElement);
    /**
     * @todo Make default when WebDriverManager has an interface
     */
    <T extends PageObject> T createPageWithCheckRule(Class<T> pageClass, CheckRule checkRule);
    <T extends PageObject> T createPageWithCheckRule(Class<T> pageClass, WebDriver webDriver, CheckRule checkRule);

}
