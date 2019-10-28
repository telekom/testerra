package eu.tsystems.mms.tic.testframework.pageobjects.internal.asserts;

public interface IStringPropertyAssertion<T> extends IQuantifiedPropertyAssertion<T>
{
    T actual();
    IStringPropertyAssertion<T> contains(final String expected);
    IStringPropertyAssertion<T> containsNot(final String expected);
    IStringPropertyAssertion<T> beginsWith(final String expected);
    IStringPropertyAssertion<T> endsWith(final String expected);
    IQuantifiedPropertyAssertion<Integer> length();

    @Override
    IStringPropertyAssertion<T> nonFunctional();
}
