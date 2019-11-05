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

import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IBinaryPropertyAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.IStringPropertyAssertion;

/**
 * An interface to allow basic GuiElement operations
 */
public interface BasicGuiElement {
    @Deprecated
    GuiElementAssert nonFunctionalAsserts();
    @Deprecated
    GuiElementAssert asserts();
    @Deprecated
    GuiElementAssert instantAsserts();

    IBinaryPropertyAssertion<Boolean> present();
    IBinaryPropertyAssertion<Boolean> displayed();
    IBinaryPropertyAssertion<Boolean> visible(boolean complete);
    IStringPropertyAssertion<String> tagName();

    /**
     * Takes a screenshot of the current element
     */
    IImageAssertion screenshot();

    /**
     * This method scrolls to the element with an given offset.
     */
    BasicGuiElement scrollTo(int yOffset);

    default BasicGuiElement scrollTo() {
        return scrollTo(0);
    }

    BasicGuiElement highlight();

    Locate getLocate();
}
