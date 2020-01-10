package eu.tsystems.mms.tic.testframework.execution.testng;

import org.testng.Assert;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Non static wrapper for TestNG {@link Assert}
 * Allows injection of {@link Assertion} implementations.
 * @author Mike Reiche
 */
public abstract class AbstractAssertion implements Assertion {

    private void failNotEquals(Object actual, Object expected, Object message) {
        fail(format(actual, String.format("equals [%s]", expected), message));
    }

    private void failEquals(Object actual, Object expected, Object message) {
        fail(format(actual, String.format("equals not [%s]", expected), message));
    }

    @Override
    public String format(Object actual, Object expected, Object message) {
        StringBuilder builder = new StringBuilder();
        String subject = null;
        if (message instanceof Message) {
            builder.append(((Message) message).prefixMessage);
            builder.append(": ");
            subject = ((Message) message).subject;
        } else if (message != null) {
            subject = message.toString();
        }
        builder.append("Expected");
        if (subject != null && !subject.isEmpty()) {
            builder.append(" that {").append(subject).append("}");
            builder.append(" actual");
        }
        builder.append(" [").append(actual).append("]");
        builder.append(" ").append(expected);
        return builder.toString();
    }

    @Override
    public void fail(String message, Throwable cause) {
        fail(new AssertionError(message, cause));
    }

    @Override
    public void fail(String message) {
        fail(message, null);
    }

    @Override
    abstract public void fail(AssertionError error);

    @Override
    public boolean assertContains(String actual, String expected, Object message) {
        if (actual == null || !actual.contains(expected)) {
            fail(format(actual, String.format("contains [%s]", expected), message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertContainsNot(String actual, String expected, Object message) {
        if (actual != null && actual.contains(expected)) {
            fail(format(actual, String.format("contains not [%s]", expected), message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertGreaterThan(BigDecimal actual, BigDecimal expected, Object message) {
        if (actual.compareTo(expected)!=1) {
            fail(format(actual, String.format("is greater than [%s]", expected), message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, Object message) {
        if (actual.compareTo(expected) < 0) {
            fail(format(actual, String.format("is greater or equal than [%s]", expected), message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertLowerThan(BigDecimal actual, BigDecimal expected, Object message) {
        if (actual.compareTo(expected)!=-1) {
            fail(format(actual, String.format("is lower than [%s]", expected), message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertLowerEqualThan(BigDecimal actual, BigDecimal expected, Object message) {
        if (actual.compareTo(expected) > 0) {
            fail(format(actual, String.format("is lower or equal than [%s]", expected), message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, Object message) {
        if (actual.compareTo(lower) == -1 || actual.compareTo(higher) != -1) {
            fail(format(actual, String.format("is between [%s] and [%s]", lower, higher), message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertTrue(boolean condition, Object message) {
        if (!condition) {
            failNotEquals(condition, Boolean.TRUE, message);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertFalse(boolean condition, Object message) {
        if (condition) {
            failNotEquals(condition, Boolean.FALSE, message); // TESTNG-81
            return false;
        } else {
            return true;
        }
    }
    @Override
    public boolean assertSame(Object actual, Object expected, Object message) {
        if (expected == actual) {
            return true;
        }
        fail(format(actual, String.format("is the same like [%s]", expected), message));
        return false;
    }
    @Override
    public boolean assertNotSame(Object actual, Object expected, Object message) {
        if (expected == actual) {
            fail(format(actual, String.format("is not the same like [%s]", expected), message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertNull(Object actual, Object message) {
        if (actual != null) {
            fail(format(actual, "is null", message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertNotNull(Object actual, Object message) {
        if (actual == null) {
            fail(format(actual, "is not null", message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertEquals(Object actual, Object expected, Object message) {
        try {
            Assert.assertEquals(actual, expected);
            return true;
        } catch (AssertionError e) {
            failNotEquals(actual, expected, message);
            return false;
        }
    }

    @Override
    public boolean assertEquals(Collection<?> actual, Collection<?> expected, Object message) {
        try {
            Assert.assertEquals(actual, expected);
            return true;
        } catch (AssertionError e) {
            failNotEquals(actual, expected, message);
            return false;
        }
    }

    @Override
    public boolean assertEquals(Iterator<?> actual, Iterator<?> expected, Object message) {
        try {
            Assert.assertEquals(actual, expected);
            return true;
        } catch (AssertionError e) {
            failNotEquals(actual, expected, message);
            return false;
        }
    }

    @Override
    public boolean assertEquals(Iterable<?> actual, Iterable<?> expected, Object message) {
        try {
            Assert.assertEquals(actual, expected);
            return true;
        } catch (AssertionError e) {
            failNotEquals(actual, expected, message);
            return false;
        }
    }

    @Override
    public boolean assertEquals(Object[] actual, Object[] expected, Object message) {
        try {
            Assert.assertEquals(actual, expected);
            return true;
        } catch (AssertionError e) {
            failNotEquals(actual, e, message);
            return false;
        }
    }

    @Override
    public boolean assertEqualsNoOrder(Object[] actual, Object[] expected, Object message) {
        try {
            Assert.assertEqualsNoOrder(actual, expected);
            return true;
        } catch (AssertionError e) {
            failNotEquals(actual, e, message);
            return false;
        }
    }

    @Override
    public boolean assertEquals(Set<?> actual, Set<?> expected, Object message) {
        try {
            Assert.assertEquals(actual, expected);
            return true;
        } catch (AssertionError e) {
            failNotEquals(actual, expected, message);
            return false;
        }
    }

    @Override
    public boolean assertEqualsDeep(Set<?> actual, Set<?> expected, Object message) {
        try {
            Assert.assertEqualsDeep(actual, expected, null);
            return true;
        } catch (AssertionError e) {
            failNotEquals(actual, expected, message);
            return false;
        }
    }

    @Override
    public boolean assertEquals(Map<?, ?> actual, Map<?, ?> expected, Object message) {
        try {
            Assert.assertEquals(actual, expected, null);
            return true;
        } catch (AssertionError e) {
            failNotEquals(actual, expected, message);
            return false;
        }
    }

    @Override
    public boolean assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected, Object message) {
        try {
            Assert.assertEqualsDeep(actual, expected);
            return true;
        } catch (AssertionError e) {
            failNotEquals(actual, expected, message);
            return false;
        }
    }

    @Override
    public boolean assertNotEquals(Object actual1, Object actual2, Object message) {
        try {
            Assert.assertNotEquals(actual1, actual2);
            return true;
        } catch (AssertionError e) {
            failEquals(actual1, actual2, message);
            return false;
        }
    }

    @Override
    public boolean assertNotEquals(Set<?> actual, Set<?> expected, Object message) {
        try {
            Assert.assertNotEquals(actual, expected);
            return true;
        } catch (AssertionError e) {
            failEquals(actual, expected, message);
            return false;
        }
    }

    @Override
    public boolean assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, Object message) {
        try {
            Assert.assertNotEquals(actual, expected);
            return true;
        } catch (AssertionError e) {
            failEquals(actual, expected, message);
            return false;
        }
    }

    @Override
    public boolean assertBeginsWith(Object actual, Object expected, Object message) {
        if (
            actual != null
            && expected != null
            && !actual.toString().startsWith(expected.toString())
        ) {
            fail(format(actual, String.format("begins with [%s]", expected), message));
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean assertEndsWith(Object actual, Object expected, Object message) {
        if (
            actual != null
            && expected != null
            && !actual.toString().endsWith(expected.toString())
        ) {
            fail(format(actual, String.format("ends with [%s]", expected), message));
            return false;
        } else {
            return true;
        }
    }
}
