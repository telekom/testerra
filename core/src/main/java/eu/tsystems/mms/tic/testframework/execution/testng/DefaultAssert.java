package eu.tsystems.mms.tic.testframework.execution.testng;

import org.testng.Assert;
import org.testng.collections.Lists;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.testng.internal.EclipseInterface.ASSERT_LEFT;
import static org.testng.internal.EclipseInterface.ASSERT_LEFT2;
import static org.testng.internal.EclipseInterface.ASSERT_MIDDLE;
import static org.testng.internal.EclipseInterface.ASSERT_RIGHT;

public class DefaultAssert implements IAssert {
    public void failSame(Object actual, Object expected, String message) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + ASSERT_LEFT2 + expected + ASSERT_MIDDLE + actual + ASSERT_RIGHT);
    }

    public void failNotSame(Object actual, Object expected, String message) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + ASSERT_LEFT + expected + ASSERT_MIDDLE + actual + ASSERT_RIGHT);
    }

    public void failNotEquals(Object actual, Object expected, String message) {
        fail(format(actual, expected, message));
    }

    private void failAssertNoEqual(String defaultMessage, String message) {
        if (message != null) {
            fail(message);
        } else {
            fail(defaultMessage);
        }
    }

    protected String format(Object actual, Object expected, String message) {
        String formatted = "";
        if (null != message) {
            formatted = message + " ";
        }

        return formatted + ASSERT_LEFT + expected + ASSERT_MIDDLE + actual + ASSERT_RIGHT;
    }

    @Override
    public void fail(String message, Throwable realCause) {

    }

    @Override
    public void fail(String message) {
        fail(message, null);
    }

    @Override
    public void assertContains(String actual, String expected, String message) {
        if (!actual.contains(expected)) {
            fail(String.format("%s [%s] contains [%s]", message, actual, expected));
        }
    }

    @Override
    public void assertContainsNot(String actual, String expected, String message) {
        if (actual.contains(expected)) {
            fail(String.format("%s [%s] contains not [%s]", message, actual, expected));
        }
    }

    @Override
    public void assertGreaterThan(BigDecimal actual, BigDecimal expected) {
        assertGreaterThan(actual, expected, "expected");
    }

    @Override
    public void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected) {
        assertGreaterEqualThan(actual, expected, "expected");
    }

    @Override
    public void assertLowerThan(BigDecimal actual, BigDecimal expected) {
        assertLowerThan(actual, expected, "expected");
    }

    @Override
    public void assertLowerEqualThan(BigDecimal actual, BigDecimal expected) {
        assertLowerEqualThan(actual, expected, "expected");
    }

    @Override
    public void assertGreaterThan(BigDecimal actual, BigDecimal expected, String message) {
        if (actual.compareTo(expected)!=1) {
            fail(String.format("%s [%s] is greater than [%s]", message, actual, expected));
        }
    }

    @Override
    public void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, String message) {
        if (actual.compareTo(expected) < 0) {
            fail(String.format("%s [%s] is greater or equal than [%s]", message, actual, expected));
        }
    }

    @Override
    public void assertLowerThan(BigDecimal actual, BigDecimal expected, String message) {
        if (actual.compareTo(expected)!=-1) {
            fail(String.format("%s [%s] is lower than [%s]", message, actual, expected));
        }
    }

    @Override
    public void assertLowerEqualThan(BigDecimal actual, BigDecimal expected, String message) {
        if (actual.compareTo(expected) > 0) {
            fail(String.format("%s [%s] is lower or equal than [%s]", message, actual, expected));
        }
    }

    @Override
    public void assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, String message) {
        if (actual.compareTo(lower) > 0 || actual.compareTo(higher) < 0) {
            fail(String.format("%s [%s] is between [%s] and [%s]", message, actual, lower, higher));
        }
    }

    @Override
    public void assertTrue(boolean condition, String message) {
        if (!condition) {
            failNotEquals(condition, Boolean.TRUE, message);
        }
    }

    @Override
    public void assertFalse(boolean condition, String message) {
        if (condition) {
            failNotEquals(condition, Boolean.FALSE, message); // TESTNG-81
        }
    }
    @Override
    public void assertSame(Object actual, Object expected, String message) {
        if (expected == actual) {
            return;
        }
        failNotSame(actual, expected, message);
    }
    @Override
    public void assertNotSame(Object actual, Object expected, String message) {
        if (expected == actual) {
            failSame(actual, expected, message);
        }
    }

    @Override
    public void assertNull(Object object, String message) {
        if (object != null) {
            failNotSame(object, null, message);
        }
    }

    @Override
    public void assertNotNull(Object object, String message) {
        if (object == null) {
            String formatted = "";
            if (message != null) {
                formatted = message + " ";
            }
            fail(formatted + "expected object to not be null");
        }
    }

    @Override
    public void assertEquals(Object actual, Object expected, String message) {
        if (expected != null && expected.getClass().isArray()) {
            assertArrayEquals(actual, expected, message);
            return;
        }
        assertEqualsImpl(actual, expected, message);
    }

    @Override
    public void assertEquals(Collection<?> actual, Collection<?> expected, String message) {
        if (actual == expected) {
            return;
        }

        if (actual == null || expected == null) {
            if (message != null) {
                fail(message);
            } else {
                fail("Collections not equal: expected: " + expected + " and actual: " + actual);
            }
        }

        assertEquals(actual.size(), expected.size(), (message == null ? "" : message + ": ") + "lists don't have the same size");

        Iterator<?> actIt = actual.iterator();
        Iterator<?> expIt = expected.iterator();
        int i = -1;
        while (actIt.hasNext() && expIt.hasNext()) {
            i++;
            Object e = expIt.next();
            Object a = actIt.next();
            String explanation = "Lists differ at element [" + i + "]: " + e + " != " + a;
            String errorMessage = message == null ? explanation : message + ": " + explanation;

            assertEqualsImpl(a, e, errorMessage);
        }
    }

    @Override
    public void assertEquals(Iterator<?> actual, Iterator<?> expected, String message) {
        if (actual == expected) {
            return;
        }

        if (actual == null || expected == null) {
            if (message != null) {
                fail(message);
            } else {
                fail("Iterators not equal: expected: " + expected + " and actual: " + actual);
            }
        }

        int i = -1;
        while (actual.hasNext() && expected.hasNext()) {

            i++;
            Object e = expected.next();
            Object a = actual.next();
            String explanation = "Iterators differ at element [" + i + "]: " + e + " != " + a;
            String errorMessage = message == null ? explanation : message + ": " + explanation;

            assertEqualsImpl(a, e, errorMessage);

        }

        if (actual.hasNext()) {

            String explanation = "Actual iterator returned more elements than the expected iterator.";
            String errorMessage = message == null ? explanation : message + ": " + explanation;
            fail(errorMessage);

        } else if (expected.hasNext()) {

            String explanation = "Expected iterator returned more elements than the actual iterator.";
            String errorMessage = message == null ? explanation : message + ": " + explanation;
            fail(errorMessage);

        }
    }

    @Override
    public void assertEquals(Iterable<?> actual, Iterable<?> expected, String message) {
        if (actual == expected) {
            return;
        }

        if (actual == null || expected == null) {
            if (message != null) {
                fail(message);
            } else {
                fail("Iterables not equal: expected: " + expected + " and actual: " + actual);
            }
        }

        Iterator<?> actIt = actual.iterator();
        Iterator<?> expIt = expected.iterator();

        assertEquals(actIt, expIt, message);
    }

    @Override
    public void assertEquals(Object[] actual, Object[] expected, String message) {
        if (actual == expected) {
            return;
        }

        if ((actual == null && expected != null) || (actual != null && expected == null)) {
            if (message != null) {
                fail(message);
            } else {
                fail("Arrays not equal: " + Arrays.toString(expected) + " and " + Arrays.toString(actual));
            }
        }
        assertEquals(Arrays.asList(actual), Arrays.asList(expected), message);
    }

    @Override
    public void assertEqualsNoOrder(Object[] actual, Object[] expected, String message) {
        if (actual == expected) {
            return;
        }

        if ((actual == null && expected != null) || (actual != null && expected == null)) {
            failAssertNoEqual(
                "Arrays not equal: " + Arrays.toString(expected) + " and " + Arrays.toString(actual),
                message);
        }

        if (actual.length != expected.length) {
            failAssertNoEqual(
                "Arrays do not have the same size:" + actual.length + " != " + expected.length,
                message);
        }

        List<Object> actualCollection = Lists.newArrayList();
        for (Object a : actual) {
            actualCollection.add(a);
        }
        for (Object o : expected) {
            actualCollection.remove(o);
        }
        if (actualCollection.size() != 0) {
            failAssertNoEqual(
                "Arrays not equal: " + Arrays.toString(expected) + " and " + Arrays.toString(actual),
                message);
        }
    }

    @Override
    public void assertEquals(Set<?> actual, Set<?> expected, String message) {
        if (actual == expected) {
            return;
        }

        if (actual == null || expected == null) {
            // Keep the back compatible
            if (message == null) {
                fail("Sets not equal: expected: " + expected + " and actual: " + actual);
            } else {
                failNotEquals(actual, expected, message);
            }
        }

        if (!actual.equals(expected)) {
            if (message == null) {
                fail("Sets differ: expected " + expected + " but got " + actual);
            } else {
                failNotEquals(actual, expected, message);
            }
        }
    }

    @Override
    public void assertEqualsDeep(Set<?> actual, Set<?> expected, String message) {
        if (actual == expected) {
            return;
        }

        if (actual == null || expected == null) {
            // Keep the back compatible
            if (message == null) {
                fail("Sets not equal: expected: " + expected + " and actual: " + actual);
            } else {
                failNotEquals(actual, expected, message);
            }
        }

        Iterator<?> actualIterator = actual.iterator();
        Iterator<?> expectedIterator = expected.iterator();
        while (expectedIterator.hasNext()) {
            Object expectedValue = expectedIterator.next();
            if (!actualIterator.hasNext()) {
                fail("Sets not equal: expected: " + expected + " and actual: " + actual);
            }
            Object value = actualIterator.next();
            if (expectedValue.getClass().isArray()) {
                assertArrayEquals(value, expectedValue, message);
            } else {
                assertEqualsImpl(value, expectedValue, message);
            }
        }
    }

    @Override
    public void assertEquals(Map<?, ?> actual, Map<?, ?> expected, String message) {
        if (actual == expected) {
            return;
        }

        if (actual == null || expected == null) {
            fail("Maps not equal: expected: " + expected + " and actual: " + actual);
        }

        if (actual.size() != expected.size()) {
            fail("Maps do not have the same size:" + actual.size() + " != " + expected.size());
        }

        Set<?> entrySet = actual.entrySet();
        for (Object anEntrySet : entrySet) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) anEntrySet;
            Object key = entry.getKey();
            Object value = entry.getValue();
            Object expectedValue = expected.get(key);
            String assertMessage = message != null ? message : "Maps do not match for key:"
                + key + " actual:" + value + " expected:" + expectedValue;
            assertEqualsImpl(value, expectedValue, assertMessage);
        }
    }

    @Override
    public void assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected, String message) {
        if (actual == expected) {
            return;
        }

        if (actual == null || expected == null) {
            fail("Maps not equal: expected: " + expected + " and actual: " + actual);
        }

        if (actual.size() != expected.size()) {
            fail("Maps do not have the same size:" + actual.size() + " != " + expected.size());
        }

        Set<?> entrySet = actual.entrySet();
        for (Object anEntrySet : entrySet) {
            Map.Entry<?, ?> entry = (Map.Entry<?, ?>) anEntrySet;
            Object key = entry.getKey();
            Object value = entry.getValue();
            Object expectedValue = expected.get(key);
            String assertMessage = message != null ? message : "Maps do not match for key:"
                + key + " actual:" + value + " expected:" + expectedValue;
            if (expectedValue.getClass().isArray()) {
                assertArrayEquals(value, expectedValue, assertMessage);
            } else {
                assertEqualsImpl(value, expectedValue, assertMessage);
            }
        }
    }

    @Override
    public void assertNotEquals(Object actual1, Object actual2, String message) {
        boolean fail;
        try {
            Assert.assertEquals(actual1, actual2);
            fail = true;
        } catch (AssertionError e) {
            fail = false;
        }

        if (fail) {
            fail(message);
        }
    }

    @Override
    public void assertNotEquals(Set<?> actual, Set<?> expected, String message) {
        boolean fail;
        try {
            Assert.assertEquals(actual, expected, message);
            fail = true;
        } catch (AssertionError e) {
            fail = false;
        }

        if (fail) {
            fail(message);
        }
    }

    @Override
    public void assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, String message) {
        boolean fail;
        try {
            Assert.assertEquals(actual, expected, message);
            fail = true;
        } catch (AssertionError e) {
            fail = false;
        }

        if (fail) {
            fail(message);
        }
    }

    /**
     * Differs from {@link #assertEquals(Object, Object, String)} by not taking arrays into
     * special consideration hence comparing them by reference. Intended to be called directly
     * to test equality of collections content.
     */
    private void assertEqualsImpl(Object actual, Object expected, String message) {
        if ((expected == null) && (actual == null)) {
            return;
        }
        if (expected == null ^ actual == null) {
            failNotEquals(actual, expected, message);
        }
        if (expected.equals(actual) && actual.equals(expected)) {
            return;
        }
        failNotEquals(actual, expected, message);
    }

    private void assertArrayEquals(Object actual, Object expected, String message) {
        if (expected == actual) {
            return;
        }
        if (null == expected) {
            fail("expected a null array, but not null found. " + message);
        }
        if (null == actual) {
            fail("expected not null array, but null found. " + message);
        }
        //is called only when expected is an array
        if (actual.getClass().isArray()) {
            int expectedLength = Array.getLength(expected);
            if (expectedLength == Array.getLength(actual)) {
                for (int i = 0; i < expectedLength; i++) {
                    Object _actual = Array.get(actual, i);
                    Object _expected = Array.get(expected, i);
                    try {
                        assertEquals(_actual, _expected, message);
                    } catch (AssertionError ae) {
                        failNotEquals(actual, expected, message == null ? "" : message
                            + " (values at index " + i + " are not the same)");
                    }
                }
                //array values matched
                return;
            } else {
                failNotEquals(Array.getLength(actual), expectedLength, message == null ? "" : message
                    + " (Array lengths are not the same)");
            }
        }
        failNotEquals(actual, expected, message);
    }
}
