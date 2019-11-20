package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;

public class DefaultQuantifiedPropertyAssertion<T> extends DefaultBinaryPropertyAssertion<T> implements QuantifiedPropertyAssertion<T> {

    public DefaultQuantifiedPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(final Object expected) {
        return testTimer(t -> instantAssertion.assertEquals(provider.getActual(), expected, traceSubjectString()));
    }

    @Override
    public boolean isGreaterThan(final BigDecimal expected) {
        return testTimer(t -> instantAssertion.assertGreaterThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
    }

    @Override
    public boolean isLowerThan(final BigDecimal expected) {
        return testTimer(t -> instantAssertion.assertLowerThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
    }

    @Override
    public boolean isGreaterEqualThan(final BigDecimal expected) {
        return testTimer(t -> instantAssertion.assertGreaterEqualThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
    }

    @Override
    public boolean isLowerEqualThan(final BigDecimal expected) {
        return testTimer(t -> instantAssertion.assertLowerEqualThan(new BigDecimal(provider.getActual().toString()), expected, traceSubjectString()));
    }

    @Override
    public boolean isBetween(BigDecimal lower, BigDecimal higher) {
        return testTimer(t -> instantAssertion.assertBetween(new BigDecimal(provider.getActual().toString()), lower, higher, traceSubjectString()));
    }

    @Override
    public QuantifiedPropertyAssertion<BigDecimal> absolute() {
        return new DefaultQuantifiedPropertyAssertion<>(this, new AssertionProvider<BigDecimal>() {
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

    @Override
    public DefaultQuantifiedPropertyAssertion<T> perhaps() {
        super.perhaps();
        return this;
    }
}
