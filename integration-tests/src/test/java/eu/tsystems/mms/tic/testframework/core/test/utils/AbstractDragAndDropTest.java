/*
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
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.AbstractTestSitesTest;
import eu.tsystems.mms.tic.testframework.annotations.Fails;
import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.Test;

public abstract class AbstractDragAndDropTest extends AbstractTestSitesTest {

    final By sourceLocatorSimple = By.id("dragLogo");
    final By sourceLocatorFrames = By.xpath(".//img[@alt='Ringo Starr']");

    @Override
    protected TestPage getTestPage() {
        return TestPage.DRAG_AND_DROP;
    }

    private WebDriver getDriver() {
        return WebDriverManager.getWebDriver();
    }

    private GuiElement[] beforeDragAndDropSimple() {
        final WebDriver driver = getDriver();
        GuiElement sourceGuiElement = new GuiElement(driver, sourceLocatorSimple);
        GuiElement destinationGuiElement = new GuiElement(driver, By.id("divRectangle"));
        return new GuiElement[]{sourceGuiElement, destinationGuiElement};
    }

    private GuiElement[] beforeDragAndDropFrames() {
        final WebDriver driver = getDriver();

        GuiElement leftFrame = new GuiElement(driver, By.id("draggableNodes"));
        GuiElement rightFrame = new GuiElement(driver, By.id("dropTargets"));

        GuiElement sourceGuiElement = new GuiElement(driver, sourceLocatorFrames, leftFrame);
        GuiElement destinationGuiElement = new GuiElement(driver, By.id("dropTarget"), rightFrame);
        return new GuiElement[]{sourceGuiElement, destinationGuiElement};
    }

    private void checkResultSimple(GuiElement destinationGuiElement) {
        final GuiElement subElement = destinationGuiElement.getSubElement(sourceLocatorSimple);
        subElement.asserts().assertIsDisplayed();
    }

    private void checkResultFrames(GuiElement destinationGuiElement) {
        final GuiElement subElement = destinationGuiElement.getSubElement(sourceLocatorFrames);
        subElement.asserts().assertIsDisplayed();
    }

    @Test
    @Fails(validFor = "unsupportedBrowser=true", description = "Does not work in this browser!")
    public void testT01_DragAndDrop() {

        if (!(this instanceof DragAndDropJSTest)) {
            throw new SkipException("Skipped. We only support DragAndDropJs");
        }

        final GuiElement[] guiElements = beforeDragAndDropSimple();

        GuiElement sourceGuiElement = guiElements[0];
        GuiElement destinationGuiElement = guiElements[1];

        WebDriver driver = getDriver();

        execute(driver, sourceGuiElement, destinationGuiElement);

        checkResultSimple(destinationGuiElement);
    }

    @Test
    @Fails(validFor = "unsupportedBrowser=true", description = "Does not work in this browser!")
    public void testT2_DragAndDropOverFrames() {

        if (!(this instanceof DragAndDropJSTest)) {
            throw new SkipException("Skipped. We only support DragAndDropJs");
        }

        WebDriver driver = getDriver();
        visitTestPage(driver, TestPage.DRAG_AND_DROP_OVER_FRAMES);

        final GuiElement[] guiElements = beforeDragAndDropFrames();

        GuiElement sourceGuiElement = guiElements[0];
        GuiElement destinationGuiElement = guiElements[1];

        execute(driver, sourceGuiElement, destinationGuiElement);
        checkResultFrames(destinationGuiElement);
    }

    protected abstract void execute(WebDriver driver, GuiElement sourceGuiElement, GuiElement destinationGuiElement);

}
