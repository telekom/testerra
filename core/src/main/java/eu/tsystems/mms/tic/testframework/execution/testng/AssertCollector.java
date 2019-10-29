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
package eu.tsystems.mms.tic.testframework.execution.testng;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;
import org.testng.Assert;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.testng.internal.EclipseInterface.ASSERT_LEFT;
import static org.testng.internal.EclipseInterface.ASSERT_MIDDLE;
import static org.testng.internal.EclipseInterface.ASSERT_RIGHT;

/**
 * A dummy class for collecting asserts.
 */
@Deprecated
public class AssertCollector {

    protected static IAssertion assertion;

    /**
     * Protect constructor since it is a static only class
     */
    protected AssertCollector() {
        // hide constructor
    }

    static {
        assertion = TesterraCommons.ioc().getInstance(CollectedAssertion.class);
    }

    /**
     * Asserts that a condition is true. If it isn't,
     * an AssertionError, with the given message, is thrown.
     *
     * @param condition the condition to evaluate
     * @param message   the assertion error message
     */
    static public void assertTrue(boolean condition, String message) {
        assertion.assertTrue(condition,null);
    }

    /**
     * Asserts that a condition is true. If it isn't,
     * an AssertionError is thrown.
     *
     * @param condition the condition to evaluate
     */
    static public void assertTrue(boolean condition) {
        assertion.assertTrue(condition,null);
    }

    /**
     * Asserts that a condition is false. If it isn't,
     * an AssertionError, with the given message, is thrown.
     *
     * @param condition the condition to evaluate
     * @param message   the assertion error message
     */
    static public void assertFalse(boolean condition, String message) {
        assertion.assertFalse(condition, message);
    }

    /**
     * Asserts that a condition is false. If it isn't,
     * an AssertionError is thrown.
     *
     * @param condition the condition to evaluate
     */
    static public void assertFalse(boolean condition) {
        assertion.assertFalse(condition,null);
    }

    /**
     * Fails a test with the given message and wrapping the original exception.
     *
     * @param message   the assertion error message
     * @param realCause the original exception
     */
    static public void fail(String message, Throwable realCause) {
        assertion.fail(message, realCause);
    }

    /**
     * Fails a test with the given message.
     *
     * @param message the assertion error message
     */
    static public void fail(String message) {
        assertion.fail(message);
    }

    /**
     * Fails a test with no message.
     */
    static public void fail() {
        fail(null);
    }

    /**
     * Asserts that two objects are equal. If they are not,
     * an AssertionError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    public static void assertEquals(Object actual, Object expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two objects are equal. If they are not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    public static void assertEquals(Object actual, Object expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that two Strings are equal. If they are not,
     * an AssertionError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEquals(String actual, String expected, String message) {
        assertEquals((Object) actual, (Object) expected, message);
    }

    /**
     * Asserts that two Strings are equal. If they are not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEquals(String actual, String expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that two doubles are equal concerning a delta.  If they are not,
     * an AssertionError, with the given message, is thrown.  If the expected
     * value is infinity then the delta value is ignored.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param delta    the absolute tolerable difference between the actual and expected values
     * @param message  the assertion error message
     */
    static public void assertEquals(double actual, double expected, double delta, String message) {
        // handle infinity specially since subtracting to infinite values gives NaN and the
        // the following test fails
        if (Double.isInfinite(expected)) {
            if (!(expected == actual)) {
                failNotEquals(actual, expected, message);
            }
        } else if (Double.isNaN(expected)) {
            if (!Double.isNaN(actual)) {
                failNotEquals(actual, expected, message);
            }
        } else if (!(Math.abs(expected - actual) <= delta)) {
            failNotEquals(actual, expected, message);
        }
    }

    /**
     * Asserts that two doubles are equal concerning a delta. If they are not,
     * an AssertionError is thrown. If the expected value is infinity then the
     * delta value is ignored.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param delta    the absolute tolerable difference between the actual and expected values
     */
    static public void assertEquals(double actual, double expected, double delta) {
        assertEquals(actual, expected, delta, null);
    }

    /**
     * Asserts that two floats are equal concerning a delta. If they are not,
     * an AssertionError, with the given message, is thrown.  If the expected
     * value is infinity then the delta value is ignored.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param delta    the absolute tolerable difference between the actual and expected values
     * @param message  the assertion error message
     */
    static public void assertEquals(float actual, float expected, float delta, String message) {
        // handle infinity specially since subtracting to infinite values gives NaN and the
        // the following test fails
        if (Float.isInfinite(expected)) {
            if (!(expected == actual)) {
                failNotEquals(actual, expected, message);
            }
        } else if (!(Math.abs(expected - actual) <= delta)) {
            failNotEquals(actual, expected, message);
        }
    }

    /**
     * Asserts that two floats are equal concerning a delta. If they are not,
     * an AssertionError is thrown. If the expected
     * value is infinity then the delta value is ignored.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param delta    the absolute tolerable difference between the actual and expected values
     */
    static public void assertEquals(float actual, float expected, float delta) {
        assertEquals(actual, expected, delta, null);
    }

    /**
     * Asserts that two longs are equal. If they are not,
     * an AssertionError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEquals(long actual, long expected, String message) {
        assertEquals(Long.valueOf(actual), Long.valueOf(expected), message);
    }

    /**
     * Asserts that two longs are equal. If they are not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEquals(long actual, long expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that two booleans are equal. If they are not,
     * an AssertionError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEquals(boolean actual, boolean expected, String message) {
        assertEquals(Boolean.valueOf(actual), Boolean.valueOf(expected), message);
    }

    /**
     * Asserts that two booleans are equal. If they are not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEquals(boolean actual, boolean expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that two bytes are equal. If they are not,
     * an AssertionError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEquals(byte actual, byte expected, String message) {
        assertEquals(Byte.valueOf(actual), Byte.valueOf(expected), message);
    }

    /**
     * Asserts that two bytes are equal. If they are not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEquals(byte actual, byte expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that two chars are equal. If they are not,
     * an AssertionFailedError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEquals(char actual, char expected, String message) {
        assertEquals(Character.valueOf(actual), Character.valueOf(expected), message);
    }

    /**
     * Asserts that two chars are equal. If they are not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEquals(char actual, char expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that two shorts are equal. If they are not,
     * an AssertionFailedError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEquals(short actual, short expected, String message) {
        assertEquals(Short.valueOf(actual), Short.valueOf(expected), message);
    }

    /**
     * Asserts that two shorts are equal. If they are not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEquals(short actual, short expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that two ints are equal. If they are not,
     * an AssertionFailedError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEquals(int actual, int expected, String message) {
        assertEquals(Integer.valueOf(actual), Integer.valueOf(expected), message);
    }

    /**
     * Asserts that two ints are equal. If they are not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEquals(int actual, int expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that an object isn't null. If it is,
     * an AssertionError is thrown.
     *
     * @param object the assertion object
     */
    static public void assertNotNull(Object object) {
        assertNotNull(object, null);
    }

    /**
     * Asserts that an object isn't null. If it is,
     * an AssertionFailedError, with the given message, is thrown.
     *
     * @param object  the assertion object
     * @param message the assertion error message
     */
    static public void assertNotNull(Object object, String message) {
        assertion.assertNotNull(object, message);
    }

    /**
     * Asserts that an object is null. If it is not,
     * an AssertionError, with the given message, is thrown.
     *
     * @param object the assertion object
     */
    static public void assertNull(Object object) {
        assertNull(object, null);
    }

    /**
     * Asserts that an object is null. If it is not,
     * an AssertionFailedError, with the given message, is thrown.
     *
     * @param object  the assertion object
     * @param message the assertion error message
     */
    static public void assertNull(Object object, String message) {
        assertion.assertNull(object, message);
    }

    /**
     * Asserts that two objects refer to the same object. If they do not,
     * an AssertionFailedError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertSame(Object actual, Object expected, String message) {
        assertion.assertSame(actual, expected, message);
    }

    /**
     * Asserts that two objects refer to the same object. If they do not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertSame(Object actual, Object expected) {
        assertSame(actual, expected, null);
    }

    /**
     * Asserts that two objects do not refer to the same objects. If they do,
     * an AssertionError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertNotSame(Object actual, Object expected, String message) {
        assertion.assertNotSame(actual, expected, message);
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertNotSame(Object actual, Object expected) {
        assertNotSame(actual, expected, null);
    }

    static private void failNotEquals(Object actual, Object expected, String message) {
        fail(format(actual, expected, message));
    }

    static String format(Object actual, Object expected, String message) {
        String formatted = "";
        if (null != message) {
            formatted = message + " ";
        }

        return formatted + ASSERT_LEFT + expected + ASSERT_MIDDLE + actual + ASSERT_RIGHT;
    }

    /**
     * Asserts that two collections contain the same elements in the same order. If they do not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEquals(Collection<?> actual, Collection<?> expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that two collections contain the same elements in the same order. If they do not,
     * an AssertionError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEquals(Collection<?> actual, Collection<?> expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two iterators return the same elements in the same order. If they do not,
     * an AssertionError is thrown.
     * Please note that this assert iterates over the elements and modifies the state of the iterators.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEquals(Iterator<?> actual, Iterator<?> expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that two iterators return the same elements in the same order. If they do not,
     * an AssertionError, with the given message, is thrown.
     * Please note that this assert iterates over the elements and modifies the state of the iterators.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEquals(Iterator<?> actual, Iterator<?> expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two iterables return iterators with the same elements in the same order. If they do not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEquals(Iterable<?> actual, Iterable<?> expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that two iterables return iterators with the same elements in the same order. If they do not,
     * an AssertionError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEquals(Iterable<?> actual, Iterable<?> expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }


    /**
     * Asserts that two arrays contain the same elements in the same order. If they do not,
     * an AssertionError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEquals(Object[] actual, Object[] expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }

    /**
     * Asserts that two arrays contain the same elements in no particular order. If they do not,
     * an AssertionError, with the given message, is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     * @param message  the assertion error message
     */
    static public void assertEqualsNoOrder(Object[] actual, Object[] expected, String message) {
        assertion.assertEqualsNoOrder(actual, expected, message);
    }

    /**
     * Asserts that two arrays contain the same elements in the same order. If they do not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEquals(Object[] actual, Object[] expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Asserts that two arrays contain the same elements in no particular order. If they do not,
     * an AssertionError is thrown.
     *
     * @param actual   the actual value
     * @param expected the expected value
     */
    static public void assertEqualsNoOrder(Object[] actual, Object[] expected) {
        assertEqualsNoOrder(actual, expected, null);
    }

    /**
     * Asserts that two sets are equal.
     */
    public static void assertEquals(Set<?> actual, Set<?> expected) {
        assertEquals(actual, expected, null);
    }

    /**
     * Assert set equals
     */
    public static void assertEquals(Set<?> actual, Set<?> expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }

    public static void assertEqualsDeep(Set<?> actual, Set<?> expected, String message) {
        assertion.assertEqualsDeep(actual, expected, message);
    }

    /**
     * Asserts that two maps are equal.
     */
    public static void assertEquals(Map<?, ?> actual, Map<?, ?> expected, String message) {
        assertion.assertEquals(actual, expected, message);
    }

    public static void assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected) {
        assertEqualsDeep(actual, expected, null);
    }


    public static void assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected, String message) {
        assertion.assertEqualsDeep(actual, expected, message);
    }

    /////
    // assertNotEquals
    //

    public static void assertNotEquals(Object actual1, Object actual2, String message) {
        assertion.assertNotEquals(actual1, actual2, message);
    }

    public static void assertNotEquals(Object actual1, Object actual2) {
        assertNotEquals(actual1, actual2, null);
    }

    static void assertNotEquals(String actual1, String actual2, String message) {
        assertNotEquals((Object) actual1, (Object) actual2, message);
    }

    static void assertNotEquals(String actual1, String actual2) {
        assertNotEquals(actual1, actual2, null);
    }

    static void assertNotEquals(long actual1, long actual2, String message) {
        assertNotEquals(Long.valueOf(actual1), Long.valueOf(actual2), message);
    }

    static void assertNotEquals(long actual1, long actual2) {
        assertNotEquals(actual1, actual2, null);
    }

    static void assertNotEquals(boolean actual1, boolean actual2, String message) {
        assertNotEquals(Boolean.valueOf(actual1), Boolean.valueOf(actual2), message);
    }

    static void assertNotEquals(boolean actual1, boolean actual2) {
        assertNotEquals(actual1, actual2, null);
    }

    static void assertNotEquals(byte actual1, byte actual2, String message) {
        assertNotEquals(Byte.valueOf(actual1), Byte.valueOf(actual2), message);
    }

    static void assertNotEquals(byte actual1, byte actual2) {
        assertNotEquals(actual1, actual2, null);
    }

    static void assertNotEquals(char actual1, char actual2, String message) {
        assertNotEquals(Character.valueOf(actual1), Character.valueOf(actual2), message);
    }

    static void assertNotEquals(char actual1, char actual2) {
        assertNotEquals(actual1, actual2, null);
    }

    static void assertNotEquals(short actual1, short actual2, String message) {
        assertNotEquals(Short.valueOf(actual1), Short.valueOf(actual2), message);
    }

    static void assertNotEquals(short actual1, short actual2) {
        assertNotEquals(actual1, actual2, null);
    }

    static void assertNotEquals(int actual1, int actual2, String message) {
        assertNotEquals(Integer.valueOf(actual1), Integer.valueOf(actual2), message);
    }

    static void assertNotEquals(int actual1, int actual2) {
        assertNotEquals(actual1, actual2, null);
    }

    static public void assertNotEquals(float actual1, float actual2, float delta, String message) {
        boolean fail;
        try {
            Assert.assertEquals(actual1, actual2, delta, message);
            fail = true;
        } catch (AssertionError e) {
            fail = false;
        }

        if (fail) {
            Assert.fail(message);
        }
    }

    static public void assertNotEquals(float actual1, float actual2, float delta) {
        assertNotEquals(actual1, actual2, delta, null);
    }

    static public void assertNotEquals(double actual1, double actual2, double delta, String message) {
        boolean fail;
        try {
            Assert.assertEquals(actual1, actual2, delta, message);
            fail = true;
        } catch (AssertionError e) {
            fail = false;
        }

        if (fail) {
            Assert.fail(message);
        }
    }

    public static void assertNotEquals(Set<?> actual, Set<?> expected) {
        assertNotEquals(actual, expected, null);
    }

    public static void assertNotEquals(Set<?> actual, Set<?> expected, String message) {
        assertion.assertNotEquals(actual, expected, message);
    }

    public static void assertNotEquals(Map<?, ?> actual, Map<?, ?> expected) {
        assertNotEquals(actual, expected, null);
    }

    public static void assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, String message) {
        assertion.assertNotEquals(actual, expected, message);
    }

    static public void assertNotEquals(double actual1, double actual2, double delta) {
        assertNotEquals(actual1, actual2, delta, null);
    }

    /**
     * Asserts that {@code runnable} throws an exception when invoked. If it does not, an
     * {@link AssertionError} is thrown.
     *
     * @param runnable A function that is expected to throw an exception when invoked
     * @since 6.9.5
     */
    public static void assertThrows(Assert.ThrowingRunnable runnable) {
        assertThrows(Throwable.class, runnable);
    }

    /**
     * Asserts that {@code runnable} throws an exception of type {@code throwableClass} when
     * executed. If it does not throw an exception, an {@link AssertionError} is thrown. If it
     * throws the wrong type of exception, an {@code AssertionError} is thrown describing the
     * mismatch; the exception that was actually thrown can be obtained by calling {@link
     * AssertionError#getCause}.
     *
     * @param throwableClass the expected type of the exception
     * @param runnable       A function that is expected to throw an exception when invoked
     * @since 6.9.5
     */
    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public static <T extends Throwable> void assertThrows(Class<T> throwableClass, Assert.ThrowingRunnable runnable) {
        expectThrows(throwableClass, runnable);
    }

    /**
     * Asserts that {@code runnable} throws an exception of type {@code throwableClass} when
     * executed and returns the exception. If {@code runnable} does not throw an exception, an
     * {@link AssertionError} is thrown. If it throws the wrong type of exception, an {@code
     * AssertionError} is thrown describing the mismatch; the exception that was actually thrown can
     * be obtained by calling {@link AssertionError#getCause}.
     *
     * @param throwableClass the expected type of the exception
     * @param runnable       A function that is expected to throw an exception when invoked
     * @return The exception thrown by {@code runnable}
     * @since 6.9.5
     */
    public static <T extends Throwable> T expectThrows(Class<T> throwableClass, Assert.ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (Throwable t) {
            if (throwableClass.isInstance(t)) {
                return throwableClass.cast(t);
            } else {
                String mismatchMessage = String.format("Expected %s to be thrown, but %s was thrown",
                        throwableClass.getSimpleName(), t.getClass().getSimpleName());

                throw new AssertionError(mismatchMessage, t);
            }
        }
        String message = String.format("Expected %s to be thrown, but nothing was thrown",
                throwableClass.getSimpleName());
        throw new AssertionError(message);
    }

    /**
     * This interface facilitates the use of {@link #expectThrows} from Java 8. It allows
     * method references to both void and non-void methods to be passed directly into
     * expectThrows without wrapping, even if they declare checked exceptions.
     * <p/>
     * This interface is not meant to be implemented directly.
     */
    public interface ThrowingRunnable {
        void run() throws Throwable;
    }
}
