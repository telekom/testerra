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
package eu.tsystems.mms.tic.testframework.core.test.utils;

import eu.tsystems.mms.tic.testframework.constants.Browsers;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.webdrivermanager.WebDriverManager;
import eu.tsystems.mms.tic.testframework.webdrivermanager.desktop.WebDriverMode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.SkipException;
import org.testng.annotations.Test;

/**
 * Created by pele on 21.12.2015.
 */
public abstract class AbstractDragAndDropTest {

    final By sourceLocatorSimple = By.id("dragLogo");
    final By sourceLocatorFrames = By.xpath(".//img[@alt='Ringo Starr']");

    private WebDriver getDriver() {
        return WebDriverManager.getWebDriver();
    }

    private GuiElement[] beforeDragAndDropSimple() {
        WebDriverManager.config().browser = Browsers.firefox;
        WebDriverManager.config().webDriverMode = WebDriverMode.remote;

        final WebDriver driver = getDriver();

        String url = TestPage.DRAG_AND_DROP.getUrl();

        driver.get(url);

        GuiElement sourceGuiElement = new GuiElement(driver, sourceLocatorSimple);
        GuiElement destinationGuiElement = new GuiElement(driver, By.id("divRectangle"));
        return new GuiElement[] { sourceGuiElement, destinationGuiElement };
    }

    private GuiElement[] beforeDragAndDropFrames() {
        WebDriverManager.config().browser = Browsers.firefox;
        WebDriverManager.config().webDriverMode = WebDriverMode.remote;

        final WebDriver driver = getDriver();

        String url = TestPage.DRAG_AND_DROP_OVER_FRAMES.getUrl();
        driver.get(url);

        GuiElement leftFrame = new GuiElement(driver, By.id("draggableNodes"));
        GuiElement rightFrame = new GuiElement(driver, By.id("dropTargets"));

        GuiElement sourceGuiElement = new GuiElement(driver, sourceLocatorFrames, leftFrame);
        GuiElement destinationGuiElement = new GuiElement(driver, By.id("dropTarget"), rightFrame);
        return new GuiElement[] { sourceGuiElement, destinationGuiElement };
    }

    private void checkResultSimple(GuiElement destinationGuiElement) {
        final GuiElement subElement = destinationGuiElement.getSubElement(sourceLocatorSimple);
        subElement.assertIsDisplayed();
    }

    private void checkResultFrames(GuiElement destinationGuiElement) {
        final GuiElement subElement = destinationGuiElement.getSubElement(sourceLocatorFrames);
        subElement.assertIsDisplayed();
    }

    @Test
    public void testT01_DragAndDrop() throws Exception {
        if (this instanceof DragAndDropWDActionsTest) {
            throw new SkipException("Skipped. Would end up in a watchdog bite while mouseMove");
        }

        final GuiElement[] guiElements = beforeDragAndDropSimple();

        GuiElement sourceGuiElement = guiElements[0];
        GuiElement destinationGuiElement = guiElements[1];

        WebDriver driver = getDriver();

        execute(driver, sourceGuiElement, destinationGuiElement);

        checkResultSimple(destinationGuiElement);
    }

    @Test
    public void testT2_DragAndDropOverFrames() throws Exception {
        if (this instanceof DragAndDropWDActionsTest) {
            throw new SkipException("Skipped. Would end up in a watchdog bite while mouseMove");
        }

        final GuiElement[] guiElements = beforeDragAndDropFrames();

        GuiElement sourceGuiElement = guiElements[0];
        GuiElement destinationGuiElement = guiElements[1];

        WebDriver driver = getDriver();
        execute(driver, sourceGuiElement, destinationGuiElement);

        checkResultFrames(destinationGuiElement);
    }

    protected abstract void execute(WebDriver driver, GuiElement sourceGuiElement, GuiElement destinationGuiElement);

}
