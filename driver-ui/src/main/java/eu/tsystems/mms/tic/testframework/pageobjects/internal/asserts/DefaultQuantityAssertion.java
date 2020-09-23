package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;
import java.util.function.Function;

public class DefaultQuantityAssertion<TYPE> extends DefaultBinaryAssertion<TYPE> implements QuantityAssertion<TYPE> {

    public DefaultQuantityAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<TYPE> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(Object expected, String failMessage) {
        return testTimer(
                () -> assertion.equals(provider.getActual(), expected),
                () -> assertion.formatExpectEquals(provider.getActual(), expected ,createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isNot(Object expected, String failMessage) {
        return testTimer(
                () -> assertion.notEquals(provider.getActual(), expected),
                () -> assertion.formatExpectNotEquals(provider.getActual(), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isGreaterThan(BigDecimal expected, String failMessage) {
        return testTimer(
                () -> assertion.isGreaterThan(new BigDecimal(provider.getActual().toString()), expected),
                () -> assertion.formatExpectGreaterThan(new BigDecimal(provider.getActual().toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isLowerThan(BigDecimal expected, String failMessage) {
        return testTimer(
                () -> assertion.isLowerThan(new BigDecimal(provider.getActual().toString()), expected),
                () -> assertion.formatExpectLowerThan(new BigDecimal(provider.getActual().toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isGreaterEqualThan(BigDecimal expected, String failMessage) {
        return testTimer(
                () -> assertion.isGreaterEqualThan(new BigDecimal(provider.getActual().toString()), expected),
                () -> assertion.formatExpectGreaterEqualThan(new BigDecimal(provider.getActual().toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isLowerEqualThan(BigDecimal expected, String failMessage) {
        return testTimer(
                () -> assertion.isLowerEqualThan(new BigDecimal(provider.getActual().toString()), expected),
                () -> assertion.formatExpectLowerEqualThan(new BigDecimal(provider.getActual().toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isBetween(BigDecimal lower, BigDecimal higher, String failMessage) {
        return testTimer(
                () -> assertion.isBetween(new BigDecimal(provider.getActual().toString()), lower, higher),
                () -> assertion.formatExpectIsBetween(new BigDecimal(provider.getActual().toString()), lower, higher, createFailMessage(failMessage))
        );
    }

    @Override
    public <MAPPED_TYPE> StringAssertion<MAPPED_TYPE> map(Function<? super TYPE, MAPPED_TYPE> mapFunction) {
        return propertyAssertionFactory.create(DefaultStringAssertion.class, this, new AssertionProvider<MAPPED_TYPE>() {
            @Override
            public MAPPED_TYPE getActual() {
                TYPE actual = provider.getActual();
                if (actual == null) {
                    return null;
                } else {
                    return mapFunction.apply(provider.getActual());
                }
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
