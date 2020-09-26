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

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

/**
 * Abstract decorator for a {@link GuiElementCore}
 */
public abstract class AbstractGuiElementCoreDecorator extends AbstractGuiElementCoreActionsDecorator implements GuiElementCore {

    public AbstractGuiElementCoreDecorator(GuiElementCore decoratedCore) {
        super(decoratedCore);
    }

    protected void beforeDelegation(String method, Object ... params) {

    }

    protected void afterDelegation() {

    }

    @Override
    public boolean isPresent() {
        beforeDelegation("isPresent");
        boolean present = decoratedCore.isPresent();
        afterDelegation();
        return present;
    }

    @Override
    public boolean isEnabled() {
        beforeDelegation("isEnabled");
        boolean enabled = decoratedCore.isEnabled();
        afterDelegation();
        return enabled;
    }

    @Override
    public boolean isDisplayed() {
        beforeDelegation("isDisplayed");
        boolean displayed = decoratedCore.isDisplayed();
        afterDelegation();
        return displayed;
    }

    @Override
    public boolean isSelected() {
        beforeDelegation("isSelected");
        boolean selected = decoratedCore.isSelected();
        afterDelegation();
        return selected;
    }

    @Override
    public String getText() {
        beforeDelegation("getText");
        String text = decoratedCore.getText();
        afterDelegation();
        return text;
    }

    @Override
    public String getAttribute(String attributeName) {
        beforeDelegation("getAttribute", attributeName);
        String attributeValue = decoratedCore.getAttribute(attributeName);
        afterDelegation();
        return attributeValue;
    }

    @Override
    public Rectangle getRect() {
        beforeDelegation("getRect");
        Rectangle rect = decoratedCore.getRect();
        afterDelegation();
        return rect;
    }

    @Override
    public boolean isSelectable() {
        beforeDelegation("isSelectable");
        boolean selectable = decoratedCore.isSelectable();
        afterDelegation();
        return selectable;
    }

    @Override
    public void findWebElement(Consumer<WebElement> consumer) {
        beforeDelegation("findWebElement");
        decoratedCore.findWebElement(consumer);
        afterDelegation();
    }

    @Override
    public String getTagName() {
        beforeDelegation("getTagName");
        String tagName = decoratedCore.getTagName();
        afterDelegation();
        return tagName;
    }

    @Override
    public Point getLocation() {
        beforeDelegation("getLocation");
        Point location = decoratedCore.getLocation();
        afterDelegation();
        return location;
    }

    @Override
    public Dimension getSize() {
        beforeDelegation("getSize");
        Dimension size = decoratedCore.getSize();
        afterDelegation();
        return size;
    }

    @Override
    public String getCssValue(String cssIdentifier) {
        beforeDelegation("getCssValue");
        String cssValue = decoratedCore.getCssValue(cssIdentifier);
        afterDelegation();
        return cssValue;
    }

    @Override
    public List<String> getTextsFromChildren() {
        beforeDelegation("getTextsFromChildren");
        List<String> textsFromChildren = decoratedCore.getTextsFromChildren();
        afterDelegation();
        return textsFromChildren;
    }

    @Override
    public int getLengthOfValueAfterSendKeys(String textToInput) {
        beforeDelegation("getLengthOfValueAfterSendKeys", textToInput);
        int lengthOfValueAfterSendKeys = decoratedCore.getLengthOfValueAfterSendKeys(textToInput);
        afterDelegation();
        return lengthOfValueAfterSendKeys;
    }

    @Override
    public int getNumberOfFoundElements() {
        beforeDelegation("getNumberOfFoundElements");
        int numberOfFoundElements = decoratedCore.getNumberOfFoundElements();
        afterDelegation();
        return numberOfFoundElements;
    }

    @Override
    public File takeScreenshot() {
        beforeDelegation("takeScreenshot");
        File screenshot = decoratedCore.takeScreenshot();
        afterDelegation();
        return screenshot;
    }

    @Override
    public boolean isVisible(boolean complete) {
        beforeDelegation("isVisible", complete);
        boolean visible = decoratedCore.isVisible(complete);
        afterDelegation();
        return visible;
    }
}
