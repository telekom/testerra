/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

public class DragAndDropOverFramesTest extends AbstractTestSitesTest {
    final By sourceLocatorFrames = By.xpath(".//img[@alt='Ringo Starr']");

    @Override
    public TestPage getTestPage() {
        return TestPage.DRAG_AND_DROP_OVER_FRAMES;
    }

    private GuiElement[] beforeDragAndDropFrames() {
        final WebDriver driver = getWebDriver();

        GuiElement leftFrame = new GuiElement(driver, By.id("draggableNodes"));
        GuiElement rightFrame = new GuiElement(driver, By.id("dropTargets"));

        GuiElement sourceGuiElement = leftFrame.getSubElement(sourceLocatorFrames);
        GuiElement destinationGuiElement = rightFrame.getSubElement(By.id("dropTarget"));
        return new GuiElement[]{sourceGuiElement, destinationGuiElement};
    }

    private void checkResultFrames(GuiElement destinationGuiElement) {
        final GuiElement subElement = destinationGuiElement.getSubElement(sourceLocatorFrames);
        subElement.asserts().assertIsDisplayed();
    }

    @Test
    @Fails(validFor = "unsupportedBrowser=true", description = "Does not work in this browser!")
    public void testT2_DragAndDropOverFrames() {
        final GuiElement[] guiElements = beforeDragAndDropFrames();
        GuiElement sourceGuiElement = guiElements[0];
        GuiElement destinationGuiElement = guiElements[1];

        MouseActions.dragAndDropJS(sourceGuiElement, destinationGuiElement);

        checkResultFrames(destinationGuiElement);
    }
}
