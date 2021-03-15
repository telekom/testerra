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
 package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.exceptions.UiElementAssertionError;
import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.core.GuiElementData;
import eu.tsystems.mms.tic.testframework.pageobjects.layout.Layout;
import org.testng.Assert;

public class ConfiguredAssert {

    private final boolean functionalAssertions;
    private final boolean collected;
    private final String prefix;
    private final GuiElementData guiElement;

    public ConfiguredAssert(boolean functionalAssertions, boolean collected, GuiElementData data) {
        this.functionalAssertions = functionalAssertions;
        this.collected = collected;
        this.guiElement = data;
        prefix = functionalAssertions ? "" : "Nonfunctional: ";
    }

    public void assertTrue(boolean value, String assertionMessage) {
        UiElementAssertionError uiElementAssertionError = null;
        try {
            Assert.assertTrue(value, assertionMessage);
            return;
        } catch (AssertionError assertionError) {
            uiElementAssertionError = new UiElementAssertionError(this.guiElement, assertionError);
        }
        if (functionalAssertions) {
            if (collected) {
                AssertCollector.fail(uiElementAssertionError);
            }
            else {
                throw uiElementAssertionError;
            }
        } else {
            NonFunctionalAssert.fail(uiElementAssertionError);
        }
    }

    public void assertFalse(boolean value, String assertionMessage) {
        UiElementAssertionError uiElementAssertionError = null;
        try {
            Assert.assertFalse(value, assertionMessage);
            return;
        } catch (AssertionError assertionError) {
            uiElementAssertionError = new UiElementAssertionError(this.guiElement, assertionError);
        }
        if (functionalAssertions) {
            if (collected) {
                AssertCollector.fail(uiElementAssertionError);
            }
            else {
                throw uiElementAssertionError;
            }
        } else {
            NonFunctionalAssert.fail(uiElementAssertionError);
        }
    }

    public void assertLayout(GuiElement guiElement, Layout layout) {
        layout.checkOn(guiElement, this);
    }
}
