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

import eu.tsystems.mms.tic.testframework.internal.asserts.BinaryAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.QuantityAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.ScreenshotAssertion;
import eu.tsystems.mms.tic.testframework.internal.asserts.StringAssertion;

/**
 * Contains basic GuiElement features which every GuiElement needs to have.
 *
 * @author Mike Reiche
 */
public interface UiElementBaseAssertion extends ScreenshotAssertion {
    QuantityAssertion<Integer> foundElements();

    BinaryAssertion<Boolean> present();

    default boolean present(boolean expected) {
        return present().is(expected);
    }

    BinaryAssertion<Boolean> displayed();

    default boolean displayed(boolean expected) {
        return displayed().is(expected);
    }

    /**
     * @Deprecated please use {@link #visiblePartial(boolean)}, {@link #visibleFull(boolean)} instead
     */
    @Deprecated
    BinaryAssertion<Boolean> visible(boolean fullyVisible);

    BinaryAssertion<Boolean> visiblePartial();

    default boolean visiblePartial(boolean expected) {
        return visiblePartial().is(expected);
    }

    BinaryAssertion<Boolean> visibleFull();

    default boolean visibleFull(boolean expected) {
        return visibleFull().is(expected);
    }

    StringAssertion tagName();

    RectAssertion bounds();
}
