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
 *     Peter Lehmann
 *     pele
 */
package eu.tsystems.mms.tic.testframework.pageobjects.internal;

import eu.tsystems.mms.tic.testframework.pageobjects.IGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.Locate;
import eu.tsystems.mms.tic.testframework.pageobjects.TestableGuiElement;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.GuiElementAssert;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.RectAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.ImageAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts.StringAssertion;
import org.openqa.selenium.WebElement;

import java.util.Iterator;

/**
 * An interface to allow basic GuiElement operations
 */
public interface BasicGuiElement<SELF> extends
    Iterable<SELF>,
    Iterator<SELF>
{
    @Deprecated
    GuiElementAssert nonFunctionalAsserts();
    @Deprecated
    GuiElementAssert asserts();
    @Deprecated
    GuiElementAssert instantAsserts();

    BinaryAssertion<Boolean> present();
    BinaryAssertion<Boolean> displayed();
    BinaryAssertion<Boolean> visible(boolean complete);
    StringAssertion<String> tagName();
    RectAssertion bounds();
    TestableGuiElement waitFor();
    QuantityAssertion<Integer> numberOfElements();

    /**
     * Takes a screenshot of the current element
     */
    ImageAssertion screenshot();

    /**
     * This method scrolls to the element with an given offset.
     */
    BasicGuiElement scrollTo(int yOffset);

    default BasicGuiElement scrollTo() {
        return scrollTo(0);
    }

    BasicGuiElement highlight();

    Locate getLocate();

    WebElement getWebElement();

    SELF element(int position);
    SELF firstElement();
    SELF lastElement();
}
