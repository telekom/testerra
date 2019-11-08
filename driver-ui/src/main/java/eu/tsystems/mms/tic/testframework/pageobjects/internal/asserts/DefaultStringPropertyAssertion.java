package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.logging.Loggable;

public class DefaultStringPropertyAssertion<T> extends DefaultQuantifiedPropertyAssertion<T> implements StringPropertyAssertion<T>, Loggable {

    public DefaultStringPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public StringPropertyAssertion<T> is(String expected) {
        super.is(expected);
        return this;
    }

    @Override
    public DefaultStringPropertyAssertion<T> contains(final String expected) {
        testTimer(t -> assertion.assertContains((String)provider.getActual(), expected, traceSubjectString()));
        return this;
    }

    @Override
    public DefaultStringPropertyAssertion<T> containsNot(final String expected) {
        testTimer(t -> assertion.assertContainsNot((String)provider.getActual(), expected, traceSubjectString()));
        return this;
    }

    @Override
    public DefaultStringPropertyAssertion<T> beginsWith(String expected) {
        testTimer(t -> assertion.assertBeginsWith(provider.getActual(), expected, traceSubjectString()));
        return this;
    }

    @Override
    public DefaultStringPropertyAssertion<T> endsWith(String expected) {
        testTimer(t -> assertion.assertEndsWith(provider.getActual(), expected, traceSubjectString()));
        return this;
    }

    @Override
    public QuantifiedPropertyAssertion<Integer> length() {
        return propertyAssertionFactory.quantified(this, new AssertionProvider<Integer>() {
            @Override
            public Integer getActual() {
                return provider.getActual().toString().length();
            }

            @Override
            public String getSubject() {
                return String.format("\"%s\".length",provider.getActual().toString());
            }
        });
    }
}
