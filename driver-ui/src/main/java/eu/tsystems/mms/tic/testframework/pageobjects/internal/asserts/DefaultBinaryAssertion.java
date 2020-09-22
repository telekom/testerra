package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public class DefaultBinaryAssertion<T> extends AbstractTestedPropertyAssertion<T> implements BinaryAssertion<T> {

    public DefaultBinaryAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(boolean expected, String failMessage) {
        if (expected) {
            return testTimer(t -> {
                final String actualString = getActual().toString();
                if (!(
                        actualString.equalsIgnoreCase("true")
                                || actualString.equalsIgnoreCase("on")
                                || actualString.equalsIgnoreCase("1")
                                || actualString.equalsIgnoreCase("yes")
                )
                ) {
                    instantAssertion.fail(instantAssertion.format(actualString, "is one of [true, 'on', '1', 'yes']", null));
                    return false;
                }
                return true;
            }, createFailMessageSupplier(failMessage));
        } else {
            return testTimer(t -> {
                final String actualString = getActual().toString();
                if (!(
                        actualString.equalsIgnoreCase("false")
                                || actualString.equalsIgnoreCase("off")
                                || actualString.equalsIgnoreCase("0")
                                || actualString.equalsIgnoreCase("no")
                )
                ) {
                    instantAssertion.fail(instantAssertion.format(actualString, "is one of [false, 'off', '0', 'no']", null));
                    return false;
                }
                return true;
            }, createFailMessageSupplier(failMessage));
        }
    }
}
