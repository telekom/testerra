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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.ConfiguredAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementCore;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.layout.ILayout;

import java.util.Arrays;

/**
 * Created by rnhb on 11.08.2015.
 */
public class ConfigurableGuiElementAssert implements GuiElementAssert {

    private final GuiElementWait guiElementWait;
    private final GuiElementCore guiElementCore;
    private final ConfiguredAssert configuredAssert;
    private final GuiElementData guiElementData;

    public ConfigurableGuiElementAssert(
        GuiElementCore guiElementCore,
        GuiElementWait guiElementWait,
        ConfiguredAssert configuredAssert,
        GuiElementData guiElementData
    ) {
        this.guiElementWait = guiElementWait;
        this.guiElementCore = guiElementCore;
        this.configuredAssert = configuredAssert;
        this.guiElementData = guiElementData;
    }

    @Override
    public void assertIsPresent() {
        configuredAssert.assertTrue(guiElementWait.waitForIsPresent(), guiElementData + " is present.");
    }

    @Override
    public void assertIsNotPresent() {
        configuredAssert.assertFalse(!guiElementWait.waitForIsNotPresent(), guiElementData + " is present.");
    }

    @Override
    public void assertIsPresentFast() {
        configuredAssert.assertTrue(guiElementCore.isPresent(), guiElementData + " is present.");
    }

    @Override
    public void assertIsNotPresentFast() {
        configuredAssert.assertFalse(guiElementCore.isPresent(), guiElementData + " is present.");
    }

    @Override
    public void assertIsDisplayed() {
        configuredAssert.assertTrue(guiElementWait.waitForIsDisplayed(), guiElementData + " is displayed.");
    }

    @Override
    public void assertIsNotDisplayed() {
        configuredAssert.assertFalse(!guiElementWait.waitForIsNotDisplayed(), guiElementData + " is displayed.");
    }

    @Override
    public void assertIsDisplayedFromWebElement() {
        configuredAssert.assertTrue(guiElementWait.waitForIsDisplayedFromWebElement(), guiElementData + " is displayed.");
    }

    @Override
    public void assertIsNotDisplayedFromWebElement() {
        configuredAssert.assertFalse(!guiElementWait.waitForIsNotDisplayedFromWebElement(), guiElementData + " is displayed.");
    }

    @Override
    public void assertIsSelected() {
        configuredAssert.assertTrue(guiElementWait.waitForIsSelected(), guiElementData + " is selected.");
    }

    @Override
    public void assertIsNotSelected() {
        configuredAssert.assertFalse(!guiElementWait.waitForIsNotSelected(), guiElementData + " is selected.");
    }

    @Override
    public void assertIsNotSelectable() {
        configuredAssert.assertFalse(!guiElementWait.waitForIsNotSelectable(), guiElementData
                + ": selection status was changed by trying to select or deselect it.");
    }

    @Override
    public void assertIsSelectable() {
        configuredAssert.assertTrue(guiElementWait.waitForIsSelectable(), guiElementData
                + ": selection status was changed by trying to select or deselect it.");
    }

    @Override
    public void assertText(String text) {
        configuredAssert.assertTrue(guiElementWait.waitForText(text),
                guiElementData + " text equals the requested text\n Expected: " + text + "\n Actual: " + getTextOrEmpty());
    }

    private String getTextOrEmpty() {
        String text = guiElementCore.getText();
        return text == null ? "" : text.trim();
    }

    @Override
    public void assertContainsText(String... text) {
        assertTextContains(text);
    }

    @Override
    public void assertTextContains(String... text) {
        configuredAssert.assertTrue(guiElementWait.waitForTextContains(text), guiElementData + " text does not contain the requested text\n " +
            "Expected: " + Arrays.toString(text) + "\n Actual: [" + guiElementCore.getText() + "]");
    }

    @Override
    public void assertTextContainsNot(String... text) {
        configuredAssert.assertTrue(guiElementWait.waitForTextContainsNot(text), guiElementData + " text does contain the requested text\n " +
            "Expected: " + Arrays.toString(text) + "\n Actual: [" + guiElementCore.getText() + "]");
    }

    @Override
    public void assertAttributeIsPresent(String attributeName) {
        configuredAssert.assertTrue(guiElementWait.waitForAttribute(attributeName), "Attribute is present: " + attributeName);
    }

    @Override
    public void assertAttributeValue(String attributeName, String value) {
        configuredAssert.assertTrue(guiElementWait.waitForAttribute(attributeName, value),
                guiElementData + " does not equal the requested text\n Expected: " + value + "\n Actual: "
                        + getAttributeOrEmpty(attributeName));
    }

    private String getAttributeOrEmpty(String attributeName) {
        String attribute = guiElementCore.getAttribute(attributeName);
        return attribute == null ? "" : attribute.trim();
    }

    @Override
    public void assertAttributeContains(String attributeName, String textContainedByAttribute) {
        configuredAssert.assertTrue(
            guiElementWait.waitForAttributeContains(attributeName, textContainedByAttribute),
            String.format("%s does not contain the requested text\n Expected: %s\n Actual: %s", guiElementData, textContainedByAttribute, guiElementCore.getAttribute(attributeName))
        );
    }

    @Override
    public void assertAttributeContainsNot(final String attributeName, final String textNotContainedByAttribute) {
        configuredAssert.assertTrue(
            guiElementWait.waitForAttributeContainsNot(attributeName, textNotContainedByAttribute),
            String.format("%s does not contain the requested text\n Expected: %s\n Actual: %s", guiElementData, textNotContainedByAttribute, guiElementCore.getAttribute(attributeName))
        );
    }

    @Override
    public void assertAnyFollowingTextNodeContains(String contains) {
        configuredAssert.assertTrue(guiElementWait.waitForAnyFollowingTextNodeContains(contains), guiElementData +
                "contains text \"" + contains + "\".");
    }

    @Override
    public void assertIsEnabled() {
        configuredAssert.assertTrue(guiElementWait.waitForIsEnabled(), guiElementData + " is enabled.");
    }

    @Override
    public void assertIsDisabled() {
        configuredAssert.assertTrue(guiElementWait.waitForIsDisabled(), guiElementData + " is disabled.");
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
            // Check 1. length-1
            int lengthOfValueAfterSendKeys = guiElementCore.getLengthOfValueAfterSendKeys(t);
            configuredAssert.assertTrue(lengthOfValueAfterSendKeys == length - 1, "InputFieldLength -1 is accepted");
        }

        // Check 2. length
        t += "T";
        int lengthOfValueAfterSendKeys = guiElementCore.getLengthOfValueAfterSendKeys(t);
        configuredAssert.assertTrue(lengthOfValueAfterSendKeys == length, "InputFieldLength +-0 is accepted");

        // Check 3. length+1
        t += "T";
        lengthOfValueAfterSendKeys = guiElementCore.getLengthOfValueAfterSendKeys(t);
        configuredAssert.assertFalse(lengthOfValueAfterSendKeys == length + 1, "InputFieldLength +1 is accepted");

        // Check 4. length+1 cut
        configuredAssert.assertTrue(lengthOfValueAfterSendKeys == length, "InputFieldLength +1 is cut");
    }

    @Override
    public void assertLayout(ILayout layout) {
        configuredAssert.assertLayout(guiElementData.guiElement, layout);
    }

    @Override
    public void assertCssClassIsPresent(final String className) {
        configuredAssert.assertTrue(guiElementWait.waitForCssClassIsPresent(className), String.format("%s has css class '%s'", guiElementData, className));
    }

    @Override
    public void assertCssClassIsNotPresent(final String className) {
        configuredAssert.assertTrue(guiElementWait.waitForCssClassIsNotPresent(className), String.format("%s has not css class '%s'", guiElementData, className));
    }

    @Override
    public void assertScreenshot(final String targetImageName, final double confidenceThreshold) {
        guiElementData.guiElement.screenshot().pixelDistance(targetImageName).lowerEqualThan(confidenceThreshold);
    }

    @Override
    public void assertVisible(boolean complete) {
        configuredAssert.assertTrue(guiElementWait.waitForIsVisible(complete), guiElementData + " is "+(complete?"complete ":"")+"visible");
    }

    @Override
    public void assertNotVisible() {
        configuredAssert.assertTrue(guiElementWait.waitForIsNotVisible(), guiElementData + " is not visible");
    }
}
