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

package eu.tsystems.mms.tic.testframework.internal.asserts;

import java.util.regex.Pattern;

/**
 * Allows string based assertions
 * @author Mike Reiche
 */
public interface StringAssertion<T> extends QuantityAssertion<T> {
    default boolean contains(String expected) {
        return contains(expected, null);
    }
    boolean contains(String expected, String subject);

    default boolean containsNot(String expected) {
        return containsNot(expected, null);
    }
    boolean containsNot(String expected, String subject);

    default boolean startsWith(String expected) {
        return startsWith(expected, null);
    }
    boolean startsWith(String expected, String subject);

    default boolean endsWith(String expected) {
        return endsWith(expected, null);
    }
    boolean endsWith(String expected, String subject);

    default PatternAssertion matches(String pattern) {
        return matches(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE));
    }
    PatternAssertion matches(Pattern pattern);

    BinaryAssertion <Boolean> containsWords(String...words);

    QuantityAssertion<Integer> length();
}
