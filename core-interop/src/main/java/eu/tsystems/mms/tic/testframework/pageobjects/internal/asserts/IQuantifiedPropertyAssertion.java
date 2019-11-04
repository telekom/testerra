package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

/**
 * Allows numeric range based assertions
 * @author Mike Reiche
 */
public interface IQuantifiedPropertyAssertion<T> extends IBinaryPropertyAssertion<T> {
    T getActual();
    IQuantifiedPropertyAssertion<T> is(Object expected);
    default IQuantifiedPropertyAssertion<T> greaterThan(long expected) {
        return greaterThan(new BigDecimal(expected));
    }
    default IQuantifiedPropertyAssertion<T> greaterThan(double expected) {
        return greaterThan(new BigDecimal(expected));
    }
    IQuantifiedPropertyAssertion<T> greaterThan(BigDecimal expected);
    default IQuantifiedPropertyAssertion<T> lowerThan(long expected) {
        return lowerThan(new BigDecimal(expected));
    }
    default IQuantifiedPropertyAssertion<T> lowerThan(double expected) {
        return lowerThan(new BigDecimal(expected));
    }
    IQuantifiedPropertyAssertion<T> lowerThan(BigDecimal expected);
    default IQuantifiedPropertyAssertion<T> greaterEqualThan(long expected) {
        return greaterEqualThan(new BigDecimal(expected));
    }
    default IQuantifiedPropertyAssertion<T> greaterEqualThan(double expected) {
        return greaterEqualThan(new BigDecimal(expected));
    }
    IQuantifiedPropertyAssertion<T> greaterEqualThan(BigDecimal expected);
    default IQuantifiedPropertyAssertion<T> lowerEqualThan(long expected) {
        return lowerEqualThan(new BigDecimal(expected));
    }
    default IQuantifiedPropertyAssertion<T> lowerEqualThan(double expected) {
        return lowerEqualThan(new BigDecimal(expected));
    }
    IQuantifiedPropertyAssertion<T> lowerEqualThan(BigDecimal expected);
    default IQuantifiedPropertyAssertion<T> between(long lower, long higher) {
        return between(new BigDecimal(lower), new BigDecimal(higher));
    }
    default IQuantifiedPropertyAssertion<T> between(double lower, double higher) {
        return between(new BigDecimal(lower), new BigDecimal(higher));
    }
    IQuantifiedPropertyAssertion<T> between(BigDecimal lower, BigDecimal higher);
}
