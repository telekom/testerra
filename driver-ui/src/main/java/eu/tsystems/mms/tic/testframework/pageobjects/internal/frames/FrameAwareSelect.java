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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.frames;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by rnhb on 17.02.2015.
 * <p>
 * To avoid frame switching, the FrameSwitchGuiElementDecorator is used. Because the Select class is independent of that,
 * we need to encapsulate its calls in the same fashion. This has to be done carefully, as a call from an overridden method to
 * another overridden method will likely result in an error, as each method returns to the default content.
 */
public class FrameAwareSelect extends Select {
    /**
     * Frames containing this element, in the correct order from outer to inner.
     */
    private final GuiElementFacade[] frames;

    private final WebDriver driver;

    private final Select select;

    private final IFrameLogic frameLogic;

    /**
     * This is not actually an extension of the Select class, but a decorator.
     * An actual extension is not advisable, as this class heavily calls its own methods, resulting in switching to default frame
     * before further commands on elements in the correct frame.
     *
     * @param selectWebElement Element to Decorate
     * @param frames           frames this element sits in
     * @param driver           driver
     */
    public FrameAwareSelect(Select selectWebElement, WebElement element, GuiElementFacade[] frames, WebDriver driver) {
        super(element);
        this.frames = frames;
        this.driver = driver;
        this.select = selectWebElement;
        frameLogic = new FrameLogic(driver, frames);
    }

    /**
     * @return All options belonging to this select tag
     */
    @Override
    public List<WebElement> getOptions() {
        frameLogic.switchToCorrectFrame();
        List<WebElement> options = select.getOptions();
        LinkedList<WebElement> elements = new LinkedList<WebElement>();
        for (WebElement option : options) {
            FrameAwareWebElementDecorator decoratedElement = new FrameAwareWebElementDecorator(option, frames, driver);
            elements.add(decoratedElement);
        }
        frameLogic.switchToDefaultFrame();
        return elements;
    }

    /**
     * @return All selected options belonging to this select tag
     */
    @Override
    public List<WebElement> getAllSelectedOptions() {
        frameLogic.switchToCorrectFrame();
        List<WebElement> allSelectedOptions = select.getAllSelectedOptions();
        LinkedList<WebElement> elements = new LinkedList<WebElement>();
        for (WebElement option : allSelectedOptions) {
            FrameAwareWebElementDecorator decoratedElement = new FrameAwareWebElementDecorator(option, frames, driver);
            elements.add(decoratedElement);
        }
        frameLogic.switchToDefaultFrame();
        return elements;
    }

    /**
     * @return The first selected option in this select tag (or the currently selected option in a
     * normal select)
     */
    @Override
    public WebElement getFirstSelectedOption() {
        frameLogic.switchToCorrectFrame();
        WebElement firstSelectedOption = select.getFirstSelectedOption();
        FrameAwareWebElementDecorator decoratedElement = new FrameAwareWebElementDecorator(firstSelectedOption, frames, driver);
        frameLogic.switchToDefaultFrame();
        return decoratedElement;
    }

    /**
     * Select all options that display text matching the argument. That is, when given "Bar" this
     * would select an option like:
     * <p>
     * &lt;option value="foo"&gt;Bar&lt;/option&gt;
     *
     * @param text The visible text to match against
     */
    @Override
    public void selectByVisibleText(String text) {
        frameLogic.switchToCorrectFrame();
        select.selectByVisibleText(text);
        frameLogic.switchToDefaultFrame();
    }

    /**
     * Select the option at the given index. This is done by examing the "index" attribute of an
     * element, and not merely by counting.
     *
     * @param index The option at this index will be selected
     */
    @Override
    public void selectByIndex(int index) {
        frameLogic.switchToCorrectFrame();
        select.selectByIndex(index);
        frameLogic.switchToCorrectFrame();
    }

    /**
     * Select all options that have a value matching the argument. That is, when given "foo" this
     * would select an option like:
     * <p>
     * &lt;option value="foo"&gt;Bar&lt;/option&gt;
     *
     * @param value The value to match against
     */
    @Override
    public void selectByValue(String value) {
        frameLogic.switchToCorrectFrame();
        select.selectByValue(value);
        frameLogic.switchToDefaultFrame();
    }

    /**
     * Clear all selected entries. This is only valid when the SELECT supports multiple selections.
     *
     * @throws UnsupportedOperationException If the SELECT does not support multiple selections
     */
    @Override
    public void deselectAll() {
        frameLogic.switchToCorrectFrame();
        select.deselectAll();
        frameLogic.switchToDefaultFrame();
    }

    /**
     * Deselect all options that have a value matching the argument. That is, when given "foo" this
     * would deselect an option like:
     * <p>
     * &lt;option value="foo"&gt;Bar&lt;/option&gt;
     *
     * @param value The value to match against
     */
    @Override
    public void deselectByValue(String value) {
        frameLogic.switchToCorrectFrame();
        select.deselectByValue(value);
        frameLogic.switchToDefaultFrame();
    }

    /**
     * Deselect the option at the given index. This is done by examing the "index" attribute of an
     * element, and not merely by counting.
     *
     * @param index The option at this index will be deselected
     */
    @Override
    public void deselectByIndex(int index) {
        frameLogic.switchToCorrectFrame();
        select.deselectByIndex(index);
        frameLogic.switchToDefaultFrame();
    }

    /**
     * Deselect all options that display text matching the argument. That is, when given "Bar" this
     * would deselect an option like:
     * <p>
     * &lt;option value="foo"&gt;Bar&lt;/option&gt;
     *
     * @param text The visible text to match against
     */
    @Override
    public void deselectByVisibleText(String text) {
        frameLogic.switchToCorrectFrame();
        select.deselectByVisibleText(text);
        frameLogic.switchToDefaultFrame();
    }

    @Override
    public String toString() {
        return select.toString();
    }
}
