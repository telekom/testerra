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

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.awt.*;
import java.io.File;
import java.util.List;

public interface GuiElementCore extends GuiElementStatusCheck {

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
    @Deprecated
    default void scrollToElement() {
        scrollToElement(0);
    }
    @Deprecated
    void scrollToElement(int yOffset);

    /**
     * Centers the element in the viewport
     */
    default void scrollIntoView() {
        scrollIntoView(new Point(0,0));
    }

    /**
     * Centers the element in the viewport with a given offset
     * @param offset
     */
    void scrollIntoView(Point offset);

    /**
     * Select a selectable element.
     *
     * @return this.
     */
    void select();

    /**
     * Deselect a selectable element.
     *
     * @return this.
     */
    void deselect();

    /**
     * Types text into element method with delete of prior content and following check if content was correct written.
     *
     * @param text The text to type.
     * @return this.
     */
    void type(String text);

    /**
     * Click on element with prior mouseover.
     *
     * @return .
     */
    void click();

    /**
     * submit
     *
     * @return .
     */
    void submit();

    /**
     * WebElement.sendKeys.
     *
     * @param charSequences .
     * @return send Keys
     */
    void sendKeys(CharSequence... charSequences);

    /**
     * WebElement.clear.
     *
     * @return clear webelement
     */
    void clear();

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
    GuiElement getSubElement(By byLocator, String description);

    /**
     * Get sub element by new locator
     * @param locator
     * @return
     */
    GuiElement getSubElement(Locate locator);

    /**
     * Get sub element by Selenium By
     * @param by
     * @return
     */
    GuiElement getSubElement(By by);

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
    String getCssValue(String cssIdentifier);

    /**
     * Mouseover using WebDriver.
     *
     * @return this.
     */
    void mouseOver();

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
    void doubleClick();

    /**
     * Highlight element.
     *
     * @return element.
     */
    default void highlight() {
        highlight(new Color(0, 0, 255));
    }
    void highlight(Color color);

    /**
     * Swipe the element by the given offset. (0,0) should be the top left.
     *
     * @param offsetX horizontal offset in pixel.
     * @param offSetY vertical offset in pixel.
     */
    void swipe(int offsetX, int offSetY);

    /**
     * Types your text and returns the length of the attribute 'value' afterwards.
     * Can be useful to check a maximal input length
     * @see GuiElementAssert#assertInputFieldLength(int)
     * *
     * @param textToInput text to check
     * @return Length of the attribute 'value'
     */
    int getLengthOfValueAfterSendKeys(String textToInput);

    /**
     * A given location strategy (Locator + WebElementFilter) can match to many elements in a Website. This method returns that number.
     * This can be useful for tests, that want to ensure that a GuiElement is found n times.
     *
     * @return The number, how many WebElements were identified by this GuiElement.
     */
    int getNumberOfFoundElements();

    void rightClick();
    /**
     * Takes a screenshot of the GuiElement
     * @return File object of the screenshot or NULL on error
     */
    File takeScreenshot();
}
