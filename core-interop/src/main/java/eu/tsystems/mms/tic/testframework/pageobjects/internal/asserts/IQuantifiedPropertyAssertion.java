package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

public interface IQuantifiedPropertyAssertion<T> extends IBinaryPropertyAssertion<T> {
    T actual();
    IQuantifiedPropertyAssertion<T> is(final Object expected);
    IQuantifiedPropertyAssertion<T> greaterThan(final long expected);
    IQuantifiedPropertyAssertion<T> greaterThan(final double expected);
    IQuantifiedPropertyAssertion<T> greaterThan(final BigDecimal expected);
    IQuantifiedPropertyAssertion<T> lowerThan(final long expected);
    IQuantifiedPropertyAssertion<T> lowerThan(final double expected);
    IQuantifiedPropertyAssertion<T> lowerThan(final BigDecimal expected);
    IQuantifiedPropertyAssertion<T> greaterEqualThan(final long expected);
    IQuantifiedPropertyAssertion<T> greaterEqualThan(final double expected);
    IQuantifiedPropertyAssertion<T> greaterEqualThan(final BigDecimal expected);
    IQuantifiedPropertyAssertion<T> lowerEqualThan(final long expected);
    IQuantifiedPropertyAssertion<T> lowerEqualThan(final double expected);
    IQuantifiedPropertyAssertion<T> lowerEqualThan(final BigDecimal expected);
    IQuantifiedPropertyAssertion<T> between(final long lower, final long higher);
    IQuantifiedPropertyAssertion<T> between(final double lower, final double higher);
    IQuantifiedPropertyAssertion<T> between(final BigDecimal lower, final BigDecimal higher);
}
