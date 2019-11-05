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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.core;

import eu.tsystems.mms.tic.testframework.pageobjects.internal.BasicGuiElement;

/**
 * @deprecated This interface should be replaced by {@link BasicGuiElement}
 */
@Deprecated
public interface GuiElementStatusCheck {
    /**
     * Checks if an element is found by webdriver.
     *
     * @return true if found, false otherwise.
     */
    boolean isPresent();

    /**
     * WebElement.isEnabled.
     *
     * @return true if element is enabled.
     */
    boolean isEnabled();

    /**
     * Returns true if any following text node of current element contains a text.
     *
     * @param contains Text.
     * @return True or false.
     */
    boolean anyFollowingTextNodeContains(String contains);

    /**
     * WebElement.isDisplayed.
     *
     * @return true if element is displayed.
     */
    boolean isDisplayed();

    /**
     * Checks if the element is visible in the current viewport
     * @return
     */
    boolean isVisible(final boolean complete);

    /**
     * WebElement.isSelected.
     *
     * @return true if element is selected.
     */
    boolean isSelected();

    /**
     * Calls WebElement.getText. Please note that this will only return a String, if the elements text is actually
     * visible.
     *
     * @return text of the element.
     */
    String getText();

    /**
     * WebElement.getAttribute.
     *
     * @param attributeName Name of the attribute.
     * @return The value of the attribute.
     */
    String getAttribute(String attributeName);

    /**
     * Calls isDisplayed on the underlying WebElement.
     *
     * @return isDisplayed from WebElement
     */
    boolean isDisplayedFromWebElement();

    /**
     * Checks if the element is selectable.
     *
     * @return ture, if the element is selectable
     */
    boolean isSelectable();
}
