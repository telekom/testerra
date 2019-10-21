package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

public interface IAssertableQuantifiedValue<T> extends IAssertableBinaryValue {
    T actual();
    IAssertableQuantifiedValue equals(final String expected);
    IAssertableQuantifiedValue greaterThan(final long expected);
    IAssertableQuantifiedValue greaterThan(final double expected);
    IAssertableQuantifiedValue greaterThan(final BigDecimal expected);
    IAssertableQuantifiedValue lowerThan(final long expected);
    IAssertableQuantifiedValue lowerThan(final double expected);
    IAssertableQuantifiedValue lowerThan(final BigDecimal expected);
    IAssertableQuantifiedValue greaterEqualThan(final long expected);
    IAssertableQuantifiedValue greaterEqualThan(final double expected);
    IAssertableQuantifiedValue greaterEqualThan(final BigDecimal expected);
    IAssertableQuantifiedValue lowerEqualThan(final long expected);
    IAssertableQuantifiedValue lowerEqualThan(final double expected);
    IAssertableQuantifiedValue lowerEqualThan(final BigDecimal expected);
}
