package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public abstract class AbstractPropertyAssertion<T> implements IActualProperty<T> {

    protected final AssertionProvider<T> provider;

    public AbstractPropertyAssertion(final AssertionProvider<T> provider) {
        this.provider = provider;
    }

    public T actual() {
        return provider.actual();
    }
}
