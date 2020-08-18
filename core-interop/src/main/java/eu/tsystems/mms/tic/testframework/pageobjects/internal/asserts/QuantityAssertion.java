package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;
import java.util.function.Function;

/**
 * Allows numeric range based assertions
 * @author Mike Reiche
 */
public interface QuantityAssertion<TYPE> extends BinaryAssertion<TYPE> {
    default boolean is(Object expected) {
        return is(expected, null);
    }
    boolean is(Object expected, String failMessage);

    default boolean isNot(Object expected) {
        return isNot(expected, null);
    }
    boolean isNot(Object expected, String failMessage);

    default boolean isGreaterThan(long expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    default boolean isGreaterThan(double expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    default boolean isGreaterThan(long expected, String failMessage) {
        return isGreaterThan(new BigDecimal(expected), failMessage);
    }
    default boolean isGreaterThan(double expected, String failMessage) {
        return isGreaterThan(new BigDecimal(expected), failMessage);
    }
    default boolean isGreaterThan(BigDecimal expected) {
        return isGreaterThan(expected, null);
    }
    boolean isGreaterThan(BigDecimal expected, String failMessage);

    default boolean isLowerThan(long expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    default boolean isLowerThan(double expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    default boolean isLowerThan(long expected, String failMessage) {
        return isLowerThan(new BigDecimal(expected), failMessage);
    }
    default boolean isLowerThan(double expected, String failMessage) {
        return isLowerThan(new BigDecimal(expected), failMessage);
    }
    default boolean isLowerThan(BigDecimal expected) {
        return isLowerThan(expected, null);
    }
    boolean isLowerThan(BigDecimal expected, String failMessage);

    default boolean isGreaterEqualThan(long expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    default boolean isGreaterEqualThan(double expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    default boolean isGreaterEqualThan(long expected, String failMessage) {
        return isGreaterEqualThan(new BigDecimal(expected), failMessage);
    }
    default boolean isGreaterEqualThan(double expected, String failMessage) {
        return isGreaterEqualThan(new BigDecimal(expected), failMessage);
    }
    default boolean isGreaterEqualThan(BigDecimal expected) {
        return isGreaterEqualThan(expected, null);
    }
    boolean isGreaterEqualThan(BigDecimal expected, String failMessage);

    default boolean isLowerEqualThan(long expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    default boolean isLowerEqualThan(double expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    default boolean isLowerEqualThan(long expected, String failMessage) {
        return isLowerEqualThan(new BigDecimal(expected), failMessage);
    }
    default boolean isLowerEqualThan(double expected, String failMessage) {
        return isLowerEqualThan(new BigDecimal(expected), failMessage);
    }
    default boolean isLowerEqualThan(BigDecimal expected) {
        return isLowerEqualThan(expected, null);
    }
    boolean isLowerEqualThan(BigDecimal expected, String failMessage);

    default boolean isBetween(long lower, long higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    default boolean isBetween(double lower, double higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    default boolean isBetween(long lower, long higher, String failMessage) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher), failMessage);
    }
    default boolean isBetween(double lower, double higher, String failMessage) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher), failMessage);
    }
    default boolean isBetween(BigDecimal lower, BigDecimal higher) {
        return isBetween(lower, higher, null);
    }
    boolean isBetween(BigDecimal lower, BigDecimal higher, String failMessage);

    QuantityAssertion<TYPE> map(Function<? super TYPE, ? extends TYPE> mapFunction);

    QuantityAssertion<BigDecimal> absolute();
}
