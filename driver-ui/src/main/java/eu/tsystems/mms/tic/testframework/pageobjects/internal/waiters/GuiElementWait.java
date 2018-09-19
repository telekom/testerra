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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters;

/**
 * Created by rnhb on 11.08.2015.
 */
public interface GuiElementWait {

    /**
     * Executes find() with default-timeout.
     *
     * @return true if present, false if not. No Exception is thrown.
     */
    boolean waitForIsPresent();

    /**
     * Waits for an element to be not present.
     *
     * @return this.
     */
    boolean waitForIsNotPresent();

    /**
     * Wait until GuiElement is "enabled".
     *
     * @return true if enabled, false if not. No Exception is thrown.
     */
    boolean waitForIsEnabled();

    /**
     * Wait until GuiElement is "disabled".
     *
     * @return true if disabled, false if not. No Exception is thrown.
     */
    boolean waitForIsDisabled();

    /**
     * Waits for any following text node of the current element to contain a text.
     *
     * @param contains Text.
     * @return true if contained, false if not. No Exception is thrown.
     */
    boolean waitForAnyFollowingTextNodeContains(String contains);

    /**
     * Wait for isDisplayed() to be true
     *
     * @return true is displayed, false if not. No Exception is thrown.
     */
    boolean waitForIsDisplayed();

    /**
     * Wait till this element disappears.
     *
     * @return this.
     */
    boolean waitForIsNotDisplayed();

    /**
     * Wait for isDisplayed() to be true
     *
     * @return true is displayed, false if not. No Exception is thrown.
     */
    boolean waitForIsDisplayedFromWebElement();

    /**
     * Wait till this element disappears.
     *
     * @return this.
     */
    boolean waitForIsNotDisplayedFromWebElement();

    /**
     * Wait till the element is selected.
     *
     * @return selection status
     */
    boolean waitForIsSelected();

    /**
     * Wait till the element is not selected.
     *
     * @return selection status
     */
    boolean waitForIsNotSelected();

    /**
     * Waiting until GuiElement equals the given text. Please note that this will only wait successfully, if the
     * elements text is actually visible at some point. Actual value will be trimmed.
     *
     * @param text Text to wait for, will be trimmed.
     * @return true is present, false if not. No Exception is thrown.
     */
    boolean waitForText(String text);

    /**
     * Waiting until GuiElement contains the given text. Please note that this will only wait successfully, if the
     * elements text is actually visible at some point.
     *
     * @param text Text to wait for.
     * @return true is present, false if not. No Exception is thrown.
     */
    boolean waitForTextContains(String... text);


    /**
     * Wait for an attribute to be present.
     *
     * @param attributeName .
     */
    boolean waitForAttribute(String attributeName);

    /**
     * Wait for an attribute to have the given value. Actual value will be trimmed.
     *
     * @param attributeName .
     * @param value         Will be trimmed.
     **/
    boolean waitForAttribute(String attributeName, String value);

    /**
     * Wait for a an attribute to have the given value.
     *
     * @param attributeName attribute to check
     * @param value         value which is waited for
     * @return true if the attribute eventually has the value
     */
    boolean waitForAttributeContains(String attributeName, String value);

    /**
     * Waits until the element is selectable or times out.
     *
     * @return .
     */
    boolean waitForIsSelectable();

    /**
     * Waits until the element is not selectable or times out.
     *
     * @return .
     */
    boolean waitForIsNotSelectable();
}

