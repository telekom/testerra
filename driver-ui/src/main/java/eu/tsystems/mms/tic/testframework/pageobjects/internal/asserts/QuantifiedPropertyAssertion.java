package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

public class QuantifiedPropertyAssertion<T> extends BinaryPropertyAssertion<T> implements IQuantifiedPropertyAssertion<T> {

    public QuantifiedPropertyAssertion(AssertionProvider<T> provider) {
        super(provider);
    }

    @Override
    public IQuantifiedPropertyAssertion<T> is(final Object expected) {
        testTimer(t -> assertion.assertEquals(provider.actual().toString(), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> greaterThan(final long expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedPropertyAssertion<T> greaterThan(final double expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedPropertyAssertion<T> greaterThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertGreaterThan(new BigDecimal(provider.actual().toString()), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> lowerThan(final long expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedPropertyAssertion<T> lowerThan(final double expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedPropertyAssertion<T> lowerThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertLowerThan(new BigDecimal(provider.actual().toString()), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> greaterEqualThan(final long expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedPropertyAssertion<T> greaterEqualThan(final double expected) {
        return greaterThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedPropertyAssertion<T> greaterEqualThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertGreaterEqualThan(new BigDecimal(provider.actual().toString()), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> lowerEqualThan(final long expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedPropertyAssertion<T> lowerEqualThan(final double expected) {
        return lowerThan(new BigDecimal(expected));
    }

    @Override
    public IQuantifiedPropertyAssertion<T> lowerEqualThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertGreaterEqualThan(new BigDecimal(provider.actual().toString()), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> between(long lower, long higher) {
        return between(new BigDecimal(lower), new BigDecimal(higher));
    }

    @Override
    public IQuantifiedPropertyAssertion<T> between(double lower, double higher) {
        return between(new BigDecimal(lower), new BigDecimal(higher));
    }

    @Override
    public IQuantifiedPropertyAssertion<T> between(BigDecimal lower, BigDecimal higher) {
        testTimer(t -> assertion.assertBetween(new BigDecimal(provider.actual().toString()), lower, higher, provider.traceSubjectString()));
        return this;
    }
}
