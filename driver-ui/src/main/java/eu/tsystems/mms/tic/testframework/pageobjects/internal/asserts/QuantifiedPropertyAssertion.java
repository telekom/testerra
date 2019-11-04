package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

public class QuantifiedPropertyAssertion<T> extends BinaryPropertyAssertion<T> implements IQuantifiedPropertyAssertion<T> {

    public QuantifiedPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public IQuantifiedPropertyAssertion<T> is(final Object expected) {
        testTimer(t -> assertion.assertEquals(provider.getActual(), expected, traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> greaterThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertGreaterThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> lowerThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertLowerThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> greaterEqualThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertGreaterEqualThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> lowerEqualThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertLowerEqualThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<T> between(BigDecimal lower, BigDecimal higher) {
        testTimer(t -> assertion.assertBetween(new BigDecimal(provider.getActual().toString()), lower, higher, traceSubjectString()));
        return this;
    }
}
