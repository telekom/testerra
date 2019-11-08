package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

/**
 * Allows numeric range based assertions
 * @author Mike Reiche
 */
public interface QuantifiedPropertyAssertion<T> extends BinaryPropertyAssertion<T> {
    T getActual();
    QuantifiedPropertyAssertion<T> is(Object expected);
    default QuantifiedPropertyAssertion<T> greaterThan(long expected) {
        return greaterThan(new BigDecimal(expected));
    }
    default QuantifiedPropertyAssertion<T> greaterThan(double expected) {
        return greaterThan(new BigDecimal(expected));
    }
    QuantifiedPropertyAssertion<T> greaterThan(BigDecimal expected);
    default QuantifiedPropertyAssertion<T> lowerThan(long expected) {
        return lowerThan(new BigDecimal(expected));
    }
    default QuantifiedPropertyAssertion<T> lowerThan(double expected) {
        return lowerThan(new BigDecimal(expected));
    }
    QuantifiedPropertyAssertion<T> lowerThan(BigDecimal expected);
    default QuantifiedPropertyAssertion<T> greaterEqualThan(long expected) {
        return greaterEqualThan(new BigDecimal(expected));
    }
    default QuantifiedPropertyAssertion<T> greaterEqualThan(double expected) {
        return greaterEqualThan(new BigDecimal(expected));
    }
    QuantifiedPropertyAssertion<T> greaterEqualThan(BigDecimal expected);
    default QuantifiedPropertyAssertion<T> lowerEqualThan(long expected) {
        return lowerEqualThan(new BigDecimal(expected));
    }
    default QuantifiedPropertyAssertion<T> lowerEqualThan(double expected) {
        return lowerEqualThan(new BigDecimal(expected));
    }
    QuantifiedPropertyAssertion<T> lowerEqualThan(BigDecimal expected);
    default QuantifiedPropertyAssertion<T> between(long lower, long higher) {
        return between(new BigDecimal(lower), new BigDecimal(higher));
    }
    default QuantifiedPropertyAssertion<T> between(double lower, double higher) {
        return between(new BigDecimal(lower), new BigDecimal(higher));
    }
    QuantifiedPropertyAssertion<T> between(BigDecimal lower, BigDecimal higher);
    QuantifiedPropertyAssertion<BigDecimal> absolute();
}
