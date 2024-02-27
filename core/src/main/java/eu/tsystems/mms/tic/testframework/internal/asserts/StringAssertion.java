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

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Allows string based assertions
 * @author Mike Reiche
 */
public interface StringAssertion extends ObjectAssertion<String> {
    BinaryAssertion <Boolean> contains(String expected);
    BinaryAssertion <Boolean> startsWith(String expected);
    BinaryAssertion <Boolean> endsWith(String expected);

    default PatternAssertion matches(String pattern) {
        return matches(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE|Pattern.MULTILINE));
    }
    PatternAssertion matches(Pattern pattern);

    default BinaryAssertion <Boolean> hasWords(String...words) {
        return hasWords(Arrays.stream(words).collect(Collectors.toList()));
    }
    BinaryAssertion <Boolean> hasWords(List<String> words);

    QuantityAssertion<Integer> length();

    default boolean isContaining(String text) {
        return contains(text).is(true);
    }

    default boolean isNotContaining(String text) {
        return contains(text).is(false);
    }

    StringAssertion map(Function<String, String> mapFunction);
}
