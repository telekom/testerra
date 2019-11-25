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

import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.WebElementAdapter;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
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

    private WebElementAdapter guiElementCore;

    public DefaultGuiElementFacade(WebElementAdapter guiElementCore) {
        this.guiElementCore = guiElementCore;
    }

    @Override
    public File takeScreenshot() {
        return guiElementCore.takeScreenshot();
    }

    @Override
    public WebElement getWebElement() {
        return guiElementCore.getWebElement();
    }

    @Override
    public List<WebElement> findWebElements() {
        return guiElementCore.findWebElements();
    }

    @Override
    public WebElement findWebElement() {
        return guiElementCore.findWebElement();
    }

    @Override
    public By getBy() {
        return guiElementCore.getBy();
    }

    @Override
    public GuiElementFacade scrollToElement() {
        guiElementCore.scrollToElement();
        return this;
    }

    @Override
    public GuiElementFacade scrollToElement(int yOffset) {
        guiElementCore.scrollToElement(yOffset);
        return this;
    }

    @Override
    public GuiElementFacade select() {
        guiElementCore.select();
        return this;
    }

    @Override
    public GuiElementFacade deselect() {
        guiElementCore.deselect();
        return this;
    }

    @Override
    public GuiElementFacade type(String text) {
        guiElementCore.type(text);
        return this;
    }

    @Override
    public GuiElementFacade click() {
        guiElementCore.click();
        return this;
    }

    @Override
    public GuiElementFacade clickJS() {
        guiElementCore.clickJS();
        return this;
    }

    @Override
    public GuiElementFacade clickAbsolute() {
        guiElementCore.clickAbsolute();
        return this;
    }

    @Override
    public GuiElementFacade mouseOverAbsolute2Axis() {
        guiElementCore.mouseOverAbsolute2Axis();
        return this;
    }

    @Override
    public GuiElementFacade submit() {
        guiElementCore.submit();
        return this;
    }

    @Override
    public GuiElementFacade sendKeys(CharSequence... charSequences) {
        guiElementCore.sendKeys(charSequences);
        return this;
    }

    @Override
    public GuiElementFacade clear() {
        guiElementCore.clear();
        return this;
    }

    @Override
    public String getTagName() {
        return guiElementCore.getTagName();
    }

    @Override
    public String getAttribute(String attributeName) {
        return guiElementCore.getAttribute(attributeName);
    }

    @Override
    public boolean isSelected() {
        return guiElementCore.isSelected();
    }

    @Override
    public boolean isEnabled() {
        return guiElementCore.isEnabled();
    }

    @Override
    public String getText() {
        return guiElementCore.getText();
    }

    @Override
    public boolean isDisplayed() {
        return guiElementCore.isDisplayed();
    }

    @Override
    public boolean isVisible(boolean complete) {
        return guiElementCore.isVisible(complete);
    }

    @Override
    public boolean isDisplayedFromWebElement() {
        return guiElementCore.isDisplayedFromWebElement();
    }

    @Override
    public boolean isSelectable() {
        return guiElementCore.isSelectable();
    }

    @Override
    public Point getLocation() {
        return guiElementCore.getLocation();
    }

    @Override
    public Dimension getSize() {
        return guiElementCore.getSize();
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        return guiElementCore.getCssValue(cssIdentifier);
    }

    @Override
    public GuiElementFacade mouseOver() {
        guiElementCore.mouseOver();
        return this;
    }

    @Override
    public GuiElementFacade mouseOverJS() {
        guiElementCore.mouseOverJS();
        return this;
    }

    @Override
    public boolean isPresent() {
        return guiElementCore.isPresent();
    }

    @Override
    public Select getSelectElement() {
        return guiElementCore.getSelectElement();
    }

    @Override
    public List<String> getTextsFromChildren() {
        return guiElementCore.getTextsFromChildren();
    }

    @Override
    public GuiElementFacade doubleClick() {
        guiElementCore.doubleClick();
        return this;
    }

    @Override
    public GuiElementFacade highlight() {
        guiElementCore.highlight();
        return this;
    }

    @Override
    public GuiElementFacade swipe(int offsetX, int offSetY) {
        guiElementCore.swipe(offsetX, offSetY);
        return this;
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        return guiElementCore.getLengthOfValueAfterSendKeys(textToInput);
    }

    @Override
    public int getNumberOfFoundElements() {
        return guiElementCore.getNumberOfFoundElements();
    }

    @Override
    public GuiElementFacade rightClick() {
        guiElementCore.rightClick();
        return this;
    }

    @Override
    public GuiElementFacade rightClickJS() {
        guiElementCore.rightClickJS();
        return this;
    }

    @Override
    public GuiElementFacade doubleClickJS() {
        guiElementCore.doubleClickJS();
        return this;
    }
}
