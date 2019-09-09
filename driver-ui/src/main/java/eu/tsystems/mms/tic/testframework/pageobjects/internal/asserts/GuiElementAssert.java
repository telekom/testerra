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

import eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters.GuiElementWait;
import eu.tsystems.mms.tic.testframework.pageobjects.layout.Layout;

/**
 * User: rnhb
 * Date: 11.08.2015
 */
public interface GuiElementAssert {

    /**
     * Assert isPresent() = true.
     */
    void assertIsPresentFast();

    /**
     * Assert waitForIsPresent() = true.
     *
     * @see GuiElementWait#waitForIsPresent()
     */
    void assertIsPresent();

    /**
     * Assert waitForIsNotPresent() = true.
     *
     * @see GuiElementWait#waitForIsNotPresent()
     */
    void assertIsNotPresent();

    /**
     * Assert isPresent() = false.
     */
    void assertIsNotPresentFast();

    /**
     * Assert waitForIsSelected() = true.
     *
     * @see GuiElementWait#waitForIsSelected()
     */
    void assertIsSelected();

    /**
     * Assert waitForIsSelected() = false.
     *
     * @see GuiElementWait#waitForIsNotSelected()
     */
    void assertIsNotSelected();

    /**
     * Checks if GuiElement is not selectable.
     * WARNING: Can change the selection status of the element, if it is selectable.
     */
    void assertIsNotSelectable();

    /**
     * Assert is selectable
     */
    void assertIsSelectable();

    /**
     * Assert waitForIsDisplayed() = true.
     *
     * @see GuiElementWait#waitForIsDisplayed()
     */
    void assertIsDisplayed();

    /**
     * Assert waitForIsDisplayed() = false.
     *
     * @see GuiElementWait#waitForIsNotDisplayed()
     */
    void assertIsNotDisplayed();

    /**
     * Assert is displayed from webelement with wait
     */
    void assertIsDisplayedFromWebElement();

    /**
     * Assert is not displayed from webelement with wait
     */
    void assertIsNotDisplayedFromWebElement();

    /**
     * Checks if GuiElement contains the given text. Please note that this will only assert successfully, if the
     * elements text is actually visible (wait). The actual value will be trimmed.
     *
     * @param text The text to check. Will be trimmed.
     * @see GuiElementWait#waitForText(String)
     **/
    void assertText(String text);

    /**
     * Checks if the GuiElement, contains the given texts. Please note that this will only assert successfully, if the
     * elements text is actually visible.
     *
     * @param text Strings that should be contained in text. Will NOT be trimmed.
     */
    void assertContainsText(String... text);

    /**
     * Assert attribute is present.
     *
     * @param attributeName Will be trimmed.
     * @see GuiElementWait#waitForAttribute(String)
     */
    void assertAttributeIsPresent(String attributeName);

    /**
     * Checks if the GuiElement contains matches a given text in an attribute. The actual value will be trimmed.
     *
     * @param attributeName Attribute whose value is checked. Will be trimmed.
     * @param value         Text that should be matched. Will be trimmed.
     * @see GuiElementWait#waitForAttribute(String, String)
     */
    void assertAttributeValue(String attributeName, String value);

    /**
     * Checks if the GuiElement, contains the given text in an attribute.
     *
     * @param attributeName        Attribute whose value is checked. Will be trimmed.
     * @param textContainedByAttribute Text that should be contained. Will NOT be trimmed.
     * @see GuiElementWait#waitForAttributeContains(String, String)
     */
    void assertAttributeContains(String attributeName, String textContainedByAttribute);

    /**
     * Checks, if the GuiElement contains the Text in one of the minor TextNodes.
     *
     * @param contains Text that should be contained. Will NOT be trimmed.
     */
    void assertAnyFollowingTextNodeContains(String contains);

    /**
     * Checks for (wait) enabled state.
     *
     * @see GuiElementWait#waitForIsEnabled()
     */
    void assertIsEnabled();

    /**
     * Checks for (wait) disabled state.
     *
     * @see GuiElementWait#waitForIsDisabled()
     */
    void assertIsDisabled();

    /**
     * Functions checks whether given input-length is valid
     *
     * @param length InputFieldsLength that is expected.
     */
    void assertInputFieldLength(int length);

    /**
     * Checks two guielements layouts against each other.
     *
     * @param layout Layout description.
     */
    void assertLayout(Layout layout);

    /**
     * Asserts the pixel of this web element
     */
    void assertPixelDistanceGreaterEqualThan(final String targetImageName, final double expectedDistance);

}
