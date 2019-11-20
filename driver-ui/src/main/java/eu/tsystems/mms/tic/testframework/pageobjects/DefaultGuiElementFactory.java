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

public class DefaultGuiElementFactory implements
    GuiElementFactory,
    Loggable
{
    @Override
    public IGuiElement createFromAncestor(
        Locate locator,
        IGuiElement ancestor
    ) {
        return new GuiElement(locator, ancestor);
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
    public IGuiElement create(
        Locate locator,
        PageObject page
    ) {
        return new GuiElement(page, locator);
    }
}
