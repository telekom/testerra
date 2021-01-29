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

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.layout.ILayout;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class GuiElementAssertDecorator implements GuiElementAssert, Loggable {
    private final GuiElementAssert decoratedAssert;

    public GuiElementAssertDecorator(GuiElementAssert decoratedAssert) {
        this.decoratedAssert = decoratedAssert;
    }

    abstract void beforeAssertion();

    /**
     * @param message
     * @param assertionErrorOrNull
     * @return Optional new assertion
     */
    abstract AssertionError afterAssertion(String message, AssertionError assertionErrorOrNull);

    private void callBeforeAssertion() {
        try {
            beforeAssertion();
        } catch (Exception e) {
            // Do not change catch to Throwable! Instead, think about getting narrower by catching only RuntimeExceptions.
            log().warn("Exception thrown on beforeAssertion in AssertDecorator.", e);
        }
    }

    private void handleAfterAssertion(String message, AssertionError assertionErrorOrNull) {
        try {
            AssertionError newAssertionError = afterAssertion(message, assertionErrorOrNull);
            if (newAssertionError != null) {
                assertionErrorOrNull = newAssertionError;
            }
        } catch (Exception e) {
            // Do not change catch to Throwable! Instead, think about getting narrower by catching only RuntimeExceptions.
            log().warn("Exception thrown on afterAssertion in AssertDecorator.", e);
        }
        if (assertionErrorOrNull != null) {
            throw assertionErrorOrNull;
        }
    }

    @Override
    public void assertIsNotPresent() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertIsNotPresent();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsNotPresent", thrownAssertionError);
    }

    @Override
    public void assertIsNotPresentFast() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertIsNotPresentFast();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsNotPresent", thrownAssertionError);
    }

    @Override
    public void assertIsSelected() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertIsSelected();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsSelected", thrownAssertionError);
    }

    @Override
    public void assertIsNotSelected() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertIsNotSelected();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsNotSelected", thrownAssertionError);
    }

    @Override
    public void assertIsNotSelectable() {
        callBeforeAssertion();
        AssertionError thrownAssertionErrorOrNull = null;
        try {
            decoratedAssert.assertIsNotSelectable();
        } catch (AssertionError e) {
            thrownAssertionErrorOrNull = e;
        }
        handleAfterAssertion("assertIsNotSelectable", thrownAssertionErrorOrNull);
    }

    @Override
    public void assertIsSelectable() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertIsSelectable();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsSelectable", thrownAssertionError);
    }

    @Override
    public void assertIsNotDisplayed() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertIsNotDisplayed();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsNotDisplayed", thrownAssertionError);
    }

    @Override
    public void assertText(String text) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertText(text);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertText \"" + text + "\"", thrownAssertionError);
    }

    @Override
    public void assertTextContains(String... text) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertTextContains(text);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertTextContains \"" + Arrays.toString(text) + "\"", thrownAssertionError);
    }

    @Override
    public void assertTextContainsNot(String... text) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertTextContainsNot(text);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertTextContainsNot \"" + Arrays.toString(text) + "\"", thrownAssertionError);
    }

    @Override
    public void assertAttributeIsPresent(String attributeName) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertAttributeIsPresent(attributeName);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertAttributeIsPresent \"" + attributeName + "\"", thrownAssertionError);
    }

    @Override
    public void assertAttributeValue(String attributeName, String value) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertAttributeValue(attributeName, value);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertAttributeValue \"" + attributeName + "\" = \"" + value + "\"", thrownAssertionError);
    }

    @Override
    public void assertAttributeContains(String attributeName, String textContainedByAttribute) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertAttributeContains(attributeName, textContainedByAttribute);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertAttributeContains \"" + attributeName + "\" =~ \"" + textContainedByAttribute + "\"", thrownAssertionError);
    }

    @Override
    public void assertAttributeContainsNot(final String attributeName, final String textNotContainedByAttribute) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertAttributeContainsNot(attributeName, textNotContainedByAttribute);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertAttributeContainsNot \"" + attributeName + "\" =~ \"" + textNotContainedByAttribute + "\"", thrownAssertionError);
    }

    @Override
    public void assertAnyFollowingTextNodeContains(String contains) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertAnyFollowingTextNodeContains(contains);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertAnyFollowingTextNodeContains \"" + contains + "\"", thrownAssertionError);
    }

    @Override
    public void assertIsEnabled() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertIsEnabled();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsEnabled", thrownAssertionError);
    }

    @Override
    public void assertIsDisabled() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertIsDisabled();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsDisabled", thrownAssertionError);
    }

    @Override
    public void assertInputFieldLength(int length) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertInputFieldLength(length);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertInputFieldLength = " + length, thrownAssertionError);
    }

    @Override
    public void assertLayout(ILayout layout) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertLayout(layout);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertLayout " + layout, thrownAssertionError);
    }

    @Override
    public void assertCssClassIsPresent(final String className) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertCssClassIsPresent(className);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertCssClassIsPresent " + className, thrownAssertionError);
    }

    @Override
    public void assertCssClassIsNotPresent(String className) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertCssClassIsNotPresent(className);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertCssClassIsNotPresent " + className, thrownAssertionError);
    }

    @Override
    public void assertIsPresent() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertIsPresent();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsPresent", thrownAssertionError);
    }

    @Override
    public void assertIsPresentFast() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertIsPresentFast();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsPresent", thrownAssertionError);
    }

    @Override
    public void assertIsDisplayed() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        log().debug("Executing assertIsDisplayed");
        try {
            decoratedAssert.assertIsDisplayed();
        } catch (AssertionError e) {
            log().debug("assertIsDisplayed threw an assertion error, executing catch-action");
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsDisplayed", thrownAssertionError);
    }

    @Override
    public void assertIsDisplayedFromWebElement() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertIsDisplayedFromWebElement();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertIsDisplayedFromWebElement", thrownAssertionError);
    }

    @Override
    public void assertScreenshot(final String targetImageName, final double confidenceThreshold) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertScreenshot(targetImageName, confidenceThreshold);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertScreenshot", thrownAssertionError);
    }

    @Override
    public void assertVisible(boolean complete) {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertVisible(complete);
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertVisible", thrownAssertionError);
    }

    @Override
    public void assertNotVisible() {
        callBeforeAssertion();
        AssertionError thrownAssertionError = null;
        try {
            decoratedAssert.assertNotVisible();
        } catch (AssertionError e) {
            thrownAssertionError = e;
        }
        handleAfterAssertion("assertNotVisible", thrownAssertionError);
    }
}
