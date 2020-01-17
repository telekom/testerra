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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement.variations;

import eu.tsystems.mms.tic.testframework.core.test.TestPage;
import eu.tsystems.mms.tic.testframework.core.test.pageobjects.guielement.AbstractGuiElementNonFunctionalAssertionTest;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

public class FrameAwareGuiElementTest extends AbstractGuiElementNonFunctionalAssertionTest {

    /**
     * Test if FrameAwareElement in deepest hierarchy is present via AllFrameLogic
     */
    @Test
    public void testTFA01_GuiElement_FrameLogic_AllFrames() {
        WebDriver driver = getWebDriver();
        GuiElement frame1 = new GuiElement(driver, By.name("frame1"));
        GuiElement frame12 = new GuiElement(driver, By.name("frame12"), frame1);
        GuiElement frame123 = new GuiElement(driver, By.name("frame123"), frame1, frame12);
        GuiElement frame1234 = new GuiElement(driver, By.name("InputFrame1234"), frame1, frame12, frame123);
        GuiElement element = new GuiElement(driver, By.id("16"), frame1, frame12, frame123, frame1234);
        element.asserts().assertIsPresent();
    }

    @Override
    public GuiElement getGuiElementBy(Locate locator) {
        WebDriver driver = getWebDriver();
        GuiElement frame = new GuiElement(driver, By.name("InputFrame1"));
        return new GuiElement(driver, locator, frame);
    }

    @Override
    protected TestPage getStartPage() {
        return TestPage.FRAME_TEST_PAGE;
    }
}
