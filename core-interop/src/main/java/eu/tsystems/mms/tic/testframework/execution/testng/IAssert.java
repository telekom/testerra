package eu.tsystems.mms.tic.testframework.execution.testng;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface IAssert {
    void fail(final String message, final Throwable realCause);
    void fail(final String message);
    void assertTrue(final boolean condition, final String message);
    void assertFalse(final boolean condition, final String message);
    void assertSame(final Object actual, final Object expected, final String message);
    void assertNotSame(Object actual, Object expected, String message);
    void assertNull(Object object, String message);
    void assertNotNull(Object object, String message);
    void assertEquals(Object actual, Object expected, String message);
    void assertEquals(Collection<?> actual, Collection<?> expected, String message);
    void assertEquals(Iterator<?> actual, Iterator<?> expected, String message);
    void assertEquals(Iterable<?> actual, Iterable<?> expected, String message);
    void assertEquals(Object[] actual, Object[] expected, String message);
    void assertEqualsNoOrder(Object[] actual, Object[] expected, String message);
    void assertEquals(Set<?> actual, Set<?> expected, String message);
    void assertEqualsDeep(Set<?> actual, Set<?> expected, String message);
    void assertEquals(Map<?, ?> actual, Map<?, ?> expected, String message);
    void assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected, String message);
    void assertNotEquals(Object actual1, Object actual2, String message);
    void assertNotEquals(Set<?> actual, Set<?> expected, String message);
    void assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, String message);
}
