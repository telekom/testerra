package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

public class DefaultQuantifiedPropertyAssertion<T> extends DefaultBinaryPropertyAssertion<T> implements QuantifiedPropertyAssertion<T> {

    public DefaultQuantifiedPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public QuantifiedPropertyAssertion<T> is(final Object expected) {
        testTimer(t -> assertion.assertEquals(provider.getActual(), expected, traceSubjectString()));
        return this;
    }

    @Override
    public QuantifiedPropertyAssertion<T> isGreaterThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertGreaterThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
        return this;
    }

    @Override
    public QuantifiedPropertyAssertion<T> isLowerThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertLowerThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
        return this;
    }

    @Override
    public QuantifiedPropertyAssertion<T> isGreaterEqualThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertGreaterEqualThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
        return this;
    }

    @Override
    public QuantifiedPropertyAssertion<T> isLowerEqualThan(final BigDecimal expected) {
        testTimer(t -> assertion.assertLowerEqualThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
        return this;
    }

    @Override
    public QuantifiedPropertyAssertion<T> isBetween(BigDecimal lower, BigDecimal higher) {
        testTimer(t -> assertion.assertBetween(new BigDecimal(provider.getActual().toString()), lower, higher, traceSubjectString()));
        return this;
    }

    @Override
    public QuantifiedPropertyAssertion<BigDecimal> absolute() {
        return propertyAssertionFactory.quantified(this, new AssertionProvider<BigDecimal>() {
            @Override
            public BigDecimal getActual() {
                BigDecimal number;
                if (!(provider.getActual() instanceof BigDecimal)) {
                    number = new BigDecimal(provider.getActual().toString());
                } else {
                    number = (BigDecimal)provider.getActual();
                }
                return number.abs();
            }

            @Override
            public String getSubject() {
                return "absolute";
            }
        });
    }
}
