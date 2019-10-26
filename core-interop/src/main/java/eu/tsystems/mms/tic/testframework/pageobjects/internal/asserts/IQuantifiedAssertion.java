package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

public interface IQuantifiedAssertion<T> extends IBinaryAssertion<T> {
    T actual();
    IQuantifiedAssertion<T> equals(final String expected);
    IQuantifiedAssertion<T> greaterThan(final long expected);
    IQuantifiedAssertion<T> greaterThan(final double expected);
    IQuantifiedAssertion<T> greaterThan(final BigDecimal expected);
    IQuantifiedAssertion<T> lowerThan(final long expected);
    IQuantifiedAssertion<T> lowerThan(final double expected);
    IQuantifiedAssertion<T> lowerThan(final BigDecimal expected);
    IQuantifiedAssertion<T> greaterEqualThan(final long expected);
    IQuantifiedAssertion<T> greaterEqualThan(final double expected);
    IQuantifiedAssertion<T> greaterEqualThan(final BigDecimal expected);
    IQuantifiedAssertion<T> lowerEqualThan(final long expected);
    IQuantifiedAssertion<T> lowerEqualThan(final double expected);
    IQuantifiedAssertion<T> lowerEqualThan(final BigDecimal expected);
    IQuantifiedAssertion<T> between(final long lower, final long higher);
    IQuantifiedAssertion<T> between(final double lower, final double higher);
    IQuantifiedAssertion<T> between(final BigDecimal lower, final BigDecimal higher);

    @Override
    IQuantifiedAssertion<T> nonFunctional();
}
