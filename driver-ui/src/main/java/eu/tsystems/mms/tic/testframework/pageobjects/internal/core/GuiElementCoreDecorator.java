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
import java.awt.Color;
import java.io.File;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

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
    @Deprecated
    public void scrollToElement(int yOffset) {
        beforeDelegation();
        decoratedGuiElementCore.scrollToElement(yOffset);
        afterDelegation();
    }

    @Override
    public void scrollIntoView(Point offset) {
        beforeDelegation();
        decoratedGuiElementCore.scrollIntoView(offset);
        afterDelegation();
    }

    @Override
    public void select() {
        beforeDelegation();
        decoratedGuiElementCore.select();
        afterDelegation();
    }

    @Override
    public void deselect() {
        beforeDelegation();
        decoratedGuiElementCore.deselect();
        afterDelegation();
    }

    @Override
    public void type(String text) {
        beforeDelegation();
        decoratedGuiElementCore.type(text);
        afterDelegation();
    }

    @Override
    public void click() {
        beforeDelegation();
        decoratedGuiElementCore.click();
        afterDelegation();
    }

    @Override
    public void rightClick() {
        beforeDelegation();
        decoratedGuiElementCore.rightClick();
        afterDelegation();
    }

    @Override
    public void submit() {
        beforeDelegation();
        decoratedGuiElementCore.submit();
        afterDelegation();
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        beforeDelegation();
        decoratedGuiElementCore.sendKeys(charSequences);
        afterDelegation();
    }

    @Override
    public void clear() {
        beforeDelegation();
        decoratedGuiElementCore.clear();
        afterDelegation();
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
    public void mouseOver() {
        beforeDelegation();
        decoratedGuiElementCore.mouseOver();
        afterDelegation();
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
    public void doubleClick() {
        beforeDelegation();
        decoratedGuiElementCore.doubleClick();
        afterDelegation();
    }

    @Override
    public void highlight(Color color) {
        beforeDelegation();
        decoratedGuiElementCore.highlight(color);
        afterDelegation();
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        beforeDelegation();
        int lengthOfValueAfterSendKeys = decoratedGuiElementCore.getLengthOfValueAfterSendKeys(textToInput);
        afterDelegation();
        return lengthOfValueAfterSendKeys;
    }

    @Override
    public void swipe(int offsetX, int offSetY) {
        beforeDelegation();
        decoratedGuiElementCore.swipe(offsetX, offSetY);
        afterDelegation();
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
