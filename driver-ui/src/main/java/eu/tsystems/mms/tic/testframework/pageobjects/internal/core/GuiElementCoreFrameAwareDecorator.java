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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.internal.ExecutionLog;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameAwareSelect;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Created by rnhb on 13.08.2015.
 */
public class GuiElementCoreFrameAwareDecorator extends GuiElementCoreDecorator {

    private GuiElementCore decoratedGuiElementCore;
    private final GuiElementData guiElementData;

    public GuiElementCoreFrameAwareDecorator(GuiElementCore decoratedGuiElementCore, GuiElementData guiElementData) {
        super(decoratedGuiElementCore);
        this.decoratedGuiElementCore = decoratedGuiElementCore;
        this.guiElementData = guiElementData;
    }

    @Override
    protected void beforeDelegation() {
        if (guiElementData.hasFrameLogic()) {
            FrameLogic frameLogic = guiElementData.frameLogic;
            ExecutionLog executionLog = guiElementData.executionLog;
            executionLog.addMessage("Switching to frames " + frameLogic);
            frameLogic.switchToCorrectFrame();
            executionLog.addMessage("Switching to frames successful.");
        }
    }

    @Override
    protected void afterDelegation() {
        ExecutionLog executionLog = guiElementData.executionLog;
        executionLog.addMessage("Switching to DefaultFrame.");
        guiElementData.frameLogic.switchToDefaultFrame();
        executionLog.addMessage("Switching to DefaultFrame successful.");
    }

    @Override
    public By getBy() {
        By by = decoratedGuiElementCore.getBy();
        return by;
    }

    @Override
    public GuiElement getSubElement(By byLocator, String description) {
        return decoratedGuiElementCore.getSubElement(byLocator, description);
    }

    @Override
    public Select getSelectElement() {
        FrameLogic frameLogic = guiElementData.frameLogic;
        beforeDelegation();
        WebElement webElement = decoratedGuiElementCore.getWebElement();
        Select select = new Select(webElement);
        Select frameAwareSelect = new FrameAwareSelect(select, webElement, frameLogic.getFrames(),guiElementData.webDriver );
        afterDelegation();
        return frameAwareSelect;
    }
}
