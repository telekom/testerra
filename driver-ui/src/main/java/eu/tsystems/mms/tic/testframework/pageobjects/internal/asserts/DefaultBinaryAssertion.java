package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public class DefaultBinaryAssertion<T> extends AbstractTestedPropertyAssertion<T> implements BinaryAssertion<T> {

    public DefaultBinaryAssertion(AbstractPropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(boolean expected, String failMessage) {
        if (expected) {
            return testSequence(
                    provider,
                    (actual) -> {
                        String actualString = actual.toString();
                        return (
                                actualString.equalsIgnoreCase("true")
                                || actualString.equalsIgnoreCase("on")
                                || actualString.equalsIgnoreCase("1")
                                || actualString.equalsIgnoreCase("yes")
                        );
                    },
                    (actual) -> assertion.format(actual, "is one of [true, 'on', '1', 'yes']", createFailMessage(failMessage))
            );
        } else {
            return testSequence(
                    provider,
                    (actual) -> {
                        String actualString = actual.toString();
                        return (
                                actualString.equalsIgnoreCase("false")
                                || actualString.equalsIgnoreCase("off")
                                || actualString.equalsIgnoreCase("0")
                                || actualString.equalsIgnoreCase("no")
                        );
                    },
                    (actual) -> assertion.format(actual, "is one of [false, 'off', '0', 'no']", createFailMessage(failMessage)));
        }
    }
}
