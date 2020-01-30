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

import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.List;

/**
 * This class only facades {@link GuiElementCore} at the moment.
 * See {@link GuiElementFacade} for details.
 * Was formerly known as StandardGuiElementFacade.
 */
public class DefaultGuiElementFacade implements GuiElementFacade {

    private GuiElementCore core;

    public DefaultGuiElementFacade(GuiElementCore guiElementCore) {
        this.core = guiElementCore;
    }

    @Override
    public File takeScreenshot() {
        return core.takeScreenshot();
    }

    @Override
    public WebElement getWebElement() {
        return core.getWebElement();
    }

    @Override
    public List<WebElement> findWebElements() {
        return core.findWebElements();
    }

    @Override
    public WebElement findWebElement() {
        return core.findWebElement();
    }

    @Override
    public By getBy() {
        return core.getBy();
    }

    @Override
    public GuiElementFacade scrollToElement() {
        core.scrollToElement();
        return this;
    }

    @Override
    public GuiElementFacade scrollToElement(int yOffset) {
        core.scrollToElement(yOffset);
        return this;
    }

    @Override
    public GuiElementFacade select() {
        core.select();
        return this;
    }

    @Override
    public GuiElementFacade deselect() {
        core.deselect();
        return this;
    }

    @Override
    public GuiElementFacade type(String text) {
        core.type(text);
        return this;
    }

    @Override
    public GuiElementFacade click() {
        core.click();
        return this;
    }

    @Override
    public GuiElementFacade clickJS() {
        core.clickJS();
        return this;
    }

    @Override
    public GuiElementFacade clickAbsolute() {
        core.clickAbsolute();
        return this;
    }

    @Override
    public GuiElementFacade mouseOverAbsolute2Axis() {
        core.mouseOverAbsolute2Axis();
        return this;
    }

    @Override
    public GuiElementFacade submit() {
        core.submit();
        return this;
    }

    @Override
    public GuiElementFacade sendKeys(CharSequence... charSequences) {
        core.sendKeys(charSequences);
        return this;
    }

    @Override
    public GuiElementFacade clear() {
        core.clear();
        return this;
    }

    @Override
    public String getTagName() {
        return core.getTagName();
    }

    @Override
    public String getAttribute(String attributeName) {
        return core.getAttribute(attributeName);
    }

    @Override
    public boolean isSelected() {
        return core.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return core.isEnabled();
    }

    @Override
    public String getText() {
        return core.getText();
    }

    @Override
    public boolean isDisplayed() {
        return core.isDisplayed();
    }

    @Override
    public boolean isVisible(boolean complete) {
        return core.isVisible(complete);
    }

    @Override
    public boolean isDisplayedFromWebElement() {
        return core.isDisplayedFromWebElement();
    }

    @Override
    public Rectangle getRect() {
        return core.getRect();
    }

    @Override
    public boolean isSelectable() {
        return core.isSelectable();
    }

    @Override
    public Point getLocation() {
        return core.getLocation();
    }

    @Override
    public Dimension getSize() {
        return core.getSize();
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        return core.getCssValue(cssIdentifier);
    }

    @Override
    public GuiElementFacade mouseOver() {
        core.mouseOver();
        return this;
    }

    @Override
    public GuiElementFacade mouseOverJS() {
        core.mouseOverJS();
        return this;
    }

    @Override
    public boolean isPresent() {
        return core.isPresent();
    }

    @Override
    public Select getSelectElement() {
        return core.getSelectElement();
    }

    @Override
    public List<String> getTextsFromChildren() {
        return core.getTextsFromChildren();
    }

    @Override
    public GuiElementFacade doubleClick() {
        core.doubleClick();
        return this;
    }

    @Override
    public GuiElementFacade highlight() {
        core.highlight();
        return this;
    }

    @Override
    public GuiElementFacade swipe(int offsetX, int offSetY) {
        core.swipe(offsetX, offSetY);
        return this;
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        return core.getLengthOfValueAfterSendKeys(textToInput);
    }

    @Override
    public int getNumberOfFoundElements() {
        return core.getNumberOfFoundElements();
    }

    @Override
    public GuiElementFacade rightClick() {
        core.rightClick();
        return this;
    }

    @Override
    public GuiElementFacade rightClickJS() {
        core.rightClickJS();
        return this;
    }

    @Override
    public GuiElementFacade doubleClickJS() {
        core.doubleClickJS();
        return this;
    }
}
