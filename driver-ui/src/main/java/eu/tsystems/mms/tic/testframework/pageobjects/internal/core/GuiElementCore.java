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

import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.WebElementRetainer;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Provides basic {@link UiElement} features
 * and acts as adapter for {@link WebDriver} implementations
 */
public interface GuiElementCore extends
        GuiElementCoreActions,
        WebElementRetainer
{
    /**
     * Checks if an element is found by webdriver.
     *
     * @return true if found, false otherwise.
     */
    boolean isPresent();

    /**
     * WebElement.isEnabled.
     *
     * @return true if element is enabled.
     */
    boolean isEnabled();

    /**
     * WebElement.isDisplayed.
     *
     * @return true if element is displayed.
     */
    boolean isDisplayed();

    /**
     * Checks if the element is visible in the current viewport
     * @return
     */
    boolean isVisible(boolean fullyVisible);

    /**
     * WebElement.isSelected.
     *
     * @return true if element is selected.
     */
    boolean isSelected();

    /**
     * Calls WebElement.getText. Please note that this will only return a String, if the elements text is actually
     * visible.
     *
     * @return text of the element.
     */
    String getText();

    /**
     * WebElement.getAttribute.
     *
     * @param attributeName Name of the attribute.
     * @return The value of the attribute.
     */
    String getAttribute(String attributeName);

    /**
     * Gets the element's rectangle
     */
    Rectangle getRect();

    /**
     * Checks if the element is selectable.
     *
     * @return ture, if the element is selectable
     */
    boolean isSelectable();

    /**
     * WebElement.getTagName.
     *
     * @return The tag name as String.
     */
    String getTagName();

    /**
     * WebElement.getLocation.
     *
     * @return The location of the webelement.
     */
    Point getLocation();

    /**
     * WebElement.getSize.
     *
     * @return Size of the webelement.
     */
    Dimension getSize();

    /**
     * WebElement.getCssValue.
     *
     * @param cssIdentifier .
     * @return Css value of the webelement.
     */
    String getCssValue(final String cssIdentifier);

    /**
     * Returns with all texts of child-elements.
     *
     * @return .
     */
    @Deprecated
    List<String> getTextsFromChildren();

    /**
     * Types your text and returns the length of the attribute 'value' afterwards.
     * Can be useful to check a maximal input length
     * @see GuiElementAssert#assertInputFieldLength(int)
     * *
     * @param textToInput text to check
     * @return Length of the attribute 'value'
     */
    @Deprecated
    int getLengthOfValueAfterSendKeys(final String textToInput);

    /**
     * A given location strategy (Locator + WebElementFilter) can match to many elements in a Website. This method returns that number.
     * This can be useful for tests, that want to ensure that a GuiElement is found n times.
     *
     * @return The number, how many WebElements were identified by this GuiElement.
     */
    int getNumberOfFoundElements();

    /**
     * Takes a screenshot of the GuiElement
     * @return File object of the screenshot or NULL on error
     */
    File takeScreenshot();

    @Deprecated
    default WebElement getWebElement() {
        AtomicReference<WebElement> atomicWebElement = new AtomicReference<>();
        this.findWebElement(atomicWebElement::set);
        return atomicWebElement.get();
    }
}
