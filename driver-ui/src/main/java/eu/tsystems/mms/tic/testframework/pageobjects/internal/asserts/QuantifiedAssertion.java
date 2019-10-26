package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import org.testng.Assert;

import java.math.BigDecimal;

public class QuantifiedAssertion<T> extends BinaryAssertion<T> implements IQuantifiedAssertion<T> {

    public QuantifiedAssertion(AssertionProvider<T> provider) {
        super(provider);
    }

    @Override
    public IQuantifiedAssertion<T> equals(final String expected) {
        testTimer(t -> {
            Assert.assertEquals(provider.actual(), expected, provider.traceSubjectString());
            return true;
        });
        return this;
    }

    @Override
    public IQuantifiedAssertion<T> greaterThan(final long expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedAssertion<T> greaterThan(final double expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedAssertion<T> greaterThan(final BigDecimal expected) {
        testTimer(t -> {
            AssertUtils.assertGreaterThan(new BigDecimal((String) provider.actual()), expected, provider.traceSubjectString());
            return true;
        });
        return this;
    }

    @Override
    public IQuantifiedAssertion<T> lowerThan(final long expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedAssertion<T> lowerThan(final double expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedAssertion<T> lowerThan(final BigDecimal expected) {
        testTimer(t -> {
            AssertUtils.assertLowerThan(new BigDecimal((String) provider.actual()), expected);
            return true;
        });
        return this;
    }

    @Override
    public IQuantifiedAssertion<T> greaterEqualThan(final long expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedAssertion<T> greaterEqualThan(final double expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedAssertion<T> greaterEqualThan(final BigDecimal expected) {
        testTimer(t -> {
            AssertUtils.assertGreaterEqualThan(new BigDecimal((String) provider.actual()), expected, provider.traceSubjectString());
            return true;
        });
        return this;
    }

    @Override
    public IQuantifiedAssertion<T> lowerEqualThan(final long expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedAssertion<T> lowerEqualThan(final double expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedAssertion<T> lowerEqualThan(final BigDecimal expected) {
        testTimer(t -> {
            AssertUtils.assertGreaterEqualThan(new BigDecimal((String) provider.actual()), expected, provider.traceSubjectString());
            return true;
        });
        return this;
    }

    @Override
    public IQuantifiedAssertion<T> between(long lower, long higher) {
        return between(new BigDecimal(lower), new BigDecimal(higher));
    }

    @Override
    public IQuantifiedAssertion<T> between(double lower, double higher) {
        return between(new BigDecimal(lower), new BigDecimal(higher));
    }

    @Override
    public IQuantifiedAssertion<T> between(BigDecimal lower, BigDecimal higher) {
        testTimer(t -> {
            AssertUtils.assertBetween(new BigDecimal((String) provider.actual()), lower, higher, provider.traceSubjectString());
            return true;
        });
        return this;
    }

    @Override
    public IQuantifiedAssertion<T> nonFunctional() {
        super.nonFunctional();
        return this;
    }
}
