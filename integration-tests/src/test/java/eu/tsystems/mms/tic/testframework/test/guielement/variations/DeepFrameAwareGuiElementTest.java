/*
 * Testerra
 *
 * (C) 2020, Eric Kubenka, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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

package eu.tsystems.mms.tic.testframework.test.guielement.variations;

import eu.tsystems.mms.tic.testframework.core.testpage.TestPage;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locator;
import eu.tsystems.mms.tic.testframework.test.guielement.AbstractGuiElementNonFunctionalAssertionTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DeepFrameAwareGuiElementTest extends AbstractGuiElementNonFunctionalAssertionTest {

    @Override
    public GuiElement getGuiElementBy(Locator locator) {
        WebDriver driver = getWebDriver();
        GuiElement frame1 = new GuiElement(driver, By.name("frame1")).setName("frame1");
        GuiElement frame12 = frame1.getSubElement(By.name("frame12")).setName("frame2");
        GuiElement frame123 = frame12.getSubElement(By.name("frame123")).setName("frame3");
        GuiElement frame1234 = frame123.getSubElement(By.name("InputFrame1234")).setName("frame4");
        return frame1234.getSubElement(locator).setName("GuiElementUnderTest");
    }

    @Override
    protected TestPage getTestPage() {
        return TestPage.FRAME_TEST_PAGE;
    }
}
