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
    String format(Object actual, Object expected, Object message);
    default boolean assertTrue(boolean condition) {
        return assertTrue(condition, null);
    }
    boolean assertTrue(boolean condition, Object message);
    default boolean assertFalse(boolean condition) {
        return assertFalse(condition, null);
    }
    boolean assertFalse(boolean condition, Object message);
    boolean assertSame(Object actual, Object expected, Object message);
    boolean assertNotSame(Object actual, Object expected, Object message);
    boolean assertNull(Object object, Object message);
    default boolean assertNotNull(Object object) {
        return assertNotNull(object, null);
    }
    boolean assertNotNull(Object object, Object message);
    boolean assertContains(String actual, String expected, Object message);
    boolean assertContainsNot(String actual, String expected, Object message);
    boolean assertGreaterThan(BigDecimal actual, BigDecimal expected, Object message);
    boolean assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, Object message);
    boolean assertLowerThan(BigDecimal actual, BigDecimal expected, Object message);
    boolean assertLowerEqualThan(BigDecimal actual, BigDecimal expected, Object message);
    boolean assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher, Object message);
    default boolean assertEquals(Object actual, Object expected) {
        return assertEquals(actual, expected, null);
    }
    boolean assertEquals(Object actual, Object expected, Object message);
    boolean assertEquals(Collection<?> actual, Collection<?> expected, Object message);
    boolean assertEquals(Iterator<?> actual, Iterator<?> expected, Object message);
    boolean assertEquals(Iterable<?> actual, Iterable<?> expected, Object message);
    boolean assertEquals(Object[] actual, Object[] expected, Object message);
    boolean assertEquals(Set<?> actual, Set<?> expected, Object message);
    boolean assertEquals(Map<?, ?> actual, Map<?, ?> expected, Object message);
    boolean assertEqualsDeep(Set<?> actual, Set<?> expected, Object message);
    boolean assertEqualsDeep(Map<?, ?> actual, Map<?, ?> expected, Object message);
    boolean assertEqualsNoOrder(Object[] actual, Object[] expected, Object message);
    boolean assertNotEquals(Object actual1, Object actual2, Object message);
    boolean assertNotEquals(Set<?> actual, Set<?> expected, Object message);
    boolean assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, Object message);
    boolean assertBeginsWith(Object actual, Object expected, Object message);
    boolean assertEndsWith(Object actual, Object expected, Object message);
}
