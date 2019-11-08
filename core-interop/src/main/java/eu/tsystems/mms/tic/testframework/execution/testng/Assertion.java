package eu.tsystems.mms.tic.testframework.execution.testng;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface Assertion {
    void fail(String message, Throwable realCause);
    void fail(String message);
    void fail(AssertionError error);
    String format(Object actual, Object expected, String subject);
    default boolean assertTrue(boolean condition) {
        return assertTrue(condition, null);
    }
    boolean assertTrue(boolean condition, String subject);
    default boolean assertFalse(boolean condition) {
        return assertFalse(condition, null);
    }
    boolean assertFalse(boolean condition, String subject);
    boolean assertSame(Object actual, Object expected, String subject);
    boolean assertNotSame(Object actual, Object expected, String subject);
    boolean assertNull(Object object, String subject);
    default boolean assertNotNull(Object object) {
        return assertNotNull(object, null);
    }
    boolean assertNotNull(Object object, String subject);
    boolean assertContains(String actual, String expected, String subject);
    boolean assertContainsNot(String actual, String expected, String subject);
    boolean assertGreaterThan(BigDecimal actual, BigDecimal expected, String subject);
    boolean assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, String subject);
    boolean assertLowerThan(BigDecimal actual, BigDecimal expected, String subject);
    boolean assertLowerEqualThan(BigDecimal actual, BigDecimal expected, String subject);
    boolean assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, String subject);
    default boolean assertEquals(Object actual, Object expected) {
        return assertEquals(actual, expected, null);
    }
    boolean assertEquals(Object actual, Object expected, String subject);
    boolean assertEquals(Collection<?> actual, Collection<?> expected, String subject);
    boolean assertEquals(Iterator<?> actual, Iterator<?> expected, String subject);
    boolean assertEquals(Iterable<?> actual, Iterable<?> expected, String subject);
    boolean assertEquals(Object[] actual, Object[] expected, String subject);
    boolean assertEquals(Set<?> actual, Set<?> expected, String subject);
    boolean assertEquals(Map<?, ?> actual, Map<?, ?> expected, String subject);
    boolean assertEqualsDeep(Set<?> actual, Set<?> expected, String subject);
    boolean assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected, String subject);
    boolean assertEqualsNoOrder(Object[] actual, Object[] expected, String subject);
    boolean assertNotEquals(Object actual1, Object actual2, String subject);
    boolean assertNotEquals(Set<?> actual, Set<?> expected, String subject);
    boolean assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, String subject);
    boolean assertBeginsWith(Object actual, Object expected, String subject);
    boolean assertEndsWith(Object actual, Object expected, String subject);
}
