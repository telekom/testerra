/*
 * Testerra
 *
 * (C) 2020, Mike Reiche, T-Systems Multimedia Solutions GmbH, Deutsche Telekom AG
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
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.TestableUiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.layout.ILayout;

@Deprecated
public class LegacyGuiElementAssertWrapper implements GuiElementAssert {

    private final UiElementAssertion uiElementAssertion;
    private final GuiElement guiElement;
    private final Assertion useAssertion;

    public LegacyGuiElementAssertWrapper(GuiElement guiElement, Assertion useAssertion) {
        this.guiElement = guiElement;
        this.useAssertion = useAssertion;
        this.uiElementAssertion = createUiElementAssertion(guiElement, useAssertion);
    }

    private UiElementAssertion createUiElementAssertion(UiElement uiElement, Assertion assertion) {
        return new DefaultUiElementAssertion(uiElement, assertion);
    }

    @Override
    public void assertIsPresent() {
        this.uiElementAssertion.numberOfElements().isGreaterThan(0);
    }

    @Override
    public void assertIsNotPresent() {
        this.uiElementAssertion.numberOfElements().is(0);
    }

    @Override
    public void assertIsDisplayed() {
        this.uiElementAssertion.displayed(true);
    }

    @Override
    public void assertIsNotDisplayed() {
        this.uiElementAssertion.displayed(false);
    }

    @Override
    public void assertIsSelected() {
        this.uiElementAssertion.selected(true);
    }

    @Override
    public void assertIsNotSelected() {
        this.uiElementAssertion.selected(false);
    }

    @Override
    public void assertIsNotSelectable() {
        this.uiElementAssertion.selectable(false);
    }

    @Override
    public void assertIsSelectable() {
        this.uiElementAssertion.selectable(true);
    }

    @Override
    public void assertText(String text) {
        this.uiElementAssertion.text(text);
    }

    @Override
    public void assertTextContains(String... text) {
        this.uiElementAssertion.text().contains(text[0]).is(true);
    }

    @Override
    public void assertTextContainsNot(String... text) {
        this.uiElementAssertion.text().contains(text[0]).is(false);
    }

    @Override
    public void assertAttributeIsPresent(String attributeName) {
        this.uiElementAssertion.attribute(attributeName).isNot(null);
    }

    @Override
    public void assertAttributeValue(String attributeName, String value) {
        this.uiElementAssertion.attribute(attributeName).is(value);
    }

    @Override
    public void assertAttributeContains(String attributeName, String textContainedByAttribute) {
        this.uiElementAssertion.attribute(attributeName).contains(textContainedByAttribute).is(true);
    }

    @Override
    public void assertAttributeContainsNot(final String attributeName, final String textNotContainedByAttribute) {
        this.uiElementAssertion.attribute(attributeName).contains(textNotContainedByAttribute).is(false);
    }

    @Override
    public void assertAnyFollowingTextNodeContains(String contains) {
        TestableUiElement testableUiElement = this.guiElement.anyElementContainsText(contains);
        UiElementAssertion uiElementAssertion = this.createUiElementAssertion((UiElement) testableUiElement, this.useAssertion);
        uiElementAssertion.numberOfElements().isGreaterThan(0);
    }

    @Override
    public void assertIsEnabled() {
        this.uiElementAssertion.enabled(true);
    }

    @Override
    public void assertIsDisabled() {
        this.uiElementAssertion.enabled(false);
    }

    @Override
    public void assertInputFieldLength(int length) {
        pTInputFieldLength(length);
    }

    private void pTInputFieldLength(int length) {
        String t = "";
        if (length > 0) {
            for (int i = 0; i < length - 1; i++) {
                t += "T";
            }
        }
        this.guiElement.sendKeys(t).expect().value().length().isBetween(length-1, length+1);
    }

    @Override
    public void assertLayout(ILayout layout) {
        layout.checkOn(this.guiElement, this.useAssertion);
    }

    @Override
    public void assertCssClassIsPresent(final String className) {
        this.uiElementAssertion.attribute(Attribute.CLASS).containsWords(className).is(true);
    }

    @Override
    public void assertCssClassIsNotPresent(final String className) {
        this.uiElementAssertion.attribute(Attribute.CLASS).containsWords(className).is(false);
    }

    @Override
    public void assertScreenshot(final String targetImageName, final double confidenceThreshold) {
        this.uiElementAssertion.screenshot().pixelDistance(targetImageName).isLowerEqualThan(confidenceThreshold);
    }

    @Override
    public void assertVisible(boolean complete) {
        this.uiElementAssertion.visible(complete).is(true);
    }

    @Override
    public void assertNotVisible() {
        this.uiElementAssertion.visible(false).is(false);
    }
}
