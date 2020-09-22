package eu.tsystems.mms.tic.testframework.execution.testng;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public interface Assertion {
    /**
     * More detailed message object that supports lazy message building
     */
    class Message {
        private final Supplier<String> subjectSupplier;
        private final String prefixMessage;

        public Message(String prefix, Supplier<String> subjectSupplier) {
            this.prefixMessage = prefix;
            this.subjectSupplier = subjectSupplier;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (prefixMessage != null) {
                sb.append(prefixMessage).append(": ");
            }
            sb.append(subjectSupplier.get());
            return sb.toString();
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
    default boolean assertNull(Object object) {
        return assertNull(object, null);
    }
    default boolean assertNotNull(Object object) {
        return assertNotNull(object, null);
    }
    boolean assertNotNull(Object object, Object message);
    default boolean assertContains(String actual, String expected) {
        return assertContains(actual, expected, null);
    }
    boolean assertContains(String actual, String expected, Object message);
    default boolean assertContainsNot(String actual, String expected) {
        return assertContainsNot(actual, expected, null);
    }
    boolean assertContainsNot(String actual, String expected, Object message);
    default boolean assertGreaterThan(BigDecimal actual, BigDecimal expected) {
        return assertGreaterThan(actual, expected, null);
    }
    boolean assertGreaterThan(BigDecimal actual, BigDecimal expected, Object message);
    default boolean assertGreaterEqualThan(BigDecimal actual, BigDecimal expected) {
        return assertGreaterEqualThan(actual, expected, null);
    }
    boolean assertGreaterEqualThan(BigDecimal actual, BigDecimal expected, Object message);
    default boolean assertLowerThan(BigDecimal actual, BigDecimal expected) {
        return assertLowerThan(actual, expected, null);
    }
    boolean assertLowerThan(BigDecimal actual, BigDecimal expected, Object message);
    default boolean assertLowerEqualThan(BigDecimal actual, BigDecimal expected) {
        return assertLowerEqualThan(actual, expected, null);
    }
    boolean assertLowerEqualThan(BigDecimal actual, BigDecimal expected, Object message);
    default boolean assertBetween(BigDecimal actual, BigDecimal lower, BigDecimal higher) {
        return assertBetween(actual, lower, higher, null);
    }
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
    default boolean assertNotEquals(Object actual1, Object actual2) {
        return assertNotEquals(actual1, actual2, null);
    }
    boolean assertNotEquals(Object actual1, Object actual2, Object message);
    boolean assertNotEquals(Set<?> actual, Set<?> expected, Object message);
    boolean assertNotEquals(Map<?, ?> actual, Map<?, ?> expected, Object message);

    default boolean assertBeginsWith(Object actual, Object expected) {
        return assertBeginsWith(actual, expected, null);
    }
    boolean assertBeginsWith(Object actual, Object expected, Object message);
    default boolean assertEndsWith(Object actual, Object expected) {
        return assertEndsWith(actual, expected, null);
    }
    boolean assertEndsWith(Object actual, Object expected, Object message);
}
