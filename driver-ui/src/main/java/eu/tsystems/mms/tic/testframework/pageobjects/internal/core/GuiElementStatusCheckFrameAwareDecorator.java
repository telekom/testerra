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

import eu.tsystems.mms.tic.testframework.internal.ExecutionLog;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;

/**
 * Created by rnhb on 13.08.2015.
 */
public class GuiElementStatusCheckFrameAwareDecorator extends GuiElementStatusCheckDecorator {

    private final IFrameLogic frameLogic;
    private final ExecutionLog executionLog;

    public GuiElementStatusCheckFrameAwareDecorator(GuiElementStatusCheck decoratedGuiElementStatusCheck, GuiElementData guiElementData) {
        super(decoratedGuiElementStatusCheck);
        frameLogic = guiElementData.frameLogic;
        executionLog = guiElementData.executionLog;
    }

    @Override
    protected void beforeDelegation() {
        executionLog.addMessage("Switching to frames " + frameLogic);
        frameLogic.switchToCorrectFrame();
        executionLog.addMessage("Switching to frames successful.");
    }

    @Override
    protected void afterDelegation() {
        executionLog.addMessage("Switching to DefaultFrame.");
        frameLogic.switchToDefaultFrame();
        executionLog.addMessage("Switching to DefaultFrame successful.");
    }
}
