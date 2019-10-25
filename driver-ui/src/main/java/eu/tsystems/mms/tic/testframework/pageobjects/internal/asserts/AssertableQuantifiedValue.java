package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import org.testng.Assert;

import java.math.BigDecimal;

public class AssertableQuantifiedValue<T, E> extends AssertableBinaryValue<T, E> implements IAssertableQuantifiedValue<T, E> {

    public AssertableQuantifiedValue(final E subject, final T actual, final String propertyName) {
        super(subject, actual, propertyName);
    }

    @Override
    public E equals(final String expected) {
        Assert.assertEquals(actual, expected, String.format("%s %s", subject, property));
        return subject;
    }

    @Override
    public E greaterThan(final long expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public E greaterThan(final double expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public E greaterThan(final BigDecimal expected) {
        AssertUtils.assertGreaterThan(new BigDecimal((String)actual), expected, String.format("%s.%s", subject, property));
        return subject;
    }

    @Override
    public E lowerThan(final long expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public E lowerThan(final double expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public E lowerThan(final BigDecimal expected) {
        AssertUtils.assertLowerThan(new BigDecimal((String)actual), expected);
        return subject;
    }

    @Override
    public E greaterEqualThan(final long expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public E greaterEqualThan(final double expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public E greaterEqualThan(final BigDecimal expected) {
        AssertUtils.assertGreaterEqualThan(new BigDecimal((String)actual), expected, String.format("%s.%s", subject, property));
        return subject;
    }

    @Override
    public E lowerEqualThan(final long expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public E lowerEqualThan(final double expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public E lowerEqualThan(final BigDecimal expected) {
        AssertUtils.assertGreaterEqualThan(new BigDecimal((String)actual), expected, String.format("%s.%s", subject, property));
        return subject;
    }

    @Override
    public E between(long lower, long higher) {
        return between(new BigDecimal(lower), new BigDecimal(higher));
    }

    @Override
    public E between(double lower, double higher) {
        return between(new BigDecimal(lower), new BigDecimal(higher));
    }

    @Override
    public E between(BigDecimal lower, BigDecimal higher) {
        AssertUtils.assertBetween(new BigDecimal((String)actual), lower, higher, String.format("%s.%s", subject, property));
        return subject;
    }

    @Override
    public IAssertableQuantifiedValue<T, E> nonFunctional() {
        super.nonFunctional();
        return this;
    }
}
