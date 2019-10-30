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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by rnhb on 26.10.2015.
 */
public class GuiElementAssertHighlightDecorator extends GuiElementAssertDecorator {

    private final GuiElementData guiElementData;
    private final WebDriver webDriver;
    private final IFrameLogic frameLogic;

    public GuiElementAssertHighlightDecorator(GuiElementAssert decoratedAssert, GuiElementData guiElementData) {
        super(decoratedAssert);
        this.webDriver = guiElementData.webDriver;
        this.frameLogic = guiElementData.frameLogic;
        this.guiElementData = guiElementData;
    }

    @Override
    void beforeAssertion() {

    }

    @Override
    void afterAssertion(String message, AssertionError assertionErrorOrNull) {
        highlight(assertionErrorOrNull == null);
    }

    private void highlight(boolean successful) {
        if (!Testerra.Properties.DEMO_MODE.asBool()) {
            return;
        }

        WebElement webElement = guiElementData.webElement;
        if (webElement == null) {
            return;
        }
        try {
            if (frameLogic != null) {
                frameLogic.switchToCorrectFrame();
            }
            if (successful) {
                JSUtils.highlightWebElementStatic(webDriver, webElement, 0, 255, 0);
            } else {
                JSUtils.highlightWebElementStatic(webDriver, webElement, 255, 0, 0);
            }
        } catch (RuntimeException e) {
            // could not highlight, but thats ok.
            LOGGER.debug("Could not highlight element", e);
        } finally {
            if (frameLogic != null) {
                frameLogic.switchToDefaultFrame();
            }
        }

    }
}
