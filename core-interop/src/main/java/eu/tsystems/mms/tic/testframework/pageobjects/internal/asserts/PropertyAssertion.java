package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

/**
 * Performs an assertion on a given property
 * @author Mike Reiche
 */
public interface PropertyAssertion<T> extends ActualProperty<T> {
    PropertyAssertion<T> shouldWait(boolean wait);
}
