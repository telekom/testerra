package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

public interface IAssertableQuantifiedValue<T, E> extends IAssertableBinaryValue<T, E> {
    T actual();
    E equals(final String expected);
    E greaterThan(final long expected);
    E greaterThan(final double expected);
    E greaterThan(final BigDecimal expected);
    E lowerThan(final long expected);
    E lowerThan(final double expected);
    E lowerThan(final BigDecimal expected);
    E greaterEqualThan(final long expected);
    E greaterEqualThan(final double expected);
    E greaterEqualThan(final BigDecimal expected);
    E lowerEqualThan(final long expected);
    E lowerEqualThan(final double expected);
    E lowerEqualThan(final BigDecimal expected);
    E between(final long lower, final long higher);
    E between(final double lower, final double higher);
    E between(final BigDecimal lower, final BigDecimal higher);

    @Override
    IAssertableQuantifiedValue<T, E> nonFunctional();
}
