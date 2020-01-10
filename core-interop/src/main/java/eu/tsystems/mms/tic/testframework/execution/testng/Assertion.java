package eu.tsystems.mms.tic.testframework.execution.testng;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public interface Assertion {
    class Message {
        public final String subject;
        public final String prefixMessage;
        public Message(String prefix, String subject) {
            this.prefixMessage = prefix;
            this.subject = subject;
        }
    }
    void fail(String message, Throwable realCause);
    void fail(String message);
    void fail(AssertionError error);
    String format(Object actual, Object expected, Object subject);
    default boolean assertTrue(boolean condition) {
        return assertTrue(condition, null);
    }
    boolean assertTrue(boolean condition, Object subject);
    default boolean assertFalse(boolean condition) {
        return assertFalse(condition, null);
    }
    boolean assertFalse(boolean condition, Object subject);
    boolean assertSame(Object actual, Object expected, Object subject);
    boolean assertNotSame(Object actual, Object expected, Object subject);
    boolean assertNull(Object object, Object subject);
    default boolean assertNotNull(Object object) {
        return assertNotNull(object, null);
    }
    boolean assertNotNull(Object object, Object subject);
    boolean assertContains(String actual, String expected, Object subject);
    boolean assertContainsNot(String actual, String expected, Object subject);
    boolean assertGreaterThan(BigDecimal actual, BigDecimal expected, Object subject);
    boolean assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, Object subject);
    boolean assertLowerThan(BigDecimal actual, BigDecimal expected, Object subject);
    boolean assertLowerEqualThan(BigDecimal actual, BigDecimal expected, Object subject);
    boolean assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, Object subject);
    default boolean assertEquals(Object actual, Object expected) {
        return assertEquals(actual, expected, null);
    }
    boolean assertEquals(Object actual, Object expected, Object subject);
    boolean assertEquals(Collection<?> actual, Collection<?> expected, Object subject);
    boolean assertEquals(Iterator<?> actual, Iterator<?> expected, Object subject);
    boolean assertEquals(Iterable<?> actual, Iterable<?> expected, Object subject);
    boolean assertEquals(Object[] actual, Object[] expected, Object subject);
    boolean assertEquals(Set<?> actual, Set<?> expected, Object subject);
    boolean assertEquals(Map<?, ?> actual, Map<?, ?> expected, Object subject);
    boolean assertEqualsDeep(Set<?> actual, Set<?> expected, Object subject);
    boolean assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected, Object subject);
    boolean assertEqualsNoOrder(Object[] actual, Object[] expected, Object subject);
    boolean assertNotEquals(Object actual1, Object actual2, Object subject);
    boolean assertNotEquals(Set<?> actual, Set<?> expected, Object subject);
    boolean assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, Object subject);
    boolean assertBeginsWith(Object actual, Object expected, Object subject);
    boolean assertEndsWith(Object actual, Object expected, Object subject);
}
