package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

/**
 * Allows numeric range based assertions
 * @author Mike Reiche
 */
public interface QuantityAssertion<T> extends BinaryAssertion<T> {
    T getActual();
    boolean is(Object expected);
    default boolean isGreaterThan(long expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    default boolean isGreaterThan(double expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    boolean isGreaterThan(BigDecimal expected);
    default boolean isLowerThan(long expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    default boolean isLowerThan(double expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    boolean isLowerThan(BigDecimal expected);
    default boolean isGreaterEqualThan(long expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    default boolean isGreaterEqualThan(double expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    boolean isGreaterEqualThan(BigDecimal expected);
    default boolean isLowerEqualThan(long expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    default boolean isLowerEqualThan(double expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    boolean isLowerEqualThan(BigDecimal expected);
    default boolean isBetween(long lower, long higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    default boolean isBetween(double lower, double higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    boolean isBetween(BigDecimal lower, BigDecimal higher);
    QuantityAssertion<BigDecimal> absolute();
}
