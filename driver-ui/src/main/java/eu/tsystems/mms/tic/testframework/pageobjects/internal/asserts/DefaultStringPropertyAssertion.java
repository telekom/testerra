package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.logging.Loggable;

public class DefaultStringPropertyAssertion<T> extends DefaultQuantifiedPropertyAssertion<T> implements StringPropertyAssertion<T>, Loggable {

    public DefaultStringPropertyAssertion(PropertyAssertion parentAssertion, AssertionProvider<T> provider) {
        super(parentAssertion, provider);
    }

    @Override
    public boolean is(String expected) {
        return super.is(expected);
    }

    @Override
    public boolean contains(String expected) {
        return testTimer(t -> instantAssertion.assertContains((String)provider.getActual(), expected, traceSubjectString()));
    }

    @Override
    public boolean containsNot(String expected) {
        return testTimer(t -> instantAssertion.assertContainsNot((String)provider.getActual(), expected, traceSubjectString()));
    }

    @Override
    public boolean beginsWith(String expected) {
        return testTimer(t -> instantAssertion.assertBeginsWith(provider.getActual(), expected, traceSubjectString()));
    }

    @Override
    public boolean endsWith(String expected) {
        return testTimer(t -> instantAssertion.assertEndsWith(provider.getActual(), expected, traceSubjectString()));
    }

    @Override
    public QuantifiedPropertyAssertion<Integer> length() {
        return new DefaultQuantifiedPropertyAssertion<>(this, new AssertionProvider<Integer>() {
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

    @Override
    public DefaultStringPropertyAssertion<T> perhaps() {
        super.perhaps();
        return this;
    }
}
