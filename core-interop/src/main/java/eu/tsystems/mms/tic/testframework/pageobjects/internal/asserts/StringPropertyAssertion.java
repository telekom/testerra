package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

/**
 * Allows string based assertions
 * @author Mike Reiche
 */
public interface StringPropertyAssertion<T> extends QuantifiedPropertyAssertion<T>
{
    T getActual();
    boolean is(String expected);
    boolean contains(String expected);
    boolean containsNot(String expected);
    boolean beginsWith(String expected);
    boolean endsWith(String expected);
    QuantifiedPropertyAssertion<Integer> length();
    StringPropertyAssertion<T> perhaps();
}
