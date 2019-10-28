package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.logging.Loggable;

public class BinaryPropertyAssertion<T> extends AbstractTestedPropertyAssertion<T> implements IBinaryPropertyAssertion<T>, Loggable {

    public BinaryPropertyAssertion(AssertionProvider<T> provider) {
        super(provider);
    }

    @Override
    public IBinaryPropertyAssertion<T> isTrue() {
        testTimer(t -> {
            final String actualString = actual().toString();
            log().info("Test: " + actualString);
            if (!(
                    actualString.equalsIgnoreCase("true")
                    || actualString.equalsIgnoreCase("on")
                    || actualString.equalsIgnoreCase("1")
                    || actualString.equalsIgnoreCase("yes")
                )
            ) {
                configuredAssert.fail(configuredAssert.format(actualString, "is one of [true, 'on', '1', 'yes']", provider.traceSubjectString()));
            }
            return true;
        });
        return this;
    }

    @Override
    public IBinaryPropertyAssertion<T> isFalse() {
        testTimer(t -> {
            final String actualString = actual().toString();
            if (!(
                    actualString.equalsIgnoreCase("false")
                    || actualString.equalsIgnoreCase("off")
                    || actualString.equalsIgnoreCase("0")
                    || actualString.equalsIgnoreCase("no")
                )
            ) {
                configuredAssert.fail(configuredAssert.format(actualString, "is one of [false, 'off', '0', 'no']", provider.traceSubjectString()));
            }
            return true;
        });
        return this;
    }

    @Override
    public IBinaryPropertyAssertion<T> nonFunctional() {
        super.nonFunctional();
        return this;
    }
}
