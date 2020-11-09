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

/**
 * Provides common useful assertions
 * @author Mike Reiche
 */
public interface SimpleAssertion {
    default void fail(String message, Throwable cause) {
        fail(new AssertionError(message, cause));
    }
    default void fail(String message) {
        fail(message, null);
    }
    void fail(AssertionError error);

    default BigDecimal toBigDecimal(Object number) {
        return new BigDecimal(number.toString());
    }

    void assertTrue(boolean actual, Object subject);
    default void assertTrue(boolean actual) {
        assertTrue(actual, null);
    }

    void assertFalse(boolean actual, Object subject);
    default void assertFalse(boolean actual) {
        assertFalse(actual, null);
    }

    void assertSame(Object actual, Object expected, Object subject);
    default void assertSame(Object actual, Object expected) {
        assertSame(actual, expected, null);
    }

    void assertNotSame(Object actual, Object expected, Object subject);
    default void assertNotSame(Object actual, Object expected) {
        assertNotSame(actual, expected, null);
    }

    void assertNull(Object actual, Object subject);
    default void assertNull(Object actual) {
        assertNull(actual, null);
    }

    void assertNotNull(Object actual, Object subject);
    default void assertNotNull(Object actual) {
        assertNotNull(actual, null);
    }

    void assertContains(String actual, String expected, Object subject);
    default void assertContains(String actual, String expected) {
        assertContains(actual, expected, null);
    }

    void assertContainsNot(String actual, String expected, Object subject);
    default void assertContainsNot(String actual, String expected) {
        assertContainsNot(actual, expected, null);
    }

    void assertGreaterThan(BigDecimal actual, BigDecimal expected, Object subject);
    default void assertGreaterThan(BigDecimal actual, BigDecimal expected) {
        assertGreaterThan(actual, expected, null);
    }
    default void assertGreaterThan(Object actual, Object expected, Object subject) {
        assertGreaterThan(toBigDecimal(actual), toBigDecimal(expected), subject);
    }
    default void assertGreaterThan(Object actual, Object expected) {
        assertGreaterThan(actual, expected, null);
    }


    void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, Object subject);
    default void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected) {
        assertGreaterEqualThan(actual, expected, null);
    }
    default void assertGreaterEqualThan(Object actual, Object expected) {
        assertGreaterEqualThan(actual, expected, null);
    }
    default void assertGreaterEqualThan(Object actual, Object expected, Object subject) {
        assertGreaterEqualThan(toBigDecimal(actual), toBigDecimal(expected), subject);
    }

    void assertLowerThan(BigDecimal actual, BigDecimal expected, Object subject);
    default void assertLowerThan(BigDecimal actual, BigDecimal expected) {
        assertLowerThan(actual, expected, null);
    }
    default void assertLowerThan(Object actual, Object expected, Object subject) {
        assertLowerThan(toBigDecimal(actual), toBigDecimal(expected), subject);
    }
    default void assertLowerThan(Object actual, Object expected) {
        assertLowerThan(actual, expected, null);
    }

    void assertLowerEqualThan(BigDecimal actual, BigDecimal expected, Object subject);
    default void assertLowerEqualThan(BigDecimal actual, BigDecimal expected) {
        assertLowerEqualThan(actual, expected, null);
    }
    default void assertLowerEqualThan(Object actual, Object expected) {
        assertLowerEqualThan(actual, expected, null);
    }
    default void assertLowerEqualThan(Object actual, Object expected, Object subject) {
        assertLowerEqualThan(this.toBigDecimal(actual), this.toBigDecimal(expected), subject);
    }

    void assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, Object subject);
    default void assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher) {
        assertBetween(actual, lower, higher, null);
    }
    default void assertBetween(Object actual, Object lower, Object higher) {
        assertBetween(actual, lower, higher, null);
    }
    default void assertBetween(Object actual, Object lower, Object higher, Object subject) {
        assertBetween(toBigDecimal(actual), toBigDecimal(lower), toBigDecimal(higher), subject);
    }

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

    void assertNotEquals(Object actual1, Object actual2, Object subject);
    void assertNotEquals(Set<?> actual, Set<?> expected, Object subject);
    void assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, Object subject);
    default void assertNotEquals(Object actual1, Object actual2) {
        assertNotEquals(actual1, actual2, null);
    }

    void assertStartsWith(Object actual, Object expected, Object subject);
    default void assertStartsWith(Object actual, Object expected) {
        assertStartsWith(actual, expected, null);
    }

    void assertEndsWith(Object actual, Object expected, Object subject);
    default void assertEndsWith(Object actual, Object expected) {
        assertEndsWith(actual, expected, null);
    }
}
