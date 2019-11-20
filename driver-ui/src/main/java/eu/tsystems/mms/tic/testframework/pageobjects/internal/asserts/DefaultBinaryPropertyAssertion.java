package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public class DefaultBinaryPropertyAssertion<T> extends AbstractTestedPropertyAssertion<T> implements BinaryPropertyAssertion<T> {

    public DefaultBinaryPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean isTrue() {
        return testTimer(t -> {
            final String actualString = getActual().toString();
            if (!(
                    actualString.equalsIgnoreCase("true")
                    || actualString.equalsIgnoreCase("on")
                    || actualString.equalsIgnoreCase("1")
                    || actualString.equalsIgnoreCase("yes")
                )
            ) {
                instantAssertion.fail(instantAssertion.format(actualString, "is one of [true, 'on', '1', 'yes']", traceSubjectString()));
                return false;
            }
            return true;
        });
    }

    @Override
    public boolean isFalse() {
        return testTimer(t -> {
            final String actualString = getActual().toString();
            if (!(
                    actualString.equalsIgnoreCase("false")
                    || actualString.equalsIgnoreCase("off")
                    || actualString.equalsIgnoreCase("0")
                    || actualString.equalsIgnoreCase("no")
                )
            ) {
                instantAssertion.fail(instantAssertion.format(actualString, "is one of [false, 'off', '0', 'no']", traceSubjectString()));
                return false;
            }
            return true;
        });
    }

    @Override
    public DefaultBinaryPropertyAssertion<T> perhaps() {
        super.perhaps();
        return this;
    }
}
