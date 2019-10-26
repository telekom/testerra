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

import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IBinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IValueAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.frames.IFrameLogic;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.location.Locate;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.util.List;

/**
 * This is meant as template. If you want to implement a Decorator, copy this one and add your decorations.
 * Obviously, the Constructor has to be made public, or any other construction method has to be offered.
 * <p>
 * DO NOT EXTEND THIS DECORATOR BY DEFAULT!!
 * If all, or nearly all methods are decorated, implement the interface instead to ensure that all methods are covered.
 * <p>
 * Created by rnhb on 11.08.2015.
 */
public abstract class GuiElementFacadeDecorator implements GuiElementFacade {

    final GuiElementFacade decoratedFacade;
    private final GuiElementData guiElementData;

    public GuiElementFacadeDecorator(GuiElementFacade decoratedFacade, GuiElementData guiElementData) {
        this.decoratedFacade = decoratedFacade;
        this.guiElementData = guiElementData;
    }

    /**
     * called before any delegation.
     */
    private void beforeDelegation(final String methodName) {
        beforeDelegation(methodName, "");
    }

    protected abstract void beforeDelegation(final String methodName, final String parameterInfo);

    /**
     * called after any delegation.
     *
     * @param result Result of delegation or null
     */
    protected abstract void afterDelegation(final String result);

    /**
     * called only before methods that perform an action
     *
     * @param message description of method to delegate
     */
    protected void beforeActionDelegation(final String message) {

    }

    /**
     * called only after methods that perform an action
     */
    protected void afterActionDelegation() {

    }

    private void afterDelegation() {
        afterDelegation(null);
    }

    @Override
    public int getNumberOfFoundElements() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        final int numberOfFoundElements = decoratedFacade.getNumberOfFoundElements();
        afterDelegation();
        return numberOfFoundElements;
    }

    @Override
    public WebElement getWebElement() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        WebElement webElement = decoratedFacade.getWebElement();
        afterDelegation();
        return webElement;
    }

    @Override
    public By getBy() {
        By by = decoratedFacade.getBy();
        return by;
    }

    @Override
    public GuiElementFacade scrollToElement() {
        return scrollToElement(0);
    }

    @Override
    public GuiElementFacade scrollToElement(int yOffset) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.scrollToElement(yOffset);
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade select() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.select();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade deselect() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.deselect();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade type(String text) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        final String msg = "\"" + obfuscateIfSensible(text) + "\"";
        beforeActionDelegation(methodName + " " + msg);
        beforeDelegation(methodName, msg);
        decoratedFacade.type(text);
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade click() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.click();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade clickJS() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.clickJS();
        afterDelegation();
        afterActionDelegation();
        return this;
    }


    @Override
    public GuiElementFacade rightClick() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.rightClick();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade rightClickJS() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.rightClickJS();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade clickAbsolute() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.clickAbsolute();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade mouseOverAbsolute2Axis() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.mouseOverAbsolute2Axis();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade submit() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.submit();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade sendKeys(CharSequence... charSequences) {
        String chars = "";

        if (charSequences == null) {
            return this;
        }

        if (charSequences.length > 1) {
            for (CharSequence charSequence : charSequences) {
                chars += "[" + charSequence + "] ";
            }
        }
        else {
            chars += charSequences[0];
        }

        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        final String message = "\"" + obfuscateIfSensible(chars) + "\"";
        beforeActionDelegation(methodName + " " + message);
        beforeDelegation(methodName, message);
        decoratedFacade.sendKeys(charSequences);
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade clear() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.clear();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public String getTagName() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        String tagName = decoratedFacade.getTagName();
        afterDelegation(String.format("%s() = %s,", methodName, tagName));
        return tagName;
    }

    @Override
    public String getAttribute(String attributeName) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName, "\"" + attributeName + "\"");
        String attribute = decoratedFacade.getAttribute(attributeName);
        afterDelegation(String.format("%s(%s) = %s,", methodName, attributeName, attribute));
        return attribute;
    }

    @Override
    public boolean isSelected() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        boolean selected = decoratedFacade.isSelected();
        afterDelegation();
        return selected;
    }

    @Override
    public boolean isEnabled() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        boolean enabled = decoratedFacade.isEnabled();
        afterDelegation();
        return enabled;
    }

    @Override
    public String getText() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        String text = decoratedFacade.getText();
        afterDelegation(String.format("%s() = %s,", methodName, text));
        return text;
    }

    @Override
    public GuiElementFacade getSubElement(By by, String description) {
        return getSubElement(by);
    }

    @Override
    public GuiElement getSubElement(Locate locator) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName, locator.toString());
        GuiElement subElement = decoratedFacade.getSubElement(locator);
        afterDelegation(String.format("%s(%s) = %s", methodName, locator.toString(), subElement));
        return subElement;
    }

    @Override
    public GuiElement getSubElement(By by) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName, by.toString());
        GuiElement subElement = decoratedFacade.getSubElement(by);
        afterDelegation(String.format("%s(%s) = %s", methodName, by.toString(), subElement));
        return subElement;
    }

    @Override
    public boolean isDisplayed() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        final boolean displayed = decoratedFacade.isDisplayed();
        afterDelegation(String.format("%s() = %s,", methodName, displayed));
        return displayed;
    }

    @Override
    public boolean isVisible(boolean complete) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        final boolean visible = decoratedFacade.isVisible(complete);
        afterDelegation(String.format("%s() = %s,", methodName, visible));
        return visible;
    }

    @Override
    public boolean isDisplayedFromWebElement() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        final boolean displayedFromWebElement = decoratedFacade.isDisplayedFromWebElement();
        afterDelegation(String.format("%s() = %s,", methodName, displayedFromWebElement));
        return displayedFromWebElement;
    }

    @Override
    public boolean isSelectable() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        final boolean selectable = decoratedFacade.isSelectable();
        afterDelegation(String.format("%s() = %s,", methodName, selectable));
        return selectable;
    }

    @Override
    public Point getLocation() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        final Point location = decoratedFacade.getLocation();
        afterDelegation(String.format("%s() = %s,", methodName, location));
        return location;
    }

    @Override
    public Dimension getSize() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        final Dimension size = decoratedFacade.getSize();
        afterDelegation(String.format("%s() = %s,", methodName, size));
        return size;
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName, "\"" + cssIdentifier + "\"");
        final String cssValue = decoratedFacade.getCssValue(cssIdentifier);
        afterDelegation(String.format("%s(%s) = %s,", methodName, cssIdentifier, cssValue));
        return cssValue;
    }

    @Override
    public GuiElementFacade mouseOver() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.mouseOver();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade mouseOverJS() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.mouseOverJS();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public boolean isPresent() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        final boolean present = decoratedFacade.isPresent();
        afterDelegation(String.format("%s(%s) = %s,", methodName, present));
        return present;
    }

    @Override
    public Select getSelectElement() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        final Select selectElement = decoratedFacade.getSelectElement();
        afterDelegation(String.format("%s(%s) = %s,", methodName, selectElement));
        return selectElement;
    }

    @Override
    public List<String> getTextsFromChildren() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation("getTextsFromChildren");
        List<String> textsFromChildren = decoratedFacade.getTextsFromChildren();
        afterDelegation("getTextsFromChildren() = " + textsFromChildren);
        return textsFromChildren;
    }

    @Override
    public boolean anyFollowingTextNodeContains(String contains) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName, "\"" + contains + "\"");
        final boolean b = decoratedFacade.anyFollowingTextNodeContains(contains);
        afterDelegation(String.format("%s(%s) = %s,", methodName, contains, b));
        return b;
    }

    @Override
    public GuiElementFacade doubleClick() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.doubleClick();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade doubleClickJS() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeActionDelegation(methodName);
        beforeDelegation(methodName);
        decoratedFacade.doubleClickJS();
        afterDelegation();
        afterActionDelegation();
        return this;
    }

    @Override
    public GuiElementFacade highlight() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        decoratedFacade.highlight();
        afterDelegation();
        return this;
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName, "\"" + textToInput + "\"");
        int lengthOfValueAfterSendKeys = decoratedFacade.getLengthOfValueAfterSendKeys(textToInput);
        afterDelegation(String.format("%s(%s) = %s,", methodName, textToInput, lengthOfValueAfterSendKeys));
        return lengthOfValueAfterSendKeys;
    }

//    @Override
//    public boolean waitForIsPresent() {
//        beforeDelegation("waitForIsPresent");
//        boolean b = decoratedFacade.waits().waitForIsPresent();
//        afterDelegation("waitForIsPresent() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsNotPresent() {
//        beforeDelegation("waitForIsNotPresent");
//        boolean b = decoratedFacade.waits().waitForIsNotPresent();
//        afterDelegation("waitForIsNotPresent() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsEnabled() {
//        beforeDelegation("waitForIsEnabled");
//        boolean b = decoratedFacade.waits().waitForIsEnabled();
//        afterDelegation("waitForIsEnabled() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsDisabled() {
//        beforeDelegation("waitForIsDisabled");
//        boolean b = decoratedFacade.waits().waitForIsDisabled();
//        afterDelegation("waitForIsDisabled() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForAnyFollowingTextNodeContains(String contains) {
//        beforeDelegation("waitForAnyFollowingTextNodeContains", "\"" + contains + "\"");
//        boolean b = decoratedFacade.waits().waitForAnyFollowingTextNodeContains(contains);
//        afterDelegation("waitForAnyFollowingTextNodeContains(" + contains + ") = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsDisplayed() {
//        beforeDelegation("waitForIsDisplayed");
//        boolean b = decoratedFacade.waits().waitForIsDisplayed();
//        afterDelegation("waitForIsDisplayed() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsNotDisplayed() {
//        beforeDelegation("waitForIsNotDisplayed");
//        boolean b = decoratedFacade.waits().waitForIsNotDisplayed();
//        afterDelegation("waitForIsNotDisplayed() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsVisible(boolean complete) {
//        beforeDelegation("waitForIsVisible");
//        boolean b = decoratedFacade.waits().waitForIsVisible(complete);
//        afterDelegation("waitForIsVisible() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsNotVisible() {
//        beforeDelegation("waitForIsNotVisible");
//        boolean b = decoratedFacade.waits().waitForIsNotVisible();
//        afterDelegation("waitForIsNotVisible() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsDisplayedFromWebElement() {
//        beforeDelegation("waitForIsDisplayedFromWebElement");
//        boolean b = decoratedFacade.waits().waitForIsDisplayedFromWebElement();
//        afterDelegation("waitForIsDisplayedFromWebElement() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsNotDisplayedFromWebElement() {
//        beforeDelegation("waitForIsNotDisplayedFromWebElement");
//        boolean b = decoratedFacade.waits().waitForIsNotDisplayedFromWebElement();
//        afterDelegation("waitForIsNotDisplayedFromWebElement() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsSelected() {
//        beforeDelegation("waitForIsSelected");
//        boolean b = decoratedFacade.waits().waitForIsSelected();
//        afterDelegation("waitForIsSelected() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsNotSelected() {
//        beforeDelegation("waitForIsNotSelected");
//        boolean b = decoratedFacade.waits().waitForIsNotSelected();
//        afterDelegation("waitForIsNotSelected() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForText(String text) {
//        beforeDelegation("waitForText", "\"" + text + "\"");
//        boolean b = decoratedFacade.waits().waitForText(text);
//        afterDelegation("waitForIsPresent(" + text + ") = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForTextContains(String... text) {
//        beforeDelegation("waitForTextContains", "\"" + Arrays.toString(text) + "\"");
//        boolean b = decoratedFacade.waits().waitForTextContains(text);
//        afterDelegation("waitForIsPresent(" + Arrays.toString(text) + ") = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForTextContainsNot(String... text) {
//        beforeDelegation("waitForTextContainsNot", "\"" + Arrays.toString(text) + "\"");
//        boolean b = decoratedFacade.waits().waitForTextContainsNot(text);
//        afterDelegation("waitForIsGone(" + Arrays.toString(text) + ") = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForAttribute(String attributeName) {
//        beforeDelegation("waitForAttribute", "\"" + attributeName + "\"");
//        boolean b = decoratedFacade.waits().waitForAttribute(attributeName);
//        afterDelegation("waitForIsPresent(" + attributeName + ") = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForAttribute(String attributeName, String value) {
//        beforeDelegation("waitForAttribute", "\"" + attributeName + "\" = \"" + value + "\"");
//        boolean b = decoratedFacade.waits().waitForAttribute(attributeName, value);
//        afterDelegation("waitForAttribute(" + attributeName + ", " + value + ") = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForAttributeContains(String attributeName, String value) {
//        beforeDelegation("waitForAttributeContains", "\"" + attributeName + "\" = \"" + value + "\"");
//        boolean b = decoratedFacade.waits().waitForAttributeContains(attributeName, value);
//        afterDelegation("waitForAttributeContains(" + attributeName + ", " + value + ") = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsSelectable() {
//        beforeDelegation("waitForIsSelectable");
//        boolean b = decoratedFacade.waits().waitForIsSelectable();
//        afterDelegation("waitForIsSelectable() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForIsNotSelectable() {
//        beforeDelegation("waitForIsNotSelectable");
//        boolean b = decoratedFacade.waits().waitForIsNotSelectable();
//        afterDelegation("waitForIsNotSelectable() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForAttributeContainsNot(final String attributeName, final String value) {
//        beforeDelegation("waitForAttributeContainsNot");
//        boolean b = decoratedFacade.waits().waitForAttributeContainsNot(attributeName, value);
//        afterDelegation("waitForAttributeContainsNot() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForCssClassIsPresent(final String className) {
//        beforeDelegation("waitForCssClassIsPresent");
//        boolean b = decoratedFacade.waits().waitForCssClassIsPresent(className);
//        afterDelegation("waitForCssClassIsPresent() = " + b);
//        return b;
//    }
//
//    @Override
//    public boolean waitForCssClassIsNotPresent(final String className) {
//        beforeDelegation("waitForCssClassIsNotPresent");
//        boolean b = decoratedFacade.waits().waitForCssClassIsNotPresent(className);
//        afterDelegation("waitForCssClassIsNotPresent() = " + b);
//        return b;
//    }


    @Override
    public String toString() {
        return decoratedFacade.toString();
    }

    private String obfuscateIfSensible(final String data) {
        if (guiElementData.sensibleData) {
            return "*****************";
        }
        return data;
    }

    @Override
    public GuiElementFacade swipe(int offsetX, int offSetY) {
        decoratedFacade.swipe(offsetX, offSetY);
        return this;
    }

    @Override
    public File takeScreenshot() {
        final String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        beforeDelegation(methodName);
        final File screenshot = decoratedFacade.takeScreenshot();
        afterDelegation();
        return screenshot;
    }

    @Override
    public IFrameLogic getFrameLogic() {
        return decoratedFacade.getFrameLogic();
    }

    @Override
    public GuiElementAssert asserts() {
        return asserts("");
    }

    @Override
    public GuiElementAssert asserts(final String errorMessage) {
        return decoratedFacade.asserts(errorMessage);
    }

    @Override
    public GuiElementWait waits() {
        return decoratedFacade.waits();
    }

    @Override
    public GuiElementAssert nonFunctionalAsserts() {
        return null;
    }

    @Override
    public GuiElementAssert nonFunctionalAsserts(String errorMessage) {
        return null;
    }

    @Override
    public IValueAssertion text() {
        return decoratedFacade.text();
    }

    @Override
    public IValueAssertion value() {
        return decoratedFacade.value();
    }

    @Override
    public IValueAssertion value(final Attribute attribute) {
        return decoratedFacade.value(attribute);
    }

    @Override
    public GuiElementFacade select(final Boolean select) {
        return decoratedFacade.select(select);
    }

    @Override
    public IBinaryAssertion present() {
        return decoratedFacade.present();
    }

    @Override
    public IBinaryAssertion visible(final boolean complete) {
        return decoratedFacade.visible(complete);
    }

    @Override
    public IBinaryAssertion displayed() {
        return decoratedFacade.displayed();
    }

    @Override
    public IBinaryAssertion enabled() {
        return decoratedFacade.enabled();
    }

    @Override
    public IBinaryAssertion selected() {
        return decoratedFacade.selected();
    }

    @Override
    public IImageAssertion screenshot() {
        return decoratedFacade.screenshot();
    }

    @Override
    public GuiElementFacade scrollTo() {
        return decoratedFacade.scrollTo();
    }

    @Override
    public GuiElementFacade scrollTo(int yOffset) {
        return decoratedFacade.scrollTo(yOffset);
    }

    @Override
    public int getTimeoutInSeconds() {
        return decoratedFacade.getTimeoutInSeconds();
    }

    @Override
    public GuiElementFacade setTimeoutInSeconds(int timeoutInSeconds) {
        return decoratedFacade.setTimeoutInSeconds(timeoutInSeconds);
    }

    @Override
    public GuiElementFacade restoreDefaultTimeout() {
         return decoratedFacade.restoreDefaultTimeout();
    }

    @Override
    public List<GuiElement> getList() {
        return decoratedFacade.getList();
    }

    @Override
    public WebDriver getWebDriver() {
        return decoratedFacade.getWebDriver();
    }

    @Override
    public GuiElementFacade setName(String name) {
        return decoratedFacade.setName(name);
    }

    @Override
    public String getName() {
        return decoratedFacade.getName();
    }
}
