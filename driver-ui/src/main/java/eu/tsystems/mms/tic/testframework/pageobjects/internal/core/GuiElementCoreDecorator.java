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
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public abstract class GuiElementCoreDecorator implements GuiElementCore {

    protected final GuiElementCore core;

    public GuiElementCoreDecorator(GuiElementCore decoratedCore) {
        this.core = decoratedCore;
    }

    protected abstract void beforeDelegation();

    protected abstract void afterDelegation();

    @Override
    public boolean isPresent() {
        beforeDelegation();
        boolean present = core.isPresent();
        afterDelegation();
        return present;
    }

    @Override
    public boolean isEnabled() {
        beforeDelegation();
        boolean enabled = core.isEnabled();
        afterDelegation();
        return enabled;
    }

    @Override
    public boolean isDisplayed() {
        beforeDelegation();
        boolean displayed = core.isDisplayed();
        afterDelegation();
        return displayed;
    }

    @Override
    public boolean isSelected() {
        beforeDelegation();
        boolean selected = core.isSelected();
        afterDelegation();
        return selected;
    }

    @Override
    public String getText() {
        beforeDelegation();
        String text = core.getText();
        afterDelegation();
        return text;
    }

    @Override
    public String getAttribute(String attributeName) {
        beforeDelegation();
        String attributeValue = core.getAttribute(attributeName);
        afterDelegation();
        return attributeValue;
    }

    @Override
    public Rectangle getRect() {
        beforeDelegation();
        Rectangle rect = core.getRect();
        afterDelegation();
        return rect;
    }

    @Override
    public boolean isSelectable() {
        beforeDelegation();
        boolean selectable = core.isSelectable();
        afterDelegation();
        return selectable;
    }

    @Override
    public WebElement findWebElement() {
        beforeDelegation();
        WebElement webElement = core.findWebElement();
        afterDelegation();
        return webElement;
    }

    @Override
    public By getBy() {
        By by = core.getBy();
        return by;
    }

    @Override
    @Deprecated
    public GuiElementCore scrollToElement(int yOffset) {
        beforeDelegation();
        core.scrollToElement(yOffset);
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore scrollIntoView(Point offset) {
        beforeDelegation();
        core.scrollIntoView(offset);
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore select() {
        beforeDelegation();
        core.select();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore deselect() {
        beforeDelegation();
        core.deselect();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore type(String text) {
        beforeDelegation();
        core.type(text);
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore click() {
        beforeDelegation();
        core.click();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore rightClick() {
        beforeDelegation();
        core.rightClick();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore submit() {
        beforeDelegation();
        core.submit();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore sendKeys(CharSequence... charSequences) {
        beforeDelegation();
        core.sendKeys(charSequences);
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore clear() {
        beforeDelegation();
        core.clear();
        afterDelegation();
        return this;
    }

    @Override
    public String getTagName() {
        beforeDelegation();
        String tagName = core.getTagName();
        afterDelegation();
        return tagName;
    }

    @Override
    public Point getLocation() {
        beforeDelegation();
        Point location = core.getLocation();
        afterDelegation();
        return location;
    }

    @Override
    public Dimension getSize() {
        beforeDelegation();
        Dimension size = core.getSize();
        afterDelegation();
        return size;
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        beforeDelegation();
        String cssValue = core.getCssValue(cssIdentifier);
        afterDelegation();
        return cssValue;
    }

    @Override
    public GuiElementCore mouseOver() {
        beforeDelegation();
        core.mouseOver();
        afterDelegation();
        return this;
    }

    @Override
    public Select getSelectElement() {
        beforeDelegation();
        Select select = core.getSelectElement();
        afterDelegation();
        return select;
    }

    @Override
    public List<String> getTextsFromChildren() {
        beforeDelegation();
        List<String> textsFromChildren = core.getTextsFromChildren();
        afterDelegation();
        return textsFromChildren;
    }

    @Override
    public GuiElementCore doubleClick() {
        beforeDelegation();
        core.doubleClick();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore highlight(Color color) {
        beforeDelegation();
        core.highlight(color);
        afterDelegation();
        return this;
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        beforeDelegation();
        int lengthOfValueAfterSendKeys = core.getLengthOfValueAfterSendKeys(textToInput);
        afterDelegation();
        return lengthOfValueAfterSendKeys;
    }

    @Override
    public GuiElementCore swipe(int offsetX, int offSetY) {
        beforeDelegation();
        core.swipe(offsetX, offSetY);
        afterDelegation();
        return this;
    }

    @Override
    public int getNumberOfFoundElements() {
        beforeDelegation();
        int numberOfFoundElements = core.getNumberOfFoundElements();
        afterDelegation();
        return numberOfFoundElements;
    }

    @Override
    public File takeScreenshot() {
        beforeDelegation();
        File screenshot = core.takeScreenshot();
        afterDelegation();
        return screenshot;
    }

    @Override
    public boolean isVisible(boolean complete) {
        beforeDelegation();
        boolean visible = core.isVisible(complete);
        afterDelegation();
        return visible;
    }

    @Override
    public UiElement find(Locate locator) {
        beforeDelegation();
        UiElement element = core.find(locator);
        afterDelegation();
        return element;
    }
}
