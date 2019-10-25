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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.List;

/**
 * Created by rnhb on 12.08.2015.
 */
public interface GuiElementCore extends GuiElementStatusCheck, TakesScreenshot {

    /**
     * Returns the Webelement.
     *
     * @return webElement
     */
    WebElement getWebElement();

    /**
     * Returns by locator element.
     *
     * @return by locator
     */
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
     * get sub element by locator
     *
     * @param byLocator   Locator of new element.
     * @param description Description for GuiElement
     * @return GuiElement
     */
    @Deprecated
    GuiElementFacade getSubElement(final By byLocator, String description);

    /**
     * Get sub element by new locator
     * @param locator
     * @return
     */
    GuiElementFacade getSubElement(final Locate locator);

    /**
     * Get sub element by Selenium By
     * @param by
     * @return
     */
    GuiElementFacade getSubElement(final By by);

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
    @Deprecated
    File takeScreenshot();
}
