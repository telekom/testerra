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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.facade;

import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.layout.Layout;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.List;

/**
 * Created by rnhb on 12.08.2015.
 */
public class StandardGuiElementFacade implements GuiElementFacade {

    private GuiElementWait guiElementWait;
    private GuiElementAssert guiElementAssert;
    private GuiElementCore guiElementCore;

    public StandardGuiElementFacade(
        GuiElementCore guiElementCore,
        GuiElementWait guiElementWait,
        GuiElementAssert guiElementAssert
    ) {
        this.guiElementWait = guiElementWait;
        this.guiElementAssert = guiElementAssert;
        this.guiElementCore = guiElementCore;
    }

    @Override
    public void assertIsPresentFast() {
        guiElementAssert.assertIsPresentFast();
    }

    @Override
    public void assertIsPresent() {
        guiElementAssert.assertIsPresent();
    }

    @Override
    public void assertIsNotPresent() {
        guiElementAssert.assertIsNotPresent();
    }

    @Override
    public void assertIsNotPresentFast() {
        guiElementAssert.assertIsNotPresentFast();
    }

    @Override
    public void assertIsSelected() {
        guiElementAssert.assertIsSelected();
    }

    @Override
    public void assertIsNotSelected() {
        guiElementAssert.assertIsNotSelected();
    }

    @Override
    public void assertIsNotSelectable() {
        guiElementAssert.assertIsNotSelectable();
    }

    @Override
    public void assertIsSelectable() {
        guiElementAssert.assertIsSelectable();
    }

    @Override
    public void assertIsDisplayed() {
        guiElementAssert.assertIsDisplayed();
    }

    @Override
    public void assertIsNotDisplayed() {
        guiElementAssert.assertIsNotDisplayed();
    }

    @Override
    public void assertIsDisplayedFromWebElement() {
        guiElementAssert.assertIsDisplayedFromWebElement();
    }

    @Override
    public void assertIsNotDisplayedFromWebElement() {
        guiElementAssert.assertIsNotDisplayedFromWebElement();
    }

    @Override
    public void assertText(String text) {
        guiElementAssert.assertText(text);
    }

    @Override
    public void assertContainsText(String... text) {
        guiElementAssert.assertContainsText(text);
    }

    @Override
    public void assertAttributeIsPresent(String attributeName) {
        guiElementAssert.assertAttributeIsPresent(attributeName);
    }

    @Override
    public void assertAttributeValue(String attributeName, String value) {
        guiElementAssert.assertAttributeValue(attributeName, value);
    }

    @Override
    public void assertAttributeContains(String attributeName, String textContainedByAttribute) {
        guiElementAssert.assertAttributeContains(attributeName, textContainedByAttribute);
    }

    @Override
    public void assertAnyFollowingTextNodeContains(String contains) {
        guiElementAssert.assertAnyFollowingTextNodeContains(contains);
    }

    @Deprecated
    @Override
    public void assertMatchPixels(final String targetImageName) {
        guiElementAssert.assertMatchPixels(targetImageName);
    }

    @Override
    public File takeScreenshot() {
        return guiElementCore.takeScreenshot();
    }

    @Override
    public void assertIsEnabled() {
        guiElementAssert.assertIsEnabled();
    }

    @Override
    public void assertIsDisabled() {
        guiElementAssert.assertIsDisabled();
    }

    @Override
    public void assertInputFieldLength(int length) {
        guiElementAssert.assertInputFieldLength(length);
    }

    @Override
    public void assertLayout(Layout layout) {
        guiElementAssert.assertLayout(layout);
    }

    @Override
    public WebElement getWebElement() {
        return guiElementCore.getWebElement();
    }

    @Override
    public By getBy() {
        return guiElementCore.getBy();
    }

    @Override
    public void scrollToElement() {
        guiElementCore.scrollToElement();
    }

    @Override
    public void scrollToElement(int yOffset) {
        guiElementCore.scrollToElement(yOffset);
    }

    @Override
    public void select() {
        guiElementCore.select();
    }

    @Override
    public void deselect() {
        guiElementCore.deselect();
    }

    @Override
    public void type(String text) {
        guiElementCore.type(text);
    }

    @Override
    public void click() {
        guiElementCore.click();
    }

    @Override
    public void clickJS() {
        guiElementCore.clickJS();
    }

    @Override
    public void clickAbsolute() {
        guiElementCore.clickAbsolute();
    }

    @Override
    public void mouseOverAbsolute2Axis() {
        guiElementCore.mouseOverAbsolute2Axis();
    }

    @Override
    public void submit() {
        guiElementCore.submit();
    }

    @Override
    public void sendKeys(CharSequence... charSequences) {
        guiElementCore.sendKeys(charSequences);
    }

    @Override
    public void clear() {
        guiElementCore.clear();
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
    public List<WebElement> findElements(By byLocator) {
        return guiElementCore.findElements(byLocator);
    }

    @Override
    public GuiElement getSubElement(By byLocator, String description) {
        return guiElementCore.getSubElement(byLocator, description);
    }

    @Override
    public WebElement findElement(By byLocator) {
        return guiElementCore.findElement(byLocator);
    }

    @Override
    public boolean isDisplayed() {
        return guiElementCore.isDisplayed();
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
    public Rectangle getRect() {
        return guiElementCore.getRect();
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        return guiElementCore.getCssValue(cssIdentifier);
    }

    @Override
    public void mouseOver() {
        guiElementCore.mouseOver();
    }

    @Override
    public void mouseOverJS() {
        guiElementCore.mouseOverJS();
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
    public void refresh() {
        guiElementCore.refresh();
    }

    @Override
    public boolean anyFollowingTextNodeContains(String contains) {
        return guiElementCore.anyFollowingTextNodeContains(contains);
    }

    @Override
    public void doubleClick() {
        guiElementCore.doubleClick();
    }

    @Override
    public void highlight() {
        guiElementCore.highlight();
    }

    @Override
    public void swipe(int offsetX, int offSetY) {
        guiElementCore.swipe(offsetX, offSetY);
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
    public void rightClick() {
        guiElementCore.rightClick();
    }

    @Override
    public void rightClickJS() {
        guiElementCore.rightClickJS();
    }

    @Override
    public void doubleClickJS() {
        guiElementCore.doubleClickJS();
    }

    @Override
    public boolean waitForIsPresent() {
        return guiElementWait.waitForIsPresent();
    }

    @Override
    public boolean waitForIsNotPresent() {
        return guiElementWait.waitForIsNotPresent();
    }

    @Override
    public boolean waitForIsEnabled() {
        return guiElementWait.waitForIsEnabled();
    }

    @Override
    public boolean waitForIsDisabled() {
        return guiElementWait.waitForIsDisabled();
    }

    @Override
    public boolean waitForAnyFollowingTextNodeContains(String contains) {
        return guiElementWait.waitForAnyFollowingTextNodeContains(contains);
    }

    @Override
    public boolean waitForIsDisplayed() {
        return guiElementWait.waitForIsDisplayed();
    }

    @Override
    public boolean waitForIsNotDisplayed() {
        return guiElementWait.waitForIsNotDisplayed();
    }

    @Override
    public boolean waitForIsDisplayedFromWebElement() {
        return guiElementWait.waitForIsDisplayedFromWebElement();
    }

    @Override
    public boolean waitForIsNotDisplayedFromWebElement() {
        return guiElementWait.waitForIsNotDisplayedFromWebElement();
    }

    @Override
    public boolean waitForIsSelected() {
        return guiElementWait.waitForIsSelected();
    }

    @Override
    public boolean waitForIsNotSelected() {
        return guiElementWait.waitForIsNotSelected();
    }

    @Override
    public boolean waitForText(String text) {
        return guiElementWait.waitForText(text);
    }

    @Override
    public boolean waitForTextContains(String... text) {
        return guiElementWait.waitForTextContains(text);
    }

    @Override
    public boolean waitForTextContainsNot(String... text) {
        return guiElementWait.waitForTextContainsNot(text);
    }

    @Override
    public boolean waitForAttribute(String attributeName) {
        return guiElementWait.waitForAttribute(attributeName);
    }

    @Override
    public boolean waitForAttribute(String attributeName, String value) {
        return guiElementWait.waitForAttribute(attributeName, value);
    }

    @Override
    public boolean waitForAttributeContains(String attributeName, String value) {
        return guiElementWait.waitForAttributeContains(attributeName, value);
    }

    @Override
    public boolean waitForCssClass(String className) {
        return guiElementWait.waitForCssClass(className);
    }

    @Override
    public boolean waitForIsSelectable() {
        return guiElementWait.waitForIsSelectable();
    }

    @Override
    public boolean waitForIsNotSelectable() {
        return guiElementWait.waitForIsNotSelectable();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) throws WebDriverException {
        return guiElementCore.getScreenshotAs(target);
    }
}
