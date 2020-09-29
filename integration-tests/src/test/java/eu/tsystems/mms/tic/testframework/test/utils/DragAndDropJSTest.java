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
package eu.tsystems.mms.tic.testframework.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.utils.MouseActions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class DragAndDropJSTest extends AbstractTestSitesTest {

    final By sourceLocatorSimple = By.id("dragLogo");

    @Override
    protected TestPage getTestPage() {
        return TestPage.DRAG_AND_DROP;
    }

    private GuiElement[] beforeDragAndDropSimple() {
        final WebDriver driver = getWebDriver();
        GuiElement sourceGuiElement = new GuiElement(driver, sourceLocatorSimple);
        GuiElement destinationGuiElement = new GuiElement(driver, By.id("divRectangle"));
        return new GuiElement[]{sourceGuiElement, destinationGuiElement};
    }

    private void checkResultSimple(GuiElement destinationGuiElement) {
        final GuiElement subElement = destinationGuiElement.getSubElement(sourceLocatorSimple);
        subElement.asserts().assertIsDisplayed();
    }

    @Test
    @Fails(validFor = "unsupportedBrowser=true", description = "Does not work in this browser!")
    public void testT01_DragAndDrop() {
        final GuiElement[] guiElements = beforeDragAndDropSimple();

        GuiElement sourceGuiElement = guiElements[0];
        GuiElement destinationGuiElement = guiElements[1];

        MouseActions.dragAndDropJS(sourceGuiElement, destinationGuiElement);

        checkResultSimple(destinationGuiElement);
    }
}
