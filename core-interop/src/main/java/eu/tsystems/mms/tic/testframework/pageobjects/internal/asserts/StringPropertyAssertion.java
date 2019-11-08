package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

/**
 * Allows string based assertions
 * @author Mike Reiche
 */
public interface StringPropertyAssertion<T> extends QuantifiedPropertyAssertion<T>
{
    T getActual();
    StringPropertyAssertion<T> is(String expected);
    StringPropertyAssertion<T> contains(String expected);
    StringPropertyAssertion<T> containsNot(String expected);
    StringPropertyAssertion<T> beginsWith(String expected);
    StringPropertyAssertion<T> endsWith(String expected);
    QuantifiedPropertyAssertion<Integer> length();
}
