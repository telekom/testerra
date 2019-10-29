package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IStringPropertyAssertion<T> extends IQuantifiedPropertyAssertion<T>
{
    T actual();
    IStringPropertyAssertion<T> contains(String expected);
    IStringPropertyAssertion<T> containsNot(String expected);
    IStringPropertyAssertion<T> beginsWith(String expected);
    IStringPropertyAssertion<T> endsWith(String expected);
    IQuantifiedPropertyAssertion<Integer> length();
}
