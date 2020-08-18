package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.execution.testng.Assertion;
import java.math.BigDecimal;
import java.util.function.Function;

public class DefaultQuantityAssertion<TYPE> extends DefaultBinaryAssertion<TYPE> implements QuantityAssertion<TYPE> {

    public DefaultQuantityAssertion(PropertyAssertion parentAssertion, AssertionProvider<TYPE> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(Object expected, String failMessage) {
        return testTimer(t -> instantAssertion.assertEquals(provider.getActual(), expected, new Assertion.Message(failMessage, traceSubjectString())));
    }

    @Override
    public boolean isNot(Object expected, String failMessage) {
        return testTimer(t -> instantAssertion.assertNotEquals(provider.getActual(), expected, new Assertion.Message(failMessage, traceSubjectString())));
    }

    @Override
    public boolean isGreaterThan(BigDecimal expected, String failMessage) {
        return testTimer(t -> instantAssertion.assertGreaterThan(new BigDecimal(provider.getActual().toString()), expected, new Assertion.Message(failMessage, traceSubjectString())));
    }

    @Override
    public boolean isLowerThan(BigDecimal expected, String failMessage) {
        return testTimer(t -> instantAssertion.assertLowerThan(new BigDecimal(provider.getActual().toString()), expected, new Assertion.Message(failMessage, traceSubjectString())));
    }

    @Override
    public boolean isGreaterEqualThan(BigDecimal expected, String failMessage) {
        return testTimer(t -> instantAssertion.assertGreaterEqualThan(new BigDecimal(provider.getActual().toString()), expected, new Assertion.Message(failMessage, traceSubjectString())));
    }

    @Override
    public boolean isLowerEqualThan(BigDecimal expected, String failMessage) {
        return testTimer(t -> instantAssertion.assertLowerEqualThan(new BigDecimal(provider.getActual().toString()), expected, new Assertion.Message(failMessage, traceSubjectString())));
    }

    @Override
    public boolean isBetween(BigDecimal lower, BigDecimal higher, String failMessage) {
        return testTimer(t -> instantAssertion.assertBetween(new BigDecimal(provider.getActual().toString()), lower, higher, new Assertion.Message(failMessage, traceSubjectString())));
    }

    @Override
    public <MAPPED_TYPE> StringAssertion<MAPPED_TYPE> map(Function<? super TYPE, MAPPED_TYPE> mapFunction) {
        return propertyAssertionFactory.create(DefaultStringAssertion.class, this, new AssertionProvider<MAPPED_TYPE>() {
            @Override
            public MAPPED_TYPE getActual() {
                return mapFunction.apply(provider.getActual());
            }

            @Override
            public String getSubject() {
                return "map";
            }
        });
    }

    @Override
    public QuantityAssertion<BigDecimal> absolute() {
        return propertyAssertionFactory.create(DefaultQuantityAssertion.class, this, new AssertionProvider<BigDecimal>() {
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
