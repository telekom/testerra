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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.UiElementFactory;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.testing.WebDriverManagerProvider;
import eu.tsystems.mms.tic.testframework.webdriver.IWebDriverFactory;
import org.openqa.selenium.WebDriver;

/**
 * Default implementation of {@link UiElementFactory}
 */
public class DefaultUiElementFactory implements UiElementFactory, Loggable, WebDriverManagerProvider {
    @Override
    public UiElement createFromParent(UiElement parent, Locator locator) {
        GuiElement parentGuiElement = (GuiElement)parent;
        IWebDriverFactory factory = WEB_DRIVER_MANAGER.getWebDriverFactoryForBrowser(WEB_DRIVER_MANAGER.getRequestedBrowser(parentGuiElement.getData().getWebDriver()).orElse(null));
        GuiElementCore core = factory.createCoreFromParent(parentGuiElement.getData(), locator);
        GuiElement guiElement = new GuiElement(core);
        guiElement.setParent(parentGuiElement);
        return guiElement;
    }

    @Override
    public UiElement createWithWebDriver(WebDriver webDriver, Locator locator) {
        return new GuiElement(webDriver, locator);
    }
}
