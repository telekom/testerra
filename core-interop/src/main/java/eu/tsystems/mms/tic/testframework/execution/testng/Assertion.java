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

package eu.tsystems.mms.tic.testframework.execution.testng;

import java.math.BigDecimal;

public interface Assertion extends SimpleAssertion {
    String format(Object actual, Object expected, Object subject);

    default void fail(String message, Throwable cause) {
        fail(new AssertionError(message, cause));
    }
    default void fail(String message) {
        fail(message, null);
    }
    void fail(AssertionError error);

    boolean isTrue(boolean actual);
    String formatExpectTrue(boolean actual, Object subject);

    boolean isFalse(boolean actual);
    String formatExpectFalse(boolean actual, Object subject);

    boolean isSame(Object actual, Object expected);
    String formatExpectSame(Object actual, Object expected, Object subject);

    boolean isNotSame(Object actual, Object expected);
    String formatExpectNotSame(Object actual, Object expected, Object subject);

    boolean isNull(Object actual);
    String formatExpectNull(Object actual, Object subject);

    boolean isNotNull(Object actual);
    String formatExpectNotNull(Object actual, Object subject);

    boolean contains(String actual, String expected);
    String formatExpectContains(String actual, String expected, Object subject);

    boolean containsNot(String actual, String expected);
    String formatExpectContainsNot(String actual, String expected, Object subject);

    boolean isGreaterThan(BigDecimal actual, BigDecimal expected);
    String formatExpectGreaterThan(BigDecimal actual, BigDecimal expected, Object subject);

    boolean isGreaterEqualThan(BigDecimal actual, BigDecimal expected);
    String formatExpectGreaterEqualThan(BigDecimal actual, BigDecimal expected, Object subject);

    boolean isLowerThan(BigDecimal actual, BigDecimal expected);
    String formatExpectLowerThan(BigDecimal actual, BigDecimal expected, Object subject);

    boolean isLowerEqualThan(BigDecimal actual, BigDecimal expected);
    String formatExpectLowerEqualThan(BigDecimal actual, BigDecimal expected, Object subject);

    boolean isBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher);
    String formatExpectIsBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, Object subject);

    boolean equals(Object actual, Object expected);
    String formatExpectEquals(Object actual, Object expected, Object subject);

    boolean notEquals(Object actual, Object expected);
    String formatExpectNotEquals(Object actual, Object expected, Object subject);

    boolean startsWith(Object actual, Object expected);
    String formatExpectStartsWith(Object actual, Object expected, Object subject);

    boolean endsWith(Object actual, Object expected);
    String formatExpectEndsWith(Object actual, Object expected, Object subject);
}
