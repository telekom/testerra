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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.exceptions.ElementNotFoundException;
import eu.tsystems.mms.tic.testframework.exceptions.NonUniqueElementException;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.List;

/**
 * Provides basic {@link UiElement} features
 * and acts as adapter for {@link WebDriver} implementations
 */
public interface GuiElementCore {
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
    boolean isVisible(final boolean complete);

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
     * Calls isDisplayed on the underlying WebElement.
     *
     * @return isDisplayed from WebElement
     */
    boolean isDisplayedFromWebElement();

    /**
     * Checks if the element is selectable.
     *
     * @return ture, if the element is selectable
     */
    boolean isSelectable();

    /**
     * Returns the Webelement.
     *
     * @return webElement
     */
    @Deprecated
    WebElement getWebElement();

    /**
     * @return All found filtered WebElements
     * @throws ElementNotFoundException If non found
     * @throws NonUniqueElementException If more than one WebElement has been found according to given {@link Locate}
     */
    List<WebElement> findWebElements();

    /**
     * @return The first found filtered WebElement
     * @throws ElementNotFoundException If none found
     * @throws NonUniqueElementException If more than one WebElement has been found according to given {@link Locate}
     */
    WebElement findWebElement();

    /**
     * Returns by locator element.
     *
     * @return by locator
     */
    @Deprecated
    By getBy();

    /**
     * Scroll to the position of this element.
     *
     * @return this.
     */
    GuiElementCore scrollToElement();
    GuiElementCore scrollToElement(final int yOffset);

    /**
     * Select a selectable element.
     *
     * @return this.
     */
    GuiElementCore select();

    /**
     * Deselect a selectable element.
     *
     * @return this.
     */
    GuiElementCore deselect();

    /**
     * Types text into element method with delete of prior content and following check if content was correct written.
     *
     * @param text The text to type.
     * @return this.
     */
    GuiElementCore type(final String text);

    /**
     * Click on element with prior mouseover.
     *
     * @return .
     */
    GuiElementCore click();

    /**
     * Clicks on a web element using javascript.
     *
     * @return .
     */
    GuiElementCore clickJS();

    /**
     * click
     *
     * @return .
     */
    GuiElementCore clickAbsolute();

    /**
     * hover mouse over 2 axis
     *
     * @return .
     */
    GuiElementCore mouseOverAbsolute2Axis();

    /**
     * submit
     *
     * @return .
     */
    GuiElementCore submit();

    /**
     * WebElement.sendKeys.
     *
     * @param charSequences .
     * @return send Keys
     */
    GuiElementCore sendKeys(final CharSequence... charSequences);

    /**
     * WebElement.clear.
     *
     * @return clear webelement
     */
    GuiElementCore clear();

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
     * Mouseover using WebDriver.
     *
     * @return this.
     */
    GuiElementCore mouseOver();

    /**
     * Mouseover directly over js event.
     *
     * @return this.
     */
    GuiElementCore mouseOverJS();

    /**
     * Returns the Select-Object.
     *
     * @return .
     */
    Select getSelectElement();

    /**
     * Returns with all texts of child-elements.
     *
     * @return .
     */
    List<String> getTextsFromChildren();

    /**
     * doubleclick
     *
     * @return .
     */
    GuiElementCore doubleClick();

    /**
     * Highlight element.
     *
     * @return element.
     */
    GuiElementCore highlight();

    /**
     * Swipe the element by the given offset. (0,0) should be the top left.
     *
     * @param offsetX horizontal offset in pixel.
     * @param offSetY vertical offset in pixel.
     */
    GuiElementCore swipe(final int offsetX, final int offSetY);

    /**
     * Types your text and returns the length of the attribute 'value' afterwards.
     * Can be useful to check a maximal input length
     * @see GuiElementAssert#assertInputFieldLength(int)
     * *
     * @param textToInput text to check
     * @return Length of the attribute 'value'
     */
    int getLengthOfValueAfterSendKeys(final String textToInput);

    /**
     * A given location strategy (Locator + WebElementFilter) can match to many elements in a Website. This method returns that number.
     * This can be useful for tests, that want to ensure that a GuiElement is found n times.
     *
     * @return The number, how many WebElements were identified by this GuiElement.
     */
    int getNumberOfFoundElements();

    GuiElementCore rightClick();
    GuiElementCore rightClickJS();

    GuiElementCore doubleClickJS();

    /**
     * Takes a screenshot of the GuiElement
     * @return File object of the screenshot or NULL on error
     */
    File takeScreenshot();
}
