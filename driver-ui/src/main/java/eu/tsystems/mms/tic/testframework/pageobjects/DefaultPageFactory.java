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
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @todo Implement Page Checks
 */
public class DefaultPageFactory implements IPageFactory {

    @Override
    public <T extends IPage> T create(Class<T> pageClass) {
        return create(pageClass, WebDriverManager.getWebDriver());
    }

    @Override
    public <T extends IPage> T create(Class<T> pageClass, WebDriver webDriver) {
        try {
            final Constructor<T> constructor = pageClass.getConstructor(WebDriver.class);
            return constructor.newInstance(webDriver);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new TesterraRuntimeException(String.format("Could not create instance of %s(%s)", pageClass, webDriver), e);
        }
    }

    @Override
    public <T extends IComponent> T createComponent(Class<T> componentClass, IGuiElement rootElement) {
        try {
            final Constructor<T> constructor = componentClass.getConstructor(IGuiElement.class);
            return constructor.newInstance(rootElement);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new TesterraRuntimeException(String.format("Could not create instance of %s(%s)", componentClass, rootElement), e);
        }
    }
}
