package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

/**
 * Provides properties for further assertion
 * @author Mike Reiche
 */
public abstract class AssertionProvider<T> implements ActualProperty<T> {
    @Override
    abstract public T actual();

    abstract public Object subject();

    /**
     * This method will be called called recursive from bottom to top
     * if one of the assertions finally failed.
     * @param assertion The failed assertion
     */
    public void failed(PropertyAssertion assertion) {
    }

    /**
     * This method will be called recursive from bottom to top
     * if one of the assertions finally failed.
     * @param assertion The failed assertion
     */
    public void failedFinally(PropertyAssertion assertion) {
    }
}
