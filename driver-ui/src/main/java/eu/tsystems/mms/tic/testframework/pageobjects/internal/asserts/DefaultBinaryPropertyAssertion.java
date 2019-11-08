package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public class DefaultBinaryPropertyAssertion<T> extends AbstractTestedPropertyAssertion<T> implements BinaryPropertyAssertion<T> {

    public DefaultBinaryPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public BinaryPropertyAssertion<T> isTrue() {
        testTimer(t -> {
            final String actualString = getActual().toString();
            if (!(
                    actualString.equalsIgnoreCase("true")
                    || actualString.equalsIgnoreCase("on")
                    || actualString.equalsIgnoreCase("1")
                    || actualString.equalsIgnoreCase("yes")
                )
            ) {
                assertion.fail(assertion.format(actualString, "is one of [true, 'on', '1', 'yes']", traceSubjectString()));
                return false;
            }
            return true;
        });
        return this;
    }

    @Override
    public BinaryPropertyAssertion<T> isFalse() {
        testTimer(t -> {
            final String actualString = getActual().toString();
            if (!(
                    actualString.equalsIgnoreCase("false")
                    || actualString.equalsIgnoreCase("off")
                    || actualString.equalsIgnoreCase("0")
                    || actualString.equalsIgnoreCase("no")
                )
            ) {
                assertion.fail(assertion.format(actualString, "is one of [false, 'off', '0', 'no']", traceSubjectString()));
                return false;
            }
            return true;
        });
        return this;
    }
}
