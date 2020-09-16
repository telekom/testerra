/*
 * Testerra
 *
 * (C) 2020, Peter Lehmann, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 *
 */
 package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.Testerra;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;
import java.awt.Color;
import org.openqa.selenium.WebElement;

/**
 * Highlights assertions in Demo Mode
 */
public class GuiElementAssertHighlightDecorator extends GuiElementAssertDecorator {

    private final GuiElementData guiElementData;

    public GuiElementAssertHighlightDecorator(GuiElementAssert decoratedAssert, GuiElementData guiElementData) {
        super(decoratedAssert);
        this.guiElementData = guiElementData;
    }

    @Override
    void beforeAssertion() {

    }

    @Override
    void afterAssertion(String message, AssertionError assertionErrorOrNull) {
        if (Testerra.Properties.DEMO_MODE.asBool()) {
            highlight(assertionErrorOrNull == null);
        }
    }

    private void highlight(boolean successful) {
        WebElement webElement = guiElementData.getGuiElement().getWebElement();
        if (webElement == null) {
            return;
        }
        try {
            if (guiElementData.getFrameLogic() != null) {
                guiElementData.getFrameLogic().switchToCorrectFrame();
            }
            if (successful) {
                JSUtils.highlightWebElementStatic(guiElementData.getWebDriver(), webElement, new Color(0, 255, 0));
            } else {
                JSUtils.highlightWebElementStatic(guiElementData.getWebDriver(), webElement, new Color(255, 0, 0));
            }
        } catch (RuntimeException e) {
            // could not highlight, but thats ok.
            LOGGER.debug("Could not highlight element", e);
        } finally {
            if (guiElementData.getFrameLogic() != null) {
                guiElementData.getFrameLogic().switchToDefaultFrame();
            }
        }

    }
}
