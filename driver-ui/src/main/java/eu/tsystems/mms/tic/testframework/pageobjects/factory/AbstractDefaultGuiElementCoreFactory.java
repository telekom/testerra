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
package eu.tsystems.mms.tic.testframework.pageobjects.factory;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCoreFrameAwareDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCoreSequenceDecorator;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public abstract class AbstractDefaultGuiElementCoreFactory implements GuiElementCoreFactory {

    @Override
    public GuiElementCore create(
        String browser,
        By by,
        WebDriver webDriver,
        GuiElementData guiElementData,
        GuiElementCore decoratedCore
    ) {
        if (guiElementData.hasFrameLogic()) {

            // if frames are set, the waiter should use frame switches when executing its sequences
            decoratedCore = new GuiElementCoreFrameAwareDecorator(decoratedCore, guiElementData);
        }
        // Wrap the core with sequence decorator, such that its methods are executed with sequence
        return new GuiElementCoreSequenceDecorator(decoratedCore, guiElementData);
    }
}
