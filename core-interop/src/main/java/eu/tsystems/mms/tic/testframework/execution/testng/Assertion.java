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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface Assertion {
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
    void assertTrue(boolean actual, Object subject);
    default void assertTrue(boolean actual) {
        assertTrue(actual, null);
    }

    boolean isFalse(boolean actual);
    String formatExpectFalse(boolean actual, Object subject);
    void assertFalse(boolean actual, Object subject);
    default void assertFalse(boolean actual) {
        assertFalse(actual, null);
    }

    boolean isSame(Object actual, Object expected);
    String formatExpectSame(Object actual, Object expected, Object subject);
    void assertSame(Object actual, Object expected, Object subject);
    default void assertSame(Object actual, Object expected) {
        assertSame(actual, expected, null);
    }

    boolean isNotSame(Object actual, Object expected);
    String formatExpectNotSame(Object actual, Object expected, Object subject);
    void assertNotSame(Object actual, Object expected, Object subject);
    default void assertNotSame(Object actual, Object expected) {
        assertNotSame(actual, expected, null);
    }

    boolean isNull(Object actual);
    String formatExpectNull(Object actual, Object subject);
    void assertNull(Object actual, Object subject);
    default void assertNull(Object actual) {
        assertNull(actual, null);
    }

    boolean isNotNull(Object actual);
    String formatExpectNotNull(Object actual, Object subject);
    void assertNotNull(Object actual, Object subject);
    default void assertNotNull(Object actual) {
        assertNotNull(actual, null);
    }

    boolean contains(String actual, String expected);
    String formatExpectContains(String actual, String expected, Object subject);
    void assertContains(String actual, String expected, Object subject);
    default void assertContains(String actual, String expected) {
        assertContains(actual, expected, null);
    }

    boolean containsNot(String actual, String expected);
    String formatExpectContainsNot(String actual, String expected, Object subject);
    void assertContainsNot(String actual, String expected, Object subject);
    default void assertContainsNot(String actual, String expected) {
        assertContainsNot(actual, expected, null);
    }

    boolean isGreaterThan(BigDecimal actual, BigDecimal expected);
    String formatExpectGreaterThan(BigDecimal actual, BigDecimal expected, Object subject);
    void assertGreaterThan(BigDecimal actual, BigDecimal expected, Object subject);
    default void assertGreaterThan(BigDecimal actual, BigDecimal expected) {
        assertGreaterThan(actual, expected, null);
    }

    boolean isGreaterEqualThan(BigDecimal actual, BigDecimal expected);
    String formatExpectGreaterEqualThan(BigDecimal actual, BigDecimal expected, Object subject);
    void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, Object subject);
    default void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected) {
        assertGreaterEqualThan(actual, expected, null);
    }

    boolean isLowerThan(BigDecimal actual, BigDecimal expected);
    String formatExpectLowerThan(BigDecimal actual, BigDecimal expected, Object subject);
    void assertLowerThan(BigDecimal actual, BigDecimal expected, Object subject);
    default void assertLowerThan(BigDecimal actual, BigDecimal expected) {
        assertLowerThan(actual, expected, null);
    }

    boolean isLowerEqualThan(BigDecimal actual, BigDecimal expected);
    String formatExpectLowerEqualThan(BigDecimal actual, BigDecimal expected, Object subject);
    void assertLowerEqualThan(BigDecimal actual, BigDecimal expected, Object subject);
    default void assertLowerEqualThan(BigDecimal actual, BigDecimal expected) {
        assertLowerEqualThan(actual, expected, null);
    }

    boolean isBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher);
    String formatExpectIsBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, Object subject);
    void assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, Object subject);
    default void assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher) {
        assertBetween(actual, lower, higher, null);
    }

    boolean equals(Object actual, Object expected);
    String formatExpectEquals(Object actual, Object expected, Object subject);
    void assertEquals(Object actual, Object expected, Object subject);
    void assertEquals(Collection<?> actual, Collection<?> expected, Object subject);
    void assertEquals(Iterator<?> actual, Iterator<?> expected, Object subject);
    void assertEquals(Iterable<?> actual, Iterable<?> expected, Object subject);
    void assertEquals(Object[] actual, Object[] expected, Object subject);
    void assertEquals(Set<?> actual, Set<?> expected, Object subject);
    void assertEquals(Map<?, ?> actual, Map<?, ?> expected, Object subject);
    void assertEqualsDeep(Set<?> actual, Set<?> expected, Object subject);
    void assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected, Object subject);
    void assertEqualsNoOrder(Object[] actual, Object[] expected, Object subject);
    default void assertEquals(Object actual, Object expected) {
        assertEquals(actual, expected, null);
    }

    boolean notEquals(Object actual, Object expected);
    String formatExpectNotEquals(Object actual, Object expected, Object subject);
    void assertNotEquals(Object actual1, Object actual2, Object subject);
    void assertNotEquals(Set<?> actual, Set<?> expected, Object subject);
    void assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, Object subject);
    default void assertNotEquals(Object actual1, Object actual2) {
        assertNotEquals(actual1, actual2, null);
    }

    boolean startsWith(Object actual, Object expected);
    String formatExpectStartsWith(Object actual, Object expected, Object subject);
    void assertStartsWith(Object actual, Object expected, Object subject);
    default void assertStartsWith(Object actual, Object expected) {
        assertStartsWith(actual, expected, null);
    }

    boolean endsWith(Object actual, Object expected);
    String formatExpectEndsWith(Object actual, Object expected, Object subject);
    void assertEndsWith(Object actual, Object expected, Object subject);
    default void assertEndsWith(Object actual, Object expected) {
        assertEndsWith(actual, expected, null);
    }
}
