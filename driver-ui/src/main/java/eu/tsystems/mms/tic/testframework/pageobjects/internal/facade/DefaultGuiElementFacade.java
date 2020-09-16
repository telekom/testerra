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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.facade;

import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
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

/**
 * This class only facades {@link GuiElementCore} at the moment.
 * See {@link GuiElementFacade} for details.
 * Was formerly known as StandardGuiElementFacade.
 * @Todo make compatible to {@link UiElement}
 */
public class DefaultGuiElementFacade implements GuiElementFacade {

    private GuiElementCore decoratedCore;

    public DefaultGuiElementFacade(GuiElementCore guiElementCore) {
        this.decoratedCore = guiElementCore;
    }

    @Override
    public File takeScreenshot() {
        return decoratedCore.takeScreenshot();
    }

    @Override
    public WebElement findWebElement() {
        return decoratedCore.findWebElement();
    }

    @Override
    public By getBy() {
        return decoratedCore.getBy();
    }

    @Override
    public GuiElementFacade scrollToElement() {
        decoratedCore.scrollToElement();
        return this;
    }

    @Override
    public GuiElementFacade scrollToElement(int yOffset) {
        decoratedCore.scrollToElement(yOffset);
        return this;
    }

    @Override
    public GuiElementFacade scrollIntoView(Point offset) {
        decoratedCore.scrollIntoView(offset);
        return this;
    }

    @Override
    public GuiElementFacade select() {
        decoratedCore.select();
        return this;
    }

    @Override
    public GuiElementFacade deselect() {
        decoratedCore.deselect();
        return this;
    }

    @Override
    public GuiElementFacade type(String text) {
        decoratedCore.type(text);
        return this;
    }

    @Override
    public GuiElementFacade click() {
        decoratedCore.click();
        return this;
    }

    @Override
    public GuiElementFacade submit() {
        decoratedCore.submit();
        return this;
    }

    @Override
    public GuiElementFacade sendKeys(CharSequence... charSequences) {
        decoratedCore.sendKeys(charSequences);
        return this;
    }

    @Override
    public GuiElementFacade clear() {
        decoratedCore.clear();
        return this;
    }

    @Override
    public String getTagName() {
        return decoratedCore.getTagName();
    }

    @Override
    public String getAttribute(String attributeName) {
        return decoratedCore.getAttribute(attributeName);
    }

    @Override
    public boolean isSelected() {
        return decoratedCore.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return decoratedCore.isEnabled();
    }

    @Override
    public String getText() {
        return decoratedCore.getText();
    }

    @Override
    public boolean isDisplayed() {
        return decoratedCore.isDisplayed();
    }

    @Override
    public boolean isVisible(boolean complete) {
        return decoratedCore.isVisible(complete);
    }

    @Override
    public Rectangle getRect() {
        return decoratedCore.getRect();
    }

    @Override
    public boolean isSelectable() {
        return decoratedCore.isSelectable();
    }

    @Override
    public Point getLocation() {
        return decoratedCore.getLocation();
    }

    @Override
    public Dimension getSize() {
        return decoratedCore.getSize();
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        return decoratedCore.getCssValue(cssIdentifier);
    }

    @Override
    public GuiElementFacade mouseOver() {
        decoratedCore.mouseOver();
        return this;
    }

    @Override
    public boolean isPresent() {
        return decoratedCore.isPresent();
    }

    @Override
    public Select getSelectElement() {
        return decoratedCore.getSelectElement();
    }

    @Override
    public List<String> getTextsFromChildren() {
        return decoratedCore.getTextsFromChildren();
    }

    @Override
    public GuiElementFacade doubleClick() {
        decoratedCore.doubleClick();
        return this;
    }

    @Override
    public GuiElementFacade highlight() {
        decoratedCore.highlight();
        return this;
    }

    @Override
    public GuiElementFacade swipe(int offsetX, int offSetY) {
        decoratedCore.swipe(offsetX, offSetY);
        return this;
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        return decoratedCore.getLengthOfValueAfterSendKeys(textToInput);
    }

    @Override
    public int getNumberOfFoundElements() {
        return decoratedCore.getNumberOfFoundElements();
    }

    @Override
    public GuiElementFacade rightClick() {
        decoratedCore.rightClick();
        return this;
    }

    @Override
    public GuiElementFacade highlight(Color color) {
        decoratedCore.highlight(color);
        return this;
    }

    @Override
    public UiElement find(Locate locator) {
        return decoratedCore.find(locator);
    }

    @Override
    public void findWebElement(Consumer<WebElement> consumer) {
        decoratedCore.findWebElement(consumer);
    }
}
