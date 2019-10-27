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

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.List;

/**
 * Created by rnhb on 13.08.2015.
 */
public abstract class GuiElementCoreDecorator extends GuiElementStatusCheckDecorator implements GuiElementCore {

    private GuiElementCore decoratedGuiElementCore;

    public GuiElementCoreDecorator(GuiElementCore decoratedGuiElementCore) {
        super(decoratedGuiElementCore);
        this.decoratedGuiElementCore = decoratedGuiElementCore;
    }

    @Override
    public WebElement getWebElement() {
        beforeDelegation();
        WebElement webElement = decoratedGuiElementCore.getWebElement();
        afterDelegation();
        return webElement;
    }

    @Override
    public By getBy() {
        By by = decoratedGuiElementCore.getBy();
        return by;
    }

    @Override
    public GuiElementCore scrollToElement() {
        beforeDelegation();
        decoratedGuiElementCore.scrollToElement();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore scrollToElement(int yOffset) {
        beforeDelegation();
        decoratedGuiElementCore.scrollToElement(yOffset);
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore select() {
        beforeDelegation();
        decoratedGuiElementCore.select();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore deselect() {
        beforeDelegation();
        decoratedGuiElementCore.deselect();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore type(String text) {
        beforeDelegation();
        decoratedGuiElementCore.type(text);
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore click() {
        beforeDelegation();
        decoratedGuiElementCore.click();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore clickJS() {
        beforeDelegation();
        decoratedGuiElementCore.clickJS();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore rightClick() {
        beforeDelegation();
        decoratedGuiElementCore.rightClick();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore rightClickJS() {
        beforeDelegation();
        decoratedGuiElementCore.rightClickJS();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore clickAbsolute() {
        beforeDelegation();
        decoratedGuiElementCore.clickAbsolute();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore mouseOverAbsolute2Axis() {
        beforeDelegation();
        decoratedGuiElementCore.mouseOverAbsolute2Axis();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore submit() {
        beforeDelegation();
        decoratedGuiElementCore.submit();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore sendKeys(CharSequence... charSequences) {
        beforeDelegation();
        decoratedGuiElementCore.sendKeys(charSequences);
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore clear() {
        beforeDelegation();
        decoratedGuiElementCore.clear();
        afterDelegation();
        return this;
    }

    @Override
    public String getTagName() {
        beforeDelegation();
        String tagName = decoratedGuiElementCore.getTagName();
        afterDelegation();
        return tagName;
    }

    @Override
    public GuiElement getSubElement(By byLocator, String description) {
        return decoratedGuiElementCore.getSubElement(byLocator, description);
    }

    @Override
    public GuiElement getSubElement(Locate locator) {
        return decoratedGuiElementCore.getSubElement(locator);
    }

    @Override
    public GuiElement getSubElement(By by) {
        return decoratedGuiElementCore.getSubElement(by);
    }

    @Override
    public Point getLocation() {
        beforeDelegation();
        Point location = decoratedGuiElementCore.getLocation();
        afterDelegation();
        return location;
    }

    @Override
    public Dimension getSize() {
        beforeDelegation();
        Dimension size = decoratedGuiElementCore.getSize();
        afterDelegation();
        return size;
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        beforeDelegation();
        String cssValue = decoratedGuiElementCore.getCssValue(cssIdentifier);
        afterDelegation();
        return cssValue;
    }

    @Override
    public GuiElementCore mouseOver() {
        beforeDelegation();
        decoratedGuiElementCore.mouseOver();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore mouseOverJS() {
        beforeDelegation();
        decoratedGuiElementCore.mouseOverJS();
        afterDelegation();
        return this;
    }

    @Override
    public Select getSelectElement() {
        beforeDelegation();
        Select select = decoratedGuiElementCore.getSelectElement();
        afterDelegation();
        return select;
    }

    @Override
    public List<String> getTextsFromChildren() {
        beforeDelegation();
        List<String> textsFromChildren = decoratedGuiElementCore.getTextsFromChildren();
        afterDelegation();
        return textsFromChildren;
    }

    @Override
    public GuiElementCore doubleClick() {
        beforeDelegation();
        decoratedGuiElementCore.doubleClick();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore doubleClickJS() {
        beforeDelegation();
        decoratedGuiElementCore.doubleClickJS();
        afterDelegation();
        return this;
    }

    @Override
    public GuiElementCore highlight() {
        beforeDelegation();
        decoratedGuiElementCore.highlight();
        afterDelegation();
        return this;
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        beforeDelegation();
        int lengthOfValueAfterSendKeys = decoratedGuiElementCore.getLengthOfValueAfterSendKeys(textToInput);
        afterDelegation();
        return lengthOfValueAfterSendKeys;
    }

    @Override
    public GuiElementCore swipe(int offsetX, int offSetY) {
        beforeDelegation();
        decoratedGuiElementCore.swipe(offsetX, offSetY);
        afterDelegation();
        return this;
    }

    @Override
    public int getNumberOfFoundElements() {
        beforeDelegation();
        int numberOfFoundElements = decoratedGuiElementCore.getNumberOfFoundElements();
        afterDelegation();
        return numberOfFoundElements;
    }

    @Override
    public File takeScreenshot() {
        beforeDelegation();
        File screenshot = decoratedGuiElementCore.takeScreenshot();
        afterDelegation();
        return screenshot;
    }

    @Override
    public boolean isVisible(boolean complete) {
        beforeDelegation();
        boolean visible = decoratedGuiElementCore.isVisible(complete);
        afterDelegation();
        return visible;
    }
}
