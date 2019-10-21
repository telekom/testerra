package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import org.testng.Assert;

import java.math.BigDecimal;

public class AssertableQuantifiedValue<T> extends AssertableBinaryValue implements IAssertableQuantifiedValue {

    public AssertableQuantifiedValue(final T actual, String propertyName, Object object) {
        super(actual, propertyName, object);
    }

    @Override
    public IAssertableQuantifiedValue equals(final String expected) {
        Assert.assertEquals(actual, expected);
        return this;
    }

    @Override
    public AssertableQuantifiedValue greaterThan(final long expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public AssertableQuantifiedValue greaterThan(final double expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public AssertableQuantifiedValue greaterThan(final BigDecimal expected) {
        AssertUtils.assertGreaterThan(new BigDecimal((String)actual), expected);
        return this;
    }

    @Override
    public AssertableQuantifiedValue lowerThan(final long expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public AssertableQuantifiedValue lowerThan(final double expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public AssertableQuantifiedValue lowerThan(final BigDecimal expected) {
        AssertUtils.assertLowerThan(new BigDecimal((String)actual), expected);
        return this;
    }

    @Override
    public AssertableQuantifiedValue greaterEqualThan(final long expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public AssertableQuantifiedValue greaterEqualThan(final double expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public AssertableQuantifiedValue greaterEqualThan(final BigDecimal expected) {
        AssertUtils.assertGreaterEqualThan(new BigDecimal((String)actual), expected);
        return this;
    }

    @Override
    public AssertableQuantifiedValue lowerEqualThan(final long expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public AssertableQuantifiedValue lowerEqualThan(final double expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public AssertableQuantifiedValue lowerEqualThan(final BigDecimal expected) {
        AssertUtils.assertGreaterEqualThan(new BigDecimal((String)actual), expected);
        return this;
    }
}
