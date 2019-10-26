package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public abstract class AbstractAssertion<T> implements IAssertion<T> {

    protected final AssertionProvider<T> provider;

    public AbstractAssertion(final AssertionProvider<T> provider) {
        this.provider = provider;
    }

    public T actual() {
        return provider.actual();
    }
}
