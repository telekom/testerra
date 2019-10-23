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

/**
 * Created by rnhb on 12.08.2015.
 */
//@Deprecated
//public class StandardGuiElementFacade implements GuiElementFacade {
//
//    private GuiElementWait guiElementWait;
//    private GuiElementCore guiElementCore;
//
//    public StandardGuiElementFacade(
//        GuiElementCore guiElementCore,
//        GuiElementWait guiElementWait,
//        GuiElementAssert guiElementAssert
//    ) {
//        this.guiElementWait = guiElementWait;
//        this.guiElementCore = guiElementCore;
//    }
//
//    @Override
//    public File takeScreenshot() {
//        return guiElementCore.takeScreenshot();
//    }
//
//    @Override
//    public WebElement getWebElement() {
//        return guiElementCore.getWebElement();
//    }
//
//    @Override
//    public By getBy() {
//        return guiElementCore.getBy();
//    }
//
//    @Override
//    public GuiElementCore scrollToElement() {
//        guiElementCore.scrollToElement();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore scrollToElement(int yOffset) {
//        guiElementCore.scrollToElement(yOffset);
//        return this;
//    }
//
//    @Override
//    public GuiElementCore select() {
//        guiElementCore.select();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore deselect() {
//        guiElementCore.deselect();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore type(String text) {
//        guiElementCore.type(text);
//        return this;
//    }
//
//    @Override
//    public GuiElementCore click() {
//        guiElementCore.click();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore clickJS() {
//        guiElementCore.clickJS();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore clickAbsolute() {
//        guiElementCore.clickAbsolute();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore mouseOverAbsolute2Axis() {
//        guiElementCore.mouseOverAbsolute2Axis();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore submit() {
//        guiElementCore.submit();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore sendKeys(CharSequence... charSequences) {
//        guiElementCore.sendKeys(charSequences);
//        return this;
//    }
//
//    @Override
//    public GuiElementCore clear() {
//        guiElementCore.clear();
//        return this;
//    }
//
//    @Override
//    public String getTagName() {
//        return guiElementCore.getTagName();
//    }
//
//    @Override
//    public String getAttribute(String attributeName) {
//        return guiElementCore.getAttribute(attributeName);
//    }
//
//    @Override
//    public boolean isSelected() {
//        return guiElementCore.isSelected();
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return guiElementCore.isEnabled();
//    }
//
//    @Override
//    public String getText() {
//        return guiElementCore.getText();
//    }
//
//    @Override
//    public GuiElementFacade getSubElement(By byLocator, String description) {
//        return guiElementCore.getSubElement(byLocator, description);
//    }
//
//    @Override
//    public GuiElementFacade getSubElement(Locate locator) {
//        return guiElementCore.getSubElement(locator);
//    }
//
//    @Override
//    public GuiElementFacade getSubElement(By by) {
//        return guiElementCore.getSubElement(by);
//    }
//
//    @Override
//    public boolean isDisplayed() {
//        return guiElementCore.isDisplayed();
//    }
//
//    @Override
//    public boolean isVisible(boolean complete) {
//        return guiElementCore.isVisible(complete);
//    }
//
//    @Override
//    public boolean isDisplayedFromWebElement() {
//        return guiElementCore.isDisplayedFromWebElement();
//    }
//
//    @Override
//    public boolean isSelectable() {
//        return guiElementCore.isSelectable();
//    }
//
//    @Override
//    public Point getLocation() {
//        return guiElementCore.getLocation();
//    }
//
//    @Override
//    public Dimension getSize() {
//        return guiElementCore.getSize();
//    }
//
//    @Override
//    public String getCssValue(String cssIdentifier) {
//        return guiElementCore.getCssValue(cssIdentifier);
//    }
//
//    @Override
//    public GuiElementCore mouseOver() {
//        guiElementCore.mouseOver();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore mouseOverJS() {
//        guiElementCore.mouseOverJS();
//        return this;
//    }
//
//    @Override
//    public boolean isPresent() {
//        return guiElementCore.isPresent();
//    }
//
//    @Override
//    public Select getSelectElement() {
//        return guiElementCore.getSelectElement();
//    }
//
//    @Override
//    public List<String> getTextsFromChildren() {
//        return guiElementCore.getTextsFromChildren();
//    }
//
//    @Override
//    public boolean anyFollowingTextNodeContains(String contains) {
//        return guiElementCore.anyFollowingTextNodeContains(contains);
//    }
//
//    @Override
//    public GuiElementCore doubleClick() {
//        guiElementCore.doubleClick();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore highlight() {
//        guiElementCore.highlight();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore swipe(int offsetX, int offSetY) {
//        guiElementCore.swipe(offsetX, offSetY);
//        return this;
//    }
//
//    @Override
//    public int getLengthOfValueAfterSendKeys(String textToInput) {
//        return guiElementCore.getLengthOfValueAfterSendKeys(textToInput);
//    }
//
//    @Override
//    public int getNumberOfFoundElements() {
//        return guiElementCore.getNumberOfFoundElements();
//    }
//
//    @Override
//    public GuiElementCore rightClick() {
//        guiElementCore.rightClick();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore rightClickJS() {
//        guiElementCore.rightClickJS();
//        return this;
//    }
//
//    @Override
//    public GuiElementCore doubleClickJS() {
//        guiElementCore.doubleClickJS();
//        return this;
//    }

//    @Override
//    public boolean waitForIsPresent() {
//        return guiElementWait.waitForIsPresent();
//    }
//
//    @Override
//    public boolean waitForIsNotPresent() {
//        return guiElementWait.waitForIsNotPresent();
//    }
//
//    @Override
//    public boolean waitForIsEnabled() {
//        return guiElementWait.waitForIsEnabled();
//    }
//
//    @Override
//    public boolean waitForIsDisabled() {
//        return guiElementWait.waitForIsDisabled();
//    }
//
//    @Override
//    public boolean waitForAnyFollowingTextNodeContains(String contains) {
//        return guiElementWait.waitForAnyFollowingTextNodeContains(contains);
//    }
//
//    @Override
//    public boolean waitForIsDisplayed() {
//        return guiElementWait.waitForIsDisplayed();
//    }
//
//    @Override
//    public boolean waitForIsNotDisplayed() {
//        return guiElementWait.waitForIsNotDisplayed();
//    }
//
//    @Override
//    public boolean waitForIsVisible(boolean complete) {
//        return guiElementWait.waitForIsVisible(complete);
//    }
//
//    @Override
//    public boolean waitForIsNotVisible() {
//        return guiElementWait.waitForIsNotVisible();
//    }
//
//    @Override
//    public boolean waitForIsDisplayedFromWebElement() {
//        return guiElementWait.waitForIsDisplayedFromWebElement();
//    }
//
//    @Override
//    public boolean waitForIsNotDisplayedFromWebElement() {
//        return guiElementWait.waitForIsNotDisplayedFromWebElement();
//    }
//
//    @Override
//    public boolean waitForIsSelected() {
//        return guiElementWait.waitForIsSelected();
//    }
//
//    @Override
//    public boolean waitForIsNotSelected() {
//        return guiElementWait.waitForIsNotSelected();
//    }
//
//    @Override
//    public boolean waitForText(String text) {
//        return guiElementWait.waitForText(text);
//    }
//
//    @Override
//    public boolean waitForTextContains(String... text) {
//        return guiElementWait.waitForTextContains(text);
//    }
//
//    @Override
//    public boolean waitForTextContainsNot(String... text) {
//        return guiElementWait.waitForTextContainsNot(text);
//    }
//
//    @Override
//    public boolean waitForAttribute(String attributeName) {
//        return guiElementWait.waitForAttribute(attributeName);
//    }
//
//    @Override
//    public boolean waitForAttribute(String attributeName, String value) {
//        return guiElementWait.waitForAttribute(attributeName, value);
//    }
//
//    @Override
//    public boolean waitForAttributeContains(String attributeName, String value) {
//        return guiElementWait.waitForAttributeContains(attributeName, value);
//    }
//
//    @Override
//    public boolean waitForAttributeContainsNot(final String attributeName, final String value) {
//        return guiElementWait.waitForAttributeContainsNot(attributeName, value);
//    }
//
//    @Override
//    public boolean waitForCssClassIsPresent(final String className) {
//        return guiElementWait.waitForCssClassIsPresent(className);
//    }
//
//    @Override
//    public boolean waitForCssClassIsNotPresent(final String className) {
//        return guiElementWait.waitForCssClassIsNotPresent(className);
//    }
//
//    @Override
//    public boolean waitForIsSelectable() {
//        return guiElementWait.waitForIsSelectable();
//    }
//
//    @Override
//    public boolean waitForIsNotSelectable() {
//        return guiElementWait.waitForIsNotSelectable();
//    }
//}
