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
package eu.tsystems.mms.tic.testframework.pageobjects.internal.waiters;

import eu.tsystems.mms.tic.testframework.logging.Loggable;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;

@Deprecated
public class DefaultGuiElementWait implements GuiElementWait, Loggable {

    private final GuiElement uiElement;

    public DefaultGuiElementWait(GuiElement uiElement) {
        this.uiElement = uiElement;
    }

    @Override
    public boolean waitForIsPresent() {
        return waitForPresentStatus(true);
    }

    @Override
    public boolean waitForIsNotPresent() {
        return waitForPresentStatus(false);
    }

    private boolean waitForPresentStatus(final boolean checkForPresent) {
        return uiElement.waitFor().present(checkForPresent);
    }

    @Override
    public boolean waitForIsEnabled() {
        return pWaitForEnableDisableStatus(true);
    }

    @Override
    public boolean waitForIsDisabled() {
        return pWaitForEnableDisableStatus(false);
    }

    private boolean pWaitForEnableDisableStatus(final boolean checkForEnabled) {
        return uiElement.waitFor().enabled(checkForEnabled);
    }

    @Override
    public boolean waitForAnyFollowingTextNodeContains(final String contains) {
        return uiElement.anyElementContainsText(contains).waitFor().present(true);
    }

    @Override
    public boolean waitForIsDisplayed() {
        return pWaitForDisplayedStatus(true);
    }

    @Override
    public boolean waitForIsNotDisplayed() {
        return pWaitForDisplayedStatus(false);
    }

    private boolean pWaitForDisplayedStatus(final boolean checkForDisplayed) {
        return this.uiElement.waitFor().displayed(checkForDisplayed);
    }

    @Override
    public boolean waitForIsVisible(boolean complete) {
        return pWaitForVisibleStatus(true, complete);
    }

    @Override
    public boolean waitForIsNotVisible() {
        return pWaitForVisibleStatus(false, false);
    }

    private boolean pWaitForVisibleStatus(final boolean visible, final boolean complete) {
        return uiElement.waitFor().visible(complete).is(visible);
    }

    @Override
    public boolean waitForIsSelected() {
        return waitForSelectionStatus(true);
    }

    @Override
    public boolean waitForIsNotSelected() {
        return waitForSelectionStatus(false);
    }

    private boolean waitForSelectionStatus(final boolean checkForSelected) {
        return this.uiElement.waitFor().selected(checkForSelected);
    }

    @Override
    public boolean waitForText(String text) {
        return pWaitForText(text);
    }

    private boolean pWaitForText(String text) {
        return this.uiElement.waitFor().text().map(String::trim).is(text);
    }

    @Override
    public boolean waitForTextContains(String... text) {
        return pWaitForTextContains(text);
    }

    private boolean pWaitForTextContains(final String... texts) {
        return this.uiElement.waitFor().text().containsWords(texts).is(true);
    }

    @Override
    public boolean waitForTextContainsNot(String... texts) {
        return this.uiElement.waitFor().text().containsWords(texts).is(false);
    }

    @Override
    public boolean waitForAttribute(final String attributeName) {
        return this.uiElement.waitFor().value(attributeName).is(true);
    }

    @Override
    public boolean waitForAttribute(final String attributeName, final String value) {
        return this.uiElement.waitFor().value(attributeName).map(String::trim).is(value);
    }

    @Override
    public boolean waitForAttributeContains(final String attributeName, final String value) {
        return this.uiElement.waitFor().value(attributeName).contains(value);
    }

    @Override
    public boolean waitForAttributeContainsNot(final String attributeName, final String value) {
        return this.uiElement.waitFor().value(attributeName).containsNot(value);
    }

    @Override
    public boolean waitForCssClassIsPresent(final String className) {
        return waitForAttributeContains("class", className);
    }

    @Override
    public boolean waitForCssClassIsNotPresent(final String className) {
        return waitForAttributeContainsNot("class", className);
    }

    @Override
    public boolean waitForIsSelectable() {
        return this.uiElement.waitFor().selected(true);
    }

    @Override
    public boolean waitForIsNotSelectable() {
        return this.uiElement.waitFor().selected(false);
    }
}
