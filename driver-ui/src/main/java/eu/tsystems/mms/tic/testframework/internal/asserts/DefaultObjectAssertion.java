package eu.tsystems.mms.tic.testframework.internal.asserts;

public class DefaultObjectAssertion<T> extends DefaultBinaryAssertion<T> implements ObjectAssertion<T> {

    public DefaultObjectAssertion(AbstractPropertyAssertion<T> parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }
    @Override
    public boolean is(Object expected, String failMessage) {
        if (expected instanceof Boolean) {
            boolean expectedBoolean = (Boolean) expected;
            return this.is(expectedBoolean, failMessage);
        }
        return testSequence(
                provider,
                (actual) -> assertionImpl.equals(actual, expected),
                (actual) -> assertionImpl.formatExpectEquals(null, expected, createFailMessage(failMessage))
        );
    }

    @Override
    public boolean isNot(Object expected, String failMessage) {
        if (expected instanceof Boolean) {
            boolean expectedBoolean = (Boolean) expected;
            return this.is(!expectedBoolean, failMessage);
        } else {
            return testSequence(
                    provider,
                    (actual) -> assertionImpl.notEquals(actual, expected),
                    (actual) -> assertionImpl.formatExpectNotEquals(null, expected, createFailMessage(failMessage))
            );
        }
    }
}
