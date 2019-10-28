package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.logging.Loggable;

public class StringPropertyAssertion<T> extends QuantifiedPropertyAssertion<T> implements IStringPropertyAssertion<T>, Loggable {

    public StringPropertyAssertion(AssertionProvider<T> provider) {
        super( provider);
    }

    @Override
    public StringPropertyAssertion<T> contains(final String expected) {
        testTimer(t -> configuredAssert.assertContains((String)provider.actual(), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public StringPropertyAssertion<T> containsNot(final String expected) {
        testTimer(t -> configuredAssert.assertContainsNot((String)provider.actual(), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public StringPropertyAssertion<T> beginsWith(String expected) {
        testTimer(t -> configuredAssert.assertBeginsWith(provider.actual(), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public StringPropertyAssertion<T> endsWith(String expected) {
        testTimer(t -> configuredAssert.assertEndsWith(provider.actual(), expected, provider.traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<Integer> length() {
        return propertyAssertionFactory.quantified(new AssertionProvider<Integer>(this) {
            @Override
            public Integer actual() {
                return provider.actual().toString().length();
            }

            @Override
            public Object subject() {
                return String.format("\"%s\".length",provider.actual().toString());
            }
        });
    }

    @Override
    public IStringPropertyAssertion<T> nonFunctional() {
        super.nonFunctional();
        return this;
    }
}
