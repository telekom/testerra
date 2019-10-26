package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.transfer.ThrowablePackedResponse;
import eu.tsystems.mms.tic.testframework.utils.AssertUtils;
import org.testng.Assert;

public class ValueAssertion<T> extends QuantifiedAssertion<T> implements IValueAssertion<T> {

    public ValueAssertion(AssertionProvider<T> provider) {
        super( provider);
    }

    @Override
    public ValueAssertion<T> contains(final String expected) {
        testTimer(t -> {
            AssertUtils.assertContains((String)provider.actual(), expected, provider.traceSubjectString());
            return true;
        });
        return this;
    }

    @Override
    public ValueAssertion<T> containsNot(final String expected) {
        testTimer(t -> {
            AssertUtils.assertContainsNot((String)provider.actual(), expected, provider.traceSubjectString());
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
            Assert.fail(String.format("%s [%s] begins with [%s]", provider.traceSubjectString(), provider.actual(), expected));
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
            Assert.fail(String.format("%s [%s] ends with [%s]", provider.traceSubjectString(), provider.actual(), expected));
        }
        return this;
    }

    @Override
    public IValueAssertion<T> nonFunctional() {
        super.nonFunctional();
        return this;
    }
}
