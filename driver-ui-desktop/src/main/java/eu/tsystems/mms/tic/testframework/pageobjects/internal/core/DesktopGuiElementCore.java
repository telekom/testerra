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

package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.utils.MouseActions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class DesktopGuiElementCore extends AbstractWebDriverCore implements Loggable {

    public DesktopGuiElementCore(GuiElementData guiElementData) {
        super(guiElementData);
    }

    @Override
    public void swipe(int offsetX, int offSetY) {
        MouseActions.swipeElement(guiElementData.getGuiElement(), offsetX, offSetY);
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        clear();
        sendKeys(textToInput);
        int valueLength = getAttribute("value").length();
        return valueLength;
    }

    protected void switchToDefaultContent(WebDriver webDriver) {
        webDriver.switchTo().defaultContent();
    }

    protected void switchToFrame(WebDriver webDriver, WebElement webElement) {
        webDriver.switchTo().frame(webElement);
    }
}
