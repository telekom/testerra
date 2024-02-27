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
import eu.tsystems.mms.tic.testframework.internal.asserts.StringAssertion;
import eu.tsystems.mms.tic.testframework.pageobjects.Attribute;
import eu.tsystems.mms.tic.testframework.pageobjects.UiElement;
import java.util.List;

/**
 * Contains all assertions on an {@link UiElement}
 * @author Mike Reiche
 */
public interface UiElementAssertion extends UiElementBaseAssertion {
    default boolean text(Object text) {
        return text().is(text);
    }
    StringAssertion text();

    default boolean value(Object text) {
        return value().is(text);
    }
    default StringAssertion value() {
        return attribute(Attribute.VALUE);
    }

    default StringAssertion attribute(Attribute attribute) {
        return attribute(attribute.toString());
    }
    default boolean attribute(Attribute attribute, Object expected) {
        return attribute(attribute.toString(), expected);
    }
    default boolean attribute(String attribute, Object expected) {
        return this.attribute(attribute).is(expected);
    }
    StringAssertion attribute(String attribute);

    StringAssertion css(String property);

    BinaryAssertion<Boolean> enabled();
    default boolean enabled(boolean expected) {
        return enabled().is(expected);
    }

    BinaryAssertion<Boolean> selected();
    default boolean selected(boolean expected) {
        return selected().is(expected);
    }

    BinaryAssertion<Boolean> selectable();
    default boolean selectable(boolean expected) {
        return selectable().is(expected);
    }

    default StringAssertion classes() {
        return this.attribute(Attribute.CLASS);
    }

    /**
     * @deprecated Use {@link #hasClasses(String...)} instead
     */
    default BinaryAssertion<Boolean> classes(String ... classes) {
        return classes().hasWords(classes);
    }

    /**
     * @deprecated Use {@link #hasClasses(List)} instead
     */
    default BinaryAssertion<Boolean> classes(List<String> classes) {
        return classes().hasWords(classes);
    }

    default BinaryAssertion<Boolean> hasClasses(String ... classes) {
        return classes().hasWords(classes);
    }

    default BinaryAssertion<Boolean> hasClasses(List<String> classes) {
        return classes().hasWords(classes);
    }
}
