package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public abstract class AssertionProvider<T> implements IAssertionProvider<T> {
    protected final AbstractAssertion parent;

    public AssertionProvider() {
        this.parent = null;
    }
    public AssertionProvider(AbstractAssertion parentAssertion) {
        this.parent = parentAssertion;
    }

    @Override
    abstract public T actual();

    @Override
    abstract public Object subject();

    public String traceSubjectString() {
        return subject().toString();
    }

    @Override
    public void failed() {
    }

    @Override
    public void passed() {
    }
}
