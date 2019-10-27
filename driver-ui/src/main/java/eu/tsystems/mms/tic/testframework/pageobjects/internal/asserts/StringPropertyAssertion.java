package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;

public class StringPropertyAssertion<T> extends QuantifiedPropertyAssertion<T> implements IStringPropertyAssertion<T> {

    public StringPropertyAssertion(AssertionProvider<T> provider) {
        super( provider);
    }

    @Override
    public StringPropertyAssertion<T> contains(final String expected) {
        testTimer(t -> {
            configuredAssert.assertContains((String)provider.actual(), expected, provider.traceSubjectString());
            return true;
        });
        return this;
    }

    @Override
    public StringPropertyAssertion<T> containsNot(final String expected) {
        testTimer(t -> {
            configuredAssert.assertContainsNot((String)provider.actual(), expected, provider.traceSubjectString());
            return true;
        });
        return this;
    }

    @Override
    public StringPropertyAssertion<T> beginsWith(String expected) {
        final ThrowablePackedResponse throwablePackedResponse = testTimer(t -> {
            final String actualString = (String) provider.actual();
            return actualString.startsWith(expected);
        });
        if (!throwablePackedResponse.isSuccessful()) {
            fail(String.format("begins with [%s]", expected));
        }
        return this;
    }

    @Override
    public StringPropertyAssertion<T> endsWith(String expected) {
        final ThrowablePackedResponse throwablePackedResponse = testTimer(t -> {
            final String actualString = (String) provider.actual();
            return actualString.endsWith(expected);
        });
        if (!throwablePackedResponse.isSuccessful()) {
            fail(String.format("ends with [%s]", expected));
        }
        return this;
    }

    @Override
    public IStringPropertyAssertion<T> nonFunctional() {
        super.nonFunctional();
        return this;
    }
}
