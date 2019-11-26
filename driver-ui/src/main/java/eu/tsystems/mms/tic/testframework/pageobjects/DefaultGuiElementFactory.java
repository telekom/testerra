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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.pageobjects;

import eu.tsystems.mms.tic.testframework.exceptions.TesterraRuntimeException;
import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.AbstractGuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.webdrivermanager.IWebDriverFactory;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverSessionsManager;

public class DefaultGuiElementFactory implements
    GuiElementFactory,
    Loggable
{
    @Override
    public IGuiElement createWithParent(
        IGuiElement parent,
        Locate locator
    ) {
        GuiElement parentGuiElement = (GuiElement)parent;
        IWebDriverFactory factory = WebDriverSessionsManager.getWebDriverFactory(parentGuiElement.guiElementData.getBrowser());
        GuiElementCore core = factory.createCoreWithParent(parentGuiElement.guiElementData, locator);
        GuiElement guiElement = new GuiElement(core);
        guiElement.setParent(parent);
        return guiElement;
    }

    @Override
    public IGuiElement createWithFrames(
        Locate locator,
        IGuiElement... frames
    ) {
        if (frames == null || frames.length == 0) {
            throw new TesterraRuntimeException("No frames given");
        }
        return new GuiElement(frames[0].getWebDriver(), locator, frames);
    }

    @Override
    public IGuiElement createWithPage(
        PageObject page,
        Locate locator
    ) {
        return new GuiElement(page, locator);
    }
}
