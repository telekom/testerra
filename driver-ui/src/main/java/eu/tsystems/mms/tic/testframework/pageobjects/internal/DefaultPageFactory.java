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
import eu.tsystems.mms.tic.testframework.pageobjects.PageObject;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.openqa.selenium.WebDriver;

public class DefaultPageFactory implements PageFactory, Loggable {

    private String GLOBAL_PAGES_PREFIX = null;
    private final ThreadLocal<String> THREAD_LOCAL_PAGES_PREFIX = new ThreadLocal<>();

    @Override
    public PageFactory setGlobalPagesPrefix(String pagePrefix) {
        GLOBAL_PAGES_PREFIX = pagePrefix;
        return this;
    }

    @Override
    public PageFactory setThreadLocalPagesPrefix(String pagePrefix) {
        THREAD_LOCAL_PAGES_PREFIX.set(pagePrefix);
        return this;
    }

    @Override
    public PageFactory clearThreadLocalPagesPrefix() {
        THREAD_LOCAL_PAGES_PREFIX.remove();
        return this;
    }

    @Override
    public <T extends Page> T createPage(Class<T> pageClass, WebDriver webDriver) {
        return createPageWithCheckRule(pageClass, webDriver, CheckRule.DEFAULT);
    }

    @Deprecated
    protected String getConfiguredPrefix() {
        String pagesPrefix = GLOBAL_PAGES_PREFIX;
        if (!StringUtils.isStringEmpty(THREAD_LOCAL_PAGES_PREFIX.get())) {
            pagesPrefix = THREAD_LOCAL_PAGES_PREFIX.get();
        }
        return (pagesPrefix!=null?pagesPrefix:"");
    }

    private <T extends Page> Class<T> findBestMatchingClass(Class<T> pageClass) {
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
            ((AbstractPage)component).checkUiElements();
            return component;
        } catch (Throwable throwable) {
            throw new PageFactoryException(componentClass, rootElement.getWebDriver(), throwable);
        }
    }

    @Override
    @Deprecated
    public <T extends Page> T createPageWithCheckRule(Class<T> pageClass, WebDriver webDriver, CheckRule checkRule) {
        pageClass = findBestMatchingClass(pageClass);
        try {
            Constructor<T> constructor = pageClass.getConstructor(WebDriver.class);
            T page = constructor.newInstance(webDriver);
            ((AbstractPage)page).checkUiElements(checkRule);
            return page;
        } catch (Throwable throwable) {
            throw new PageFactoryException(pageClass, webDriver, throwable);
        }
    }
}
