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
package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.execution.testng.AssertCollector;
import eu.tsystems.mms.tic.testframework.execution.testng.NonFunctionalAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IConfiguredAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.facade.GuiElementFacade;
import eu.tsystems.mms.tic.testframework.pageobjects.layout.ILayout;
import org.testng.Assert;

/**
 * Created by rnhb on 13.11.2015.
 */
public class ConfiguredAssert implements IConfiguredAssert {

    private final boolean functionalAssertions;
    private final boolean collected;

    public ConfiguredAssert(boolean functionalAssertions, boolean collected) {
        this.functionalAssertions = functionalAssertions;
        this.collected = collected;
    }

    @Override
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

    @Override
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

    @Override
    public void assertLayout(GuiElementFacade guiElement, ILayout layout) {
        layout.checkOn(guiElement, this);
    }
}
