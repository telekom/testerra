package eu.tsystems.mms.tic.testframework.execution.testng;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface IAssertion {
    void fail(final String message, final Throwable realCause);
    void fail(final String message);
    void assertTrue(final boolean condition, final String subject);
    void assertFalse(final boolean condition, final String subject);
    void assertSame(final Object actual, final Object expected, final String subject);
    void assertNotSame(Object actual, Object expected, String subject);
    void assertNull(Object object, String subject);
    void assertNotNull(Object object, String subject);
    void assertContains(String actual, String expected, String subject);
    void assertContainsNot(String actual, String expected, String subject);
    void assertGreaterThan(BigDecimal actual, BigDecimal expected, String subject);
    void assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, String subject);
    void assertLowerThan(BigDecimal actual, BigDecimal expected, String subject);
    void assertLowerEqualThan(BigDecimal actual, BigDecimal expected, String subject);
    void assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, String subject);
    void assertEquals(Object actual, Object expected, String subject);
    void assertEquals(Collection<?> actual, Collection<?> expected, String subject);
    void assertEquals(Iterator<?> actual, Iterator<?> expected, String subject);
    void assertEquals(Iterable<?> actual, Iterable<?> expected, String subject);
    void assertEquals(Object[] actual, Object[] expected, String subject);
    void assertEqualsNoOrder(Object[] actual, Object[] expected, String subject);
    void assertEquals(Set<?> actual, Set<?> expected, String subject);
    void assertEqualsDeep(Set<?> actual, Set<?> expected, String subject);
    void assertEquals(Map<?, ?> actual, Map<?, ?> expected, String subject);
    void assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected, String subject);
    void assertNotEquals(Object actual1, Object actual2, String subject);
    void assertNotEquals(Set<?> actual, Set<?> expected, String subject);
    void assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, String subject);
}
