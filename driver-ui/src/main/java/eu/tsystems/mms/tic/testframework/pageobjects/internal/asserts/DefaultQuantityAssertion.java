package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import java.math.BigDecimal;
import java.util.function.Function;

public class DefaultQuantityAssertion<TYPE> extends DefaultBinaryAssertion<TYPE> implements QuantityAssertion<TYPE> {

    public DefaultQuantityAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<TYPE> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(Object expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertion.equals(actual, expected),
                (actual) -> assertion.formatExpectEquals(actual, expected ,createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isNot(Object expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertion.notEquals(actual, expected),
                (actual) -> assertion.formatExpectNotEquals(actual, expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isGreaterThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertion.isGreaterThan(new BigDecimal(actual.toString()), expected),
                (actual) -> assertion.formatExpectGreaterThan(new BigDecimal(actual.toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isLowerThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertion.isLowerThan(new BigDecimal(actual.toString()), expected),
                (actual) -> assertion.formatExpectLowerThan(new BigDecimal(provider.getActual().toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isGreaterEqualThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertion.isGreaterEqualThan(new BigDecimal(actual.toString()), expected),
                (actual) -> assertion.formatExpectGreaterEqualThan(new BigDecimal(actual.toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isLowerEqualThan(BigDecimal expected, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertion.isLowerEqualThan(new BigDecimal(actual.toString()), expected),
                (actual) -> assertion.formatExpectLowerEqualThan(new BigDecimal(actual.toString()), expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isBetween(BigDecimal lower, BigDecimal higher, String failMessage) {
        return testSequence(
                provider,
                (actual) -> assertion.isBetween(new BigDecimal(actual.toString()), lower, higher),
                (actual) -> assertion.formatExpectIsBetween(new BigDecimal(actual.toString()), lower, higher, createFailMessage(failMessage))
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
