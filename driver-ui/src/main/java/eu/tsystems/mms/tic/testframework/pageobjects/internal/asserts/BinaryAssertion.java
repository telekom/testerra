package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import org.testng.Assert;

public class BinaryAssertion<T> extends AbstractTestAssertion<T> implements IBinaryAssertion<T> {
    protected boolean nonFunctional = false;

    public BinaryAssertion(AssertionProvider<T> provider) {
        super(provider);
    }

    @Override
    public IBinaryAssertion isTrue() {
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
            Assert.fail(String.format("%s [%s] is true", provider.traceSubjectString(), provider.actual()));
        }
        return this;
    }

    @Override
    public IBinaryAssertion isFalse() {
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
            Assert.fail(String.format("%s [%s] is false", provider.traceSubjectString(), provider.actual()));
        }
        return this;
    }

    @Override
    public INonFunctionalAssertion nonFunctional() {
        nonFunctional = true;
        return this;
    }
}
