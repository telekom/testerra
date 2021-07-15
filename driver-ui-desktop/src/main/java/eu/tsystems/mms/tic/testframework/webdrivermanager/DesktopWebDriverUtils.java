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
 package eu.tsystems.mms.tic.testframework.webdrivermanager;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.FrameLogic;
import eu.tsystems.mms.tic.testframework.utils.JSUtils;

public final class DesktopWebDriverUtils implements Loggable {

    public DesktopWebDriverUtils() {

    }

    private void inFrame(GuiElement guiElement, Runnable runnable) {
        FrameLogic frameLogic = guiElement.getFrameLogic();
        if (frameLogic != null) {
            frameLogic.switchToCorrectFrame();
        }
        runnable.run();
        if (frameLogic != null) {
            frameLogic.switchToDefaultFrame();
        }
    }

    public void clickAbsolute(GuiElement guiElement) {
        log().trace("Absolute navigation and click on: " + guiElement.toString());
        JSUtils utils = new JSUtils();
        inFrame(guiElement, () -> {
            utils.clickAbsolute(guiElement.getWebDriver(), guiElement.getWebElement());
        });
    }

    public void mouseOverAbsolute2Axis(GuiElement guiElement) {
        JSUtils utils = new JSUtils();
        inFrame(guiElement, () -> {
            utils.mouseOverAbsolute2Axis(guiElement.getWebDriver(), guiElement.getWebElement());
        });
    }

    public void mouseOverJS(GuiElement guiElement) {
        JSUtils utils = new JSUtils();
        inFrame(guiElement, () -> {
            utils.mouseOver(guiElement.getWebDriver(), guiElement.getWebElement());
        });
    }

    public void clickJS(GuiElement guiElement) {
        JSUtils utils = new JSUtils();
        inFrame(guiElement, () -> {
            utils.click(guiElement.getWebDriver(), guiElement.getWebElement());
        });
    }

    public void rightClickJS(GuiElement guiElement) {
        JSUtils utils = new JSUtils();
        inFrame(guiElement, () -> {
            utils.rightClick(guiElement.getWebDriver(), guiElement.getWebElement());
        });
    }

    public void doubleClickJS(GuiElement guiElement) {
        JSUtils utils = new JSUtils();
        inFrame(guiElement, () -> {
            utils.doubleClick(guiElement.getWebDriver(), guiElement.getWebElement());
        });
    }
}
