package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public class DefaultBinaryAssertion<T> extends AbstractTestedPropertyAssertion<T> implements BinaryAssertion<T> {

    public DefaultBinaryAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean isTrue(String messageOnFailure) {
        return testTimer(t -> {
            final String actualString = getActual().toString();
            if (!(
                    actualString.equalsIgnoreCase("true")
                    || actualString.equalsIgnoreCase("on")
                    || actualString.equalsIgnoreCase("1")
                    || actualString.equalsIgnoreCase("yes")
                )
            ) {
                String message;
                if (messageOnFailure != null) {
                    message = formatCustomMessage(messageOnFailure, actualString, "true");
                } else {
                    message = instantAssertion.format(actualString, "is one of [true, 'on', '1', 'yes']", traceSubjectString());
                }
                instantAssertion.fail(message);
                return false;
            }
            return true;
        });
    }

    @Override
    public boolean isFalse(String messageOnFailure) {
        return testTimer(t -> {
            final String actualString = getActual().toString();
            if (!(
                    actualString.equalsIgnoreCase("false")
                    || actualString.equalsIgnoreCase("off")
                    || actualString.equalsIgnoreCase("0")
                    || actualString.equalsIgnoreCase("no")
                )
            ) {
                String message;
                if (messageOnFailure != null) {
                    message = formatCustomMessage(messageOnFailure, actualString, "true");
                } else {
                    message = instantAssertion.format(actualString, "is one of [false, 'off', '0', 'no']", traceSubjectString());
                }
                instantAssertion.fail(message);
                return false;
            }
            return true;
        });
    }
}
