package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;

public class BinaryPropertyAssertion<T> extends AbstractTestedPropertyAssertion<T> implements IBinaryPropertyAssertion<T> {

    public BinaryPropertyAssertion(AssertionProvider<T> provider) {
        super(provider);
    }

    @Override
    public IBinaryPropertyAssertion isTrue() {
        final ThrowablePackedResponse throwablePackedResponse = testTimer(t -> {
            if ((boolean) actual()) {
                return true;
            } else {
                final String actualString = (String) actual();
                return (
                    actualString.equalsIgnoreCase("true")
                    || actualString.equalsIgnoreCase("on")
                    || actualString.equalsIgnoreCase("1")
                    || actualString.equalsIgnoreCase("yes")
                );
            }
        });
        if (!throwablePackedResponse.isSuccessful()) {
            fail("is one of [true, 'true', 'on', '1', 'yes']");
        }
        return this;
    }

    @Override
    public IBinaryPropertyAssertion isFalse() {
        final ThrowablePackedResponse throwablePackedResponse = testTimer(t -> {
            if (!(boolean) actual()) {
                return true;
            } else {
                final String actualString = (String) actual();
                return (
                    actualString.equalsIgnoreCase("false")
                    || actualString.equalsIgnoreCase("off")
                    || actualString.equalsIgnoreCase("0")
                    || actualString.equalsIgnoreCase("no")
                );
            }
        });
        if (!throwablePackedResponse.isSuccessful()) {
            fail("is one of [false, 'false', 'off', '0', 'no']");
        }
        return this;
    }

    @Override
    public IBinaryPropertyAssertion<T> nonFunctional() {
        super.nonFunctional();
        return this;
    }
}
