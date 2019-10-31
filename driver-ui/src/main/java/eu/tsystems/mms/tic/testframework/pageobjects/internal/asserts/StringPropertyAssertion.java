package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.logging.Loggable;

public class StringPropertyAssertion<T> extends QuantifiedPropertyAssertion<T> implements IStringPropertyAssertion<T>, Loggable {

    public StringPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public IStringPropertyAssertion<T> is(String expected) {
        super.is(expected);
        return this;
    }

    @Override
    public StringPropertyAssertion<T> contains(final String expected) {
        testTimer(t -> assertion.assertContains((String)provider.actual(), expected, traceSubjectString()));
        return this;
    }

    @Override
    public StringPropertyAssertion<T> containsNot(final String expected) {
        testTimer(t -> assertion.assertContainsNot((String)provider.actual(), expected, traceSubjectString()));
        return this;
    }

    @Override
    public StringPropertyAssertion<T> beginsWith(String expected) {
        testTimer(t -> assertion.assertBeginsWith(provider.actual(), expected, traceSubjectString()));
        return this;
    }

    @Override
    public StringPropertyAssertion<T> endsWith(String expected) {
        testTimer(t -> assertion.assertEndsWith(provider.actual(), expected, traceSubjectString()));
        return this;
    }

    @Override
    public IQuantifiedPropertyAssertion<Integer> length() {
        return propertyAssertionFactory.quantified(this, new AssertionProvider<Integer>() {
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
}
