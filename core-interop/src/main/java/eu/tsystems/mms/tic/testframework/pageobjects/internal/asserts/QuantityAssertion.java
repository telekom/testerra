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
    boolean is(Object expected, String message);

    default boolean isNot(Object expected) {
        return isNot(expected, null);
    }
    boolean isNot(Object expected, String message);

    default boolean isGreaterThan(long expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    default boolean isGreaterThan(double expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    default boolean isGreaterThan(long expected, String message) {
        return isGreaterThan(new BigDecimal(expected), message);
    }
    default boolean isGreaterThan(double expected, String message) {
        return isGreaterThan(new BigDecimal(expected), message);
    }
    default boolean isGreaterThan(BigDecimal expected) {
        return isGreaterThan(expected, null);
    }
    boolean isGreaterThan(BigDecimal expected, String message);

    default boolean isLowerThan(long expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    default boolean isLowerThan(double expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    default boolean isLowerThan(long expected, String message) {
        return isLowerThan(new BigDecimal(expected), message);
    }
    default boolean isLowerThan(double expected, String message) {
        return isLowerThan(new BigDecimal(expected), message);
    }
    default boolean isLowerThan(BigDecimal expected) {
        return isLowerThan(expected, null);
    }
    boolean isLowerThan(BigDecimal expected, String message);

    default boolean isGreaterEqualThan(long expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    default boolean isGreaterEqualThan(double expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    default boolean isGreaterEqualThan(long expected, String message) {
        return isGreaterEqualThan(new BigDecimal(expected), message);
    }
    default boolean isGreaterEqualThan(double expected, String message) {
        return isGreaterEqualThan(new BigDecimal(expected), message);
    }
    default boolean isGreaterEqualThan(BigDecimal expected) {
        return isGreaterEqualThan(expected, null);
    }
    boolean isGreaterEqualThan(BigDecimal expected, String message);

    default boolean isLowerEqualThan(long expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    default boolean isLowerEqualThan(double expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    default boolean isLowerEqualThan(long expected, String message) {
        return isLowerEqualThan(new BigDecimal(expected), message);
    }
    default boolean isLowerEqualThan(double expected, String message) {
        return isLowerEqualThan(new BigDecimal(expected), message);
    }
    default boolean isLowerEqualThan(BigDecimal expected) {
        return isLowerEqualThan(expected, null);
    }
    boolean isLowerEqualThan(BigDecimal expected, String message);

    default boolean isBetween(long lower, long higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    default boolean isBetween(double lower, double higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    default boolean isBetween(long lower, long higher, String message) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher), message);
    }
    default boolean isBetween(double lower, double higher, String message) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher), message);
    }
    default boolean isBetween(BigDecimal lower, BigDecimal higher) {
        return isBetween(lower, higher, null);
    }
    boolean isBetween(BigDecimal lower, BigDecimal higher, String message);

    QuantityAssertion<BigDecimal> absolute();
}
