package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;

public class ValueAssertion<T> extends QuantifiedAssertion<T> implements IValueAssertion<T> {

    public ValueAssertion(AssertionProvider<T> provider) {
        super( provider);
    }

    @Override
    public ValueAssertion<T> contains(final String expected) {
        testTimer(t -> {
            configuredAssert.assertContains((String)provider.actual(), expected, provider.traceSubjectString());
            return true;
        });
        return this;
    }

    @Override
    public ValueAssertion<T> containsNot(final String expected) {
        testTimer(t -> {
            configuredAssert.assertContainsNot((String)provider.actual(), expected, provider.traceSubjectString());
            return true;
        });
        return this;
    }

    @Override
    public ValueAssertion<T> beginsWith(String expected) {
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
    public ValueAssertion<T> endsWith(String expected) {
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
    public IValueAssertion<T> nonFunctional() {
        super.nonFunctional();
        return this;
    }
}
