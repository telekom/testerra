package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

import eu.tsystems.mms.tic.testframework.common.TesterraCommons;

public abstract class AbstractPropertyAssertion<T> implements IActualProperty<T> {

    protected static final PropertyAssertionFactory propertyAssertionFactory = TesterraCommons.ioc().getInstance(PropertyAssertionFactory.class);

    protected final AssertionProvider<T> provider;

    public AbstractPropertyAssertion(final AssertionProvider<T> provider) {
        this.provider = provider;
    }

    public T actual() {
        return provider.actual();
    }
}
