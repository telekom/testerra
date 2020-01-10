package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

/**
 * Allows numeric range based assertions
 * @author Mike Reiche
 */
public interface QuantityAssertion<T> extends BinaryAssertion<T> {
    default boolean is(Object expected) {
        return is(expected, null);
    }
    boolean is(Object expected, String prefixMessage);

    default boolean isGreaterThan(long expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    default boolean isGreaterThan(double expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    default boolean isGreaterThan(long expected, String prefixMessage) {
        return isGreaterThan(new BigDecimal(expected), prefixMessage);
    }
    default boolean isGreaterThan(double expected, String prefixMessage) {
        return isGreaterThan(new BigDecimal(expected), prefixMessage);
    }
    default boolean isGreaterThan(BigDecimal expected) {
        return isGreaterThan(expected, null);
    }
    boolean isGreaterThan(BigDecimal expected, String prefixMessage);

    default boolean isLowerThan(long expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    default boolean isLowerThan(double expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    default boolean isLowerThan(long expected, String prefixMessage) {
        return isLowerThan(new BigDecimal(expected), prefixMessage);
    }
    default boolean isLowerThan(double expected, String prefixMessage) {
        return isLowerThan(new BigDecimal(expected), prefixMessage);
    }
    default boolean isLowerThan(BigDecimal expected) {
        return isLowerThan(expected, null);
    }
    boolean isLowerThan(BigDecimal expected, String prefixMessage);

    default boolean isGreaterEqualThan(long expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    default boolean isGreaterEqualThan(double expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    default boolean isGreaterEqualThan(long expected, String prefixMessage) {
        return isGreaterEqualThan(new BigDecimal(expected), prefixMessage);
    }
    default boolean isGreaterEqualThan(double expected, String prefixMessage) {
        return isGreaterEqualThan(new BigDecimal(expected), prefixMessage);
    }
    default boolean isGreaterEqualThan(BigDecimal expected) {
        return isGreaterEqualThan(expected, null);
    }
    boolean isGreaterEqualThan(BigDecimal expected, String prefixMessage);

    default boolean isLowerEqualThan(long expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    default boolean isLowerEqualThan(double expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    default boolean isLowerEqualThan(long expected, String prefixMessage) {
        return isLowerEqualThan(new BigDecimal(expected), prefixMessage);
    }
    default boolean isLowerEqualThan(double expected, String prefixMessage) {
        return isLowerEqualThan(new BigDecimal(expected), prefixMessage);
    }
    default boolean isLowerEqualThan(BigDecimal expected) {
        return isLowerEqualThan(expected, null);
    }
    boolean isLowerEqualThan(BigDecimal expected, String prefixMessage);

    default boolean isBetween(long lower, long higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    default boolean isBetween(double lower, double higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    default boolean isBetween(long lower, long higher, String prefixMessage) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher), prefixMessage);
    }
    default boolean isBetween(double lower, double higher, String prefixMessage) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher), prefixMessage);
    }
    default boolean isBetween(BigDecimal lower, BigDecimal higher) {
        return isBetween(lower, higher, null);
    }
    boolean isBetween(BigDecimal lower, BigDecimal higher, String prefixMessage);

    QuantityAssertion<BigDecimal> absolute();
}
