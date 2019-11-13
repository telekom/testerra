package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

/**
 * Allows numeric range based assertions
 * @author Mike Reiche
 */
public interface QuantifiedPropertyAssertion<T> extends BinaryPropertyAssertion<T> {
    T getActual();
    QuantifiedPropertyAssertion<T> is(Object expected);
    default QuantifiedPropertyAssertion<T> isGreaterThan(long expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    default QuantifiedPropertyAssertion<T> isGreaterThan(double expected) {
        return isGreaterThan(new BigDecimal(expected));
    }
    QuantifiedPropertyAssertion<T> isGreaterThan(BigDecimal expected);
    default QuantifiedPropertyAssertion<T> isLowerThan(long expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    default QuantifiedPropertyAssertion<T> isLowerThan(double expected) {
        return isLowerThan(new BigDecimal(expected));
    }
    QuantifiedPropertyAssertion<T> isLowerThan(BigDecimal expected);
    default QuantifiedPropertyAssertion<T> isGreaterEqualThan(long expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    default QuantifiedPropertyAssertion<T> isGreaterEqualThan(double expected) {
        return isGreaterEqualThan(new BigDecimal(expected));
    }
    QuantifiedPropertyAssertion<T> isGreaterEqualThan(BigDecimal expected);
    default QuantifiedPropertyAssertion<T> isLowerEqualThan(long expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    default QuantifiedPropertyAssertion<T> isLowerEqualThan(double expected) {
        return isLowerEqualThan(new BigDecimal(expected));
    }
    QuantifiedPropertyAssertion<T> isLowerEqualThan(BigDecimal expected);
    default QuantifiedPropertyAssertion<T> isBetween(long lower, long higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    default QuantifiedPropertyAssertion<T> isBetween(double lower, double higher) {
        return isBetween(new BigDecimal(lower), new BigDecimal(higher));
    }
    QuantifiedPropertyAssertion<T> isBetween(BigDecimal lower, BigDecimal higher);
    QuantifiedPropertyAssertion<BigDecimal> absolute();
}
