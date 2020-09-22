package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;
import java.util.function.Function;

public class DefaultQuantityAssertion<TYPE> extends DefaultBinaryAssertion<TYPE> implements QuantityAssertion<TYPE> {

    public DefaultQuantityAssertion(PropertyAssertion parentAssertion, AssertionProvider<TYPE> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(Object expected, String failMessage) {
        return testTimer(
                () -> instantAssertion.equals(provider.getActual(), expected),
                () -> instantAssertion.formatExpectEquals(provider.getActual(), expected ,createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isNot(Object expected, String failMessage) {
        return testTimer(
                () -> instantAssertion.notEquals(provider.getActual(), expected),
                () -> instantAssertion.formatExpectNotEquals(provider.getActual(), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isGreaterThan(BigDecimal expected, String failMessage) {
        return testTimer(
                () -> instantAssertion.isGreaterThan(new BigDecimal(provider.getActual().toString()), expected),
                () -> instantAssertion.formatExpectGreaterThan(new BigDecimal(provider.getActual().toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isLowerThan(BigDecimal expected, String failMessage) {
        return testTimer(
                () -> instantAssertion.isLowerThan(new BigDecimal(provider.getActual().toString()), expected),
                () -> instantAssertion.formatExpectLowerThan(new BigDecimal(provider.getActual().toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isGreaterEqualThan(BigDecimal expected, String failMessage) {
        return testTimer(
                () -> instantAssertion.isGreaterEqualThan(new BigDecimal(provider.getActual().toString()), expected),
                () -> instantAssertion.formatExpectGreaterEqualThan(new BigDecimal(provider.getActual().toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isLowerEqualThan(BigDecimal expected, String failMessage) {
        return testTimer(
                () -> instantAssertion.isLowerEqualThan(new BigDecimal(provider.getActual().toString()), expected),
                () -> instantAssertion.formatExpectLowerEqualThan(new BigDecimal(provider.getActual().toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isBetween(BigDecimal lower, BigDecimal higher, String failMessage) {
        return testTimer(
                () -> instantAssertion.isBetween(new BigDecimal(provider.getActual().toString()), lower, higher),
                () -> instantAssertion.formatExpectIsBetween(new BigDecimal(provider.getActual().toString()), lower, higher, createFailMessage(failMessage))
        );
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
