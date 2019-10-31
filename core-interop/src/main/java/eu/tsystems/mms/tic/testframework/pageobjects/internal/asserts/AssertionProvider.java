package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public abstract class AssertionProvider<T> implements ActualProperty<T> {
    @Override
    abstract public T actual();

    abstract public Object subject();

    /**
     * Override this method if you want to finalize something on failed assertion
     */
    public void failed() {
    }
}
