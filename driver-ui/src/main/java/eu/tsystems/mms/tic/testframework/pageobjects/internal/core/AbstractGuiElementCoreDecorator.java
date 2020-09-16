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

import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public abstract class AbstractGuiElementCoreDecorator implements GuiElementCore {

    protected final GuiElementCore decoratedCore;

    public AbstractGuiElementCoreDecorator(GuiElementCore decoratedCore) {
        this.decoratedCore = decoratedCore;
    }

    protected abstract void beforeDelegation();

    protected abstract void afterDelegation();

    @Override
    public boolean isPresent() {
        beforeDelegation();
        boolean present = decoratedCore.isPresent();
        afterDelegation();
        return present;
    }

    @Override
    public boolean isEnabled() {
        beforeDelegation();
        boolean enabled = decoratedCore.isEnabled();
        afterDelegation();
        return enabled;
    }

    @Override
    public boolean isDisplayed() {
        beforeDelegation();
        boolean displayed = decoratedCore.isDisplayed();
        afterDelegation();
        return displayed;
    }

    @Override
    public boolean isSelected() {
        beforeDelegation();
        boolean selected = decoratedCore.isSelected();
        afterDelegation();
        return selected;
    }

    @Override
    public String getText() {
        beforeDelegation();
        String text = decoratedCore.getText();
        afterDelegation();
        return text;
    }

    @Override
    public String getAttribute(String attributeName) {
        beforeDelegation();
        String attributeValue = decoratedCore.getAttribute(attributeName);
        afterDelegation();
        return attributeValue;
    }

    @Override
    public Rectangle getRect() {
        beforeDelegation();
        Rectangle rect = decoratedCore.getRect();
        afterDelegation();
        return rect;
    }

    @Override
    public boolean isSelectable() {
        beforeDelegation();
        boolean selectable = decoratedCore.isSelectable();
        afterDelegation();
        return selectable;
    }

    @Override
    public WebElement findWebElement() {
        beforeDelegation();
        WebElement webElement = decoratedCore.findWebElement();
        afterDelegation();
        return webElement;
    }

    @Override
    public void findWebElement(Consumer<WebElement> consumer) {
        beforeDelegation();
        decoratedCore.findWebElement(consumer);
        afterDelegation();
    }

    @Override
    public By getBy() {
        By by = decoratedCore.getBy();
        return by;
    }

    @Override
    @Deprecated
    public GuiElementCore scrollToElement(int yOffset) {
        beforeDelegation();
        decoratedCore.scrollToElement(yOffset);
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore scrollIntoView(Point offset) {
        beforeDelegation();
        decoratedCore.scrollIntoView(offset);
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore select() {
        beforeDelegation();
        decoratedCore.select();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore deselect() {
        beforeDelegation();
        decoratedCore.deselect();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore type(String text) {
        beforeDelegation();
        decoratedCore.type(text);
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore click() {
        beforeDelegation();
        decoratedCore.click();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore rightClick() {
        beforeDelegation();
        decoratedCore.rightClick();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore submit() {
        beforeDelegation();
        decoratedCore.submit();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore sendKeys(CharSequence... charSequences) {
        beforeDelegation();
        decoratedCore.sendKeys(charSequences);
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore clear() {
        beforeDelegation();
        decoratedCore.clear();
        afterDelegation();
        return this;
    }

    @Override
    public String getTagName() {
        beforeDelegation();
        String tagName = decoratedCore.getTagName();
        afterDelegation();
        return tagName;
    }

    @Override
    public Point getLocation() {
        beforeDelegation();
        Point location = decoratedCore.getLocation();
        afterDelegation();
        return location;
    }

    @Override
    public Dimension getSize() {
        beforeDelegation();
        Dimension size = decoratedCore.getSize();
        afterDelegation();
        return size;
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        beforeDelegation();
        String cssValue = decoratedCore.getCssValue(cssIdentifier);
        afterDelegation();
        return cssValue;
    }

    @Override
    public GuiElementCore mouseOver() {
        beforeDelegation();
        decoratedCore.mouseOver();
        afterDelegation();
        return this;
    }

    @Override
    public Select getSelectElement() {
        beforeDelegation();
        Select select = decoratedCore.getSelectElement();
        afterDelegation();
        return select;
    }

    @Override
    public List<String> getTextsFromChildren() {
        beforeDelegation();
        List<String> textsFromChildren = decoratedCore.getTextsFromChildren();
        afterDelegation();
        return textsFromChildren;
    }

    @Override
    public GuiElementCore doubleClick() {
        beforeDelegation();
        decoratedCore.doubleClick();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore highlight(Color color) {
        beforeDelegation();
        decoratedCore.highlight(color);
        afterDelegation();
        return this;
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        beforeDelegation();
        int lengthOfValueAfterSendKeys = decoratedCore.getLengthOfValueAfterSendKeys(textToInput);
        afterDelegation();
        return lengthOfValueAfterSendKeys;
    }

    @Override
    public GuiElementCore swipe(int offsetX, int offSetY) {
        beforeDelegation();
        decoratedCore.swipe(offsetX, offSetY);
        afterDelegation();
        return this;
    }

    @Override
    public int getNumberOfFoundElements() {
        beforeDelegation();
        int numberOfFoundElements = decoratedCore.getNumberOfFoundElements();
        afterDelegation();
        return numberOfFoundElements;
    }

    @Override
    public File takeScreenshot() {
        beforeDelegation();
        File screenshot = decoratedCore.takeScreenshot();
        afterDelegation();
        return screenshot;
    }

    @Override
    public boolean isVisible(boolean complete) {
        beforeDelegation();
        boolean visible = decoratedCore.isVisible(complete);
        afterDelegation();
        return visible;
    }

    @Override
    public UiElement find(Locate locator) {
        beforeDelegation();
        UiElement element = decoratedCore.find(locator);
        afterDelegation();
        return element;
    }
}
