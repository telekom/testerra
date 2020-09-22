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
import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.openqa.selenium.WebDriver;

public class DefaultPageFactory implements PageObjectFactory, Loggable {

    private String GLOBAL_PAGES_PREFIX = null;
    private final ThreadLocal<String> THREAD_LOCAL_PAGES_PREFIX = new ThreadLocal<>();

    @Override
    public PageObjectFactory setGlobalPagePrefix(String pagePrefix) {
        GLOBAL_PAGES_PREFIX = pagePrefix;
        return this;
    }

    @Override
    public PageObjectFactory setThreadLocalPagePrefix(String pagePrefix) {
        THREAD_LOCAL_PAGES_PREFIX.set(pagePrefix);
        return this;
    }

    @Override
    public PageObjectFactory removeThreadLocalPagePrefix() {
        THREAD_LOCAL_PAGES_PREFIX.remove();
        return this;
    }

    @Override
    public <T extends PageObject> T createPage(Class<T> pageClass) {
        return createPage(pageClass, WebDriverManager.getWebDriver());
    }

    protected String getConfiguredPrefix() {
        String pagesPrefix = GLOBAL_PAGES_PREFIX;
        if (!StringUtils.isStringEmpty(THREAD_LOCAL_PAGES_PREFIX.get())) {
            pagesPrefix = THREAD_LOCAL_PAGES_PREFIX.get();
        }
        return (pagesPrefix!=null?pagesPrefix:"");
    }

    @Override
    public <T extends PageObject> Class<T> findBestMatchingClass(Class<T> pageClass, WebDriver webDriver) {
        try {
            return (Class<T>) Class.forName(String.format("%s.%s%s",pageClass.getPackage().getName(), getConfiguredPrefix(), pageClass.getSimpleName()));
        } catch (ClassNotFoundException e) {
            log().error(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public <T extends Component> T createComponent(Class<T> componentClass, UiElement rootElement) {
        try {
            Constructor<T> constructor = componentClass.getConstructor(UiElement.class);
            T component = constructor.newInstance(rootElement);
            component.checkUiElements();
            return component;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new TesterraRuntimeException(String.format("Could not create instance of %s(%s)", componentClass, rootElement), e);
        }
    }

    @Override
    public <T extends PageObject> T createPageWithCheckRule(Class<T> pageClass, CheckRule checkRule) {
        return createPageWithCheckRule(pageClass, WebDriverManager.getWebDriver(), checkRule);
    }

    @Override
    public <T extends PageObject> T createPageWithCheckRule(Class<T> pageClass, WebDriver webDriver, CheckRule checkRule) {
        pageClass = findBestMatchingClass(pageClass, webDriver);
        try {
            Constructor<T> constructor = pageClass.getConstructor(WebDriver.class);
            T page = constructor.newInstance(webDriver);
            page.checkUiElements(checkRule);
            return page;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new TesterraRuntimeException(String.format("Could not create instance of %s(%s)", pageClass, webDriver), e);
        }
    }
}
