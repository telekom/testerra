package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

public class QuantifiedPropertyAssertion<T> extends BinaryPropertyAssertion<T> implements IQuantifiedPropertyAssertion<T> {

    public QuantifiedPropertyAssertion(AssertionProvider<T> provider) {
        super(provider);
    }

    @Override
    public IQuantifiedPropertyAssertion<T> is(final Object expected) {
        testTimer(t -> assertion.assertEquals(provider.actual(), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> greaterThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertGreaterThan(new BigDecimal(provider.actual().toString()), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> lowerThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertLowerThan(new BigDecimal(provider.actual().toString()), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> greaterEqualThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertGreaterEqualThan(new BigDecimal(provider.actual().toString()), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> lowerEqualThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertLowerEqualThan(new BigDecimal(provider.actual().toString()), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> between(BigDecimal lower, BigDecimal higher) {
        testTimer(t -> assertion.assertBetween(new BigDecimal(provider.actual().toString()), lower, higher, provider.traceSubjectString()));
        return this;
    }
}
