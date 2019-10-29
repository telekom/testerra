package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

public interface IQuantifiedPropertyAssertion<T> extends IBinaryPropertyAssertion<T> {
    T actual();
    IQuantifiedPropertyAssertion<T> is(Object expected);
    IQuantifiedPropertyAssertion<T> greaterThan(long expected);
    IQuantifiedPropertyAssertion<T> greaterThan(double expected);
    IQuantifiedPropertyAssertion<T> greaterThan(BigDecimal expected);
    IQuantifiedPropertyAssertion<T> lowerThan(long expected);
    IQuantifiedPropertyAssertion<T> lowerThan(double expected);
    IQuantifiedPropertyAssertion<T> lowerThan(BigDecimal expected);
    IQuantifiedPropertyAssertion<T> greaterEqualThan(long expected);
    IQuantifiedPropertyAssertion<T> greaterEqualThan(double expected);
    IQuantifiedPropertyAssertion<T> greaterEqualThan(BigDecimal expected);
    IQuantifiedPropertyAssertion<T> lowerEqualThan(long expected);
    IQuantifiedPropertyAssertion<T> lowerEqualThan(double expected);
    IQuantifiedPropertyAssertion<T> lowerEqualThan(BigDecimal expected);
    IQuantifiedPropertyAssertion<T> between(long lower, long higher);
    IQuantifiedPropertyAssertion<T> between(double lower, double higher);
    IQuantifiedPropertyAssertion<T> between(BigDecimal lower, BigDecimal higher);
}
