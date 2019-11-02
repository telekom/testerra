package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public abstract class AssertionProvider<T> implements ActualProperty<T> {
    @Override
    abstract public T actual();

    abstract public Object subject();

    /**
     * This method will be called if one of this provider assertion failed
     */
    public void failed() {
    }

    /**
     * This method will be called recursive from bottom to top
     * if one of the assertions finally failed.
     * @param assertion The failed assertion
     */
    public void failedFinally(PropertyAssertion assertion) {
    }
}
