/*
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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.GuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.layout.Layout;
import org.testng.Assert;

public class ConfiguredAssert {

    private final boolean functionalAssertions;
    private final boolean collected;
    private final String prefix;

    public ConfiguredAssert(boolean functionalAssertions, boolean collected) {
        this.functionalAssertions = functionalAssertions;
        this.collected = collected;
        prefix = functionalAssertions ? "" : "Nonfunctional: ";
    }

    public void assertTrue(boolean value, String assertionMessage) {
        if (functionalAssertions) {
            if (collected) {
                AssertCollector.assertTrue(value, assertionMessage);
            }
            else {
                Assert.assertTrue(value, assertionMessage);
            }
        } else {
            NonFunctionalAssert.assertTrue(value, assertionMessage);
        }
    }

    public void assertFalse(boolean value, String assertionMessage) {
        if (functionalAssertions) {
            if (collected) {
                AssertCollector.assertFalse(value, assertionMessage);
            }
            else {
                Assert.assertFalse(value, assertionMessage);
            }
        } else {
            NonFunctionalAssert.assertFalse(value, assertionMessage);
        }
    }

    public void assertLayout(GuiElement guiElement, Layout layout) {
        layout.checkOn(guiElement, this);
    }
}
