/*
 * (C) Copyright T-Systems Multimedia Solutions GmbH 2018, ..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann <p.lehmann@t-systems.com>
 *     pele <p.lehmann@t-systems.com>
 */
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.pageobjects.factory.ClassFinder;
import eu.tsystems.mms.tic.testframework.utils.StringUtils;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultPageFactory implements IPageFactory {

    private String GLOBAL_PAGES_PREFIX = null;
    private final ThreadLocal<String> THREAD_LOCAL_PAGES_PREFIX = new ThreadLocal<>();

    @Override
    public IPageFactory setGlobalPagePrefix(String pagePrefix) {
        GLOBAL_PAGES_PREFIX = pagePrefix;
        return this;
    }

    @Override
    public IPageFactory setThreadLocalPagePrefix(String pagePrefix) {
        THREAD_LOCAL_PAGES_PREFIX.set(pagePrefix);
        return this;
    }

    @Override
    public IPageFactory removeThreadLocalPagePrefix() {
        THREAD_LOCAL_PAGES_PREFIX.remove();
        return this;
    }

    @Override
    public <T extends IPage> T createPage(Class<T> pageClass) {
        return createPage(pageClass, WebDriverManager.getWebDriver());
    }

    @Override
    public <T extends IPage> T createPage(Class<T> pageClass, WebDriver webDriver) {
        pageClass = findBestMatchingClass(pageClass, webDriver);
        try {
            Constructor<T> constructor = pageClass.getConstructor(WebDriver.class);
            T page = constructor.newInstance(webDriver);
            page.checkGuiElements();
            return page;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new TesterraRuntimeException(String.format("Could not create instance of %s(%s)", pageClass, webDriver), e);
        }
    }

    @Override
    public <T extends IPage> Class<T> findBestMatchingClass(Class<T> pageClass, WebDriver webDriver) {
        String pagesPrefix = GLOBAL_PAGES_PREFIX;
        if (!StringUtils.isStringEmpty(THREAD_LOCAL_PAGES_PREFIX.get())) {
            pagesPrefix = THREAD_LOCAL_PAGES_PREFIX.get();
        }
        return ClassFinder.getBestMatchingClass(pageClass, webDriver, pagesPrefix);
    }

    @Override
    public <T extends IComponent> T createComponent(Class<T> componentClass, IGuiElement rootElement) {
        try {
            Constructor<T> constructor = componentClass.getConstructor(IGuiElement.class);
            T component = constructor.newInstance(rootElement);
            component.checkGuiElements();
            return component;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new TesterraRuntimeException(String.format("Could not create instance of %s(%s)", componentClass, rootElement), e);
        }
    }
}
