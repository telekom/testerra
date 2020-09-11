/*
 * Testerra
 *
 * (C) 2020, Clemens Große, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
package eu.tsystems.mms.tic.testframework.core.pageobjects.testdata;

import eu.tsystems.mms.tic.testframework.pageobjects.Check;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GuiElementShadowRootPage extends Page {

    @Check
    public final GuiElement container = new GuiElement(getWebDriver(), By.cssSelector("#container"));
    @Check
    public final GuiElement normalContent = container.getSubElement(By.cssSelector("#normal-content p"));

    // shadow root element
    public final GuiElement shadowRootElement = new GuiElement(getWebDriver(), By.id("shadow-host")).shadowRoot();
    public final GuiElement shadowContent = shadowRootElement.getSubElement(By.cssSelector("#shadow-content"));

    public final GuiElement shadowContentParagraph = shadowContent.getSubElement(By.cssSelector("p"));
    public final GuiElement shadowContentInput = shadowContent.getSubElement(By.tagName("input"));

    public GuiElementShadowRootPage(WebDriver driver) {
        super(driver);
    }


    public void typeText(final String text) {
        shadowContentInput.type(text);
    }

    public void assertInputText(final String expectedText) {
        final String currentInputText = shadowContentInput.getAttribute("value");
        Assert.assertEquals(currentInputText, expectedText, "ShadowInput contains expected text");
    }

    public void assertShadowRootVisibility() {
        // shadow root is never displayed but present
        shadowRootElement.asserts().assertIsNotDisplayed();
        shadowRootElement.asserts().assertIsPresent();

        // sub elements from shadow root are displayed
        shadowContent.asserts().assertIsDisplayed();
        shadowContentInput.asserts().assertIsDisplayed();
        shadowContentParagraph.asserts().assertIsDisplayed();
    }
}
